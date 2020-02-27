#pragma once

#include <iostream>
#include "AirVehicle.h"

const int AirVehicle::_speed_coefficient = 15;

AirVehicle::AirVehicle()
{
	this->_max_speed = 0;
	this->_length = 0;
}

AirVehicle::AirVehicle(int max_speed, int length)
{
	this->_max_speed = max_speed;
	this->_length = length;
}

AirVehicle::~AirVehicle(){
}

int AirVehicle::get_max_speed()
{
	return this->_max_speed;
}

int AirVehicle::get_length()
{
	return this->_length;
}

int AirVehicle::computeCost()
{
	return (this->_max_speed * _speed_coefficient);
}

void AirVehicle::show()
{
	std::cout << "Length (m): " << this->_length << std::endl << "Max speed (m|s) : " << this->_max_speed << std::endl;
}