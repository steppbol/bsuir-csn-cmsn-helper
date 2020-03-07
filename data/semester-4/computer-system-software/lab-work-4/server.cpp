#include "cashmachine.h"
#include "server.h"  

#ifdef _WIN32
int main(int argc, char* argv[])
{
	Cashmachine cashmachine(1);
	fstream file;
	string messageInPipe;
	HANDLE hServerClientPipe;
	DWORD numberOfBytesWritten;
	DWORD numberOfBytesRead;
	HANDLE messageSemaphores[3];
	PROCESS_INFORMATION processOfControls = createNewProcess();
	char amountOfMoney[256];
	char currencyOfMoney[256];
	const char* nameOfCurrentPipe = "\\\\.\\pipe\\hServerClientPipe";
	bool successOperationFlag;
	char bufferOfMessageInPipe[20];
	int sizeOfbufferOfMessageInPipe = sizeof(bufferOfMessageInPipe);
	int sizeOfMessageInPipe = messageInPipe.size();
	int numberOfBlocksInPipe;

	file.open("log.txt", ios::in);
	cashmachine.loadFromFileCurrency(file);
	file.close();

	hServerClientPipe = (CreateNamedPipe(nameOfCurrentPipe,
		PIPE_ACCESS_DUPLEX,
		PIPE_TYPE_MESSAGE | PIPE_WAIT,
		PIPE_UNLIMITED_INSTANCES,
		0,
		0,
		INFINITE,
		(LPSECURITY_ATTRIBUTES)NULL));

	messageSemaphores[0] = OpenSemaphore(SEMAPHORE_ALL_ACCESS, TRUE, "canPrint");
	messageSemaphores[1] = OpenSemaphore(SEMAPHORE_ALL_ACCESS, TRUE, "endPrint");
	messageSemaphores[2] = OpenSemaphore(SEMAPHORE_ALL_ACCESS, TRUE, "exit");

	while (true)
	{
		if (!ConnectNamedPipe(hServerClientPipe, (LPOVERLAPPED)NULL))
			cout << "Connection failure\n";

		successOperationFlag = TRUE;
		messageInPipe.clear();

		int indexOfSemaphore = WaitForMultipleObjects(3, messageSemaphores, FALSE, INFINITE) - WAIT_OBJECT_0; // ѕолучили уведомление о возможности чтени€
		if (indexOfSemaphore == 2)
			break;

		if (!ReadFile(hServerClientPipe, &numberOfBlocksInPipe, sizeof(numberOfBlocksInPipe), &numberOfBytesRead, NULL))
			break;

		if (!ReadFile(hServerClientPipe, &sizeOfMessageInPipe, sizeof(sizeOfMessageInPipe), &numberOfBytesRead, NULL))
			break;

		for (int i = 0; i < numberOfBlocksInPipe; i++)
		{
			successOperationFlag = ReadFile(hServerClientPipe, bufferOfMessageInPipe, sizeOfbufferOfMessageInPipe, &numberOfBytesRead, NULL);
			if (!successOperationFlag) break;

			messageInPipe.append(bufferOfMessageInPipe, sizeOfbufferOfMessageInPipe);
		}
		if (!successOperationFlag) break;
		messageInPipe.resize(sizeOfMessageInPipe);
		ReleaseSemaphore(messageSemaphores[1], 1, NULL);

		strcpy(amountOfMoney, parseAmount(bufferOfMessageInPipe));
		strcpy(currencyOfMoney, parseCurrency(bufferOfMessageInPipe));

		cashmachine.withdrawMoneyWin(currencyOfMoney, atof(amountOfMoney));

		file.open("log.txt", ios::out);
		cashmachine.loadToFileCurrency(file);
		file.close();

		ReleaseSemaphore(messageSemaphores[0], 1, NULL);
		numberOfBlocksInPipe = sizeOfMessageInPipe / sizeOfbufferOfMessageInPipe + 1;
		WriteFile(hServerClientPipe, &numberOfBlocksInPipe, sizeof(numberOfBlocksInPipe), &numberOfBytesWritten, (LPOVERLAPPED)NULL);

		sizeOfMessageInPipe = messageInPipe.size();
		WriteFile(hServerClientPipe, &sizeOfMessageInPipe, sizeof(sizeOfMessageInPipe), &numberOfBytesWritten, (LPOVERLAPPED)NULL);

		for (int i = 0; i < numberOfBlocksInPipe; i++)
		{
			messageInPipe.copy(bufferOfMessageInPipe, sizeOfbufferOfMessageInPipe, i*sizeOfbufferOfMessageInPipe);
			if (!WriteFile(hServerClientPipe, bufferOfMessageInPipe, sizeOfbufferOfMessageInPipe, &numberOfBytesWritten, (LPOVERLAPPED)NULL))
				cout << "Write Error\n";
		}

		WaitForSingleObject(messageSemaphores[1], INFINITE);
		if (!DisconnectNamedPipe(hServerClientPipe))
			cout << "Disconnect failure\n";
	}

	CloseHandle(processOfControls.hProcess);
	return 0;
}

PROCESS_INFORMATION createNewProcess()
{
	STARTUPINFO si;
	ZeroMemory(&si, sizeof(si));
	si.cb = sizeof(si);
	PROCESS_INFORMATION pi;
	ZeroMemory(&pi, sizeof(pi));
	string str = "Client.exe";
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
		cout << "Create Process failed " << GetLastError() << endl;
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

#elif __linux__
int main(int argc, char *argv[])
{
	initscr();
	curs_set(0);
	clear();
	noecho();
	refresh();

	srand(time(NULL));

	Cashmachine cashmachine(1);
	char* amountOfMoney = new char[256];
	char* currencyOfMoney = new char[256];
	int sizeOfBufffer = 64;
	key_t shmKey = getpid()*(rand() % 100);
	int shmId = shmget(shmKey, sizeOfBufffer, IPC_CREAT | 0666);

	if (shmId < 0)
	{
		printf("shmget error\n");
		return 1;
	}

	key_t textSemKey = ftok("/Client", 'A') * (rand() % 100);
	int textSemId = semget(textSemKey, 4, IPC_CREAT | 0666);

	semctl(textSemId, 0, SETALL, 0);
	if (textSemId < 0)
	{
		printf("Text Semaphores is not created.");
		return 0;
	}

	fstream file;
	file.open("log.txt", ios::in);
	cashmachine.loadFromFileCurrency(file);
	file.close();

	int pid = fork();
	switch (pid)
	{
	case -1:
		printf("New process is not created\n");
		return 0;
	case 0:
	{
		stringstream strs;
		strs << (int)shmKey;
		string tempStr = strs.str();
		char* charType = (char*)tempStr.c_str();
		stringstream strs1;
		strs1 << (int)textSemKey;
		string tempStr1 = strs1.str();
		char* charType1 = (char*)tempStr1.c_str();
		if (execlp("./controls", "controls", charType, charType1, NULL) == -1)
			printf("Error.");
		break;
	}
	break;

	default:
	{
		char *shm, *s;
		shm = (char*)shmat(shmId, NULL, 0);
		if (shm == (char *)-1)
		{
			perror("shmat");
			return 1;
		}

		while (true)
		{
			waitSemaphore(textSemId, 2, -1);
			int p = 0;
			char tempMessage[sizeOfBufffer];
			for (s = shm; *s != NULL; s++, p++)
			{
				tempMessage[p] = *s;
			}
			releaseSemaphore(textSemId, 1);
			string message(tempMessage);
			if (message == "quit") break;

			parseMessage(message, amountOfMoney, currencyOfMoney);
			cashmachine.withdrawMoneyWin(currencyOfMoney, atof(amountOfMoney));

			file.open("log.txt", ios::out);
			cashmachine.loadToFileCurrency(file);
			file.close();

			releaseSemaphore(textSemId, 2);
			s = shm;
			strncpy(s, message.c_str(), sizeof(message));
			waitSemaphore(textSemId, 1, -1);
		}
		semctl(textSemId, 0, IPC_RMID, semunion);
		shmdt(shm);
	}
	break;
	}
	clear();
	endwin();
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
#endif
