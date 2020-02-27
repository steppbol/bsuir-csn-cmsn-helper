#include "header.h"
#include <stdio.h>
#include <conio.h>
#include <stdlib.h>
#include <assert.h>

int main(int argc, char** argv)
{
	int i, memory=0;
	char* new_str;
	for(i=1; i<argc; i++)
	{
		memory+=stringLen(argv[i]);
	}
	new_str=(char*)calloc(memory+1, sizeof(char));
	assert(new_str!=nullptr);
	stringSort(argv, argc);
	for(i=1; i<argc; i++)
		stringCat(new_str, argv[i]);
	printf("%s", new_str);
	free(new_str);
	_getch();
	return 0;
}

