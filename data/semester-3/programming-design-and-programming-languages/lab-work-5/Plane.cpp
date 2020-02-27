#pragma once

#include <iostream>
#include "Plane.h"

Plane::Plane() : AirVehicle()
{
	this->_wing_length = 0;
	this->_number_of_passengers = 0;
	this->_seat_price = 0;
}

Plane::Plane(int max_speed, int length, int wing_length, int number_of_passengers, int seat_price) : AirVehicle(max_speed, length)
{
	this->_wing_length = wing_length;
	this->_number_of_passengers = number_of_passengers;
	this->_seat_price = seat_price;
}

Plane::~Plane()
{
}

int Plane::getWingLength()
{
	return this->_wing_length;
}

int Plane::getPassengersNumber()
{
	return this->_number_of_passengers;
}

int Plane::getSeatPrice()
{
	return this->_seat_price;
}

int Plane::computeCost()
{
	return AirVehicle::computeCost() + (_number_of_passengers * _seat_price);
}

void Plane::show()
{
	AirVehicle::show();
	std::cout << "Wing's length : " << this->_wing_length << std::endl;
	std::cout << "Number of passengers : " << this->_number_of_passengers << std::endl;
	std::cout << "Seat's price ($) : " << this->_seat_price << std::endl;
}