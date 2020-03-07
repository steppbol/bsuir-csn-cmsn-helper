#ifdef __linux__
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
#include <ncurses.h>
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include <cstring>
#include <signal.h>
#include <semaphore.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <iterator>
#include <algorithm>
#include <csignal>
#include <sys/sem.h>

using namespace std;

struct sigaction printSignal, endSignal;
int printFlag = 0;
int flagEnd = 1;
void canPrint(int);
void setEndFlag(int);

int main(int argc, char* argv[])
{
	initscr();

	printSignal.sa_handler = canPrint;
	sigaction(SIGUSR1, &printSignal, NULL);

	endSignal.sa_handler = setEndFlag;
	sigaction(SIGUSR2, &endSignal, NULL);

	char* strAmountOfMoney = "10";
	char* strCurrency = "BYN";
	char putString[256];
	
	char arrOfString[256][256] = { "First process", "Second process", "Third process", "Fourth process", "Fifth process" };
	int currentProcess = atoi(argv[0]);
	if(currentProcess == 1)
		sprintf(putString, "%s ", arrOfString[0]);
	else if (currentProcess == 2)
		sprintf(putString, "%s ", arrOfString[1]);
	else if (currentProcess == 3)
		sprintf(putString, "%s ", arrOfString[2]);
	else if (currentProcess == 4)
		sprintf(putString, "%s ", arrOfString[3]);
	else if (currentProcess == 5)
		sprintf(putString, "%s ", arrOfString[4]);

	pid_t pid = fork();

	if (pid == -1) cout << "Error.";

	if (pid == 0)
	{
		if (execlp("./child_atm", strCurrency, strAmountOfMoney, NULL) == -1)
			cout << "Error.";
	}

	else
	{
		if (atoi(argv[0]) == 1) kill(getppid(), SIGUSR2);
		flagEnd = 0;
		while (!flagEnd)
		{
			usleep(100000);
			if (printFlag)
			{
				for (int i = 0; i < strlen(putString); i++)
				{
					if (flagEnd)
					{
						printFlag = 0;
						kill(getppid(), SIGUSR2);
						return 0;
					}
					printw("%c", putString[i]);
					refresh();
					usleep(100000);
				}
				refresh();
				printFlag = 0;
				kill(getppid(), SIGUSR2);
			}
		}
		return 0;
	}
	endwin();
	return 0;
}

void canPrint(int signo)
{
	printFlag = 1;
}

void setEndFlag(int signo)
{
	flagEnd = 1;
}

#endif

#ifdef _WIN32 | _WIN64
#define _CRT_SECURE_NO_WARNINGS
#include <windows.h>
#include <iostream>
#include <fstream>

using namespace std;

void printString(int, char*);

int main(int argc, char* argv[])
{
	STARTUPINFO si;
	PROCESS_INFORMATION pi;

	ZeroMemory(&si, sizeof(si));
	si.cb = sizeof(si);
	ZeroMemory(&pi, sizeof(pi));

	char strCurrency[256] = "BYN";
	char strAmountOfMoney[256] = "10";

	char strWin[256];
	char strRes[256];
	char eventID[30];

	strcpy(eventID, argv[1]);

	char arrOfString[256][256] = { "First process", "Second process", "Third process", "Fourth process", "Fifth process" };
	int currentProcess = atoi(argv[1]);
	if (currentProcess == 1)
		sprintf(strRes, "%s ", arrOfString[0]);
	else if (currentProcess == 2)
		sprintf(strRes, "%s ", arrOfString[1]);
	else if (currentProcess == 3)
		sprintf(strRes, "%s ", arrOfString[2]);
	else if (currentProcess == 4)
		sprintf(strRes, "%s ", arrOfString[3]);
	else if (currentProcess == 5)
		sprintf(strRes, "%s ", arrOfString[4]);

	sprintf_s(strWin, "ATM.exe %s %s", strCurrency, strAmountOfMoney);

	if (!CreateProcess(NULL,                 // No module name (use command line).
		strWin,							     // Command line.
		NULL,								 // Process handle not inheritable.
		NULL,								 // Thread handle not inheritable.
		FALSE,								 // Set handle inheritance to FALSE.
		NULL,
		NULL,								 // Use parent's environment block.
		NULL,								 // Use parent's starting directory.
		&si,								 // Pointer to STARTUPINFO structure.
		&pi)								 // Pointer to PROCESS_INFORMATION structure.
		)
	{
		printf("CreateProcess failed (%d)\n", GetLastError());
		return -1;
	}

	printString(atoi(eventID), strRes);

	//system("pause");

	CloseHandle(pi.hThread);

	return 0;
}

void printString(int eventID, char* string)
{
	char tempEventID[30];
	sprintf_s(tempEventID, " %d", eventID);
	HANDLE closeEvent = OpenEvent(EVENT_ALL_ACCESS, FALSE, tempEventID);

	sprintf_s(tempEventID, " %dp", eventID);
	HANDLE canPrintEvent = OpenEvent(EVENT_ALL_ACCESS, FALSE, tempEventID);

	while (true)
	{
		if (WaitForSingleObject(canPrintEvent, 1) == WAIT_OBJECT_0)
		{
			for (int i = 0; i < strlen(string) - 1; i++)
			{
				if (WaitForSingleObject(closeEvent, 0) == WAIT_OBJECT_0)
				{
					CloseHandle(closeEvent);
					CloseHandle(canPrintEvent);
					return;
				}
				cout << string[i];
				Sleep(100);
			}
			cout << " ";
			ResetEvent(canPrintEvent);
		}

		if (WaitForSingleObject(closeEvent, 0) == WAIT_OBJECT_0)
		{
			CloseHandle(closeEvent); CloseHandle(canPrintEvent);
			return;
		}
	}
	return;
}
#endif