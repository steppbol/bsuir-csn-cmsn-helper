#pragma once

#include <ostream>

using namespace std;

class addspecial
{
public:
	friend ostream &operator<<(ostream &stream,
							   addspecial & object);

	explicit addspecial(int);

private:
	int _amount;
};