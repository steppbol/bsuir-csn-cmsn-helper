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
#include <sstream>
#include <sys/sem.h>

using namespace std;

#define MAX_COUNT_OF_PROCESS 5

int initialProcessLinux();
void addNewProcess();
void delProcess();
void lastProcess();
void delAllProcesses();
void canPrint(int);
void setEndFlag(int);

pid_t cpid[MAX_COUNT_OF_PROCESS];
int currentNum = 0;
int processNumber = 0;
int flag = 0;
int printFlag = 0;
int flagEnd = 1;
struct sigaction printSignal, endSignal;

int main()
{
	initscr();
	curs_set(0);
	clear();
	noecho();
	refresh();

	initialProcessLinux();

	clear();
	endwin();
	return 0;
}

int initialProcessLinux()
{
	printSignal.sa_handler = canPrint;
	sigaction(SIGUSR1, &printSignal, NULL);

	endSignal.sa_handler = setEndFlag;
	sigaction(SIGUSR2, &endSignal, NULL);

	char character = 0;

	while (character != 'q')
	{
		character = getchar();
		if (c == '+') addNewProcess();
		if (c == '-') delProcess();
		lastProcess();
	}

	delAllProcesses();
}

void addNewProcess()
{
	if (processNumber < MAX_COUNT_OF_PROCESS)
	{
		cpid[processNumber] = fork();
		processNumber++;

		{
			if (cpid[processNumber - 1] == 0)
			{
				char str[256];
				sprintf(str, "%d", processNumber);
				if (execlp("./parent_atm", str, NULL) == -1)
					cout << "Error.";
			}

		}
	}
}

void delProcess()
{
	if (processNumber > 0)
	{
		kill(cpid[processNumber - 1], SIGUSR2);
		waitpid(cpid[processNumber - 1], NULL, NULL);

		processNumber--;

		if (currentNum >= processNumber)
		{
			currentNum = 0;
			flag = 1;
			flagEnd = 1;
		}
	}
}

void lastProcess()
{
	if (flagEnd && processNumber > 0)
	{
		flagEnd = 0;
		if (currentNum >= processNumber - 1)
			currentNum = 0;
		else if (!flag) currentNum++;

		flag = 0;
		kill(cpid[currentNum], SIGUSR1);
	}
}

void delAllProcesses()
{
	if (cpid[processNumber - 1] != 0)
		while (processNumber >= 0)
		{
			kill(cpid[processNumber - 1], SIGUSR2);
			waitpid(cpid[processNumber - 1], NULL, NULL);
			processNumber--;
		}
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
#include <conio.h>
#include <iostream>
#include <vector>

#define MAX_COUNT_OF_PROCESS 5

using namespace std;

vector<HANDLE> closeEvents;
vector<HANDLE> canPrintEvents;
PROCESS_INFORMATION procInf[MAX_COUNT_OF_PROCESS];
char EventID[30];
int currentNum = 0;
bool flag = false;

int initialProcessWin();
void addNewProcess();
void delProcess();
int delAllProcesses();
void lastProcessPrint();
int getch_noblock();

int main()
{
	initialProcessWin();
	return 0;
}

int initialProcessWin()
{
	char character;

	while (true)
	{
		character = getch_noblock();
		if (character == '+') addNewProcess();
		else if (character == '-') delProcess();
		else if (character == 'q') return delAllProcesses();

		lastProcessPrint();

	}
	return EXIT_FAILURE;
}

void addNewProcess()
{
	if (closeEvents.size() < MAX_COUNT_OF_PROCESS)
	{
		STARTUPINFO si;
		ZeroMemory(&si, sizeof(si));
		si.cb = sizeof(si);
		PROCESS_INFORMATION infProc;
		ZeroMemory(&infProc, sizeof(infProc));

		char strWin[256];

		sprintf_s(EventID, " %d", closeEvents.size() + 1);
		closeEvents.push_back(CreateEvent(NULL, FALSE, FALSE, EventID));

		sprintf_s(EventID, " %dp", canPrintEvents.size() + 1);
		canPrintEvents.push_back(CreateEvent(NULL, TRUE, FALSE, EventID));

		sprintf_s(strWin, "Parent.exe %s", EventID);

		if (!CreateProcess(NULL, strWin, NULL, NULL, FALSE, NULL, NULL, NULL, &si, &infProc))
			printf("Create Process failed (%d)\n", GetLastError());

		procInf[closeEvents.size() - 1] = infProc;
	}
}

void delProcess()
{
	if (closeEvents.size() > 0)
	{

		SetEvent(closeEvents.back());
		WaitForSingleObject(procInf[closeEvents.size() - 1].hProcess, INFINITE);

		if (!CloseHandle(closeEvents.back()))
			printf("Close Handle failed (%d)\n", GetLastError());
		if (!CloseHandle(canPrintEvents.back()))
			printf("Close Handle failed (%d)\n", GetLastError());

		closeEvents.pop_back();
		canPrintEvents.pop_back();
		if (currentNum >= closeEvents.size())
		{
			currentNum = 0;
			flag = true;
		}
	}
}

int delAllProcesses()
{
	if (closeEvents.size() > 0)
	{
		while (closeEvents.size() > 0)
		{
			SetEvent(closeEvents.back());
			WaitForSingleObject(procInf[closeEvents.size() - 1].hProcess, INFINITE);

			if (!CloseHandle(closeEvents.back()) || !CloseHandle(canPrintEvents.back()) || !CloseHandle(procInf[closeEvents.size() - 1].hProcess)
				|| !CloseHandle(procInf[closeEvents.size() - 1].hThread))
				printf("Close Handle failed (%d)\n", GetLastError());

			closeEvents.pop_back();
			canPrintEvents.pop_back();
		}
		currentNum = 0;
	}
	printf("\n\n");
	system("pause");
	return EXIT_SUCCESS;
}

void lastProcessPrint()
{
	if (canPrintEvents.size() > 0 && WaitForSingleObject(canPrintEvents[currentNum], 0) == WAIT_TIMEOUT)
	{
		if (currentNum >= canPrintEvents.size() - 1) currentNum = 0;
		else if (!flag) currentNum++;
		flag = false;

		SetEvent(canPrintEvents[currentNum]);
	}
}

int getch_noblock()
{
	if (_kbhit())
		return _getch();
	else
		return -1;
}
#endif