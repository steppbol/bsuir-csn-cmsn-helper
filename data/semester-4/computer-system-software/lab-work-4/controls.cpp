#include "controls.h"

#ifdef _WIN32
int main()
{
	InitializeCriticalSection(&criticalSectionOfPrint);//Инициализируем перед использованием
	if (chooseActionMenu() == 0)
		DeleteCriticalSection(&criticalSectionOfPrint);//Удаляем критическую секцию
	cout << endl;
	system("pause");
	return 0;
}

int chooseActionMenu()
{
	while (true)
	{
		switch (getch())
		{
		case '+':
			if (stackOfThreads.size() < MAX_COUNT_OF_THREADS)
				addNewThreadInStack();
			break;
		case '-':
			if (stackOfThreads.size() > 0)
				delThreadInStack();
			break;
		case 'q':
			delAllThreadsInStack();
			return 0;
		default:
			break;
		}
	}
	return 1;
}

unsigned int WINAPI launchClientThread(PVOID argumnets)
{
	string amountOfMoney = "60";
	string currencyOfMoney = "BYN";
	string messageInPipe = amountOfMoney + " " + currencyOfMoney + '\0';
	string stringOfThread = " Thread #";
	HANDLE hServerClientPipe;
	DWORD numberOfBytesWritten;
	DWORD numberOfBytesRead;
	HANDLE messageSemaphores[3];
	const char* nameOfCurrentPipe = "\\\\.\\pipe\\hServerClientPipe";
	bool successOperationFlag;
	char bufferOfMessageInPipe[20];
	int sizeOfbufferOfMessageInPipe = sizeof(bufferOfMessageInPipe);
	int sizeOfMessageInPipe = messageInPipe.size();
	int numberOfBlocksInPipe = sizeOfMessageInPipe / sizeOfbufferOfMessageInPipe + 1;

	WaitNamedPipe(nameOfCurrentPipe, NULL);

	hServerClientPipe = CreateFile(nameOfCurrentPipe,
		GENERIC_WRITE | GENERIC_READ,
		FILE_SHARE_WRITE | FILE_SHARE_READ,
		NULL,
		OPEN_EXISTING,
		0,
		NULL);

	if (hServerClientPipe == INVALID_HANDLE_VALUE)
	{
		cout << "Could not open file " << GetLastError() << endl;
		return EXIT_FAILURE;
	}

	messageSemaphores[0] = OpenSemaphore(SEMAPHORE_ALL_ACCESS, TRUE, "canPrint");
	messageSemaphores[1] = OpenSemaphore(SEMAPHORE_ALL_ACCESS, TRUE, "endPrint");
	messageSemaphores[2] = OpenSemaphore(SEMAPHORE_ALL_ACCESS, TRUE, "exit");

	ReleaseSemaphore(messageSemaphores[0], 1, NULL);

	if (!WriteFile(hServerClientPipe, &numberOfBlocksInPipe, sizeof(numberOfBlocksInPipe), &numberOfBytesWritten, (LPOVERLAPPED)NULL))
		return 1;
	if (!WriteFile(hServerClientPipe, &sizeOfMessageInPipe, sizeof(sizeOfMessageInPipe), &numberOfBytesWritten, (LPOVERLAPPED)NULL))
		return 1;

	for (int i = 0; i < numberOfBlocksInPipe; i++)
	{
		messageInPipe.copy(bufferOfMessageInPipe, sizeOfbufferOfMessageInPipe, i*sizeOfbufferOfMessageInPipe);
		if (!WriteFile(hServerClientPipe, bufferOfMessageInPipe, sizeOfbufferOfMessageInPipe, &numberOfBytesWritten, (LPOVERLAPPED)NULL))
			cout << "Write Error\n";
	}

	WaitForSingleObject(messageSemaphores[1], INFINITE);

	numberOfBlocksInPipe = 0;
	successOperationFlag = TRUE;
	messageInPipe.clear();

	int indexOfSemaphore = WaitForMultipleObjects(3, messageSemaphores, FALSE, INFINITE) - WAIT_OBJECT_0;
	if (indexOfSemaphore == 2)
		return 0;

	if (!ReadFile(hServerClientPipe, &numberOfBlocksInPipe, sizeof(numberOfBlocksInPipe), &numberOfBytesRead, NULL))
		return 1;
	sizeOfMessageInPipe = 0;
	if (!ReadFile(hServerClientPipe, &sizeOfMessageInPipe, sizeof(sizeOfMessageInPipe), &numberOfBytesRead, NULL))
		return 1;

	for (int i = 0; i < numberOfBlocksInPipe; i++)
	{
		successOperationFlag = ReadFile(hServerClientPipe, bufferOfMessageInPipe, sizeOfbufferOfMessageInPipe, &numberOfBytesRead, NULL);
		if (!successOperationFlag)
			break;

		messageInPipe.append(bufferOfMessageInPipe, sizeOfbufferOfMessageInPipe);
	}

	if (!successOperationFlag)
		return 1;

	messageInPipe.resize(sizeOfMessageInPipe);
	ReleaseSemaphore(messageSemaphores[1], 1, NULL);
	CloseHandle(hServerClientPipe);

	bool *quitFlag = (*(threadArguments*)argumnets).quitCurrentThreadFlag;
	int numberOfThread = (*(threadArguments*)argumnets).numberOfThread;
	delete argumnets;

	stringOfThread = stringOfThread + to_string(numberOfThread + 1);

	outputString(stringOfThread, quitFlag);

	delete quitFlag;
	return 0;
}

int addNewThreadInStack()
{
	stackOfQuitFlags.push(new bool(false));

	threadArguments* argumentsOfThread = new threadArguments();
	(*argumentsOfThread).numberOfThread = stackOfThreads.size();
	(*argumentsOfThread).quitCurrentThreadFlag = stackOfQuitFlags.top();

	HANDLE currentThread = (HANDLE)_beginthreadex(NULL,
		0,
		launchClientThread,
		(PVOID)argumentsOfThread,
		0,
		NULL);
	stackOfThreads.push(currentThread);

	return 0;
}

int delThreadInStack()
{
	stackOfClothingThreads.push(stackOfThreads.top());
	*(stackOfQuitFlags.top()) = true;
	stackOfQuitFlags.pop();
	stackOfThreads.pop();

	return 0;
}

int delAllThreadsInStack()
{
	while (stackOfThreads.size() > 0)
		delThreadInStack();

	while (stackOfClothingThreads.size() > 0)
	{
		WaitForSingleObject(stackOfClothingThreads.top(), INFINITE);
		stackOfClothingThreads.pop();
	}

	return 0;
}

void outputString(string stringOfThread, bool* &quitFlag)
{
	while (true)
	{
		SetThreadPriority(GetCurrentThread(), THREAD_PRIORITY_ABOVE_NORMAL);
		if (*quitFlag)
			break;

		EnterCriticalSection(&criticalSectionOfPrint);
		for (int i = 0; i < stringOfThread.length(); i++)
		{

			if (*quitFlag)
				break;
			printf("%c", stringOfThread[i]);
			Sleep(100);
		}

		LeaveCriticalSection(&criticalSectionOfPrint);
		SetThreadPriority(GetCurrentThread(), THREAD_PRIORITY_BELOW_NORMAL);
		Sleep(1);
	}
}

#elif __linux__
int main(int argc, char *argv[])
{
	initscr();
	clear();
	noecho();
	refresh();
	nodelay(stdscr, TRUE);

	if (argv[1])
	{
		mainPid = atoi(argv[1]);
	}
	else return 0;

	if (argv[2])
	{
		textSemKey = atoi(argv[2]);
	}
	else return 0;

	if (pthread_mutex_init(&printMutex, NULL) != 0)
	{
		printw("Initialize mutex error...\n");
		endwin();
		return 0;
	}

	if (chooseActionMenu() == 0)
		pthread_mutex_destroy(&printMutex);

	clear();
	endwin();
	return 0;
}

int chooseActionMenu()
{
	while (true)
	{
		usleep(10000);
		switch (getch())
		{
		case '+':
			if (stackOfThreads.size() < MAX_COUNT_OF_THREADS)
				addNewThreadInStack();
			break;

		case '-':
			if (stackOfThreads.size() > 0)
				delThreadInStack();
			break;

		case 'q':
			while (stackOfThreads.size() > 0)
				delAllThreadsInStack();
			return 0;

		default:
			break;
		}
	}
}

void* launchClientThread(void* arguments)
{
	string amountOfMoney = "60";
	string currencyOfMoney = "BYN";
	string message = amountOfMoney + " " + currencyOfMoney + '\0';
	string stringOfThread = " Thread #";
	int sizeOfBuffer = 64;
	int shmId = shmget((key_t)mainPid, sizeOfBuffer, 0666);

	if (shmId < 0)
	{
		printf("shmget error\n");
		return NULL;
	}

	int textSemId = semget((key_t)textSemKey, 0, IPC_CREAT | 0666);

	semctl(textSemId, 0, SETALL, 0);
	if (textSemId < 0)
	{
		printf("Text Semaphores is not connected.");
		return NULL;
	}

	char *shm, *s;
	shm = (char*)shmat(shmId, NULL, 0);
	if (shm == (char *)-1)
	{
		perror("shmat");
		return NULL;
	}

	releaseSemaphore(textSemId, 2);
	s = shm;
	strncpy(s, message.c_str(), sizeof(message));

	waitSemaphore(textSemId, 1, -1);
	char tempMessage[sizeOfBuffer];
	waitSemaphore(textSemId, 2, -1);
	int p = 0;

	for (s = shm; *s != NULL; s++, p++)
	{
		tempMessage[p] = *s;
		refresh();
	}

	releaseSemaphore(textSemId, 1);

	char *putString;
	bool *quitFlag = (*(threadArguments*)arguments).quitCurrentThreadFlag;
	int numberOfThread = (*(threadArguments*)arguments).numberOfThread;
	delete (threadArguments*)arguments;

	stringOfThread = stringOfThread + toString(numberOfThread + 1);
	putString = const_cast<char*>(stringOfThread.c_str());

	outputString(putString, quitFlag);

	delete quitFlag;
	return 0;
}

int addNewThreadInStack()
{
	stackOfQuitFlags.push(new bool(false));
	threadArguments* argumentsOfThreads = new threadArguments();
	(*argumentsOfThreads).numberOfThread = stackOfThreads.size();
	(*argumentsOfThreads).quitCurrentThreadFlag = stackOfQuitFlags.top();

	pthread_t currentThread;

	if (pthread_create(&currentThread, NULL, launchClientThread, argumentsOfThreads) != 0)
	{
		printw("Creating new thread error...\n");
		endwin();
		return 0;
	}
	stackOfThreads.push(currentThread);
}

int delThreadInStack()
{
	stackOfClosingThreads.push(stackOfThreads.top());
	*(stackOfQuitFlags.top()) = true;
	stackOfQuitFlags.pop();
	stackOfThreads.pop();
}

int delAllThreadsInStack()
{
	while (stackOfThreads.size() > 0)
		delThreadInStack();

	while (stackOfClosingThreads.size() > 0)
	{
		pthread_join(stackOfClosingThreads.top(), NULL);
		stackOfClosingThreads.pop();
	}

	return 0;
}

int waitSemaphore(int textSemId, int num, int tempTime)
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

int releaseSemaphore(int textSemId, int num)
{
	struct sembuf buf;
	buf.sem_op = 1;
	buf.sem_flg = SEM_UNDO;
	buf.sem_num = num;
	return semtimedop(textSemId, &buf, 1, NULL);
}

void outputString(char*&putString, bool* &quitFlag)
{
	while (true)
	{
		if (*quitFlag) break;

		pthread_mutex_lock(&printMutex);
		pthread_setschedprio(pthread_self(), 300);

		for (int i = 0; i < strlen(putString); i++)
		{
			if (*quitFlag) break;
			printw("%c", putString[i]);
			usleep(100000);
		}

		pthread_setschedprio(pthread_self(), 1);
		pthread_mutex_unlock(&printMutex);
		usleep(100);
	}
}

string toString(int val)
{
	stringstream stream;
	stream << val;
	return stream.str();
}
#endif