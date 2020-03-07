#ifdef __linux__
#include <iostream>
#include <stdio.h>
#include <termios.h> 
#include <unistd.h>
#include <stdlib.h>
#include <sys/shm.h>
#include <ncurses.h>
#include <unistd.h>
#include <sys/sem.h>
#include <sys/types.h>
#include <wait.h>
#include <semaphore.h>
#include <string.h>
#include <math.h>
#include <sstream>  
#include <fstream>

#include "cashmachine.h"

using namespace std;

union semun
{
	int              val;  
	struct semid_ds *buf;    
	unsigned short  *array; 
	struct seminfo  *__buf;  
} semunion;

int WaitSemaphore(int textSemId, int num, int waitTime);
int ReleaseSemaphore(int textSemId, int num);
void parseMessage(string, char* &, char* &);
char* parseCurrency(string);

int main(int argc, char *argv[])
{
	initscr();
	curs_set(0);
	clear();
	noecho();
	refresh();

	Cashmachine cashmachine(1);
	fstream file;
	file.open("log.txt", ios::in);
	cashmachine.loadFromFileCurrency(file);
	file.close();


	char* amountOfMoney = new char[256];
	char* currencyOfMoney = new char[256];

	int bufferSize = 64;
	srand(time(NULL));
	key_t shmkey = getpid() * (rand() % 100);
	int shmId = shmget(shmkey, bufferSize, IPC_CREAT | 0666);

	if (shmId < 0)
	{
		printf("shmget error\n");
		return 1;
	}

	srand(time(NULL));
	key_t textSemKey = ftok("/client", 'A') * (rand() % 100);
	int textSemId = semget(textSemKey, 4, IPC_CREAT | 0666);

	semctl(textSemId, 0, SETALL, 0);
	if (textSemId < 0)
	{
		printf("Text Semaphores is not created.");
		return 0;
	}

	int pid = fork();
	switch (pid)
	{
	case -1:
		printf("New process is not created\n");
		return 0;

	case 0:
	{
		stringstream strs;
		strs << (int)shmkey;
		string temp_str = strs.str();
		char* char_type = (char*)temp_str.c_str();
		stringstream strs1;
		strs1 << (int)textSemKey;
		string temp_str1 = strs1.str();
		char* char_type1 = (char*)temp_str1.c_str();
		if (execlp("./server", "server", char_type, char_type1, NULL) == -1)
			printf("Error.");
		break;
	}
	break;

	default:
	{
		char *shm, *s;
		shm = (char*)shmat(shmId, NULL, 0); //идентификатор и флаг доступа
		if (shm == (char *)-1)
		{
			perror("shmat");
			return 1;
		}


		while (true)
		{
			WaitSemaphore(textSemId, 2, -1);
			int p = 0;
			char tempMessage[bufferSize];
			for (s = shm; *s != NULL; s++, p++)
			{
				tempMessage[p] = *s;
			}
			ReleaseSemaphore(textSemId, 1);
			string message(tempMessage);
			if (message == "quit") break;

			parseMessage(message, amountOfMoney, currencyOfMoney);
			cashmachine.withdrawMoneyWin(currencyOfMoney, atof(amountOfMoney));

			ReleaseSemaphore(textSemId, 2);
			s = shm;
			strncpy(s, message.c_str(), sizeof(message));
			WaitSemaphore(textSemId, 1, -1);
		}
		semctl(textSemId, 0, IPC_RMID, semunion);
		shmdt(shm);
	}
	break;
	}

	clear();
	endwin();

	file.open("log.txt", ios::out);
	cashmachine.loadToFileCurrency(file);
	file.close();

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

void parseMessage(string message, char* &amountOfMoney, char* &currencyOfMoney)
{
	char buffer[256];
	strcpy(buffer, message.c_str());

	int i = 0;
	while (buffer[i] != ' ')
	{
		amountOfMoney[i] = buffer[i];
		i++;
	}
	amountOfMoney[i] = '\0';

	while (buffer[i] != ' ')
		i++;

	i++;

	currencyOfMoney[0] = buffer[i];
	i++;
	currencyOfMoney[1] = buffer[i];
	i++;
	currencyOfMoney[2] = buffer[i];

	currencyOfMoney[3] = '\0';
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

#include "cashmachine.h"
#pragma warning(disable:4996)

using namespace std;

void printString(char* eventID, const char* string);
char* parseAmount(char*);
char* parseCurrency(char*);
PROCESS_INFORMATION CreateNewProcess();

int main(int argc, char* argv[])
{
	Cashmachine cashmachine(1);
	fstream file;
	file.open("log.txt", ios::in);
	cashmachine.loadFromFileCurrency(file);
	file.close();

	char amountOfMoney[256];
	char currencyOfMoney[256];

	bool successFlag;
	char buffer[20];				 // Буфер для передачи
	int bufferSize = sizeof(buffer); // Размер буфера
	int size;

	string message;

	PROCESS_INFORMATION proc = CreateNewProcess();

	HANDLE hMyPipe;

	//-----------------------------------------------------------------------------------------

	HANDLE MessageSemaphores[3];
	MessageSemaphores[0] = CreateSemaphore(NULL, 0, 1, "semaphore_can_print_in_pipe");
	MessageSemaphores[1] = CreateSemaphore(NULL, 0, 1, "semaphore_end_print_in_pipe");
	MessageSemaphores[2] = CreateSemaphore(NULL, 0, 1, "semaphore_exit");

	//-----------------------------------------------------------------------------------------

	hMyPipe = (CreateNamedPipe("MyPipe",  // адрес строки имени канала
		PIPE_ACCESS_DUPLEX,   // режим открытия канала
		PIPE_TYPE_MESSAGE | PIPE_WAIT, // режим работы канала 
		PIPE_UNLIMITED_INSTANCES,  //максимальное количество реализаций канала
		0,						//размер выходного буфера
		0,					//размер входного буфера
		INFINITE,			//время ожидания в миллисекундах
		(LPSECURITY_ATTRIBUTES)NULL));	//атрибуты защиты

	while (true)
	{
		if (!ConnectNamedPipe(hMyPipe, (LPOVERLAPPED)NULL))// адрес структуры LPOVERLAPPED
			cout << "Connection failure\n";

		int NumberOfBlocks;
		successFlag = TRUE;
		DWORD NumberOfBytesRead;
		message.clear();

		int index = WaitForMultipleObjects(3, MessageSemaphores, FALSE, INFINITE) - WAIT_OBJECT_0; 
		// Получил уведомление о возможности чтения, семафор занят

		if (index == 2) // Если сигнальный семафор выхода
			break;

		if (!ReadFile(hMyPipe, &NumberOfBlocks, sizeof(NumberOfBlocks), &NumberOfBytesRead, NULL)) break;

		if (!ReadFile(hMyPipe, &size, sizeof(size), &NumberOfBytesRead, NULL)) break;

		for (int i = 0; i < NumberOfBlocks; i++)
		{
			successFlag = ReadFile(hMyPipe, buffer, bufferSize, &NumberOfBytesRead, NULL);
			if (!successFlag) break;

		message.append(buffer, bufferSize); // Добавляем полученный буфер в строку
		}

		if (!successFlag) break;

		message.resize(size);

		ReleaseSemaphore(MessageSemaphores[1], 1, NULL);

		strcpy(amountOfMoney, parseAmount(buffer));
		strcpy(currencyOfMoney, parseCurrency(buffer));

		cashmachine.withdrawMoneyWin(currencyOfMoney, atof(amountOfMoney));

		DWORD NumberOfBytesWritten;
		ReleaseSemaphore(MessageSemaphores[0], 1, NULL);

		NumberOfBlocks = message.size() / bufferSize + 1;	// Количество блоков размером с буфер			
		WriteFile(hMyPipe, &NumberOfBlocks, sizeof(NumberOfBlocks), &NumberOfBytesWritten, (LPOVERLAPPED)NULL);

		size = message.size();
		WriteFile(hMyPipe, &size, sizeof(size), &NumberOfBytesWritten, (LPOVERLAPPED)NULL);

		for (int i = 0; i < NumberOfBlocks; i++)
		{
			message.copy(buffer, bufferSize, i*bufferSize);		// Заполняем буфер
			if (!WriteFile(hMyPipe, buffer, bufferSize, &NumberOfBytesWritten, (LPOVERLAPPED)NULL)) cout << "Write Error\n";
		}

		WaitForSingleObject(MessageSemaphores[1], INFINITE);
		if (!DisconnectNamedPipe(hMyPipe))
			cout << "Disconnect failure\n";
	}

	//-----------------------------------------------------------------------------------------

	file.open("log.txt", ios::out);
	cashmachine.loadToFileCurrency(file);
	file.close();

	CloseHandle(proc.hProcess);
	return EXIT_SUCCESS;
}

PROCESS_INFORMATION CreateNewProcess()
{
	STARTUPINFO si;
	ZeroMemory(&si, sizeof(si));
	si.cb = sizeof(si);
	PROCESS_INFORMATION pi;
	ZeroMemory(&pi, sizeof(pi));
	string str = "ParentControl.exe";
	LPSTR commandLineText = const_cast<char *>(str.c_str());
	if (!CreateProcess(NULL,
		commandLineText,
		NULL,
		NULL,
		FALSE,
		CREATE_NEW_CONSOLE,
		NULL,
		NULL,
		&si,
		&pi))
		printf("Create Process failed (%d)\n", GetLastError());
	return pi;
}

char* parseAmount(char* buffer)
{
	char amountOfMoney[256];
	int i = 0;
	while (buffer[i] != ' ')
	{
		amountOfMoney[i] = buffer[i];
		i++;
	}
	amountOfMoney[i] = '\0';
	return amountOfMoney;
}

char* parseCurrency(char* buffer)
{
	char currencyOfMoney[256];
	int i = 0;
	while (buffer[i] != ' ')
		i++;

	i++;

	currencyOfMoney[0] = buffer[i];
	i++;
	currencyOfMoney[1] = buffer[i];
	i++;
	currencyOfMoney[2] = buffer[i];

	currencyOfMoney[3] = '\0';
	return currencyOfMoney;
}
#endif