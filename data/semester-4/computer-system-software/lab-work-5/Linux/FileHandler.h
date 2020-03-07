#pragma once
#include <errno.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <aio.h>
#include <sys/stat.h>
#include <dlfcn.h>
#include <sys/types.h>
#include <dirent.h>
#include <string.h>
#include <fcntl.h>
#include <sys/stat.h>

struct FileHeader
{
  int fileHeader;
  char  buffer[100];
  size_t numberOfBytes;
  size_t numberOfBytesTransferred;
  off_t positionInFile;
  off_t positionOutFile;
  struct aiocb aiocbStruct;
} fileInfo;

int (*runReader) (struct FileHeader *);
int (*runWriter) (struct FileHeader *);

void* readThread(void*);
void* writeThread(void*);

pthread_mutex_t writeCompleted = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t readCompleted = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t readStop = PTHREAD_MUTEX_INITIALIZER;

void* readThread(void *path)
{
  struct dirent entry;
  struct dirent *result;
  int readFileHeader;
  char readFilePath[256];
  int readResult = 0;
  const char* folderPath = (const char*) path;
  DIR *folder = opendir(folderPath);
  if(folder == NULL)
    return NULL;
 
  while ( readdir_r(folder, &entry, &result) == 0 && result != NULL)
    if( strcmp( entry.d_name, "." ) != 0 && strcmp( entry.d_name, ".." ) != 0 )
      break;

  if (result == NULL)
    return NULL;
  
  while(1)
  {
    pthread_mutex_lock(&writeCompleted);
 
    if(readResult == 0)
    {
      fileInfo.positionInFile = 0;
      strcpy(readFilePath, folderPath); strcat(readFilePath, "/");
      strcat(readFilePath, entry.d_name); 
      readFileHeader = open(readFilePath, O_RDONLY);
    }
    fileInfo.fileHeader = readFileHeader;
    readResult = runReader(&fileInfo);
    
    if(readResult == 0)
    { 
      printf("%s read!\n", entry.d_name);
      while ( readdir_r(folder, &entry, &result) == 0 && result != NULL)
      if( strcmp( entry.d_name, "." ) != 0 && strcmp( entry.d_name, ".." ) != 0 )
      break;     
      
      if (result != NULL)
      {
      close(readFileHeader);
    pthread_mutex_unlock(&writeCompleted);
      continue;
      } else break;
    }
    pthread_mutex_unlock(&readCompleted);
  } 
  pthread_mutex_unlock(&readStop);
  pthread_mutex_unlock(&readCompleted);
  close(readFileHeader);
  return NULL;
}

void* writeThread(void * path)
{
  const char* outputFilePath = (const char*) path;
  int outputFileHeader = open(outputFilePath, O_WRONLY | O_CREAT | O_APPEND |O_TRUNC, S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH);
  while(1)
  {   
    pthread_mutex_lock(&readCompleted);
    if(pthread_mutex_trylock(&readStop) == 0)
      break;    
    fileInfo.fileHeader = outputFileHeader;
    runWriter(&fileInfo);
    pthread_mutex_unlock(&writeCompleted);
  }
  close(outputFileHeader);
  return NULL; 
}
