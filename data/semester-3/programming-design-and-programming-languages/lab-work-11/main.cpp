#include <iostream>
#include <string>
#include <vector>
#include <fstream>
#include <iomanip>

#include "FileException.h"
#include "ArgumentException.h"
#include "Functions.h"

using namespace std;

int main()
{
	try
	{
		string exodusFile = "Exodus_File.txt";
		string resultFile = "Result_File.txt";

		//reading from exodus file
		vector<string> exodusStrings;
		ifstream istream(exodusFile, ios::in);
		if (!istream.is_open()) throw FileException("Cannot open text file for reading!");
		while (!istream.eof())
		{
			string stringUnit;
			getline(istream, stringUnit);
			exodusStrings.push_back(stringUnit);
			stringUnit.clear();
		}
		istream.close();

		//parcing the strings
		vector<double> resultValues;
		vector<string>::iterator iteratorUnit;
		for (iteratorUnit = exodusStrings.begin(); iteratorUnit != exodusStrings.end(); iteratorUnit++)
			resultValues.push_back(parceString(iteratorUnit->c_str()));

		//writing to result file
		ofstream ostream(resultFile, ios::out | ios::trunc);
		if (!ostream.is_open()) throw FileException("Cannot open text file for writing!");

		for each (double value in resultValues)
		{
			ostream << "Result: " << value << endl;
		}

		ostream.close();
	}
	catch (FileException &exception)
	{
		cout << endl << exception.what() << endl;
		cin.ignore();
	}
	catch (ArgumentException &exception)
	{
		cout << endl << exception.what() << endl;
		cin.ignore();
	}

	return EXIT_SUCCESS;
}