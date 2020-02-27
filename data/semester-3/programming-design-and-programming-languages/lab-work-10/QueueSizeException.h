#ifndef QUEUESIZEEXCEPTION_H
#define QUEUESIZEEXCEPTION_H
#include <stdexcept>

using namespace std;

class QueueSizeException : public out_of_range //exception
{
public:
	explicit QueueSizeException(const char*);
	~QueueSizeException();
};

#endif QUEUESIZEEXCEPTION_H