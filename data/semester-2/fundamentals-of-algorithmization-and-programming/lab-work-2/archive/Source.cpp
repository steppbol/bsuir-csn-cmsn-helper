#include <stdio.h>
#include <conio.h>
#include "Header.h"

int main()
{
	FILE* original;
	FILE* key;
	FILE* compressed;
	word_queue line;

	fopen_s(&original, "original.txt", "r");
	if (!check_File(original))
	{
		printf("Original file not found");
		_getch();
		return 0;
	}

	printf("Creating queue...\n");
	line = create_Word_Queue();
	printf("COMPLETE\n\n");

	printf("Fullfilling queue with words...\n");
	fullfill_Word_Queue(original, line);
	printf("COMPLETE\n\n");

	printf("Fullfilling a key file and deleting the queue...\n");
	key = queue_To_File(line);		   // заполнение файла выполняется за счет пошагового удаления элементов очереди.
	printf("COMPLETE\n\n");

	printf("Compressing...\n");
	compressed = compress(original, key);
	printf("COMPLETE\n\n");

	printf("Press any key to exit\n");
	_getch();

	fclose(key);
	fclose(original);
	fclose(compressed);

	return 0;
}