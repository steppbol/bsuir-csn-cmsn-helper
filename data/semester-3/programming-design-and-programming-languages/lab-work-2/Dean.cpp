#pragma once
#include "Dean.h"
#include <iostream>

void Dean::set_rate( Student & instance, double rate) {
	if (rate <= 10)
		instance.rate = rate;
	else
		cout << "ERROR: incorrect input" << endl;
	return;
}

double Dean::get_rate(const Student & instance) {
	return instance.rate;
}

