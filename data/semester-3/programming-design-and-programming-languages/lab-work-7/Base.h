#pragma once

template <class Type>
class Base
{
public:
	void set(int number, Type variable)
	{
		this->_number = number;
		this->_variable = variable;
	}
	Type getVariable()
	{
		return this->_variable;
	}
	int getNumber()
	{
		return this->_number;
	}
	void show()
	{
		cout << "Number = " << this->_number << "\tVariable : " << this->_variable << endl;
	}

	Base() : _number(0),
			 _variable(0)
	{
	}
	Base(int number, Type variable) : _number(number),
									  _variable(variable)
	{
	}
	~Base()
	{
	}
	private:
		int _number;
		Type _variable;
};