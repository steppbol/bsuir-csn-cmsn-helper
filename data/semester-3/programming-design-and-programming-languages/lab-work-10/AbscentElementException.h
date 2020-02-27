#ifndef ABSCENTELEMENTEXCEPTION_H
#define ABSCENTELEMENTEXCEPTION_H
#include <stdexcept>

using namespace std;

class AbscentElementException : public invalid_argument //exception
{
public:
	explicit AbscentElementException(const char*);
	~AbscentElementException();
};

#endif ABSCENTELEMENTEXCEPTION_H