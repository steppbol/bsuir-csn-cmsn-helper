#pragma once

class Vehicle
{
	public:
		virtual ~Vehicle();

		virtual int computeCost() = 0;
		virtual void show() = 0;
};