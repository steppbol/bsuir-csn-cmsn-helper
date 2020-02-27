#include <stdio.h>
#include <conio.h>
#include <stdlib.h>
#include <string.h>
#include "Header.h"


FILE* decompress(FILE* compressed, FILE* key)
{
	rewind(key);
	rewind(compressed);

	FILE* new_file;
	int check = 0;
	int position = 0;
	char symbol;
	char pair[MAX_LENGTH] = { 0 };
	char found_word[MAX_LENGTH] = { 0 };

	fopen_s(&new_file, "decompressed_file.txt", "w");
	if (new_file == nullptr) emergency_Exit();

	while(true)
	{
		position = ftell(compressed);
		check = fscanf_s(compressed, "%[a-zA-Z]", found_word, MAX_LENGTH - 1);

		if (feof(compressed)) break;

		if (!check)		// если прочитано не слово, то в новый файл записывается информация между словами
		{
			fseek(compressed, position, SEEK_SET);
			memset(found_word, '\0', MAX_LENGTH);
			fscanf_s(compressed, "%[^a-zA-Z]", found_word, MAX_LENGTH - 1);
			fprintf_s(new_file, "%s", found_word);
			symbol = found_word[0];
			memset(found_word, '\0', MAX_LENGTH);
			continue;
		}

		pair_From_Key(found_word, pair, key);	// находитим пару для замены слова

		if (pair[0] == 0)	// если пары нет, то пишем то же слово
		{
			fprintf_s(new_file, "%s", found_word);
		}
		else
		{
			if (symbol == '.')	// если встретили новое предложение
			{
				if ((int)pair[0] > HIGH_UPPER_CASE_BORDER)	// если первая буква маленькая
				{
					pair[0] = (char)((int)pair[0] - REGISTER_DIFF);
				}
			}
			if (symbol == ' ')	// если предложение продолжается
			{
				if ((int)pair[0] > LOW_UPPER_CASE_BORDER && (int)pair[0] < HIGH_UPPER_CASE_BORDER)	// если первая буква большая
				{
					pair[0] = (char)((int)pair[0] + REGISTER_DIFF);
				}
			}
			fprintf_s(new_file, "%s", pair);
		}

		memset(found_word, '\0', MAX_LENGTH);
		memset(pair, '\0', MAX_LENGTH);
	}

	rewind(compressed);
	return new_file;
}

void pair_From_Key(char* found_word, char pair[], FILE* key)
{
	int i = 0;
	int position = 0;
	int length = 0;
	int check = 0;
	char symbol;

	position = ftell(key);

	while(true)
	{
		check = fscanf_s(key, "%[a-zA-Z]", pair, MAX_LENGTH - 1);

		if (feof(key)) break;

		if (stringCmp(pair, found_word) != 0 || !check)	// если слово не найдено, то начинается следующая итерация.
		{
			memset(pair, '\0', MAX_LENGTH);
			fscanf_s(key, "%c", &symbol);
			if (symbol == '\n') position = ftell(key);
			continue;
		}
		else
		{
			fscanf_s(key, "%c", &symbol);

			if (symbol != '-')	// если слово в паре второе.
			{
				fseek(key, position, SEEK_SET);
			}

			memset(pair, '\0', MAX_LENGTH);
			fscanf_s(key, "%[a-zA-Z]", pair, MAX_LENGTH - 1);
			break;
		}
	}

	if (feof(key))
	{
		rewind(key);
		return;
	}

	rewind(key);
}

int stringCmp(char* str1, char* str2)
{
	int result, i = 1;
	if (str1[0] != str2[0] && ((int)str1[0] + REGISTER_DIFF != (int)str2[0] && (int)str1[0] != (int)str2[0] + REGISTER_DIFF))
	{
		return result = str1[0]>str2[0] ? 1 : -1;	// если первые две буквы не отличаются регистром, а являются совершенно разными, то функция завершается
	}
	while (str1[i] == str2[i])
	{
		if (str1[i] == '\0' && str2[i] == '\0') return 0;
		i++;
	}
	return result = str1[i]>str2[i] ? 1 : -1;
}

void emergency_Exit()
{
	system("cls");
	printf("The memory has not been allocated!\nPress any key to exit the programm...");
	_getch();
	exit(1);
}

bool check_File(FILE* file)
{
	if (file == nullptr) return false;

	fseek(file, 0, SEEK_END);

	if (!ftell(file))
	{
		return false;
	}

	rewind(file);

	return true;
}