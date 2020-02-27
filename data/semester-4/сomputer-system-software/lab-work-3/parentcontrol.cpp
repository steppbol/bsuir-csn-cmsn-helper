#ifdef __linux__
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/sem.h>
#include <sys/ipc.h>
#include <stdio.h>
#include <stdlib.h>
#include <ncurses.h>
#include <time.h>
#include <unistd.h>
#include <signal.h>
#include <string>
#include <iostream>
#include <algorithm>
#include <sstream>  
#include <termios.h>

using namespace std;

#define MAX_COUNT 5

int WaitSemaphore(int sem_id, int num, int waitTime);
int ReleaseSemaphore(int sem_id, int num);

int main(int argc, char *argv[])
{
	initscr();
	curs_set(0);

	int currentNum = 0;
	int processNumber = 0;

	int flag = 0;
	int flagEnd = 1;
	char c = 0;
	int i = 0;

	pid_t cpid[MAX_COUNT];
	int semaphores[MAX_COUNT];

	while (c != 'q')
	{
		timeout(1);
		c = getch();
		switch (c)
		{
		case '+':
			if (processNumber < MAX_COUNT)
			{
				cpid[processNumber] = fork();
				processNumber++;

				switch (cpid[processNumber - 1])
				{
				case 0: {
					stringstream strs;
					strs << processNumber;
					string temp_str = strs.str();
					char* char_type = (char*)temp_str.c_str();
					if (execlp("./client", "client", char_type, argv[1], argv[2], NULL) == -1)
						printw("Error.");
					break;
				}

				case -1:
					printf("Error!\n");
					break;

				default:
					semaphores[processNumber - 1] = semget(cpid[processNumber - 1], 4, IPC_CREAT | 0666);
					semctl(semaphores[processNumber - 1], 0, SETALL, 0);
					if (semaphores[processNumber - 1] < 0)
					{
						printf("Semaphores is not created.");
						return 0;
					}
					break;
				}
			}

			break;

		case '-':
			if (processNumber > 0)
			{
				ReleaseSemaphore(semaphores[processNumber - 1], 3);
				waitpid(cpid[processNumber - 1], NULL, NULL);
				processNumber--;
				if (currentNum >= processNumber)
				{
					currentNum = 0;
					flag = 1;
					flagEnd = 1;
				}

			}
			break;
		}

		if (WaitSemaphore(semaphores[currentNum], 2, 1) == 0)
		{
			flagEnd = 1;
		}

		if (flagEnd && processNumber > 0)
		{
			flagEnd = 0;
			if (currentNum >= processNumber - 1)
				currentNum = 0;
			else if (!flag)
				currentNum++;

			flag = 0;
			ReleaseSemaphore(semaphores[currentNum], 1);
		}
	}

	if (cpid[processNumber - 1] != 0)
	{
		while (processNumber >= 0)
		{
			ReleaseSemaphore(semaphores[processNumber - 1], 3);
			waitpid(cpid[processNumber - 1], NULL, NULL);
			processNumber--;
		}
	}

	return 0;
}

int WaitSemaphore(int textSemId, int num, int tempTime)
{
	long long int waitTime = tempTime * 100000;
	const struct timespec time = { 0, waitTime };
	struct sembuf buf;
	buf.sem_op = -1;
	buf.sem_flg = SEM_UNDO;
	buf.sem_num = num;
	if (waitTime < 0)
	{
		return semop(textSemId, &buf, 1);
	}
	else
	{
		return semtimedop(textSemId, &buf, 1, &time);
	}
}

int ReleaseSemaphore(int sem_id, int num)
{
	struct sembuf buf;
	buf.sem_op = 1;
	buf.sem_flg = SEM_UNDO;
	buf.sem_num = num;
	return semtimedop(sem_id, &buf, 1, NULL);
}
#elif _WIN32 | _WIN64
#include<stdio.h>
#include<windows.h>
#include<conio.h>
#include<time.h>
#include<iostream>
#include<string>
#include <vector>
#include <fstream>

#define MAX_COUNT 5

using namespace std;

PROCESS_INFORMATION CreateNewProcess(char *commandline);
int getch_noblock();

int main()
{
	bool flag = true;
	char eventID[30];
	char character = 0;
	int currentNumber = 0;
	int processCounter = 0;
	const string SemaphoreCanPrint = "semaphore_can_print";
	const string SemaphoreEndPrint = "semaphore_end_print";
	const string SemaphoreExit = "semaphore_exit";

	PROCESS_INFORMATION procInf[MAX_COUNT];

	HANDLE canPrintSemaphores[MAX_COUNT];
	HANDLE endPrintSemaphores[MAX_COUNT];
	HANDLE exitSemaphores[MAX_COUNT];

	while (true)
	{
		Sleep(1);
		character = getch_noblock();
		switch (character)
		{
		case '+':
		{
			if (processCounter < MAX_COUNT)
			{
				sprintf_s(eventID, " %d", processCounter + 1);
				procInf[processCounter] = CreateNewProcess(eventID);
				string buf = SemaphoreCanPrint;
				buf = buf + to_string(processCounter + 1);
				canPrintSemaphores[processCounter] = CreateSemaphore(NULL, 0, 1, const_cast<char *>(buf.c_str())); // Семафор, уведомляющий о разрешении печати
				buf = SemaphoreEndPrint;
				buf = buf + to_string(processCounter + 1);
				endPrintSemaphores[processCounter] = CreateSemaphore(NULL, 0, 1, const_cast<char *>(buf.c_str())); // Семафор, уведомляющий о завершении печати
				buf = SemaphoreExit;
				buf = buf + to_string(processCounter + 1);
				exitSemaphores[processCounter] = CreateSemaphore(NULL, 0, 1, const_cast<char *>(buf.c_str())); // Семафор, уведомляющий о завершении работы	
				processCounter++;
			}
			break;
		}

		case '-':
		{
			if (processCounter > 0)
			{
				ReleaseSemaphore(exitSemaphores[processCounter - 1], 1, NULL);
				WaitForSingleObject(procInf[processCounter - 1].hProcess, INFINITE); // Ожидаем завершения последнего процесса
				CloseHandle(canPrintSemaphores[processCounter - 1]);
				CloseHandle(endPrintSemaphores[processCounter - 1]);
				CloseHandle(exitSemaphores[processCounter - 1]);

				if(currentNumber >= processCounter)
				{
					currentNumber = 0;
					flag = true;
				}

				processCounter--;
			}
			break;
		}
		case 'q':
		{
			if (processCounter > 0)
			{
				while (processCounter > 0)
				{
					ReleaseSemaphore(exitSemaphores[processCounter - 1], 1, NULL);
					WaitForSingleObject(procInf[processCounter - 1].hProcess, INFINITE); // Ожидаем завершения последнего процесса
					CloseHandle(canPrintSemaphores[processCounter - 1]);
					CloseHandle(endPrintSemaphores[processCounter - 1]);
					CloseHandle(exitSemaphores[processCounter - 1]);
					processCounter--;
				}
				currentNumber = 0;
			}
			printf("\n\n");
			system("pause");
			return EXIT_SUCCESS;
		}
		}

		if ((processCounter > 0 && flag) || (WaitForSingleObject(endPrintSemaphores[currentNumber - 1], 1) == WAIT_OBJECT_0 && !flag))
		{
			if (currentNumber >= processCounter)
			{
				currentNumber = 0;
			}
			ReleaseSemaphore(canPrintSemaphores[currentNumber], 1, NULL);
			currentNumber++;
			flag = false;
		}

	}
	while (processCounter >= 0)
	{
		CloseHandle(canPrintSemaphores[processCounter]);
		CloseHandle(endPrintSemaphores[processCounter]);
		processCounter--;
	}
	return EXIT_FAILURE;
}

PROCESS_INFORMATION CreateNewProcess(char *commandline)
{
	STARTUPINFO si;
	ZeroMemory(&si, sizeof(si));
	si.cb = sizeof(si);
	PROCESS_INFORMATION pi;
	ZeroMemory(&pi, sizeof(pi));
	string str = "Child.exe";
	string space = " ";
	str = str + space + commandline;
	LPSTR commandLineText = const_cast<char *>(str.c_str());
	if (!CreateProcess(NULL,
		commandLineText,
		NULL,
		NULL,
		FALSE,
		NULL,
		NULL,
		NULL,
		&si,
		&pi))
		printf("Create Process failed (%d)\n", GetLastError());
	return pi;
}

int getch_noblock()
{
	if (_kbhit())
		return _getch();
	else
		return -1;
}
#endif