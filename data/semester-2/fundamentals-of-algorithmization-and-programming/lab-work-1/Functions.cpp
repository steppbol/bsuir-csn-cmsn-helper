#include <stdio.h>
#include <conio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>
#include "Header.h"

laptop* createArray(int size)
{
	laptop* arr;

	arr = (laptop*)malloc(size * sizeof(laptop));
	if (arr == nullptr)
	{
		emergencyExit();
	}

	for (int i = 0; i < size; i++)
	{
		arr[i].name = createStr(MAX_LENGTH);
		arr[i].processor = createStr(MAX_LENGTH);
		arr[i].resolution = createStr(MAX_LENGTH);
		arr[i].graphics_chip = createStr(MAX_LENGTH);
	}
	return arr;
}

void freeArray(laptop* arr, int size)
{
	for (int i = 0; i < size; i++)
	{
		free(arr[i].name);
		free(arr[i].processor);
		free(arr[i].resolution);
		free(arr[i].graphics_chip);
	}
	free(arr);
}

char* createStr(int size)
{
	char* str;
	str = (char*)calloc(size, sizeof(char));
	if (str == nullptr)
	{
		emergencyExit();
	}
	return str;
}

void getNameField(char* name, char str[])
{
	int i = 0;
	int j = 0;

	while (str[i] != '/') i++;

	i++;

	while (str[i] != '-')
	{
		name[j] = str[i];
		i++;
		j++;
	}

	//name = (char*)realloc(name, j * sizeof(char));
	name[j] = '\0';
}

void getCharField(char* field, char str[])
{
	int i;

	for (i = 0; str[i] != '<'; i++)
	{
		field[i] = str[i];
	}

	//field = (char*)realloc(field, i * sizeof(char));
	field[i] = '\0';
}

void getIntField(int &field, char str[])
{
	int i = 0;
	while (str[i] != ' ')
	{
		i++;
	}
	while (str[i] != '<')
	{
		str[i] = str[i + 1];
		i++;
	}
	field = atoi(str);
}

void getFloatField(float &field, char str[])
{
	int i = 0;
	float temp = 0;

	field = (float)atoi(str);

	for (; str[i] != '.'; i++);

	i++;

	temp = (float)atoi(str+i);

	while (temp > 1)
	{
		temp /= 10;
	}
	field += temp;
}

void outputArray(laptop* arr, int size)
{
	system("cls");
	for (int i = 0; i < size; i++)
	{
		printf("==========================================");
		printf("\nName : %s", arr[i].name);
		printf("\nProcessor : %s", arr[i].processor);
		printf("\nScreen Size : %.1f", arr[i].screen_size);
		printf("\nResolution : %s",arr[i].resolution);
		printf("\nRAM : %d MB", arr[i].ram);
		printf("\nCapasity : %d GB", arr[i].capasity);
		printf("\nGraphics chip : %s\n", arr[i].graphics_chip);
		printf("==========================================\n\n\n");
	}
}

void skipField(FILE* file, int number_of_fields)
{
	char temp_string[BUFF_SIZE] = {0};
	do
	{
		fscanf_s(file, "%s", temp_string, BUFF_SIZE);
		if (strcmp(temp_string, "class=\"nb_td") == 0)
		{
			number_of_fields--;
		}
	} while (number_of_fields);
}

void findData(FILE* file, char string[], int push_type)
{
	do
	{
		fscanf_s(file, "%s", string, BUFF_SIZE);
	} while (strcmp(string, "class=\"nb_td") != 0);
	fseek(file, push_type, SEEK_CUR);
	fgets(string, BUFF_SIZE - 1, file);
}

int calculateAmount(FILE* file)
{
	int amount = 0;
	char string[BUFF_SIZE] = { 0 };

	while (fscanf_s(file, "%s", string, BUFF_SIZE) != EOF)
	{
		if (strcmp(string, "class=\"nb_td") == 0) amount++;
	}
	amount /= KEY_TAGS_PER_UNIT;

	return amount;
}

void emergencyExit()
{
	system("cls");
	printf("The memory has not been allocated!\nPress any key to exit the programm...");
	_getch();
	exit(1);
}