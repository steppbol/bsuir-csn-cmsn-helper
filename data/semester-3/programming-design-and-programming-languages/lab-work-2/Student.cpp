#include "Student.h"
#include <iostream>
#include <sstream>

Student::Student()
{
	this->id = ++id_stack;
	this->name = "Undefined";
	this->rate = 0;
}

Student::Student(string name, double rate) : name(name), rate(rate)
{
	id = ++id_stack;
}

Student::~Student()
{
	cout << "Destructor for " << this->name <<" works!" << endl;
}


int Student::get_id() {
	return this->id;
}

string Student::get_name() {
	return this->name;
}

void Student::set_name(string value) {
	if (value.length() < 20)
		this->name = value;
	else
		cout << "ERROR: incorrect input" << endl;
}

void show(const Student & instance)
{
	stringstream output_stream;
	output_stream.precision(2);
	output_stream << "Name: " << instance.name << " Rate: " << instance.rate << " ID: " << to_string(instance.id) << endl << endl;
	cout << output_stream.str() << endl;
}
