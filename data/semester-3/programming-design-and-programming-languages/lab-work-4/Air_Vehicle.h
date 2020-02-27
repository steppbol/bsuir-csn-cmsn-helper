#pragma once

#include <iostream>
#include "Vehicle.h"

class Air_Vehicle : virtual public Vehicle
{
	public:
		Air_Vehicle();
		Air_Vehicle(int , int , int);
		~Air_Vehicle();

		int get_speed();
		void showAirVehicleSpecific();

	private:
		int _max_speed;
};