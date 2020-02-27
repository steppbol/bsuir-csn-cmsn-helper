#include "ejectutility.h"

//-------------------------------------------------
int EjectDevice(char DriveLetter)
{
	DriveLetter &= ~0x20; // uppercase

	if (DriveLetter < 'A' || DriveLetter > 'Z') {
		return 1;
	}

	char szRootPath[] = "X:\\";   // "X:\"  -> for GetDriveType
	szRootPath[0] = DriveLetter;

	char szVolumeAccessPath[] = "\\\\.\\X:";   // "\\\\.\X:"  -> to open the volume
	szVolumeAccessPath[4] = DriveLetter;

	long DeviceNumber = -1;

	// open the storage volume
	HANDLE hVolume = CreateFile(szVolumeAccessPath, 0, FILE_SHARE_READ | FILE_SHARE_WRITE, NULL, OPEN_EXISTING, NULL, NULL);
	if (hVolume == INVALID_HANDLE_VALUE) {
		return 1;
	}

	// get the volume's device number
	STORAGE_DEVICE_NUMBER sdn;
	DWORD dwBytesReturned = 0;
	long res = DeviceIoControl(hVolume, IOCTL_STORAGE_GET_DEVICE_NUMBER, NULL, 0, &sdn, sizeof(sdn), &dwBytesReturned, NULL);
	if (res) {
		DeviceNumber = sdn.DeviceNumber;
	}
	CloseHandle(hVolume);

	if (DeviceNumber == -1) {
		return 1;
	}

	// get the drive type which is required to match the device numbers correctely
	UINT DriveType = GetDriveType(szRootPath);

	// get the device instance handle of the storage volume by means of a SetupDi enum and matching the device number
	DEVINST DevInst = GetDrivesDevInstByDeviceNumber(DeviceNumber, DriveType);

	if (DevInst == 0) {
		return 1;
	}

	PNP_VETO_TYPE VetoType = PNP_VetoTypeUnknown;
	WCHAR VetoNameW[MAX_PATH];
	VetoNameW[0] = 0;
	int bSuccess = FALSE;

	// get drives's parent, e.g. the USB bridge
	DEVINST DevInstParent = 0;
	res = CM_Get_Parent(&DevInstParent, DevInst, 0);


	VetoNameW[0] = 0;

	res = CM_Request_Device_EjectW(DevInstParent, &VetoType, VetoNameW, MAX_PATH, 0);

	bSuccess = (res == CR_SUCCESS && VetoType == PNP_VetoTypeUnknown);

	if (bSuccess) {
		printf("Success\n");
		return 0;
	}

	printf("Failed!\n");

	printf("Result=0x%2X\n", res);

	if (VetoNameW[0]) {
		printf("VetoName=%ws", VetoNameW);
		printf("\n");
	}
	return 1;
}
//-----------------------------------------------------------

//----------------------------------------------------------------------
// returns the device instance handle of a storage volume or 0 on error
//----------------------------------------------------------------------
DEVINST GetDrivesDevInstByDeviceNumber(long DeviceNumber, UINT DriveType)
{
	GUID* guid;

	switch (DriveType) {
	case DRIVE_REMOVABLE:
		guid = (GUID*)&GUID_DEVINTERFACE_DISK;
		break;
	default:
		return 0;
	}

	// Get device interface info set handle for all devices attached to system
	HDEVINFO hDevInfo = SetupDiGetClassDevs(guid, NULL, NULL, DIGCF_PRESENT | DIGCF_DEVICEINTERFACE);

	if (hDevInfo == INVALID_HANDLE_VALUE) {
		return 0;
	}

	// Retrieve a context structure for a device interface of a device information set
	DWORD dwIndex = 0;
	long res;

	BYTE Buf[1024];
	PSP_DEVICE_INTERFACE_DETAIL_DATA pspdidd = (PSP_DEVICE_INTERFACE_DETAIL_DATA)Buf;
	SP_DEVICE_INTERFACE_DATA         spdid;
	SP_DEVINFO_DATA                  spdd;
	DWORD                            dwSize;

	spdid.cbSize = sizeof(spdid);

	while (TRUE) {
		res = SetupDiEnumDeviceInterfaces(hDevInfo, NULL, guid, dwIndex, &spdid);
		if (!res) {
			break;
		}

		dwSize = 0;
		SetupDiGetDeviceInterfaceDetail(hDevInfo, &spdid, NULL, 0, &dwSize, NULL); // check the buffer size

		if (dwSize != 0 && dwSize <= sizeof(Buf)) {

			pspdidd->cbSize = sizeof(*pspdidd);

			ZeroMemory(&spdd, sizeof(spdd));
			spdd.cbSize = sizeof(spdd);

			long res = SetupDiGetDeviceInterfaceDetail(hDevInfo, &spdid, pspdidd, dwSize, &dwSize, &spdd);
			if (res) {
				// open the disk
				HANDLE hDrive = CreateFile(pspdidd->DevicePath, 0, FILE_SHARE_READ | FILE_SHARE_WRITE, NULL, OPEN_EXISTING, 0, NULL);
				if (hDrive != INVALID_HANDLE_VALUE) {
					// get its device number
					STORAGE_DEVICE_NUMBER sdn;
					DWORD dwBytesReturned = 0;
					res = DeviceIoControl(hDrive, IOCTL_STORAGE_GET_DEVICE_NUMBER, NULL, 0, &sdn, sizeof(sdn), &dwBytesReturned, NULL);
					if (res) {
						if (DeviceNumber == (long)sdn.DeviceNumber) {  // match the given device number with the one of the current device
							CloseHandle(hDrive);
							SetupDiDestroyDeviceInfoList(hDevInfo);
							return spdd.DevInst;
						}
					}
					CloseHandle(hDrive);
				}
			}
		}
		dwIndex++;
	}

	SetupDiDestroyDeviceInfoList(hDevInfo);

	return 0;
}
//-----------------------------------------------------------
