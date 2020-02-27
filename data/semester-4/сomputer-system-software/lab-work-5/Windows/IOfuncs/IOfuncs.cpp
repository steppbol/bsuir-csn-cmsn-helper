// IOfuncs.cpp : Defines the exported functions for the DLL application.  

#include "stdafx.h"  
#include <windows.h>
#include "stdio.h"
#include "string.h"

#ifdef __cplusplus 
extern "C" {
#endif

	struct FileInfo
	{
		HANDLE fileHeader;
		DWORD numberOfBytes;
		CHAR  buffer[100];
		DWORD  positionInFile;
		DWORD  positionOutFile;
		OVERLAPPED Overlapped;
	};

	__declspec(dllexport) BOOL readFromFile(FileInfo* fileInfo)
	{
		BOOL readResult;
		DWORD numberOfBytesTrasferred;

		fileInfo->Overlapped.Offset = fileInfo->positionInFile;
		ReadFile(fileInfo->fileHeader, fileInfo->buffer, fileInfo->numberOfBytes, NULL, &fileInfo->Overlapped);
		WaitForSingleObject(fileInfo->Overlapped.hEvent, INFINITE);
		readResult = GetOverlappedResult(fileInfo->fileHeader, &fileInfo->Overlapped, &numberOfBytesTrasferred, FALSE);
		if (readResult) fileInfo->positionInFile = fileInfo->positionInFile + numberOfBytesTrasferred;
		return readResult;
	}
	__declspec(dllexport) BOOL writeToFile(FileInfo* fileInfo)
	{
		BOOL writeResult;
		DWORD numberOfBytesTrasferred;

		fileInfo->Overlapped.Offset = fileInfo->positionOutFile;
		WriteFile(fileInfo->fileHeader, fileInfo->buffer, fileInfo->Overlapped.InternalHigh, NULL, &fileInfo->Overlapped);
		WaitForSingleObject(fileInfo->Overlapped.hEvent, INFINITE);
		writeResult = GetOverlappedResult(fileInfo->fileHeader, &fileInfo->Overlapped, &numberOfBytesTrasferred, FALSE);
		if (writeResult) fileInfo->positionOutFile = fileInfo->positionOutFile + numberOfBytesTrasferred;
		return writeResult;
	}
#ifdef __cplusplus
}
#endif
