#pragma once

#include <iostream>
#include "Space_Vehicle.h"

Space_Vehicle::Space_Vehicle() : Vehicle()
{
	this->_disposable = true;
}

Space_Vehicle::Space_Vehicle(int length, int mass, bool disposable) : Vehicle(length, mass)
{
	this->_disposable = disposable;
}

Space_Vehicle::~Space_Vehicle()
{
}

bool Space_Vehicle::get_type()
{
	return this->_disposable;
}

void Space_Vehicle::showSpaceVehicleSpecific()
{
	if (this->_disposable) std::cout << "Disposable : Yes" << std::endl;
	else std::cout << "Disposable : No" << std::endl;
}