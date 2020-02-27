#include <iostream>
#include "Shuttle.h"

using namespace std;

int main()
{
	Shuttle unit(1200, 10000, 900, false, 6, LIQUID_PROPELLANTS);

	unit.showShuttle();

	system("pause");
	return 0;
}