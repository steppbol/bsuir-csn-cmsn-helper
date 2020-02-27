#include "AbscentElementException.h"

AbscentElementException::AbscentElementException(const char* message) : invalid_argument(message)
{
}

AbscentElementException::~AbscentElementException()
{
}