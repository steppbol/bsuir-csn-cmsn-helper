#pragma once
#include <exception>
#include <string>

using namespace std;

class GuitarFileException : public exception
{
public:
	explicit GuitarFileException(const char*);
	explicit GuitarFileException(const string);
};
