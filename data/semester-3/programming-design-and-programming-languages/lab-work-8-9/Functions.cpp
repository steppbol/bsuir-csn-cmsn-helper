#include "Functions.h"

double atod(char* str)
{
	int i = 0;
	double number = 0;

	int integerSize = 0;
	while (str[integerSize] != '.')
		integerSize++;

	while (str[i] != '.')
	{
		integerSize--;
		number += (static_cast<int>(str[i]) - 48) * pow(10, integerSize);
		i++;
	}

	i++;
	int j = 1;
	while (str[i] != 0 && str[i] != '\n')
	{
		number += (static_cast<int>(str[i]) - 48) * pow(10, -j);
		i++;
		j++;
	}

	return number;
}