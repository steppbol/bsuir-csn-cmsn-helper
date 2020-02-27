#pragma once
#pragma once
#include <string>



using namespace std;

class Dean;

class Student
{
public:
	Student();
	Student(string, double);
	~Student();

	static int id_stack;
	
	friend Dean;
	friend void show(const Student & instance);

	string get_name();
	int get_id();
	void set_name(string value);


private:
	int id;
	double rate;
	string name;


};

