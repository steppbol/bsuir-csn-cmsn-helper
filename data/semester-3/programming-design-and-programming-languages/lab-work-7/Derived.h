#pragma once
#include "Base.h"

template <class Type>
class Derived : public Base<Type>
{
	public:
	void set(int number, Type variable, Type variable_2)
	{
		this->Base::set(number, variable);
		this->_variable_2 = variable_2;
	}
	Type getSecondVariable()
	{
		return this->_variable_2;
	}
	void show()
	{
		this->Base::show();
		cout << "Derived class variable = " << this->_variable_2 << endl;
	}

	Derived() : Base<Type>(),
				_variable_2(0)
	{
	}
	Derived(int number, Type variable, Type variable_2) : Base<Type>(number, variable),
														  _variable_2(variable_2)
	{
	}
	~Derived()
	{
	}
	private:
		Type _variable_2;
};