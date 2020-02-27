#include "FileException.h"

FileException::FileException(string message) : exception(message.c_str())
{
}

FileException::FileException(const char* message) : exception(message)
{
}

