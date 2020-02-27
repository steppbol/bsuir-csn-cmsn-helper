#pragma once
#include <stdexcept>

using namespace std;

class DeletingException : public logic_error
{
public:
	explicit DeletingException(const char*);
	~DeletingException();
};