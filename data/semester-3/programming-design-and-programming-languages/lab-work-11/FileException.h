#ifndef FILEEXCEPTION_H
#define FILEEXCEPTION_H
#include <stdexcept>

using namespace std;

class FileException : public exception
{
public:
	explicit FileException(const char*);
	explicit FileException(std::string);
};

#endif FILEEXCEPTION_H