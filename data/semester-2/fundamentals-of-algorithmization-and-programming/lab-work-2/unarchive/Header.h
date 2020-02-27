#pragma once

#define MAX_LENGTH 25
#define PRE_INFO 20
#define FIRST_PUSH 1
#define REGISTER_DIFF 32
#define LOW_UPPER_CASE_BORDER 65
#define HIGH_UPPER_CASE_BORDER 90


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


void pair_From_Key(char* found_word, char pair[], FILE* key);
FILE* decompress(FILE* compressed, FILE* key);
int stringCmp(char* str1, char* str2);
bool check_File(FILE* file);
void emergency_Exit();