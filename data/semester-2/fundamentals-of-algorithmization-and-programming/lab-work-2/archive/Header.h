#pragma once

#define MAX_LENGTH 25
#define PRE_INFO 20
#define FIRST_PUSH 1
#define REGISTER_DIFF 32

struct word_data
{
	char* word;
	int amount;
	word_data *next;
};

struct word_queue
{
	word_data* head;
	word_data* tail;
};

word_queue create_Word_Queue();
char* createStr(int size);
bool ckeck_Word(char* found_word, word_queue &line);
void add_Word(word_queue &line, char* found_word);
void fullfill_Word_Queue(FILE* file, word_queue &line);
word_data* find_Pair(word_queue &line);
void remove_Word(word_data* &prev_to_doomed, word_queue &unit);
FILE* queue_To_File(word_queue &line);
void pair_From_Key(char* found_word, char pair[], FILE* key);
FILE* compress(FILE* original, FILE* key);
int stringCmp(char* str1, char* str2);
bool check_File(FILE* file);
void emergency_Exit();