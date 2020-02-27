#pragma once

#include <iostream>
#include "Air_Vehicle.h"

Air_Vehicle::Air_Vehicle() : Vehicle()
{
	this->_max_speed = 0;
}

Air_Vehicle::Air_Vehicle(int length, int mass, int speed) : Vehicle(length, mass)
{
	this->_max_speed = speed;
}

Air_Vehicle::~Air_Vehicle()
{
}

int Air_Vehicle::get_speed()
{
	return this->_max_speed;
}

void Air_Vehicle::showAirVehicleSpecific()
{
	std::cout << "Max speed : " << this->_max_speed << std::endl;
}
