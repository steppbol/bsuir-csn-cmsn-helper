#pragma once

#define KEY_TAGS_PER_UNIT 11
#define MAX_LENGTH 50
#define BUFF_SIZE 50
#define PUSH_CELL 12
#define PUSH_NAME 38
#define FIRST_SKIP 1
#define SECOND_SKIP 2

struct laptop
{
	char* name;
	char* processor;
	float screen_size;
	char* resolution;
	int ram;
	int capasity;
	char* graphics_chip;
};

laptop* createArray(int size);
int calculateAmount(FILE* file);
void freeArray(laptop* arr, int size);
char* createStr(int size);
void getNameField(char* name, char str[]);
void getCharField(char* field, char str[]);
void getIntField(int &field, char str[]);
void getFloatField(float &field, char str[]);
void findData(FILE* file, char string[], int push_type);
void skipField(FILE* file, int number_of_fields);
void outputArray(laptop* arr, int size); 
void emergencyExit();