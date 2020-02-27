#pragma once
#include <iostream>
#include <string>
#include <cstring>
#include <fstream>

using namespace std;

class Cashmachine
{
public:
	float rate_USD;
	float rate_BYN;
	float rate_RUB;
	float balanceATM_USD;
	float balanceATM_BYN;
	float balanceATM_RUB;
	float balanceClient_USD;
	float balanceClient_BYN;
	float balanceClient_RUB;

	Cashmachine(int);
	Cashmachine();
	~Cashmachine();


	bool loadFromFileCurrency(fstream&);
	bool loadToFileCurrency(fstream&);
	void ouputInformationOfClient();
	int checkBalance(string, double);
	int withdrawMoneyWin(string, double);

};