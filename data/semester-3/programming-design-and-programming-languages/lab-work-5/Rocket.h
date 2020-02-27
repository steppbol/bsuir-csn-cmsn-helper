#pragma once

#include <iostream>
#include "SpaceVehicle.h"
#include "Fuel.h"


class Rocket : public SpaceVehicle 
{
	public:
		Rocket();
		Rocket(int, int, FuelState, int);
		~Rocket();
		
		int getFuelmass();
		FuelState getFueltype();
		Fuel getFuel();

		int computeCost();
		void show();
	private:
		Fuel _fuel_object;
};
