#pragma once

#include <iostream>
#include "Shuttle.h"

Shuttle::Shuttle() : Plane(), Rocket()
{
}

Shuttle::Shuttle(int length, int mass, int speed, bool disposable, int wing_length, FTYPE fuel) : 
				Plane(length, mass, speed, wing_length),
				Rocket(length, mass, disposable, fuel),
				Vehicle(length, mass)
{
}

Shuttle::~Shuttle()
{
}

void Shuttle::showShuttle()
{
	showVehicle();
	showAirVehicleSpecific();
	showSpaceVehicleSpecific();
	showPlaneSpecific();
	showRocketSpecific();
}
