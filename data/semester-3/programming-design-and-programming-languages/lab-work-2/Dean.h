#pragma once
#include "Student.h"
class Dean
{
public:

	void set_rate(Student & instance, double rate);
	double get_rate(const Student & instance);
};

