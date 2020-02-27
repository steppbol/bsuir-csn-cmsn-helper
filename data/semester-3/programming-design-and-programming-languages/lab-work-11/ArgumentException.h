#ifndef ARGUMENTEXCEPTION_H
#define ARGUMENTEXCEPTION_H
#include <stdexcept>

using namespace std;

class ArgumentException : public invalid_argument
{
public:
	explicit ArgumentException(const char*);
	explicit ArgumentException(std::string);
};

#endif ARGUMENTEXCEPTION_H