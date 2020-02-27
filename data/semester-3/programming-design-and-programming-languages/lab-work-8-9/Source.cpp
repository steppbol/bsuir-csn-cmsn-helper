#include "Guitar.h"

using namespace std;

int main() 
{
	try 
	{
		string binaryFile = "binary";
		string textFile = "text.txt";
		string textBinaryFile = "textBinary.txt";

		vector<Guitar> guitars;
		guitars.push_back(Guitar("Jackson KV JS32", 2016, 26.5));
		guitars.push_back(Guitar("Gibson LP Standart", 2017, 24.5));
		guitars.push_back(Guitar("Washburn", 1999, 26.5));

		Guitar::writeToBinary(binaryFile, guitars);

		vector<Guitar> guitarsFromBinary = Guitar::readBinary(binaryFile);

		cout << "BINARY FILE" << endl << endl;
		for each(Guitar guitar in guitarsFromBinary)
			cout << guitar;

		vector<Guitar> guitars_2;
		guitars_2.push_back(Guitar("Dean", 2010, 26.5));
		guitars_2.push_back(Guitar("Fender", 1965, 26.5));
		guitars_2.push_back(Guitar("Ibanez", 2000, 26.5));

		Guitar::writeToText(textFile, guitars_2);

		vector<Guitar> guitarsFromText = Guitar::readText(textFile);

		cout << endl << "TEXT FILE (text mode)" << endl << endl;
		for each(Guitar guitar in guitarsFromText)
			cout << guitar;

		vector<Guitar> guitars_3;
		guitars_3.push_back(Guitar("Gibson", 2010, 24.5));
		guitars_3.push_back(Guitar("Yamaha", 2003, 26.5));
		guitars_3.push_back(Guitar("Cort", 2012, 26.5));

		Guitar::writeToText(textBinaryFile, guitars_3);

		vector<Guitar> guitarsFromBinaryText = Guitar::readTextBinary(textBinaryFile);

		cout << endl << "TEXT FILE (binary mode)" << endl << endl;
		for each(Guitar guitar in guitarsFromBinaryText)
			cout << guitar;

		vector<Guitar> guitarsFromReverseText = Guitar::readTextReverse(textFile);

		cout << endl << "TEXT FILE REVERSE (text mode)" << endl << endl;
		for each(Guitar guitar in guitarsFromReverseText)
			cout << guitar;

		vector<Guitar> guitarsFromBinaryReverse = Guitar::readBinaryReverse(binaryFile);

		cout << endl << endl << "BINARY FILE REVERSE" << endl << endl;
		for each(Guitar guitar in guitarsFromBinaryReverse)
			cout << guitar;

		Guitar::deleteFromText(textFile, guitars_2[2]);
		vector<Guitar> guitarsFromText_2 = Guitar::readText(textFile);

		cout << endl << "TEXT FILE AFTER DELETING (text mode)" << endl << endl;
		for each(Guitar guitar in guitarsFromText_2)
			cout << guitar;

		Guitar::deleteFromBinary(binaryFile, guitars[0]);
		vector<Guitar> guitarsFromBinary_2 = Guitar::readBinary(binaryFile);

		cout << endl << "BINARY FILE AFTER DELETING" << endl << endl;
		for each(Guitar guitar in guitarsFromBinary_2)
			cout << guitar;
		
		system("pause");
	}
	catch (GuitarFileException &exception) 
	{
		cout << exception.what() << endl;
		cin.ignore();
	}
	catch (GuitarArgumentException &exception)
	{
		cout << exception.what() << endl;
		cin.ignore();
	}

	return 0;
}