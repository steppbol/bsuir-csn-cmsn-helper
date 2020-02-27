#ifndef __NOTIFY_H
#define __NOTIFY_H

//
// Copied Macros from ntddk.h
//

#define CONTAINING_RECORD(address, type, field) ((type *)( \
                          (PCHAR)(address) - \
                          (ULONG_PTR)(&((type *)0)->field)))


#define InitializeListHead(ListHead) (\
    (ListHead)->Flink = (ListHead)->Blink = (ListHead))

#define RemoveHeadList(ListHead) \
    (ListHead)->Flink;\
    {RemoveEntryList((ListHead)->Flink)}

#define IsListEmpty(ListHead) \
    ((ListHead)->Flink == (ListHead))


#define RemoveEntryList(Entry) {\
    PLIST_ENTRY _EX_Blink;\
    PLIST_ENTRY _EX_Flink;\
    _EX_Flink = (Entry)->Flink;\
    _EX_Blink = (Entry)->Blink;\
    _EX_Blink->Flink = _EX_Flink;\
    _EX_Flink->Blink = _EX_Blink;\
    }

#define InsertTailList(ListHead,Entry) {\
    PLIST_ENTRY _EX_Blink;\
    PLIST_ENTRY _EX_ListHead;\
    _EX_ListHead = (ListHead);\
    _EX_Blink = _EX_ListHead->Blink;\
    (Entry)->Flink = _EX_ListHead;\
    (Entry)->Blink = _EX_Blink;\
    _EX_Blink->Flink = (Entry);\
    _EX_ListHead->Blink = (Entry);\
    }

typedef struct _DEVICE_INFO
{
   HANDLE       hDevice; // file handle
   HDEVNOTIFY   hHandleNotification; // notification handle
   TCHAR        DeviceName[MAX_PATH];// friendly name of device description
   TCHAR        DevicePath[MAX_PATH];//
   ULONG        SerialNo; // Serial number of the device.
   LIST_ENTRY   ListEntry;
} DEVICE_INFO, *PDEVICE_INFO;

#define ID_EDIT 1

LRESULT FAR PASCAL
WndProc (
    HWND hwnd,
    UINT message,
    WPARAM wParam,
    LPARAM lParam
    );

BOOLEAN EnumExistingDevices(
    HWND   hWnd
    );

BOOL HandleDeviceInterfaceChange(
    HWND hwnd,
    DWORD evtype,
    PDEV_BROADCAST_DEVICEINTERFACE dip
    );

BOOL HandleDeviceChange(
    HWND hwnd,
    DWORD evtype,
    PDEV_BROADCAST_HANDLE dhp
    );

BOOLEAN Cleanup(
    HWND hWnd
    );

BOOL
GetDeviceDescription(
    _In_ LPTSTR DevPath,
    _Out_writes_bytes_(OutBufferLen) PBYTE OutBuffer,
    _In_ ULONG OutBufferLen,
    _In_ PULONG SerialNo
    );

DWORD
WINAPI
WindowThreadRoutine(
	PVOID pvParam
	);

#endif

