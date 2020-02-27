#pragma once

#include <iostream>
#include "Rocket.h"

Rocket::Rocket() : SpaceVehicle(), _fuel_object()
{
}

Rocket::Rocket(int sv_mass, int engine_power, FuelState type, int fmass) : SpaceVehicle(sv_mass, engine_power), _fuel_object(type, fmass)
{
}

Rocket::~Rocket()
{
}

int Rocket::getFuelmass()
{
	return _fuel_object.getFuelMass();
}

FuelState Rocket::getFueltype()
{
	return _fuel_object.getFuelType();
}

Fuel Rocket::getFuel()
{
	return _fuel_object;
}

int Rocket::computeCost()
{
	return this->SpaceVehicle::computeCost() + this->_fuel_object.fuelCost();
}

void Rocket::show()
{
	SpaceVehicle::show();
	this->_fuel_object.showFuelType();
	std::cout << "Fuel's mass : " << this->_fuel_object.getFuelMass() << std::endl;
}