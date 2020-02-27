#include "MyString.h"
#include <iostream>

using namespace std;

int main()
{
	char *ptr;
	int a = 5;
	const int max_size = 100;
	String str_1, str_2("Some information"), str_3("Some more information"), str_4;
	String *str_arr;

	str_arr = new String[3];

	str_4("Initializing with ()"); 

	cout << str_2 << "str_2" << endl << endl;
	cout << str_3 << "str_3" << endl << endl;
	cout << str_4 << "str_4" << endl << endl;

	str_2 = str_3;
	++str_3;
	str_2--;
	str_2 - 'm';
	str_3 + str_4;
	str_1 + str_4;

	cout << endl << endl;
	cout << str_2 << "str_2" << endl << endl;
	cout << str_3 << "str_3" << endl << endl;
	cout << str_1 << "str_1" << endl << endl;

	cout << endl << endl;
	if (str_2 == str_3) cout << "str_2 == str_3" << endl;
	else cout << "str_2 != str_3" << endl;
	if (str_2 > str_1) cout << "str_2 > str_1" << endl;
	else cout << "str_1 >= str_2" << endl;
	if (str_3 < str_2) cout << "str_3 < str_2" << endl;
	else cout << "str_1 <= str_2" << endl;

	cout << endl << endl;
	cout << str_1[0];

	cout << endl << endl;
	cout << "Working with char*" << endl;
	char* str = new char[max_size];
	cout << "Please enter the string" << endl;
	cin.getline(str, max_size);

	str_1 = str;
	cout << str_1 << str << endl;
	str_2 + str;
	cout << str_2 << endl << endl;

	if (str_2 > str) cout << "str_2 > str" << endl;
	else cout << "str_2 <= str" << endl;
	if (str_1 < str) cout << "str_1 < str" << endl;
	else cout << "str_1 >= str" << endl;
	if (str_3 == str) cout << "str_3 == str" << endl;
	else cout << "str_3 != str" << endl;

	cout << (int)str_2 + a << endl << endl;
	ptr = (char*)str_1;

	cout << ptr << ": ptr = str_1" << endl << endl;

	delete[] str_arr;

	system("pause");
	return 0;
}