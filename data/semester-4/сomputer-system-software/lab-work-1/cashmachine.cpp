#include "cashmachine.h"

Cashmachine::Cashmachine(int check)
{
	this->rate_USD = 1.96;
	this->rate_BYN = 1;
	this->rate_RUB = 3.45;
	this->balanceATM_USD = 2000;
	this->balanceATM_BYN = 2000;
	this->balanceATM_RUB = 2000;
	this->balanceClient_USD = 300;
	this->balanceClient_BYN = 310;
	this->balanceClient_RUB = 200;
}

Cashmachine::Cashmachine() {}

Cashmachine::~Cashmachine() {}

bool Cashmachine::loadFromFileCurrency(fstream& fileName)
{
	fileName >> this->rate_USD;
	fileName >> this->rate_BYN;
	fileName >> this->rate_RUB;
	fileName >> this->balanceATM_USD;
	fileName >> this->balanceATM_BYN;
	fileName >> this->balanceATM_RUB;
	fileName >> this->balanceClient_USD;
	fileName >> this->balanceClient_BYN;
	fileName >> this->balanceClient_RUB;
	return true;
}

bool Cashmachine::loadToFileCurrency(fstream& fileName)
{
	fileName << this->rate_USD << '\n';
	fileName << this->rate_BYN << '\n';
	fileName << this->rate_RUB << '\n';
	fileName << this->balanceATM_USD << '\n';
	fileName << this->balanceATM_BYN << '\n';
	fileName << this->balanceATM_RUB << '\n';
	fileName << this->balanceClient_USD << '\n';
	fileName << this->balanceClient_BYN << '\n';
	fileName << this->balanceClient_RUB << '\n';
	return true;
}

void Cashmachine::ouputInformationOfClient()
{
	cout << "==============================================\r\n";
	cout << "      INFORMATION ABOUT CLIENT:\r\n";
	cout << "                USD: " << this->balanceClient_USD << "\r\n";
	cout << "                BYN: " << this->balanceClient_BYN << "\r\n";
	cout << "                RUB: " << this->balanceClient_RUB << "\r\n";
	cout << "==============================================" << "\r\n";
	cout << "Exchange rate for today: USD:" << this->rate_USD << ", RUB:" << this->rate_RUB << "\r\n";
}

int Cashmachine::checkBalance(string currency, double amountOfMoney)
{
	if (amountOfMoney < 0) return 3;

	if (currency == "USD")
	{
		if (amountOfMoney > this->balanceClient_USD) return 1;
		if (amountOfMoney > this->balanceATM_USD) return 2;
		else return 0;
	}
	if (currency == "BYN")
	{
		if (amountOfMoney > this->balanceClient_BYN) return 1;
		if (amountOfMoney > this->balanceATM_BYN) return 2;
		else return 0;
	}
	if (currency == "RUB")
	{
		if (amountOfMoney > this->balanceClient_RUB) return 1;
		if (amountOfMoney > this->balanceATM_RUB) return 2;
		else return 0;
	}
	if (currency != "USD" && currency != "BYN" && currency != "RUB") return 3;
}

int Cashmachine::withdrawMoneyWin(string currency, double amountOfMoney)
{
	if (checkBalance(currency, amountOfMoney) == 1) return 1;
	if (checkBalance(currency, amountOfMoney) == 2) return 2;
	if (checkBalance(currency, amountOfMoney) == 3) return 3;
	if (currency == "USD")
	{
		this->balanceATM_USD -= amountOfMoney;
		this->balanceClient_USD -= amountOfMoney;
	}
	if (currency == "BYN")
	{
		this->balanceATM_BYN -= amountOfMoney;
		this->balanceClient_BYN -= amountOfMoney;
	}
	if (currency == "RUB")
	{
		this->balanceATM_RUB -= amountOfMoney;
		this->balanceClient_RUB -= amountOfMoney;
	}
	return 0;
}
