#include <stdio.h>
#include <Windows.h>
#include <setupapi.h>
#include <locale.h> 
#include <iostream>
#include <wdmguid.h>
#include <devguid.h>
#include <iomanip>
#include <opencv2/core.hpp>
#include <opencv2/videoio.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/imgproc.hpp>

using namespace std;
using namespace cv;
#pragma comment(lib, "setupapi.lib")

int main() {

	setlocale(LC_ALL, "rus");
	SP_DEVINFO_DATA DeviceInfoData = { 0 };
	HDEVINFO DeviceInfoSet = SetupDiGetClassDevs(&GUID_DEVCLASS_CAMERA, "USB", NULL, DIGCF_PRESENT);
	if (DeviceInfoSet == INVALID_HANDLE_VALUE) {
		return 1;
	}
	DeviceInfoData.cbSize = sizeof(SP_DEVINFO_DATA);
	SetupDiEnumDeviceInfo(DeviceInfoSet, 0, &DeviceInfoData);
	PBYTE deviceID[256];
	PBYTE deviceName[256];
	PBYTE deviceMfg[256];
	SetupDiGetDeviceRegistryProperty(DeviceInfoSet, &DeviceInfoData, SPDRP_HARDWAREID, NULL, (PBYTE)deviceID, sizeof(deviceID), 0);
	SetupDiGetDeviceRegistryProperty(DeviceInfoSet, &DeviceInfoData, SPDRP_FRIENDLYNAME, NULL, (PBYTE)deviceName, sizeof(deviceName), 0);
	SetupDiGetDeviceRegistryProperty(DeviceInfoSet, &DeviceInfoData, SPDRP_MFG, NULL, (PBYTE)deviceMfg, sizeof(deviceMfg), 0);


	Mat src;
	VideoCapture cap(0 + CAP_DSHOW);
	system("cls");
	cout << left << setw(20) << "Name: " << (char*)deviceName << endl
		<< setw(20) << "Manufacturer: " << (char*)deviceMfg << endl
		<< setw(20) << "Camera HardwareID: " << (char*)deviceID << endl << endl;

	cap >> src;

	cout << "1. Get screen" << endl
		<< "2. Get video" << endl;

	int c;
	cin >> c;
	switch (c) {
	case 1:
		cap.read(src);
		imwrite("screen.jpg", src);
		break;
	case 2:
		int videoLength;
		cout << "How long video is in seconds?" << endl;
		cin >> videoLength;
		ShowWindow(GetConsoleWindow(), SW_HIDE);
		VideoWriter writer;
		int codec = VideoWriter::fourcc('P', 'I', 'M', '1');
		string filename = "./video.mpeg";
		writer.open(filename, codec, 10.0, src.size(), true);

		time_t timeStart;
		time(&timeStart);
		while (time(NULL) <= timeStart + videoLength)
		{
			writer.write(src);
			//imshow("Live", src);
			waitKey(60);
			cap.read(src);
		}
		break;
	}
	SetupDiDestroyDeviceInfoList(DeviceInfoSet);
	return 0;
}