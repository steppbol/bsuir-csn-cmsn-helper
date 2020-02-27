#pragma once
#include <stdexcept>

using namespace std;

class InvalidSizeException : public logic_error
{
public:
	explicit InvalidSizeException(const char*);
	~InvalidSizeException();
};
