#pragma once
#include <stdexcept>

using namespace std;

class AddingException : public logic_error //exception
{
	public:
		explicit AddingException(const char*);
		~AddingException();
};
