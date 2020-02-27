#ifndef BATTERY_H
#define BATTERY_H

#include <Windows.h>
#include <iostream>

#include "ACLineStatus.h"
#include "BatteryChargeStatus.h"

using namespace std;

const unsigned int MAX_VALUE = 4294967295;

class Battery
{
public:
	friend ostream& operator<<(ostream&, const Battery&);

	Battery()
	{
		this->UpdateStats();
	}

	~Battery()
	{

	}

	void UpdateStats()
	{
		SYSTEM_POWER_STATUS battery_descriptor;

		if (!GetSystemPowerStatus(&battery_descriptor)) 
		{
			throw system_error(error_code(GetLastError(), system_category()), "Error while retreiving SYSTEM_POWER_STATUS");
		}

		this->ac_line_status_ = (ACLineStatus)battery_descriptor.ACLineStatus;
		this->charge_status_ = (BatteryChargeStatus)battery_descriptor.BatteryFlag;
		this->battery_lifetime_ = battery_descriptor.BatteryLifeTime;
		this->life_percent_ = (unsigned int)(battery_descriptor.BatteryLifePercent);
		this->saver_is_on_ = (unsigned int)battery_descriptor.SystemStatusFlag == 1;
	}

private:
	ACLineStatus ac_line_status_;
	BatteryChargeStatus charge_status_;
	unsigned int life_percent_;
	DWORD battery_lifetime_;
	bool saver_is_on_;
};

ostream& operator<<(ostream& stream, const Battery& battery)
{
	stream << "AC power status: ";
	stream << GetACLineStatusName(battery.ac_line_status_);
	stream << endl;

	stream << "Battery charge status: ";
	stream << GetBatteryChargeStatusName(battery.charge_status_);
	stream << endl;

	stream << "Battery life percentage: ";
	if (battery.life_percent_ == 255)
	{
		stream << "Unknown";
	}
	else
	{
		stream << battery.life_percent_ << "%";
	}
	stream << endl;

	stream << "Battery lifetime: ";
	if (battery.battery_lifetime_ == MAX_VALUE)
	{
		stream << "Invalid time";
	}
	else
	{
		stream << battery.battery_lifetime_ / 60 << " minutes";
	}
	stream << endl;

	stream << "Battery saver is on: ";
	stream << std::boolalpha << battery.saver_is_on_;
	stream << endl;

	return stream;
}

#endif
