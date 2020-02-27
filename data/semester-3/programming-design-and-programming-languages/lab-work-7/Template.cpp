#include "Template.h"
#include <iostream>

char& findElement(char* arr, char key)
{
	for (int i = 0; arr[i] != '\0' && arr[i] != '\n'; i++)
	{
		if (arr[i] == key) return arr[i];
	}
	std::cout << "String does not contain this elemnt.\nThe first element is returned" << std::endl;
	return arr[0];
}
