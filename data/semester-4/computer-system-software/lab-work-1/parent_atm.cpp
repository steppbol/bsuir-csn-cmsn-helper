#ifdef __linux__
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
#include <ncurses.h>
#include <time.h>

#elif _WIN32 | _WIN64
#define _CRT_SECURE_NO_WARNINGS
#include <windows.h>
#endif

#include <iostream>
#include <fstream>
#include <ctime>

using namespace std;

void showTime();

int main()
{
#ifdef __linux__
#elif _WIN32 | _WIN64
	STARTUPINFO si;
	PROCESS_INFORMATION pi;

	ZeroMemory(&si, sizeof(si));
	si.cb = sizeof(si);
	ZeroMemory(&pi, sizeof(pi));
#endif
	cout << "Please enter currency and amount of money.\n";
	char strCurrency[256];
	char strAmountOfMoney[256];
	cout << "Currency:";
	cin >> strCurrency;
	cout << "Amount of money:";
	cin >> strAmountOfMoney;

#ifdef __linux__

	char strLinux[256];
	sprintf(strLinux, "%s %s", strCurrency, strAmountOfMoney);

	initscr();
	curs_set(0);

	pid_t pid = fork();

	int st;

	if(pid == -1)
	{
		cout << "Error.\r\n";
	}

	else if(pid== 0)
	{
		if (execlp("./child_atm", strCurrency, strAmountOfMoney, NULL) == -1)
			cout << "Error.\r\n";
	}

	else
	{
		while (1)
		{
			showTime();
			if (waitpid(pid, &st, WNOHANG) > 0)
				break;
			napms(1);
		}

		if (WIFEXITED(st))
		{
			if ((int)WEXITSTATUS(st) == 0) cout << "\r\n\nOperation was successfully completed!\r\n\n";
			if ((int)WEXITSTATUS(st) == 1) cout << "\r\n\nInsufficient funds.\r\n\n";
			if ((int)WEXITSTATUS(st) == 2) cout << "\r\n\nInsufficient funds in the ATM.\r\n\n";
			if ((int)WEXITSTATUS(st) == 3) cout << "\r\n\nInvalid input!\r\n\n";
		}
		napms(5000);
	}

	getch();
	endwin();

#elif _WIN32 | _WIN64

	char strWin[256];
	sprintf_s(strWin, "ATM.exe %s %s", strCurrency, strAmountOfMoney);

	if (!CreateProcess(NULL,                 // No module name (use command line).
		strWin,							 // Command line.
		NULL,								 // Process handle not inheritable.
		NULL,								 // Thread handle not inheritable.
		FALSE,								 // Set handle inheritance to FALSE.
		CREATE_NEW_CONSOLE,
		NULL,								 // Use parent's environment block.
		NULL,								 // Use parent's starting directory.
		&si,								 // Pointer to STARTUPINFO structure.
		&pi)								 // Pointer to PROCESS_INFORMATION structure.
		)
	{
		printf("CreateProcess failed (%d)\n", GetLastError());
		return -1;
	}

	while (WaitForSingleObject(pi.hProcess, 50))
	{
		showTime();
	}

	DWORD exitCode;
	GetExitCodeProcess(pi.hProcess, &exitCode);
	if (exitCode == 0) cout << "\r\n\nOperation was successfully completed!\r\n\n";
	if (exitCode == 1) cout << "\r\n\nInsufficient funds.\r\n\n";
	if (exitCode == 2) cout << "\r\n\nInsufficient funds in the ATM.\r\n\n";
	if (exitCode == 3) cout << "\r\n\nInvalid input!\r\n\n";

	system("pause");

	CloseHandle(pi.hThread);
#endif
	return 0;
}


void showTime()
{
	char buffer[80];
	time_t seconds = time(NULL);
	tm* timeinfo = localtime(&seconds);
	char* format = "%A, %B %d, %Y %I:%M:%S";
	strftime(buffer, 80, format, timeinfo);
	cout << "Current Datetime: " << buffer << "\r";
}
