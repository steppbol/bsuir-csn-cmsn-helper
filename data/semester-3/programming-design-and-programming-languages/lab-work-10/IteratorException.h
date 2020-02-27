#ifndef ITERATOREXCEPTION_H
#define ITERATOREXCEPTION_H
#include <stdexcept>

using namespace std;

class IteratorException : public logic_error //exception
{
public:
	explicit IteratorException(const char*);
	~IteratorException();
};

#endif ITERATOREXCEPTION_H