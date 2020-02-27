#include "IteratorException.h"

IteratorException::IteratorException(const char* message) : logic_error(message)
{
}

IteratorException::~IteratorException()
{
}