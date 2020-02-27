#include "ArgumentException.h"

ArgumentException::ArgumentException(string message) : invalid_argument(message.c_str())
{
}

ArgumentException::ArgumentException(const char* message) : invalid_argument(message)
{
}

