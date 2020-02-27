#pragma once


enum FuelState {SOLID_PROPELLANTS, LIQUID_PROPELLANTS, GAS_PROPELLANTS, GEL_PROPELLANTS };

class Fuel
{
	public:
		Fuel();
		Fuel(FuelState, int);
		Fuel(const Fuel&);
		~Fuel();

		FuelState getFuelType();
		int getFuelMass();
		int fuelCost();
		void showFuelType();
	private:
		FuelState _type;
		int _fuel_mass;
		static const int _solid_price;
		static const int _liquid_price;
		static const int _gas_price;
		static const int _gel_price;
};