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
#include <stack>
#include <process.h>
#pragma warning(disable:4996)
#elif __linux__
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
#include <iostream>
#include <algorithm>
#include <sstream>  
#include <termios.h>
#include <vector>
#include <stack>
#include <pthread.h>
#include <string.h>
#include <sys/shm.h>
#include <sstream>
#endif

#define MAX_COUNT_OF_THREADS 5

using namespace std;

struct threadArguments
{
	bool* quitCurrentThreadFlag;
	int numberOfThread;
};

stack<bool*> stackOfQuitFlags;

int chooseActionMenu();
int addNewThreadInStack();
int delThreadInStack();
int delAllThreadsInStack();

#ifdef _WIN32
CRITICAL_SECTION criticalSectionOfPrint; // Объект критической секции, который обеспечивает синхроницацию

stack<HANDLE> stackOfThreads;
stack<HANDLE> stackOfClothingThreads;

unsigned int WINAPI launchClientThread(PVOID);
void outputString(string, bool *&);

#elif __linux__
pthread_mutex_t printMutex;

stack<pthread_t> stackOfThreads;
stack<pthread_t> stackOfClosingThreads;

int mainPid;
int textSemKey;

void* launchClientThread(void*);
int waitSemaphore(int, int, int);
int releaseSemaphore(int, int);
void outputString(char*&, bool *&);
string toString(int val);
#endif