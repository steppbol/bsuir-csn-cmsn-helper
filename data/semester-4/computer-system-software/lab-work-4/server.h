#pragma once
#ifdef _WIN32
#include<stdio.h>
#include<windows.h>
#include<conio.h>
#include<time.h>
#include<iostream>
#include<string>
#include <vector>
#include <fstream>
#pragma warning(disable:4996)
#elif __linux__
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
#endif

using namespace std;

#define MAX_COUNT_OF_THREADS 5

#ifdef _WIN32
PROCESS_INFORMATION createNewProcess();
char* parseAmount(char*);
char* parseCurrency(char*);

#elif __linux__
union semun
{
	int              val;
	struct semid_ds *buf;
	unsigned short  *array;
	struct seminfo  *__buf;
} semunion;

int waitSemaphore(int, int, int);
int releaseSemaphore(int, int);
void parseMessage(string, char*&, char*&);
#endif