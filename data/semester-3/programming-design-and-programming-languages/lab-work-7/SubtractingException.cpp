#include "SubtractingException.h"

SubtractingException::SubtractingException(const char* message) : logic_error(message)
{
}

SubtractingException::~SubtractingException()
{
}