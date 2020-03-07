#include "FileThreads.h"

int main()
{
	HANDLE readAndWriteThreads[2];  
	HANDLE handleEvent = createEvents();
	fileInfo.Overlapped.Offset = 0;
	fileInfo.Overlapped.OffsetHigh = 0;
	fileInfo.Overlapped.hEvent = handleEvent;
	fileInfo.positionOutFile = 0;
	fileInfo.numberOfBytes = sizeof(fileInfo.buffer);

	dinamicLibrary = LoadLibrary("IOfuncs.dll");

	cout << "Start parsing!\n";
	readAndWriteThreads[0] = CreateThread(NULL, 0, threadWriter, (LPVOID)("output.txt"), 0, NULL);
	readAndWriteThreads[1] = CreateThread(NULL, 0, threadReader, (LPVOID)("D:\\Workspace\\C++\\lab-work-5\\Files"), 0, NULL);

	WaitForMultipleObjects(2, readAndWriteThreads, TRUE, INFINITE);

	CloseHandle(readAndWriteThreads[0]);
	CloseHandle(readAndWriteThreads[1]);
	closeEvents(handleEvent);
	FreeLibrary(dinamicLibrary);
	cout << "Operation complete!";
	cin.get();
	return 0;
}
