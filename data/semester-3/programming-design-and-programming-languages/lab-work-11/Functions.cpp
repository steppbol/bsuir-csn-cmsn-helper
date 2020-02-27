#include "Functions.h"

double parceString(const char* str)
{
	int i = 0;
	double number = 0;

	while (str[i] != '\n' && str[i] != '\0')
	{
		if (i > 0) i++;
		double multiplier = 1;

		while (str[i] != '+' && str[i] != '-' && static_cast<int>(str[i]) < 48 && static_cast<int>(str[i]) > 57) // skipping all the non-number
			i++;																								 // related symbols

		if (str[i] == '-')				// if the number is negative
		{
			multiplier = -1;
			i++;
		}
		else if (str[i] == '+')	i++;	// if the number is positive

		int integerSize = i;
		while (static_cast<int>(str[integerSize]) >= 48 && static_cast<int>(str[integerSize]) <= 57)	// calculating the size of the integer part
			integerSize++;
		integerSize -= i;

		while (integerSize)	// calculating the integer part
		{
			integerSize--;
			number += (static_cast<int>(str[i]) - 48) * pow(10, integerSize) * multiplier;
			i++;
		}
		if (str[i] == '.' || str[i] == ',')
		{
			i++;	// skipping the dot
			int fractionSize = 1;
			while (static_cast<int>(str[i]) >= 48 && static_cast<int>(str[i]) <= 57)	// calculating the fraction part
			{
				number += (static_cast<int>(str[i]) - 48) * pow(10, -fractionSize) * multiplier;
				i++;
				fractionSize++;
			}
		}
	}

	return number;
}