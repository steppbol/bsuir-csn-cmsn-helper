#include "DeletingException.h"

DeletingException::DeletingException(const char* message) : logic_error(message)
{
}

DeletingException::~DeletingException()
{
}