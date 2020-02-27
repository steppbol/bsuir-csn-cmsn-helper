#pragma once
#include <stdexcept>

using namespace std;

class SubtractingException : public logic_error
{
	public:
		explicit SubtractingException(const char*);
		~SubtractingException();
};