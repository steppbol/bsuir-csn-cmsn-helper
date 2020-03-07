#include <aio.h>
#include <errno.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>

struct FileInfo
{
  int fileHeader;
  char  buffer[100];
  size_t numberOfBytes;
  size_t numberOfBytesTransferred;
  off_t positionInfile;
  off_t positionOutfile;
  struct aiocb aiocbStruct;
};


int runReader(struct FileInfo *fileInfo)
{
  int bytesTransferred;
  fileInfo->aiocbStruct.aio_offset = fileInfo->positionInfile;
  fileInfo->aiocbStruct.aio_fildes = fileInfo->fileHeader;
  fileInfo->aiocbStruct.aio_nbytes = fileInfo->numberOfBytes;
  aio_read(&fileInfo->aiocbStruct);
  while(aio_error(&fileInfo->aiocbStruct) == EINPROGRESS);
  fileInfo->numberOfBytesTransferred = aio_return(&fileInfo->aiocbStruct);
  if(fileInfo->numberOfBytesTransferred) 
    fileInfo->positionInfile = fileInfo->positionInfile + fileInfo->numberOfBytesTransferred;
  return fileInfo->numberOfBytesTransferred;
}

int runWriter(struct FileInfo *fileInfo)
{
  int writeResult;
  fileInfo->aiocbStruct.aio_offset = fileInfo->positionOutfile;
  fileInfo->aiocbStruct.aio_fildes = fileInfo->fileHeader;
  fileInfo->aiocbStruct.aio_nbytes = fileInfo->numberOfBytesTransferred;
  aio_write(&fileInfo->aiocbStruct);
  while((writeResult = aio_error(&fileInfo->aiocbStruct)) == EINPROGRESS);//return AIO_ERROR(3): 0 - успех, EOVERFLOW - конец файла 
  fileInfo->positionOutfile = fileInfo->positionOutfile + aio_return(&fileInfo->aiocbStruct);
  return writeResult;
}
