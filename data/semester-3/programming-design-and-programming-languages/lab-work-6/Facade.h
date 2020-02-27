#pragma once
#include <iostream>
enum Facade
{
	CommonFacade,
	GlassFacade,
	CPBFacade,
	AeratedConcreteFacade
};

inline std::ostream& operator<<(std::ostream & os, Facade source) {
	switch (source)
	{
	case CommonFacade:
		os << "CommonFacade";
		break;
	case GlassFacade:
		os << "GlassFacade";
		break;
	case CPBFacade:
		os << "CPBFacade";
		break;
	case AeratedConcreteFacade:
		os << "AeratedConcreteFacade";
		break;
	default:
		break;
	}
	return os;
}