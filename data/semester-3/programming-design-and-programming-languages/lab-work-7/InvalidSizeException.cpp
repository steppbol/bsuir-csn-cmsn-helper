#include "InvalidSizeException.h"

InvalidSizeException::InvalidSizeException(const char* message) : logic_error(message)
{
}

InvalidSizeException::~InvalidSizeException()
{
}