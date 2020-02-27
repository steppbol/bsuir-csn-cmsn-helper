#pragma once

class Vehicle
{
	public:
		Vehicle();
		Vehicle(int length, int mass);
		~Vehicle();

		int get_lenght();
		int get_mass();
		void showVehicle();

	protected:
		int _length;
		int _mass;
};