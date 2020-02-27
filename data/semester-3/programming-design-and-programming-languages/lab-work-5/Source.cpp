#include <iostream>
#include "Shuttle.h"

using namespace std;

//(int max_speed, int length, int wing_length, int number_of_passengers, int seat_price, int sv_mass, int engine_power, FuelState type, int fmass) : Shuttle

int main()
{
	const int arr_size = 5;

	Vehicle *arr[arr_size];
	
	arr[0] = new AirVehicle(600, 43);
	arr[1] = new SpaceVehicle(6300, 7000);
	arr[2] = new Rocket(12000, 9800, GAS_PROPELLANTS, 750);
	arr[3] = new Plane(1000, 70, 15, 50, 600);
	arr[4] = new Shuttle(900, 55, 10, 12, 10000, 9000, 10000, LIQUID_PROPELLANTS, 1000);

	for (int i = 0; i < arr_size; i++)
	{
		arr[i]->show();
		cout << endl << endl;
	}

	for (int i = 0; i < arr_size; i++)
	{
		cout << arr[i]->computeCost() << '$' << endl << endl;
	}

	AirVehicle *temp_air_vehicle = dynamic_cast<AirVehicle*>(arr[0]);
	cout << endl << "AIR VEHICLE'S length through dynamic_cast : "
		<< temp_air_vehicle->get_length() << endl << endl;

	SpaceVehicle *temp_space_vehicle = dynamic_cast<SpaceVehicle*>(arr[1]);
	cout << endl << "SPACE VEHICLE'S mass through dynamic_cast : "
		<< temp_space_vehicle->get_sv_mass() << endl << endl;

	Rocket *temp_rocket = dynamic_cast<Rocket*>(arr[2]);
	cout << endl << "ROCKET'S fuel mass through dynamic_cast : "
		<< temp_rocket->getFuelmass() << endl << endl;

	Plane *temp_plane = dynamic_cast<Plane*>(arr[3]);
	cout << endl << "Number of PLANE'S Passengers through dynamic_cast : "
		 << temp_plane->getPassengersNumber() << endl << endl;

	for (int i = 0; i < arr_size; i++)
	{
		delete arr[i];
	}

	system("pause");
	return 0;
}