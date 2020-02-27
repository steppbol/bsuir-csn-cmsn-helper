#include "House.h"


House::House()
{
}

House::House(int number, std::string street) : number(number), street(street)
{
}

void House::Show() {
	std::cout << "Number of house: " << this->number << std::endl << "Street: " << this->street << std::endl;
}


House::~House()
{
}
