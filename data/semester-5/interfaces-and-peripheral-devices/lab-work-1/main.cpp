// Link with SetupAPI.Lib.
#pragma comment (lib, "setupapi.lib")
#include <iostream>
#include <windows.h>
#include <setupapi.h>
#include <string>

using namespace std;

string GetDevicePropertyRegistry(HDEVINFO device_info_set, SP_DEVINFO_DATA device_info_data, DWORD _property)
{
	DWORD reg_data_type;
	PBYTE buffer = nullptr;
	DWORD buffer_size = 0;

	while (!SetupDiGetDeviceRegistryProperty(
		device_info_set,
		&device_info_data,
		_property,											//Retrieves a REG_SZ string that contains the name of the device manufacturer.
		&reg_data_type,
		buffer,
		buffer_size,
		&buffer_size))
	{
		if (GetLastError() == ERROR_INSUFFICIENT_BUFFER)
		{
			if (buffer)
			{
				delete buffer;
				buffer = nullptr;
				buffer_size *= 2;
			}
			buffer = new BYTE[buffer_size + 1];
		}
		else break;
	}

	string result((char*)buffer);

	delete buffer;

	return result;
}

int main()
{
	HDEVINFO device_info_set;					//A handle to a device information set
	SP_DEVINFO_DATA device_info_data;			//Structure defines a device instance
	DWORD i;

	device_info_set = SetupDiGetClassDevs(
		NULL,
		"PCI",
		0,
		DIGCF_PRESENT |							//Return only devices that are currently present.
		DIGCF_ALLCLASSES						//Return a list of installed devices for the specified device setup classes or device interface classes.
	);											
										

	if (device_info_set == INVALID_HANDLE_VALUE)
	{
		exit(EXIT_FAILURE);
	}

	device_info_data.cbSize = sizeof(SP_DEVINFO_DATA);
	for (i = 0; SetupDiEnumDeviceInfo(device_info_set, i, &device_info_data); i++)
	{
		DWORD reg_data_type;
		string vendor;
		string device;
		DWORD buffer_size = 0;

		//SPDRP_MFG - Retrieves a REG_SZ string that contains the name of the device manufacturer.
		vendor = GetDevicePropertyRegistry(device_info_set, device_info_data, SPDRP_MFG);

		cout << "Vendor: " << vendor << endl;

		//SPDRP_DEVICEDESC - Retrieves a REG_SZ string that contains the description of a device.
		device = GetDevicePropertyRegistry(device_info_set, device_info_data, SPDRP_DEVICEDESC);

		cout << "Device: " << device << endl << endl;
	}

	if (GetLastError() != NO_ERROR && GetLastError() != ERROR_NO_MORE_ITEMS)
	{
		exit(EXIT_FAILURE);
	}

	SetupDiDestroyDeviceInfoList(device_info_set);

	system("pause");

	return 0;
}