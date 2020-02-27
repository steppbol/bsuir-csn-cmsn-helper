#include "Student.h"
#include "Dean.h"
#include <iostream>
#include <cstdlib>


#define array_length 4

int Student::id_stack = 0;

int main() {
	Dean dean;
	Student instance_first, instance_second = {"Ivan" , 9.3 };

	show(instance_first);
	dean.set_rate(instance_first, 20);
	show(instance_first);

	show(instance_second);
	dean.set_rate(instance_second, 9.1);
	show(instance_second);

	Student group[array_length] = { Student("Stepan", 8.5), Student("Valentin", 6), Student("Eugeny", 7.5), Student("Pavel", 7) };


	for (int  i = 0; i < array_length; i++)
	{
		show(group[i]);
		dean.set_rate(group[i], dean.get_rate(group[i]) + 0.1*i);
		show(group[i]);
	}

	//for each(Student pupil in group)
	//{
	//	show(pupil);
	//	dean.set_rate(pupil, dean.get_rate(pupil) + 0.5);
	//	show(pupil);
	//}

	system("pause");
	return 0;
}