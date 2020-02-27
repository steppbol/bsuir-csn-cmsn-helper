#pragma once

#include <iostream>
#include "Vehicle.h"

Vehicle::Vehicle()
{
	this->_length = 0;
	this->_mass = 0;
}

Vehicle::Vehicle(int length, int mass)
{
	this->_length = length;
	this->_mass = mass;
}

Vehicle::~Vehicle()
{
}

int Vehicle::get_lenght()
{
	return this->_length;
}

int Vehicle::get_mass()
{
	return this->_mass;
}

void Vehicle::showVehicle()
{
	std::cout << std::endl << "Length : " << this->_length << std::endl << "Mass : " << this->_mass << std::endl;
}