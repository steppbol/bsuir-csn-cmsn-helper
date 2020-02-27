#include "AddingException.h"

AddingException::AddingException(const char* message) : logic_error(message)
{
}

AddingException::~AddingException()
{
}