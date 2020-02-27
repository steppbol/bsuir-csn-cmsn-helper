#include <stdio.h>
#include <string.h>
#include <conio.h>
#include <stdlib.h>
#include <assert.h>

#define MAX_LENGTH 10

struct inform
{
	char* word;
	int amount;
};

inform* createArray(int size)
{
	inform *arr;
	arr = (inform*)malloc(size*sizeof(inform));
	assert (arr != nullptr);
	for(int i = 0; i < size; i++)
	{
		arr[i].word = (char*)calloc(MAX_LENGTH+1,sizeof(char));
		assert(arr[i].word != nullptr);
		arr[i].amount = 0;
	}
	return arr;
}

int main()
{
	inform* arr;
	FILE *file1, *file2, *file3;
	char pointer_f;
	char j;
	int size = 0;
	char *buf_1, *buf_2;
	int position2 = 0;
	int position = 0;
	int k = 0;
	int wordlen = 0;

	buf_1 = (char*)calloc(MAX_LENGTH, sizeof(char));
	buf_2 = (char*)calloc(MAX_LENGTH, sizeof(char));

	file1 = fopen("read_file.txt", "r");
	if(file1 == nullptr) return 0;

	file2 = fopen("input_file.txt", "w+");
	if(file2 == nullptr) return 0;

	rewind(file1);
	pointer_f = fgetc(file1);

	while(pointer_f != EOF)
	{
		pointer_f = getc(file1);
		fputc(pointer_f,file2);
		fseek(file1,1,1);
		fseek(file2,1,1);
	}

	rewind(file1);
	pointer_f = fgetc(file1);
	while(pointer_f != EOF)   // находим число слов
	{
		for (int i = 0; pointer_f != ' ' || pointer_f != '\t' || pointer_f != '\n'; i++)
		{
			pointer_f=getc(file2);
			fseek(file2,1, 1);
			buf_1[i] = pointer_f;
		}
		pointer_f=getc(file2);
		size++;
	}

	fclose(file1);
	arr = createArray(size);

	size = 0;

	rewind(file2);
	j = getc(file2);
	while(j != EOF)
	{
		for (int i = 0; j != ' ' || j != '\t' || j != '\n'; i++)
		{
			j=getc(file2);
			fseek(file2,1, 1);
			arr[k].word[i] = j;
		}
		fseek(file2,1, 0);
		while(j != EOF)
		{
			position2 = ftell(file2);
			for (int i = 0; j != ' ' || j != '\t' || j != '\n'; i++)
			{
				j=getc(file2);
				fseek(file2,1, 1);
				buf_2[i] = j;
			}
			if(strcmp(arr[k].word, buf_2) == 0)
			{
				arr[k].amount++;
				fseek(file2, position, 0);
				while( j != ' ' || j!= '\n' || '\t')
				{

					putc(' ', file2);
					fseek(file2, 1,0);
				}
			}
		}
		fseek(file2, 0, position2);
		k++;
	}

	fclose(file2);

	arr =(inform*)realloc(arr,k*sizeof(inform*));

	file3 = fopen("result_file.txt","w");
	if(file3 == nullptr) return 0;

	fseek(file3,0,0);
	while(k)
	{
		fprintf(file3,"%s",arr[k].word);
		putc(' ', file3);
		fprintf(file3,"%c",(char)(arr[k].amount + 48));
		k--;
	}

	fclose(file3);
	_getch();
	return 0;
}