#pragma once

#define MAX_SIZE 80
#define MIN_SIZE 6
#define REGISTER_DIFF 32
#define ALPHABET_SIZE 6
#define MAGIC_PUSH 3
#define SKIP_INFO 19
#define TEMP_BUFF 20
#define NAME_LENGTH 25
#define PUSH_HEADER 8
#define DID 4
#define DOES 5

struct NODE
{
	char* string;
	NODE* left;
	NODE* right;
};

struct STATISTICS
{
	char *user_name;
	int current_score;
	int highscore;
};

struct ARRAY_INFO
{
	STATISTICS* player_array;
	int amount;
};

struct NODES_GROUP
{
	char parent[MAX_SIZE];
	char left[MAX_SIZE];
	char right[MAX_SIZE];
};

NODE* create_Node(char* string);
char* createStr(int size);
void find_Place(NODE* root, char* common_node, NODE* &place, bool &force_return);
NODE* create_Tree(FILE* file);
void tree_To_File(NODE* root, FILE* file);
FILE* create_Tree_File(NODE* root);
void delete_Tree(NODE* root);
void accept_Pushtype(bool &push_type);
NODE* create_Actor();
NODE* create_Distinction(NODE* &ptr);
void change_Nodes(NODE* &node_1, NODE* &node_2);
void guess_Succed();
void guess_Failed(STATISTICS* player, NODE* &root);
bool check_if_Axuilary();
bool push_Pointer(NODE* &ptr, bool push);
void check_Time(char* str, bool axuilary);
bool check_Letter(char letter);
void recReverse(char *str, int length);
int wordLen(char* word);
char* getWord(char* str);
void reverse_Two_Words(char* str);
ARRAY_INFO* load_Spredsheet(FILE* spreadsheet);
STATISTICS* find_Player(ARRAY_INFO* &ms, char* name);
void free_Player_Ms(ARRAY_INFO* ms);
FILE* create_Spreadsheet(ARRAY_INFO* ms);
void write_To_Spredsheet(FILE* spreadsheet, STATISTICS player);
bool check_File(FILE* file);
void output_menu(char* name);
void emergency_Exit();