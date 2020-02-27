#pragma once
#include "Dormitory.h"
#include "Fachwerk.h"

class StudentsHouse : public Dormitory, public Fachwerk
{
public:
	StudentsHouse ();
	StudentsHouse(int number, std::string street, double yardSquare, int numberOfApaprtments,
		bool isAdmittedToStudents, Facade facade, std::string timetable);
	~StudentsHouse ();
	void Show() override;
private:
	std::string timetable;
};

