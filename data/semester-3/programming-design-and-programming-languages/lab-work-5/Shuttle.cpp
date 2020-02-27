#pragma once

#include <iostream>
#include "Shuttle.h"

Shuttle::Shuttle() : Plane(), Rocket()
{
}

Shuttle::Shuttle(int max_speed, int length, int wing_length, int number_of_passengers,
				 int seat_price, int sv_mass, int engine_power, FuelState type, int fmass) :
				Plane(max_speed, length, wing_length, number_of_passengers, seat_price),
				Rocket(sv_mass, engine_power, type, fmass)
{
}

Shuttle::~Shuttle(){
}

int Shuttle::computeCost()
{
	return Plane::computeCost() + Rocket::computeCost();
}

void Shuttle::show()
{
	Plane::show();
	Rocket::show();
}