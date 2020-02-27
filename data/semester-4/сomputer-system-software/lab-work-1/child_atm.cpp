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

#include <cstdlib>
#include "cashmachine.h"

int main(int argc, char* argv[])
{
	Cashmachine cashmachine(1);
	fstream file;
	file.open("log.txt", ios::in);
	cashmachine.loadFromFileCurrency(file);
	file.close();

#ifdef _WIN32 | _WIN64
	cashmachine.ouputInformationOfClient();
	int checkError = cashmachine.withdrawMoneyWin(argv[1], atof(argv[2]));
	cashmachine.ouputInformationOfClient();
#endif

#ifdef __linux__
	cashmachine.ouputInformationOfClient();
	int checkError = cashmachine.withdrawMoneyWin(argv[0], atof(argv[1]));
	cashmachine.ouputInformationOfClient();
	cout << "\r\n";
#endif

	file.open("log.txt", ios::out);
	cashmachine.loadToFileCurrency(file);
	file.close();

#ifdef __linux__
	cin.get();

#elif _WIN32 | _WIN64
	system("pause");
#endif
	if (checkError == 0) return 0;
	if (checkError == 1) return 1;
	if (checkError == 2) return 2;
	if (checkError == 3) return 3;

}