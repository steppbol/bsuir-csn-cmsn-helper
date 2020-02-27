#include "PrivateHouse.h"



PrivateHouse::PrivateHouse()
{
}


PrivateHouse::PrivateHouse(int number, std::string street, double yardSquare) : House(number, street), yardSquare(yardSquare)
{
	
}

PrivateHouse::~PrivateHouse()
{
}

void PrivateHouse::Show()
{
	House::Show();
	std::cout << "Yard square: " << this->yardSquare << std::endl;
}
