#include "Template.h"
#include "Derived.h"
#include <iostream>

using namespace std;

int main()
{
	try
	{
		set_terminate(MyTerminate);

		Array<int> ms(11), ms_2(12), ms_3, ms_4, ms_5;;

		//ms_3 + ms_4;	// will cause AddingException, becase both arrays are empty

		cout << "ms_1 block" << endl;
		ms(2, 11, 3);
		//ms.randomSet();
		cout << "Initializing sized ms with()" << endl;
		ms.show();
		ms(12, 20, 0, 1, 12, 0, 3, 8, 0, 57, 18, 10, 0);
		//ms.randomSet();
		cout << endl << "Reinitializing ms with()" << endl;
		ms.show();
		ms -= 20;
		cout << endl << "Deletig one 20 with -=" << endl;
		ms.show();
		ms += 20;
		ms += 19;
		ms += 1111;
		ms - 0;
		cout << endl << "Adding one 20 with += and deleting all 0 with -" << endl;
		ms.show();

		cout << endl << endl << "ms_1 & 2 block" << endl;
		ms_2(12, 20, 1, 12, 3, 8, 0, 57, 18, 10, 41, 123, 82);
		//ms_2.set();
		//ms_2.randomSet();
		cout << "ms" << endl;
		ms.show();
		cout << "ms_2" << endl;
		ms_2.show();
		ms = ms + ms_2;
		ms.sortGrow();
		cout << endl << "Adding ms & ms_2 with + and sorting them" << endl;
		ms.show();
		ms = ms - ms_2;
		ms = ms - ms_2;
		cout << endl << "Subtracting ms & ms_2 twice with overloaded -" << endl;
		ms.show();

		cout << endl << endl << "ms_3 block" << endl;
		cout << endl << "Initializing empty ms_3 with ()" << endl;
		ms_3(1, 1000);
		ms_3.show();

		cout << endl << endl << "ms_4 block" << endl;
		ms_4 += 120;
		cout << endl << "Adding 120 with += to empty ms_4" << endl;
		ms_4.show();

		cout << endl << endl << "ms_5 block" << endl;
		ms_5 = ms;
		cout << endl << "Copying ms to empty ms_5 with =" << endl;
		ms_5.show();

		cout << endl << endl << "Function block" << endl;
		char arr[20] = "Hello, world!";

		cout << findElement(ms.getPtr(), ms.getSize(), -29) << endl;
		cout << "template function" << endl << endl;
		cout << findElement(arr, 20,'d') << endl;
		cout << "temlate function for char" << endl << endl;

		/*	Derived<char> object(10, 'a', 'b');
			object.show();
			object.set(20, 'c', 'd');
			object.show();
			cout << object.getNumber() << endl;
			cout << object.getVariable() << endl;
			cout << object.getSecondVariable() << endl;*/

		system("pause");
		return 0;
	}
	catch (AddingException &excepetion)
	{
		cout << "Exception found! " << excepetion.what() << endl;
		system("pause");
		exit(EXIT_FAILURE);
	}
	catch (SubtractingException &excepetion)
	{
		cout << "Exception found! " << excepetion.what() << endl;
		system("pause");
		exit(EXIT_FAILURE);
	}
	catch (DeletingException &excepetion)
	{
		cout << "Exception found! " << excepetion.what() << endl;
		system("pause");
		exit(EXIT_FAILURE);
	}
	catch (InvalidSizeException &excepetion)
	{
		cout << "Exception found! " << excepetion.what() << endl;
		system("pause");
		exit(EXIT_FAILURE);
	}
	catch (domain_error &excepetion)
	{
		cout << "Exception found! " << excepetion.what() << endl;
		system("pause");
		exit(EXIT_FAILURE);
	}
	catch (out_of_range &excepetion)
	{
		cout << "Exception found! " << excepetion.what() << endl;
		system("pause");
		exit(EXIT_FAILURE);
	}
	catch (bad_alloc)
	{
		cout << "Bad memmory allocation";
		system("pause");
		exit(EXIT_FAILURE);
	}
	catch (...)
	{
		cout << "Unknown exception!" << endl;
		system("pause");
		terminate();
	}
}


























/*	catch (...)
	{
		cout << endl << endl << "An error has occured!";
		system("pause");
		exit(1);
		}*/