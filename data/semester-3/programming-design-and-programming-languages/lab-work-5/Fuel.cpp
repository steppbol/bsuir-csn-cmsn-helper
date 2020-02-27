#include <iostream>
#include "Fuel.h"

const int Fuel::_solid_price = 12;
const int Fuel::_liquid_price = 15;
const int Fuel::_gas_price = 7;
const int Fuel::_gel_price = 20;

Fuel::Fuel()
{
	this->_type = SOLID_PROPELLANTS;
	this->_fuel_mass = 0;
}

Fuel::Fuel(FuelState type, int fmass)
{
	this->_type = type;
	this->_fuel_mass = fmass;
}

Fuel::Fuel(const Fuel &object)
{
	this->_fuel_mass = object._fuel_mass;
	this->_type = object._type;
}

Fuel::~Fuel()
{
}

FuelState Fuel::getFuelType()
{
	return this->_type;
}

int Fuel::getFuelMass()
{
	return this->_fuel_mass;
}

int Fuel::fuelCost()
{
	int type_price = 0;

	switch (this->_type)
	{
		case SOLID_PROPELLANTS :
		{
			type_price = this->_solid_price;
		}
		case LIQUID_PROPELLANTS:
		{
			type_price = this->_liquid_price;
		}
		case GAS_PROPELLANTS:
		{
			type_price = this->_gas_price;
		}
		case GEL_PROPELLANTS:
		{
			type_price = this->_gel_price;
		}
	}

	return (type_price * this->_fuel_mass);
}

void Fuel::showFuelType()
{
	switch (this->_type)
	{
		case SOLID_PROPELLANTS:
		{
			std::cout << "Fuel's type : Solid propellants" << std::endl;
			break;
		}
		case LIQUID_PROPELLANTS:
		{
			std::cout << "Fuel's type : Liquid propellants" << std::endl;
			break;
		}
		case GAS_PROPELLANTS:
		{
			std::cout << "Fuel's type : Gas propellants" << std::endl;
			break;
		}
		case GEL_PROPELLANTS:
		{
			std::cout << "Fuel's type : Gel propellants" << std::endl;
			break;
		}
	}
}