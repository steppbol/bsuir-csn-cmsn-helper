#ifndef EJECT_UTILITY
#define EJECT_UTILITY

#include <stdio.h>
#include <windows.h>
#include <Setupapi.h>
#include <winioctl.h>
#include <winioctl.h>
#include <cfgmgr32.h>

//-------------------------------------------------
DEVINST GetDrivesDevInstByDeviceNumber(long DeviceNumber, UINT DriveType);
int EjectDevice(char DriveLetter);
//-------------------------------------------------

#endif
