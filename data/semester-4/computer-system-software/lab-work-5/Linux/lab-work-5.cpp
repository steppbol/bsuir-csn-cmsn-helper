#include "FileHandler.h"

int main(int argc, char *argv[])
{
  void *dinamicLibrary = dlopen("./IOfuncs.so",RTLD_NOW);
  if(dinamicLibrary == NULL)
    return 1;
  runReader =(int(*)(struct FileHeader*)) dlsym(dinamicLibrary,"runReader");
  runWriter = (int(*)(struct FileHeader*))dlsym(dinamicLibrary,"runWriter");  
  pthread_mutex_lock(&readCompleted);
  pthread_mutex_lock(&readStop);
  pthread_t threadRead, threadWrite;
  fileInfo.aiocbStruct.aio_offset = 0;
  fileInfo.aiocbStruct.aio_buf = fileInfo.buffer;
  fileInfo.numberOfBytes = sizeof(fileInfo.buffer);
  fileInfo.aiocbStruct.aio_sigevent.sigev_notify = SIGEV_NONE;
  fileInfo.positionInFile = 0;
  fileInfo.positionOutFile = 0;
  printf("Start parsing...\n");
  pthread_create(&threadRead, NULL, readThread, (void*)"./files");
  pthread_create(&threadWrite, NULL, writeThread, (void*)"output.txt");
  pthread_join(threadRead, NULL);
  pthread_join(threadWrite, NULL);
  printf("Operation complete!\n");
  return 0;
}