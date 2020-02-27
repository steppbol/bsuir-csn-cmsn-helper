#include "QueueSizeException.h"

QueueSizeException::QueueSizeException(const char* message) : out_of_range(message)
{
}

QueueSizeException::~QueueSizeException()
{
}