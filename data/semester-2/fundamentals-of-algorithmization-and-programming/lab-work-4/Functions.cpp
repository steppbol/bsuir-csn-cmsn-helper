#include <stdio.h>
#include <conio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include "Header.h"

NODE* create_Node(char* string)
{
	NODE* new_node = (NODE*)malloc(sizeof(NODE));
	if (new_node == nullptr) return nullptr;

	new_node->string = createStr(MAX_SIZE);
	if (new_node->string == nullptr)
	{
		free(new_node);
		return nullptr;
	}

	strcpy_s(new_node->string, MAX_SIZE, string);

	new_node->left = new_node->right = nullptr;

	return new_node;
}

void find_Place(NODE* root, char* common_node, NODE* &place, bool &force_return)
{
	if (force_return)
	{
		return;
	}

	if (root == nullptr) return;

	if (strcmp(root->string, common_node) == 0)
	{
		place = root;
		force_return = true;
		return;
	}

	find_Place(root->left, common_node, place, force_return);
	find_Place(root->right, common_node, place, force_return);
}

NODE* create_Tree(FILE* file)
{
	rewind(file);

	int i = 1;
	char temp[MAX_SIZE];
	bool force_return;
	NODE* root = nullptr;
	NODES_GROUP temp_group;
	NODE* place_holder;

	while (!feof(file))
	{
		force_return = false;

		memset(temp_group.parent, '\0', MAX_SIZE * sizeof(char));
		ftell(file);
		fscanf_s(file, "%[^?]", temp_group.parent, MAX_SIZE);
		fscanf_s(file, "%[^a-zA-Z]", temp, MAX_SIZE);

		memset(temp_group.left, '\0', MAX_SIZE * sizeof(char));
		ftell(file);
		fscanf_s(file, "%[^?]", temp_group.left, MAX_SIZE);
		fscanf_s(file, "%[^a-zA-Z]", temp, MAX_SIZE);;

		memset(temp_group.right, '\0', MAX_SIZE * sizeof(char));
		ftell(file);
		fscanf_s(file, "%[^?]", temp_group.right, MAX_SIZE);
		fscanf_s(file, "%[^a-zA-Z]", temp, MAX_SIZE);

		if (i == 1)
		{
			root = create_Node(temp_group.parent);
			root->left = create_Node(temp_group.left);
			root->right = create_Node(temp_group.right);

			i++;
			continue;
		}

		find_Place(root, temp_group.parent, place_holder, force_return);
		place_holder->left = create_Node(temp_group.left);
		place_holder->right = create_Node(temp_group.right);
	}
	return root;
}

void tree_To_File(NODE* root, FILE* file)
{
	fseek(file, 0, SEEK_CUR);

	if (root == nullptr || (root->left == nullptr && root->left == nullptr)) return;
	fprintf_s(file, "%s?", root->string);
	if (root->left)  fprintf_s(file, "%s?", root->left->string);
	if (root->right) fprintf_s(file, "%s?", root->right->string);
	fprintf(file, "\n\n");

	tree_To_File(root->left, file);
	tree_To_File(root->right, file);
}

FILE* create_Tree_File(NODE* root)
{
	FILE* file;
	fopen_s(&file, "Tree.txt", "w+");

	tree_To_File(root, file);

	return file;
}

char* createStr(int size)
{
	char* str;
	str = (char*)calloc(size, sizeof(char));
	if (str == nullptr)
	{
		return nullptr;
	}
	return str;
}

bool push_Pointer(NODE* &ptr, bool push)
{
	if (ptr->left == nullptr && ptr->right == nullptr) return false;

	if (!push)
	{
		ptr = ptr->left;
	}
	else
	{
		ptr = ptr->right;
	}
	return true;
}

void delete_Tree(NODE* root)
{
	if (root->right != nullptr)
		delete_Tree(root->right);

	if (root->left != nullptr)
		delete_Tree(root->left);

	free(root->string);
	free(root);
}

void accept_Pushtype(bool &push_type)
{
	while (true)
	{
		switch (_getch())
		{
		case '0':
		{
			push_type = false;
			return;
		}
		case '1':
		{
			push_type = true;
			return;
		}
		default: continue;
		}
	}
}

void guess_Succed()
{
	system("cls");
	printf("Well, it was fun :D Thank you for playing along with me\n\n");
	printf("\n\nPress any key to return to the menu...");
	_getch();
	system("cls");
}

NODE* create_Actor()
{
	char temp_str[MAX_SIZE] = { 0 };

	system("cls");

	printf("Then who is this?\n");
	rewind(stdin);
	fgets(temp_str, MAX_SIZE, stdin);				// запись данных
	temp_str[strlen(temp_str) - 1] = '\0';			// загаданного актера
	
	system("cls");
	return create_Node(temp_str);
}

NODE* create_Distinction(NODE* &ptr)
{
	assert(ptr != nullptr);

	bool axuilary = false;
	char temp_str[MAX_SIZE] = { 0 };

	system("cls");

	printf("What positive quality distincts him\\her from %s?\n\n", ptr->string);
	printf("Please write your answer in the next form :\nHe/She auxiliary(if needed) verb(in Present\\Past Simple\\Continius form)...\n");

	rewind(stdin);
	fgets(temp_str, MAX_SIZE, stdin);
	temp_str[strlen(temp_str) - 1] = '\0';

	printf("\nDoes your answer have axuilary verb?\n(Hint: words like do, does, was, is and etc.)\n\n0. No\n1. Yes");
	axuilary = check_if_Axuilary();									// проверка на наличие вспомогательного глагола
	check_Time(temp_str, axuilary);									// проврека времени (грамматики), в котором задано отличие

	system("cls");
	return create_Node(temp_str);									// создание узла с отличием
}

void change_Nodes(NODE* &node_1, NODE* &node_2)
{
	assert(node_1 != nullptr);
	assert(node_2 != nullptr);

	char temp_str[MAX_SIZE] = { 0 };

	memset(temp_str, '\0', MAX_SIZE * sizeof(char));				// меняем местами 
	strcpy_s(temp_str, MAX_SIZE, node_1->string);					// содержимое узлов
	memset(node_1->string, '\0', MAX_SIZE * sizeof(char));	
	strcpy_s(node_1->string, MAX_SIZE, node_2->string);					
	memset(node_2->string, '\0', MAX_SIZE * sizeof(char));
	strcpy_s(node_2->string, MAX_SIZE, temp_str);
}

void guess_Failed(STATISTICS* player, NODE* &ptr)
{
	NODE* actor;
	NODE* distinction;

	actor = create_Actor();
	if (actor == nullptr)
	{
		system("cls");
		printf("Can not create an actor!");
		_getch();
		system("cls");
		return;
	}
	distinction = create_Distinction(ptr);
	if (distinction == nullptr)
	{
		system("cls");
		printf("Can not create a distinction!");
		_getch();
		system("cls");
		return;
	}

	ptr->left = distinction;										// слева от листа мы пишем новый отличительный узел
	ptr->right = actor;												// а справа актера, который описан критерием в узле

	change_Nodes(ptr, ptr->left);									// меняем местами былой лист и новый вопрос, чтобы все стало на свои места

	system("cls");

	player->current_score++;

	printf("Congratulations, you've beaten me!\nBut remember, next time I'll be a bit smarter... ;)");
	printf("\n\nPress any key to return to the menu...");
	_getch();

	system("cls");
}

bool check_if_Axuilary()
{
	bool axuilary;
	while(true)
	{
		switch (_getch())
		{
		case '0':
		{
			axuilary = false;
			break;
		}
		case '1':
		{
			axuilary = true;
			break;
		}
		default: continue;
		}
		break;
	}
	return axuilary;
}

void check_Time(char* str, bool axuilary)
{
	assert(str != nullptr);
	assert(strlen(str) >= MIN_SIZE);

	int i = 0;
	int axuilary_len = 0;
	bool skip = false;
	if (axuilary)							// если есть вспомогательный глагол
	{										// то мы меняем его местами с метоимением
		str[0] = str[0] + REGISTER_DIFF;	// в начале преложения
		reverse_Two_Words(str);				
		str[0] = str[0] - REGISTER_DIFF;
	}
	else									// если же такого глагола нет
	{										// то определяется время предложения (Present\Past Simple and so on...)
		char* temp;							// и составляется вопрос

		str[0] = str[0] + REGISTER_DIFF;

		temp = getWord(str);

		switch (temp[wordLen(temp) - 1])		// определяем время
		{
			case 'd': axuilary_len = DID; break;
			case 's': axuilary_len = DOES; break;
		}

		if (axuilary_len != DOES && axuilary_len != DID)			// если время прошедшее, но глагол в 3ей форме не отличатеся от 1ой
		{
			axuilary_len = DID;
			skip = true;
		}

		if (!skip)
		{
			for (int j = wordLen(temp) - 1; j < strlen(temp); j++)	// удаляем последнюю букву окончания
			{
				temp[j] = temp[j + 1];
			}

			if (check_Letter(temp[wordLen(temp) - 1]))				// если в окончании есть вторая буква, то она удаляется
			{
				for (int j = wordLen(temp) - 1; j < strlen(temp); j++)	
				{
					temp[j] = temp[j + 1];
				}
			}
		}

		i = strlen(str) + 1;
		while (i--)									// сдвиг строки на число символов
		{											// необходимое, для записи
			str[i + axuilary_len] = str[i];			// всопомогательного глагола
		}

		if (axuilary_len == DID)
		{
			str[0] = 'D';
			str[1] = 'i';
			str[2] = 'd';
			str[3] = ' ';
		}
		else
		{
			str[0] = 'D';
			str[1] = 'o';
			str[2] = 'e';
			str[3] = 's';
			str[4] = ' ';
		}
	}
}

bool check_Letter(char letter)
{
	int  i = ALPHABET_SIZE;
	char alphabet[] = { 'a', 'e', 'i', 'o', 'u', 'y' };

	while (i--)
	{
		if (letter == alphabet[i]) return true;
	}

	return false;
}

void recReverse(char *str, int length)
{
	char temp;
	if (length > 1)
	{
		temp = *str;
		*str = *(str + length - 1);
		*(str + length - 1) = temp;
		recReverse(str + 1, length - 2);
	}
}

int wordLen(char* word)
{
	int i = 0;
	while (word[i] != ' ' && word[i] != '\0' && word[i] != ',') i++;
	return i;
}

char* getWord(char* str)
{
	while (*str != ' ' && *str != '\0' && *str != ',') str++;
	while (*str == ' ' && *str != '\0' && *str != ',') str++;
	if (*str == '\0') return nullptr;
	return str;
}

void reverse_Two_Words(char* str)
{
	char* second = getWord(str);
	int separatores = 0;

	int i = 0;

	while (str[wordLen(str) + i] == ' ')
	{
		separatores++;
		i++;
	}

	recReverse(str, wordLen(str) + wordLen(second) + separatores);

	recReverse(str, wordLen(str));

	str = getWord(str);
	recReverse(str, wordLen(str));
}

ARRAY_INFO* load_Spredsheet(FILE* spreadsheet)
{
	char enter;
	char temp_str[TEMP_BUFF] = { 0 };
	rewind(spreadsheet);

	ARRAY_INFO* unit = (ARRAY_INFO*)malloc(sizeof(ARRAY_INFO));
	if (unit == nullptr) return nullptr;

	fscanf_s(spreadsheet, "%[^0-9]", temp_str, TEMP_BUFF);
	memset(temp_str, '\0', TEMP_BUFF);
	fscanf_s(spreadsheet, "%d", &unit->amount);

	unit->player_array = (STATISTICS*)malloc((unit->amount + 1) * sizeof(STATISTICS));

	fscanf_s(spreadsheet, "%[\n]", &enter, 1);
	fseek(spreadsheet, SKIP_INFO, SEEK_CUR);

	for (int i = 0; i < unit->amount; i++)
	{
		fscanf_s(spreadsheet, "%[\n]", &enter, 1);
		unit->player_array[i].user_name = createStr(NAME_LENGTH);
		if (unit->player_array[i].user_name == nullptr)
		{
			i--;
			for (; i >= 0; i--)
			{
				free(unit->player_array[i].user_name);
			}
			free(unit->player_array);
			return nullptr;
		}
		fscanf_s(spreadsheet, "%[a-zA-Z0-9]", unit->player_array[i].user_name, NAME_LENGTH);
		fseek(spreadsheet, 2, SEEK_CUR);
		fscanf_s(spreadsheet, "%d", &unit->player_array[i].highscore);
		unit->player_array[i].current_score = 0;
	}

	return unit;
}

STATISTICS* find_Player(ARRAY_INFO* &ms, char* name)
{
	for (int i = 0; i < ms->amount; i++)
	{
		if (strcmp(name, ms->player_array[i].user_name) == 0)
		{
			return &ms->player_array[i];
		}
	}

	ms->player_array[ms->amount].user_name = createStr(NAME_LENGTH);
	if (ms->player_array[ms->amount].user_name == nullptr) return nullptr;

	strcpy_s(ms->player_array[ms->amount].user_name, NAME_LENGTH, name);
	ms->player_array[ms->amount].current_score = 0;
	ms->player_array[ms->amount].highscore = 0;

	ms->amount++;

	return &ms->player_array[ms->amount - 1];
}

void free_Player_Ms(ARRAY_INFO* ms)
{
	if (ms->player_array == nullptr) return;

	for (int i = 0; i < ms->amount; i++)
	{
		free(ms->player_array[i].user_name);
	}
	free(ms->player_array);
}

FILE* create_Spreadsheet(ARRAY_INFO* ms)
{
	FILE* spreadsheet;

	fopen_s(&spreadsheet, "Spreadsheet.txt", "w");

	if (spreadsheet == nullptr)
	{
		printf("Error while creating spreadsheet!\nPress any key to continue...");
		_getch();
		return nullptr;
	}

	fprintf_s(spreadsheet, "Amount: %d\nNickname\t\tHighscore\n", ms->amount);

	for (int i = 0; i < ms->amount; i++)
	{
		write_To_Spredsheet(spreadsheet, ms->player_array[i]);
	}
	return spreadsheet;
}

void write_To_Spredsheet(FILE* spreadsheet, STATISTICS player)
{
	int score = player.highscore;

	if (player.highscore < player.current_score) score = player.current_score;

	fseek(spreadsheet, 0, SEEK_END);

	fprintf_s(spreadsheet, "%s\t\t%d\n", player.user_name, score);
}

void output_menu(char* name)
{
	system("cls");
	printf("Welcom to 'THE GUESSING MACHINE'\n\nAwards:People's choice 2020\n\n\n");
	printf("You have logged as %s\n\n\n", name);
	printf("===================================================================\n\n");
	printf("1. Start the game\n");
	printf("2. Log off and change player\n");
	printf("0. Exit\n\n");
	printf("(HINT: You can watch your highscore in the 'Spreadsheet.txt' file)\n");
	printf("===================================================================\n\n");
	printf("\n\n\n\n\n\nLab Inc. (c)All rights reserved");
}

bool check_File(FILE* file)
{
	if (file == nullptr) return false;

	fseek(file, 0, SEEK_END);

	if (!ftell(file))
	{
		fclose(file);
		return false;
	}

	rewind(file);

	return true;
}

void emergency_Exit()
{
	system("cls");
	printf("The memory has not been allocated or the file is corrupted!\nPress any key to exit the programm...");
	_getch();
	exit(1);
}