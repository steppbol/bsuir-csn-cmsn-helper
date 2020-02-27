#include <windows.h>
#include <stdlib.h>
#include <string.h>
#include <setupapi.h>
#include <dbt.h>
#include <strsafe.h>
#include "notify.h"
#include "ejectutility.h"
#include <dontuse.h>
#include <conio.h>

#define CREATE_WINDOW_EVENT_NAME "windowCreateEvent"

int main(int argc, char* argv[])
{
	HANDLE window_create_event = CreateEvent(NULL, FALSE, FALSE, CREATE_WINDOW_EVENT_NAME);

	HANDLE thread_handle_ = CreateThread(NULL, 256, WindowThreadRoutine, (LPVOID)CREATE_WINDOW_EVENT_NAME, NULL, NULL);
	if (thread_handle_ == NULL)
	{
		printf("Couldn't create thread\n");
	}

	WaitForSingleObject(window_create_event, INFINITE);

	printf("Press 'e' to eject a USB device\n");

	while (TRUE)
	{
		rewind(stdin);

		char key = _getch();

		if (key == 'q') return 0;
		if (key != 'e') continue;

		char drive_letter = 0;

		printf("Please enter volume's letter, you want to eject\n");

		if (!scanf_s("%c", &drive_letter))
		{
			printf("Invalid drive letter value was provided!\n");
			continue;
		}

		if (EjectDevice(drive_letter) == 1)
		{
			printf("Couldn't eject the drive\n");
		}
	}

	return 0;
}