#include "SpecialSymbol.h"

ostream &operator<<(ostream &stream,
	addspecial & object)
{
	for (int i = 0; i < object._amount; i++) stream << "@";
	
	return stream;
}

addspecial::addspecial(int amount) : _amount(amount)
{
}