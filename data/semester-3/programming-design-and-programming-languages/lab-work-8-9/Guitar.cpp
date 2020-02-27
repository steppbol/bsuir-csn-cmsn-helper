#include "Guitar.h"
#include "SpecialSymbol.h"
#include "Functions.h"

const int Guitar::_modelStringSize = 24;
const int Guitar::_yearStringSize = 4;
const int Guitar::_mensurStringSize = 4;
const int Guitar::_guitarSize = 33;
const char Guitar::_specialSymbol = '@';

void Guitar::setSpecialSymbol()
{
	for (int i = 0; i < _modelStringSize; i++)
		this->_model[i] = _specialSymbol;
}

void Guitar::clearModel()
{
	for (int i = 0; i < _modelStringSize; i++)
		this->_model[i] = 0;
}

Guitar::Guitar():
	_year(2017), _mensur(26.5)
{
	this->clearModel();
	strcpy_s(this->_model, "Jackson JS32T");
}

Guitar::Guitar(string model, int year, double mensur)
{
	if (!this->checkModel(model)) throw "Invalid model name! :constructor:";
	if (!this->checkYear(year)) throw "Invalid year! :constructor:";
	if (!this->checkMensur(mensur)) throw "Invalid mensur value! :constructor:";

	this->clearModel();
	strcpy_s(this->_model, model.c_str());
	this->_year = year;
	this->_mensur = mensur;
}

Guitar::Guitar(const Guitar &guitar)
{
	this->clearModel();
	strcpy_s(this->_model, guitar._model);

	this->_year = guitar._year;
	this->_mensur = guitar._mensur;
}

bool Guitar::operator==(const Guitar &object) const
{
	int i = 0;
	while (this->_model[i] == object._model[i])
	{
		if (this->_model[i] != object._model[i] ) return false;
		i++;
	}

	if (this->_year != object._year) return false;
	if (this->_mensur != object._mensur) return false;

	return true;
}

Guitar::~Guitar()
{
}

string Guitar::getModel()
{
	return string(this->_model);
}

int Guitar::getYear()
{
	return this->_year;
}

double Guitar::getMensur()
{
	return this->_mensur;
}

void Guitar::setModel(string model)
{
	if (!this->checkModel(model)) throw "Invalid model name! :setter:";
	strcpy_s(this->_model, model.c_str());
}

void Guitar::setYear(int year)
{
	if(!this->checkYear(year)) throw "Invalid year! :setter:";
	this->_year = year;
}

void Guitar::setMensur(double mensur)
{
	if(!this->checkMensur(mensur)) throw "Invalid mensur value! :setter:";
	this->_mensur = mensur;
}

int Guitar::getModelSize()
{
	int i = 0;

	while (this->_model[i] != 0 && this->_model[i] != '\n') i++;

	return i;
}

void Guitar::writeToText(string file, vector<Guitar> guitars)
{
	ofstream stream(file, ios::out | ios::trunc);
	if (!stream.is_open()) throw GuitarFileException("Cannot open text file for writing! :writeToText:");

	try 
	{
		for each (Guitar guitar in guitars)
		{
			int diff = _modelStringSize + 1 - guitar.getModelSize();

			stream << guitar._model << addspecial(diff - 1);
			stream << " " << guitar._year;
			stream << " " << guitar._mensur;
		}
	}
	catch (...) 
	{
		stream.close();
		throw GuitarFileException("Error while writing to text file! :writeToText:");
	}

	stream.close();
}

vector<Guitar> Guitar::readText(string file)
{
	vector<Guitar> guitars;
	ifstream stream(file, ios::in);

	if (!stream.is_open()) throw GuitarFileException("Cannot open text file for reading! :readText:");

	try
	{
		while (!stream.eof()) 
		{
			Guitar guitar;
			stream >> guitar;
			guitars.push_back(guitar);
		}
	}
	catch (...) 
	{
		stream.close();
		throw GuitarArgumentException("Text file contains invalid guitar's arguments! :readText:");
	}

	stream.close();

	return guitars;
}

vector<Guitar> Guitar::readTextReverse(string file)
{
	vector<Guitar> guitars = vector<Guitar>();
	ifstream stream(file, ios::in);
	if (!stream.is_open()) throw GuitarFileException("Cannot open text file for reading! :readTextReverse:");

	int offset = _modelStringSize + _yearStringSize + _mensurStringSize + 2;

	try
	{
		stream.seekg(-offset, stream.end);

		int amount = 1;
		while (static_cast<int>(stream.tellg()) != -1)
		{
			Guitar guitar;
			stream >> guitar;
			guitars.push_back(guitar);

			amount++;
			stream.seekg(-amount * offset, stream.end);
		}
	}
	catch (...)
	{
		throw GuitarArgumentException("Text file contains invalid guitar's arguments! :readTextReverse:");
	}

	stream.close();

	return guitars;
}

vector<Guitar> Guitar::readTextBinary(string file)
{
	vector<Guitar> guitars;
	ifstream stream(file, ios::in | ios::binary);
	if (!stream.is_open()) throw GuitarFileException("Cannot open binary file for reading! :readTextBinary:");

	stream.seekg(0, ios::end);

	int blockSize = _modelStringSize + _yearStringSize + _mensurStringSize + 2;
	int size = (static_cast<int>(stream.tellg())) / blockSize;

	stream.seekg(0, ios::beg);

	Guitar guitar;
	try
	{
		for (int i = 0; i < size; i++)
		{
			char year[_yearStringSize + 1] = { 0 };
			char mensur[_mensurStringSize + 1] = { 0 };
			char model[_modelStringSize + 1] = { 0 };
			char* spaceBuff;

			stream.read((byte*)&model, _modelStringSize);
			stream.read((byte*)&spaceBuff, 1);
			stream.read((byte*)&year, _yearStringSize);
			stream.read((byte*)&spaceBuff, 1);
			stream.read((byte*)&mensur, _mensurStringSize);

			string temp(model);
			int start = temp.find(guitar._specialSymbol);
			if (start != -1)
				temp.erase(start, temp.length() - 1);

			guitar.setModel(temp);
			guitar.setYear(atoi(year));
			guitar.setMensur(atod(mensur));

			guitars.push_back(guitar);
		}
	}
	catch (...)
	{
		stream.close();
		throw GuitarFileException("Binary file contains invalid guitar's arguments! :readTextBinary:");
	}

	stream.close();

	return guitars;
}

void Guitar::writeToBinary(string file, vector<Guitar> guitars)
{
	ofstream stream(file, ios::out | ios::trunc | ios::binary);
	if (!stream.is_open()) throw GuitarFileException("Cannot open binary file for writing! :writeToBinary:");

	try
	{
		for each(Guitar guitar in guitars)
			stream.write((byte*)&guitar,sizeof(Guitar));
	}
	catch (...)
	{
		stream.close();
		throw GuitarFileException("Error while writing to binary file! :writeToBinary:");
	}

	stream.close();
}

vector<Guitar> Guitar::readBinaryReverse(string file)
{
	vector<Guitar> guitars;
	ifstream stream(file, ios::in | ios::binary);
	if (!stream.is_open()) throw GuitarFileException("Cannot open binary file for reading! :readBinaryReverse:");

	stream.seekg(0, ios::end);

	int amount = stream.tellg() / sizeof(Guitar);
	int offset = sizeof(Guitar);

	try
	{
		stream.seekg(-offset, stream.end);

		int i = 1;
		while(i <= amount)
		{
			Guitar guitar;
			stream.read((byte*)&guitar, sizeof(guitar));
			guitars.push_back(guitar);

			i++;
			stream.seekg(-i * offset, stream.end);
		}
	}
	catch (...)
	{
		stream.close();
		throw GuitarFileException("Binary file contains invalid guitar's arguments! :readBinaryReverse:");
	}

	stream.close();
	
	return guitars;
}

vector<Guitar> Guitar::readBinary(string file)
{
	vector<Guitar> guitars;
	ifstream stream(file, ios::in | ios::binary);
	if (!stream.is_open()) throw GuitarFileException("Cannot open binary file for reading! :readBinary:");

	stream.seekg(0, ios::end);
	int amount = stream.tellg() / sizeof(Guitar);
	stream.seekg(0, ios::beg);

	Guitar guitar;
	try
	{
		for (int i = 0; i < amount; i++)
		{
			stream.read((byte*)&guitar, sizeof(Guitar));
			guitars.push_back(guitar);
		}
	}
	catch (...)
	{
		stream.close();
		throw GuitarFileException("Binary file contains invalid guitar's arguments! :readBinary:");
	}

	stream.close();

	return guitars;
}

void Guitar::deleteFromText(string file, Guitar keyGuitar)
{
	fstream stream(file, ios::in);
	fstream tempStream("Temp.txt", ios::out);

	if (!stream.is_open()) throw GuitarFileException("Cannot open text file for reading! :deleteFromText:");

	try
	{
		Guitar guitar;

		while (!stream.eof())
		{
			stream >> guitar;

			if (guitar == keyGuitar)
				continue;

			int diff = _modelStringSize + 1 - guitar.getModelSize();
			tempStream << guitar._model << addspecial(diff - 1);
			tempStream << " " << guitar._year;
			tempStream << " " << guitar._mensur;
		}

		stream.close();
		tempStream.close();
		remove(file.c_str());
		rename("Temp.txt", file.c_str());

		return;
	}
	catch (...)
	{
		stream.close();
		throw GuitarArgumentException("Text file contains invalid guitar's arguments! :deleteFromText:");
	}
}

void Guitar::deleteFromBinary(string file, Guitar keyGuitar)
{
	fstream stream(file, ios::in);
	fstream tempStream("Temp", ios::out);

	if (!stream.is_open()) throw GuitarFileException("Cannot open binary file for reading! :deleteFromBinary:");

	try
	{
		stream.seekg(0, ios::end);
		int amount = stream.tellg() / sizeof(Guitar);
		stream.seekg(0, ios::beg);

		Guitar guitar;

		int i = 0;
		while (i < amount)
		{
			stream.read((byte*)&guitar, sizeof(Guitar));

			if (guitar == keyGuitar)
			{
				amount--;
				continue;
			}

			tempStream.write((byte*)&guitar, sizeof(Guitar));
			i++;
		}

		stream.close();
		tempStream.close();
		remove(file.c_str());
		rename("Temp", file.c_str());

		return;
	}
	catch (...)
	{
		stream.close();
		throw GuitarArgumentException("Binary file contains invalid guitar's arguments! :deleteFromBinary:");
	}
}

ostream& operator<<(ostream& stream, const Guitar& guitar)
{
		stream << guitar._model << " " << guitar._year << " "
			<< guitar._mensur << " " << endl;

	return stream;
}

istream& operator>>(istream& stream, Guitar& guitar)
{
	int year;
	double mensur;
	string model;
	
	stream >> model >> year >> mensur;

	int start = model.find(guitar._specialSymbol);
	if (start != -1)
		model.erase(start, model.length() - 1);

	guitar.setModel(model);
	guitar.setYear(year);
	guitar.setMensur(mensur);

	return stream;
}

bool Guitar::checkModel(string model)
{
	if (!model.empty() && model.length() > 1 && model.length() <= _modelStringSize)
		return true;
	else
		return false;
}

bool Guitar::checkYear(int year)
{
	if (year >= 1600 && year <= 2017)
		return true;
	else
		return false;
}

bool Guitar::checkMensur(double mensur)
{
	if (mensur >= 24.5 && mensur <= 26.5)
		return true;
	else
		return false;
}