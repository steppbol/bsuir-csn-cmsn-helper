#include "GuitarFileException.h"

GuitarFileException::GuitarFileException(string message) : exception(message.c_str())
{
}

GuitarFileException::GuitarFileException(const char* message) : exception(message)
{
}

