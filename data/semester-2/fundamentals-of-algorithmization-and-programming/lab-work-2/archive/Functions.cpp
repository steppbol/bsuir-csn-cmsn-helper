#include <stdio.h>
#include <conio.h>
#include <stdlib.h>
#include <string.h>
#include "Header.h"

word_queue create_Word_Queue()
{
	word_queue unit;

	word_data* temp = (word_data*)calloc(1, sizeof(word_data));
	if (temp == nullptr) emergency_Exit();

	unit.head = temp; 
	unit.tail = unit.head;
	unit.head->next = nullptr;

	return unit;
}

void add_Word(word_queue &line, char* found_word)
{
	word_data* new_one = (word_data*)malloc(sizeof(word_data));
	if (new_one == nullptr) emergency_Exit();

	new_one->word = createStr(MAX_LENGTH);
	strcpy_s(new_one->word , MAX_LENGTH, found_word);

	new_one->amount = 1;

	new_one->next = nullptr;

	line.tail->next = new_one;
	line.tail = new_one;
}

void fullfill_Word_Queue(FILE* file, word_queue &line) 
{
	rewind(file);

	int check = 0;
	char found_word[MAX_LENGTH] = { 0 };

	while(true) // заполняем очередь словами и их встерчаемостью в файле.
	{
		check = fscanf_s(file, "%[a-zA-Z]", found_word, MAX_LENGTH - 1);

		if (feof(file)) break;

		if (!check || !ckeck_Word(found_word, line))
		{
			fscanf_s(file, "%[^a-zA-Z]", found_word, MAX_LENGTH - 1);
			memset(found_word, '\0', MAX_LENGTH);
			continue;
		}

		add_Word(line, found_word);
	}

	rewind(file);
}

bool ckeck_Word(char* found_word, word_queue &line)
{
	int check = 0;
	word_data* temp = line.head->next;

	while (temp != nullptr)
	{
		check = stringCmp(found_word,temp->word);
		if (check == 0)
		{
			temp->amount += 1;
			return false;
		}
		temp = temp->next;
	}
	return true;
}

word_data* find_Pair(word_queue &line)
{
	word_data* suitable = nullptr;
	word_data* temp = line.head->next;			// элемент line.head->next - первый элемент очереди.
	word_data* studied_word = line.head->next;
	int exodus_memory = 0;
	int exodus_temp = 0;
	int exodus_studied = 0;
	int temp_studied = 0;
	int temp_temp = 0;
	int temp_memory = 0;
	int min_memory = 0;
	int i = 0;

	while (temp->next != nullptr)
	{
		exodus_studied = (studied_word->amount)*strlen(studied_word->word);	// исходный объем памяти
		exodus_temp = (temp->next->amount)*strlen(temp->next->word);		// занимаемый этими словами.
		exodus_memory = exodus_studied + exodus_temp;						
																			
		temp_studied = (studied_word->amount)*strlen(temp->next->word);		// объем памяти, который слова
		temp_temp = (temp->next->amount)*strlen(studied_word->word);		// займут при перестановке.
		temp_memory = temp_studied + temp_temp;
																															
		if (temp_memory+strlen(studied_word->word)+ strlen(temp->next->word) < exodus_memory) // если исходная память больше, 
		{																					  // чем новая + память под слова
			if (!i)																			  // в файле ключе, то продолжаем проверки
			{
				min_memory = temp_memory;
				suitable = temp;
				i++;
				continue;
			}

			if (temp_memory < min_memory)
			{
				min_memory = temp_memory;
				suitable = temp;
			}
		}

		temp = temp->next;
	}
	return suitable;
}

void remove_Word(word_data* &prev_to_doomed, word_queue &unit)
{
	if (unit.head->next->next == nullptr)	// если осталось лишь 1 слово, то удаляем очередь
	{
		free(unit.head->next->word);
		free(unit.head->next);
		free(unit.head);
		unit.head = nullptr;
		unit.tail = nullptr;
		return;
	}

	word_data* ptr = (prev_to_doomed)->next;
	
	if (unit.tail->word == (prev_to_doomed)->next->word) // если удаляемое слово - последнее, то передвигаем хвост
	{
		unit.tail = prev_to_doomed;
		(prev_to_doomed)->next = nullptr;
	}
	else
	{
		(prev_to_doomed)->next = (prev_to_doomed)->next->next;
	}

	free(ptr->word);
	free(ptr);
}

FILE* queue_To_File(word_queue &line)			// удаление очереди и заполнение файла-ключа
{
	FILE* key;
	word_data* pair;

	fopen_s(&key, "compression_key.txt", "w+");
	if (key == nullptr) emergency_Exit();

	fprintf_s(key, "PAIRED WORDS INFO\n\n");

	while (line.head->next != nullptr && line.head->next->next != nullptr)  // продолжается до тех пор, пока в очереди не осталось слов
	{																		// или осталось только одно.
		pair = find_Pair(line);			// находим пару для слова в голове очереди.

		if (pair == nullptr)
		{
			remove_Word(line.head, line);	// удaляем слово из головы очереди.
			continue;
		}

		fprintf_s(key, "%s-", line.head->next->word);
		fprintf_s(key, "%s\n", pair->next->word);

		remove_Word(pair, line);		// удаляем из очереди второе слово в паре.
		remove_Word(line.head, line);	// удaляем слово из головы очереди.
	}

	if (line.head->next != nullptr)
	{
		remove_Word(line.head, line);	// если в очереди осталось одно слово, то оно удаляется и очередь освобождается
	}

	rewind(key);

	return key;
}

FILE* compress(FILE* original, FILE* key)
{
	rewind(key);
	rewind(original);

	FILE* new_file;
	int check = 0;
	int position = 0;
	char pair[MAX_LENGTH] = { 0 };
	char found_word[MAX_LENGTH] = { 0 };

	fopen_s(&new_file, "compressed_file.txt", "w");
	if (new_file == nullptr) emergency_Exit();

	for (;;)
	{
		position = ftell(original);
		check = fscanf_s(original, "%[a-zA-Z]", found_word, MAX_LENGTH - 1);

		if (feof(original)) break;

		if (!check)		// если прочитано не слово, то в новый файл записывается информация между словами
		{
			fseek(original, position, SEEK_SET);
			memset(found_word, '\0', MAX_LENGTH);
			fscanf_s(original, "%[^a-zA-Z]", found_word, MAX_LENGTH - 1);
			fprintf_s(new_file, "%s", found_word);
			memset(found_word, '\0', MAX_LENGTH);
			continue;
		}

		pair_From_Key(found_word, pair, key);	// находитим пару для замены слова

		if (pair[0] == 0)
		{
			fprintf_s(new_file, "%s", found_word);
		}
		else
		{
			fprintf_s(new_file, "%s", pair);
		}

		memset(found_word, '\0', MAX_LENGTH);
		memset(pair, '\0', MAX_LENGTH);
	}
	
	rewind(original);
	return new_file;
}

void pair_From_Key(char* found_word, char pair[], FILE* key)
{
	int i = 0;
	int position = 0;
	int length = 0;
	int check = 0;
	char symbol;

	fseek(key, PRE_INFO, SEEK_SET);

	position = ftell(key);

	for (;;)
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

			if (symbol != '-')	// если слово не первое в паре.
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

char* createStr(int size)
{
	char* str;
	str = (char*)calloc(size, sizeof(char));
	if (str == nullptr)
	{
		emergency_Exit();
	}
	return str;
}

int stringCmp(char* str1, char* str2)
{
	int result, i = 1;
	while (str1[0] != str2[0] && ((int)str1[0] + REGISTER_DIFF != (int)str2[0] && (int)str1[0] != (int)str2[0] + REGISTER_DIFF))
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