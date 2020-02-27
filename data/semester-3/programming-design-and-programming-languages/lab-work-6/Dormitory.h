#pragma once
#include "BlockOfFlats.h"
class Dormitory :
	public BlockOfFlats
{
public:
	Dormitory();
	Dormitory(int number, std::string street, int numberOfApartments, bool isAdmittedToStudents);
	~Dormitory();
	virtual void Show() override;
protected:
	bool isAdmittedToStudents;
};

