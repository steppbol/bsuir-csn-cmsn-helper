#pragma once
#include <string>
#include <iostream>
#include <vector>
#include <fstream>
#include <iomanip>
#include "GuitarArgumentException.h"
#include "GuitarFileException.h"

using namespace std;

class Guitar
{
public:
	typedef char byte;

	bool operator==(const Guitar &) const;

	int getYear();
	string getModel();
	double getMensur();
	
	void setYear(int);
	void setModel(string);
	void setMensur(double);

	static void writeToText(string, vector<Guitar>);
	static void writeToBinary(string, vector<Guitar>);

	static vector<Guitar> readText(string);
	static vector<Guitar> readTextBinary(string);
	static vector<Guitar> readTextReverse(string);
	static vector<Guitar> readBinary(string);
	static vector<Guitar> readBinaryReverse(string);

	static void deleteFromText(string, Guitar);
	static void deleteFromBinary(string, Guitar);

	Guitar();
	Guitar(string, int, double);
	Guitar(const Guitar&);
	~Guitar();
private:
	friend ostream& operator<<(ostream&, const Guitar&);
	friend istream& operator>>(istream&, Guitar&);

	bool checkYear(int);
	bool checkModel(string);
	bool checkMensur(double);

	void setSpecialSymbol();
	void clearModel();
	int getModelSize();

private:
	char _model[25];
	int _year;
	double _mensur;

	static const int _modelStringSize;
	static const int _yearStringSize;
	static const int _mensurStringSize;
	static const int _guitarSize;
	static const char _specialSymbol;
};