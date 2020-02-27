#include <stdio.h>
#include <conio.h>
#include "Header.h"

int main()
{
	FILE* compressed;
	FILE* key;
	FILE* original;

	fopen_s(&compressed, "compressed_file.txt", "r");
	if (!check_File(compressed))
	{
		printf("Compressed file not found!");
		_getch();
		return 0;
	}

	fopen_s(&key, "compression_key.txt", "r");
	if (!check_File(key))
	{
		printf("Key file not found!");
		_getch();
		fclose(compressed);
		return 0;
	}

	printf("Decompressing...\n");
	original = decompress(compressed, key);
	printf("COMPLETE\n\n");

	printf("Press any key to exit\n");
	_getch();

	fclose(key);
	fclose(original);
	fclose(compressed);

	return 0;
}