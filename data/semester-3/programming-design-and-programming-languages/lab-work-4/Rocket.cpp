#pragma once

#include <iostream>
#include "Rocket.h"

Rocket::Rocket() : Space_Vehicle()
{
	this->_fuel = SOLID_PROPELLANTS;
}

Rocket::Rocket(int length, int mass, bool disposable, FTYPE fuel) : Space_Vehicle(length, mass, disposable),
																   Vehicle(length, mass)
{
	this->_fuel = fuel;
}

Rocket::~Rocket()
{
}

FTYPE Rocket::get_fuel_type()
{
	return this->_fuel;
}

void Rocket::showRocketSpecific()
{
	switch (this->_fuel)
	{
		case SOLID_PROPELLANTS:
		{
			std::cout << "Fuel type : Solid propellants" << std::endl;
			break;
		}
		case LIQUID_PROPELLANTS:
		{
			std::cout << "Fuel type : Liquid propellants" << std::endl;
			break;
		}
		case GAS_PROPELLANTS:
		{
			std::cout << "Fuel type : Gas propellants" << std::endl;
			break;
		}
		case GEL_PROPELLANTS:
		{
			std::cout << "Fuel type : Gel propellants" << std::endl;
			break;
		}
	}
}