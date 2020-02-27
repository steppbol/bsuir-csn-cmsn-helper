#pragma once

#include <iostream>
#include "Vehicle.h"

class AirVehicle : virtual public Vehicle
{
	public:
		AirVehicle();
		AirVehicle(int, int);
		~AirVehicle();

		int get_max_speed();
		int get_length();

		int computeCost();
		void show();
	private:
		int _max_speed;
		int _length;
		static const int _speed_coefficient;
};