#pragma once

#include <iostream>
#include "Plane.h"
#include "Rocket.h"


class Shuttle : public Plane, public Rocket
{
	public:
		Shuttle();
		Shuttle(int , int , int , bool , int , FTYPE);
		~Shuttle();

		void showShuttle();
};