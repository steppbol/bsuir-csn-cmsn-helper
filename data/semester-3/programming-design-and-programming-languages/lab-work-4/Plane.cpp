#pragma once

#include <iostream>
#include "Plane.h"

Plane::Plane() : Air_Vehicle()
{
	this->_wing_length = 0;
}

Plane::Plane(int length, int mass, int speed, int wing_length) : Air_Vehicle(length, mass, speed),
																 Vehicle(length, mass)
{
	this->_wing_length = wing_length;
}

Plane::~Plane()
{
}

int Plane::get_wing_length()
{
	return this->_wing_length;
}

void Plane::showPlaneSpecific()
{
	std::cout << "Wing's length : " << this->_wing_length << std::endl;
}
