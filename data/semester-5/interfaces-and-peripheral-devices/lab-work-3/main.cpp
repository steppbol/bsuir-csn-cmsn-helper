#include "Battery.h"
#include <iostream>
#include <conio.h>
#include <cstdlib>

using namespace std;

const int DELAY = 1000;

int main()
{
	try
	{
		Battery battery;

		while (!_kbhit())
		{
			battery.UpdateStats();
			cout << battery;
			Sleep(DELAY);
			system("cls");
		}

		system("pause");
		return 0;
	}
	catch (const system_error &exception)
	{
		cout << endl;

		cout << "Caught system_error with code: " << exception.code() << endl
			<< "Description: " << exception.what() << endl;

		system("pause");
		exit(EXIT_FAILURE);
	}
}