#ifdef __linux__
#include <iostream>
#include <stdio.h>
#include <termios.h> 
#include <ncurses.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/shm.h>
#include <unistd.h>
#include <sys/sem.h>
#include <sys/types.h>
#include <wait.h>
#include <semaphore.h>
#include <string.h>
#include <math.h>
#include <sstream>

using namespace std;

int WaitSemaphore(int textSemId, int num, int waitTime);
int ReleaseSemaphore(int textSemId, int num);

int main(int argc, char *argv[])
{
	initscr();
	curs_set(0);

	string amountOfMoney = "60";
	string currencyOfMoney = "BYN";

	int mainPid;
	int textSemKey;

	char strRes[256];

	char arrOfString[256][256] = { "But I would walk 500 miles.", "And I would walk 500 more.",
		"Just to be the man who walks a thousand miles.",
		"To fall down at your door.", "500 MILES." };
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

	if (argv[2])
	{
		mainPid = atoi(argv[2]);
	}
	else
	{
		return 0;
	}

	int bufferSize = 64;
	int shmId = shmget((key_t)mainPid, bufferSize, 0666);

	if (shmId < 0)
	{
		printf("shmget error\n");
		return(0);
	}

	int mainSemId = semget(getpid(), 0, IPC_CREAT | 0666);

	semctl(mainSemId, 0, SETALL, 0);

	if (mainSemId < 0)
	{
		printf("Main Semaphores is not created.");
		return 0;
	}

	if (argv[3])
	{
		textSemKey = atoi(argv[3]);
	}
	else
	{
		return 0;
	}

	int textSemId = semget((key_t)textSemKey, 0, IPC_CREAT | 0666);

	semctl(textSemId, 0, SETALL, 0);
	if (textSemId < 0)
	{
		printf("Text Semaphores is not connected.");
		return 0;
	}

	string message;
	message = amountOfMoney + " " + currencyOfMoney + '\0';

	char *shm, *s;
	shm = (char*)shmat(shmId, NULL, 0);
	if (shm == (char *)-1)
	{
		perror("shmat");
		return 1;
	}

	ReleaseSemaphore(textSemId, 2);
	s = shm;
	strncpy(s, message.c_str(), sizeof(message));

	WaitSemaphore(textSemId, 1, -1);

	char tempMessage[bufferSize];
	WaitSemaphore(textSemId, 2, -1);
	int p = 0;
	for (s = shm; *s != NULL; s++, p++)
	{
		tempMessage[p] = *s;
		refresh();
	}

	ReleaseSemaphore(textSemId, 1);

	char putString[256];

	if (argv[1])
	{
		sprintf(putString, "%s", strRes);
	}
	else
	{
		return 0;
	}

	while (WaitSemaphore(mainSemId, 3, 1) != 0)
	{
		if (WaitSemaphore(mainSemId, 1, 1) == 0)
		{
			for (int i = 0; i < strlen(putString); i++)
			{
				if (WaitSemaphore(mainSemId, 3, 1) == 0)
				{
					ReleaseSemaphore(mainSemId, 2);
					return 0;
				}
				printw("%c", putString[i]);
				refresh();
				usleep(70000);
			}
			refresh();
			ReleaseSemaphore(mainSemId, 2);
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

int ReleaseSemaphore(int textSemId, int num)
{
	struct sembuf buf;
	buf.sem_op = 1;
	buf.sem_flg = SEM_UNDO;
	buf.sem_num = num;
	return semtimedop(textSemId, &buf, 1, NULL);
}

#elif _WIN32 | _WIN64

#include<stdio.h>
#include<windows.h>
#include<conio.h>
#include<time.h>
#include<iostream>
#include<string>
#pragma warning(disable:4996)

using namespace std;

void printString(char* eventID, const char* putString);

int main(int argc, char* argv[])
{
	string amountOfMoney = "60";
	string currencyOfMoney = "BYN";

	bool successFlag;

	char strRes[256];

	char arrOfString[256][256] = { "But I would walk 500 miles.", "And I would walk 500 more.",
		"Just to be the man who walks a thousand miles.",
		"To fall down at your door.", "500 MILES." };
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

	string message;
	message = amountOfMoney + " " + currencyOfMoney + '\0';


	char buffer[20];				 // Буфер для передачи
	int bufferSize = sizeof(buffer); // Размер буфера

	//-----------------------------------------------------------------------------------------

	HANDLE MessageSemaphores[3];

	MessageSemaphores[0] = OpenSemaphore(SEMAPHORE_ALL_ACCESS, TRUE, "semaphore_can_print_in_pipe");
	MessageSemaphores[1] = OpenSemaphore(SEMAPHORE_ALL_ACCESS, TRUE, "semaphore_end_print_in_pipe");
	MessageSemaphores[2] = OpenSemaphore(SEMAPHORE_ALL_ACCESS, TRUE, "semaphore_exit");

	ReleaseSemaphore(MessageSemaphores[0], 1, NULL);

	//-----------------------------------------------------------------------------------------

	HANDLE hMyPipe;

	const char* pipeName = "MyPipe";
	WaitNamedPipe(pipeName, NULL);

	hMyPipe = CreateFile(pipeName, //Имя канала
		GENERIC_WRITE | GENERIC_READ, // режим доступа
		FILE_SHARE_WRITE | FILE_SHARE_READ, // совместный доступ
		NULL,									// SD (дескр. защиты)
		OPEN_EXISTING,							// как действовать
		0,										// атрибуты файла
		NULL);									// дескр.шаблона файла

	if (hMyPipe == INVALID_HANDLE_VALUE)
	{
		printf("Could not open file (error %d)\n", GetLastError());
		return EXIT_FAILURE;
	}

	//-----------------------------------------------------------------------------------------

	DWORD NumberOfBytesWritten;

	int NumberOfBlocks = message.size() / bufferSize + 1;	// Количество блоков размером с буфер			
	WriteFile(hMyPipe, &NumberOfBlocks, sizeof(NumberOfBlocks), &NumberOfBytesWritten, (LPOVERLAPPED)NULL);

	int size = message.size();
	WriteFile(hMyPipe, &size, sizeof(size), &NumberOfBytesWritten, (LPOVERLAPPED)NULL);

	for (int i = 0; i < NumberOfBlocks; i++)
	{
		message.copy(buffer, bufferSize, i*bufferSize);		// Заполняем буфер
		if (!WriteFile(hMyPipe, buffer, bufferSize, &NumberOfBytesWritten, (LPOVERLAPPED)NULL)) cout << "Write Error\n";
	}

	WaitForSingleObject(MessageSemaphores[1], INFINITE);

	NumberOfBlocks = 0;
	successFlag = TRUE;
	DWORD NumberOfBytesRead;
	message.clear();

	int index = WaitForMultipleObjects(3, MessageSemaphores, FALSE, INFINITE) - WAIT_OBJECT_0; 
	// Получил уведомление о возможности чтения, семафор занят
	if (index == 2) // Если сигнальный семафор выхода
		return 0;

	if (!ReadFile(hMyPipe, &NumberOfBlocks, sizeof(NumberOfBlocks), &NumberOfBytesRead, NULL)) return 0;

	size = 0;
	if (!ReadFile(hMyPipe, &size, sizeof(size), &NumberOfBytesRead, NULL)) return 0;

	for (int i = 0; i < NumberOfBlocks; i++)
	{
		successFlag = ReadFile(hMyPipe, buffer, bufferSize, &NumberOfBytesRead, NULL);
		if (!successFlag) break;

		message.append(buffer, bufferSize); // Добавляем полученный буфер в строку
	}
	if (!successFlag) return 0;

	//-----------------------------------------------------------------------------------------

	message.resize(size);

	ReleaseSemaphore(MessageSemaphores[1], 1, NULL);

	CloseHandle(hMyPipe);

	printString(argv[1], strRes);
	return EXIT_SUCCESS;
}

void printString(char* eventID, const char* putString)
{
	const string SemaphoreCanPrint = "semaphore_can_print";
	const string SemaphoreEndPrint = "semaphore_end_print";
	const string SemaphoreExit = "semaphore_exit";

	HANDLE Semaphores[3];

	string buf = SemaphoreCanPrint;
	buf = buf + eventID;
	Semaphores[0] = OpenSemaphore(SEMAPHORE_ALL_ACCESS, FALSE, const_cast<char *>(buf.c_str()));
	buf = SemaphoreEndPrint;
	buf = buf + eventID;
	Semaphores[1] = OpenSemaphore(SEMAPHORE_ALL_ACCESS, FALSE, const_cast<char *>(buf.c_str()));
	buf = SemaphoreExit;
	buf = buf + eventID;
	Semaphores[2] = OpenSemaphore(SEMAPHORE_ALL_ACCESS, FALSE, const_cast<char *>(buf.c_str()));

	while (true)
	{
		if (WaitForSingleObject(Semaphores[0], 1) == WAIT_OBJECT_0)
		{
			for (int i = 0; i < strlen(putString); i++)     // Печать строки
			{
				if (WaitForSingleObject(Semaphores[2], 0) == WAIT_OBJECT_0)   // Если установлено событие закрытия данного процесса		
				{
					ReleaseSemaphore(Semaphores[1], 1, NULL);
					CloseHandle(Semaphores[0]);
					CloseHandle(Semaphores[1]);
					CloseHandle(Semaphores[2]);
					return;
				}
				printf("%c", putString[i]);
				Sleep(100);
			}
			ReleaseSemaphore(Semaphores[1], 1, NULL);
		}

		if (WaitForSingleObject(Semaphores[2], 1) == WAIT_OBJECT_0)
		{
			CloseHandle(Semaphores[0]);
			CloseHandle(Semaphores[1]);
			CloseHandle(Semaphores[2]);
			return;
		}
	}
	return;
}
#endif