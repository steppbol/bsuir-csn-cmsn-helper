#pragma once

#include "Vehicle.h"

class Space_Vehicle : virtual public Vehicle
{
	public:
		Space_Vehicle();
		Space_Vehicle(int, int, bool);
		~Space_Vehicle();

		bool get_type();
		void showSpaceVehicleSpecific();
		
	private:
		bool _disposable;
};