#ifndef BATTERY_CHARGE_STATUS
#define BATTERY_CHARGE_STATUS

#include <string>

using namespace std;

enum BatteryChargeStatus
{
	High = 1,
	Low = 2,
	Critical = 4,
	Charging = 8,
	NoSystemBattery = 128,
	UnknownChargeLevelStatus = 255
};

std::string GetBatteryChargeStatusName(int status)
{
	switch (status)
	{
	case BatteryChargeStatus::High:
	{
		return string("High");
	}
	case BatteryChargeStatus::Low:
	{
		return string("Low");
	}
	case BatteryChargeStatus::Critical:
	{
		return string("Critical");
	}
	case BatteryChargeStatus::Charging:
	{
		return string("Charging");
	}
	case BatteryChargeStatus::NoSystemBattery:
	{
		return string("No System Battery");
	}
	case BatteryChargeStatus::UnknownChargeLevelStatus:
	{
		return string("Unknown");
	}
	default:
		return string("Invalid status");
	}
}

#endif