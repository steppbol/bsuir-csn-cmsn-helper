#include <stdio.h>
#include <conio.h>
#include <assert.h>
#include <string.h>
#include "Header.h"

int main()
{
	FILE *file;
	laptop *arr;
	int amount = 0;
	char temp_string[BUFF_SIZE] = { 0 };

	fopen_s(&file, "internet_doc.txt", "r");
	if (file == nullptr)
	{
		emergencyExit();
	}

	amount = calculateAmount(file);
	arr = createArray(amount);

	rewind(file);
	for(int i = 0; i < amount; i++)
	{
		if (i != 0) skipField(file, FIRST_SKIP);
		skipField(file, FIRST_SKIP);

		findData(file, temp_string, PUSH_NAME);
		getNameField(arr[i].name, temp_string);

		findData(file, temp_string, PUSH_CELL);
		getCharField(arr[i].processor, temp_string);

		skipField(file, SECOND_SKIP);

		findData(file, temp_string, PUSH_CELL);
		getFloatField(arr[i].screen_size, temp_string);

		findData(file, temp_string, PUSH_CELL);
		getCharField(arr[i].resolution, temp_string);

		findData(file, temp_string, PUSH_CELL);
		getIntField(arr[i].ram, temp_string); 
		
		findData(file, temp_string, PUSH_CELL);
		getIntField(arr[i].capasity, temp_string);
		
		findData(file, temp_string, PUSH_CELL);
		getCharField(arr[i].graphics_chip, temp_string);
	}

	outputArray(arr, amount);
	_getch();

	fclose(file);
	freeArray(arr, amount);
	return 0;
}