#ifndef ACLINE_STATUS_H
#define ACLINE_STATUS_H

#include <string>

using namespace std;

enum ACLineStatus
{
	Offline = 0,
	Online = 1,
	UnknownStatus = 255
};

string GetACLineStatusName(int status)
{
	switch (status)
	{
	case ACLineStatus::Offline:
	{
		return string("Offline");
	}
	case ACLineStatus::Online:
	{
		return string("Online");
	}
	case ACLineStatus::UnknownStatus:
	{
		return string("Unknown");
	}
	default: 
		return string("Invalid status");
	}
}

#endif