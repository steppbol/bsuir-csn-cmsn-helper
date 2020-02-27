#pragma once
#include <windows.h>
#include <process.h>
#include <stdio.h>
#include <string>
#include <iostream>

using namespace std;

struct FileInfo
{
	HANDLE fileHeader;        
	DWORD numberOfBytes;	  
	CHAR  buffer[100];		 
	DWORD  positionInFile;	  
	DWORD  positionOutFile;	  
	OVERLAPPED Overlapped;
} fileInfo;

HINSTANCE dinamicLibrary;
HANDLE readerCompleted;
HANDLE readerStop;
HANDLE writerCompleted;

DWORD WINAPI threadWriter(PVOID);
DWORD WINAPI threadReader(PVOID);

HANDLE createEvents();
void closeEvents(HANDLE);

DWORD WINAPI threadReader(PVOID path)
{
	WIN32_FIND_DATA findFileData;
	HANDLE readFileHandle;
	BOOL readResult = false;
	string folder(((const char*)path));
	folder.append("\\");
	string fileMask = folder + "*.txt";
	char readFilePath[MAX_PATH];
	HANDLE findHandle = FindFirstFile(fileMask.c_str(), &findFileData);
	BOOL(*Read)(FileInfo*) = (BOOL(*)(FileInfo*))GetProcAddress(dinamicLibrary, "readFromFile");

	if (findHandle == INVALID_HANDLE_VALUE)
		return EXIT_FAILURE;
	while (true)
	{
		WaitForSingleObject(writerCompleted, INFINITE);
		if (readResult == false)
		{
			fileInfo.positionInFile = 0;
			strcpy_s(readFilePath, folder.c_str());
			strcat_s(readFilePath, findFileData.cFileName);
			readFileHandle = CreateFile(readFilePath, GENERIC_READ, 0, NULL, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, NULL);
		}
		fileInfo.fileHeader = readFileHandle;
		readResult = (Read)(&fileInfo);
		if (!readResult && GetLastError() == ERROR_HANDLE_EOF)
		{
			if (FindNextFile(findHandle, &findFileData))
			{
				CloseHandle(readFileHandle);
				SetEvent(writerCompleted);
				continue;
			}
			else break;
		}

			SetEvent(readerCompleted);
	}
	FindClose(findHandle);
	CloseHandle(readFileHandle);
	SetEvent(readerStop);
	return EXIT_SUCCESS;
}

DWORD WINAPI threadWriter(PVOID path)
{
	HANDLE outFileHandle = CreateFile((const char*)path, GENERIC_WRITE, 0, NULL, CREATE_ALWAYS, FILE_FLAG_OVERLAPPED, NULL);
	BOOL(*Write)(FileInfo*) = (BOOL(*)(FileInfo*))GetProcAddress(dinamicLibrary, "writeToFile");
	HANDLE eventsOfReadAndStopRead[2] = { readerCompleted, readerStop };
	int stopEvent = 1;
	while (true)
	{
		int currentEvent = WaitForMultipleObjects(2, eventsOfReadAndStopRead, FALSE, INFINITE) - WAIT_OBJECT_0;
		if (currentEvent == stopEvent) break;
		fileInfo.fileHeader = outFileHandle;
		(Write)(&fileInfo);
		SetEvent(writerCompleted);
	}
	CloseHandle(outFileHandle);
	return EXIT_SUCCESS;
}

HANDLE createEvents()
{
	HANDLE eventHandler = CreateEvent(NULL, FALSE, TRUE, TEXT("eventMain")); ;
	writerCompleted = CreateEvent(NULL, FALSE, TRUE, NULL);
	readerCompleted = CreateEvent(NULL, FALSE, FALSE, NULL);
	readerStop = CreateEvent(NULL, TRUE, FALSE, NULL);

	return eventHandler;
}

void closeEvents(HANDLE eventHandler)
{
	CloseHandle(writerCompleted);
	CloseHandle(readerCompleted);
	CloseHandle(readerStop);
	CloseHandle(eventHandler);
}
