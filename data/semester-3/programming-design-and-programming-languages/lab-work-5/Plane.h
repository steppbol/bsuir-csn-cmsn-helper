#pragma once

#include <iostream>
#include "AirVehicle.h"


class Plane : public AirVehicle
{
	public:
		Plane();
		Plane(int, int, int, int, int);
		~Plane();
		
		int getWingLength();
		int getPassengersNumber();
		int getSeatPrice();

		int computeCost();
		void show();
	private:
		int _wing_length;
		int _number_of_passengers;
		int _seat_price;
};