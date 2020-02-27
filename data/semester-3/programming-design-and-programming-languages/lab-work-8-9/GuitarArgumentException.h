#pragma once
#include <exception>
#include <string>

using namespace std;

class GuitarArgumentException : public exception
{
public:
	explicit GuitarArgumentException(const char*);
	explicit GuitarArgumentException(const string);
};