#pragma once

#include <iostream>
#include "Space_Vehicle.h"

enum FTYPE {SOLID_PROPELLANTS, LIQUID_PROPELLANTS, GAS_PROPELLANTS, GEL_PROPELLANTS};


class Rocket : public Space_Vehicle
{
	public:
		Rocket();
		Rocket(int, int, bool, FTYPE);
		~Rocket();

		FTYPE get_fuel_type();
		void showRocketSpecific();

	private:
		FTYPE _fuel;
};
