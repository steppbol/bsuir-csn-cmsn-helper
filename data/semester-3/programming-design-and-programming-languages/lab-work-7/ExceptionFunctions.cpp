#include "ExceptionFunctionsHeader.h"
#include <iostream>

void MyTerminate()
{
	std::cout << "Exception was not caught! Terminate function is called!" << std::endl;
	system("pause");
	abort();
}