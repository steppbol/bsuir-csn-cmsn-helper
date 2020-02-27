#pragma once

#include <iostream>
#include "Air_Vehicle.h"


class Plane : public Air_Vehicle
{
	public:
		Plane();
		Plane(int , int , int , int);
		~Plane();

		int get_wing_length();
		void showPlaneSpecific();

	private:
		int _wing_length;
};