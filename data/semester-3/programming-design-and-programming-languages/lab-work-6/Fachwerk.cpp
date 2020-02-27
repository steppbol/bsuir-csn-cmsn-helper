#include "Fachwerk.h"



Fachwerk::Fachwerk()
{
}

Fachwerk::Fachwerk(int number, std::string street, double yardSquare, Facade facade)
	: PrivateHouse::PrivateHouse(number, street, yardSquare), facade(facade)
{
}


Fachwerk::~Fachwerk()
{
}

void Fachwerk::Show()
{
	PrivateHouse::Show();
	std::cout << "Fachwerk facade type: " << this->facade << std::endl;
}
