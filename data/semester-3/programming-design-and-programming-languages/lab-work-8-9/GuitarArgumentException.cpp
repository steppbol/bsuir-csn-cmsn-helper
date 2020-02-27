#include "GuitarArgumentException.h"

GuitarArgumentException::GuitarArgumentException(string message) : exception(message.c_str())
{
}

GuitarArgumentException::GuitarArgumentException(const char* message) : exception(message)
{
}