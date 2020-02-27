#pragma once
#include "House.h"
class BlockOfFlats :
	public virtual House
{
public:
	BlockOfFlats();
	BlockOfFlats(int number, std::string street, int numberOfApaprtments);
	virtual void Show() override;
	~BlockOfFlats();
protected:
	int numberOfApaprtments;
};

