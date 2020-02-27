#pragma once

#include <iostream>
#include "SpaceVehicle.h"

const int SpaceVehicle::_engine_coefficient = 7500;
const int SpaceVehicle::_mass_coefficient = 2000;

SpaceVehicle::SpaceVehicle()
{
	this->_sv_mass = 0;
	this->_engine_power = 0;
}
SpaceVehicle::SpaceVehicle(int sv_mass, int engine_power)
{
	this->_sv_mass = sv_mass;
	this->_engine_power = engine_power;
}
SpaceVehicle::~SpaceVehicle(){
}

int SpaceVehicle::get_sv_mass()
{
	return this->_sv_mass;
}

int SpaceVehicle::get_engine_power()
{
	return this->_engine_power;
}

int SpaceVehicle::computeCost()
{
	return (_engine_coefficient * _engine_power) + (_sv_mass * _mass_coefficient);
}

void SpaceVehicle::show()
{
	std::cout << "Mass (kg): " << this->_sv_mass << std::endl << "Engine power (Watt) : " << this->_engine_power << std::endl;
}