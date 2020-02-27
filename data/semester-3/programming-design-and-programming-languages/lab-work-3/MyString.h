#pragma once

#include <ostream>

using std::ostream;

class String
{
	public:
		friend ostream &operator<<(ostream &, const String &);

		String();					//коструктор по умолчанию
		String(const char*);		//коструктор инициализации
		String(const String &);		//коструктор копирования
		~String();

		String &operator()(const char*);
		char& operator[](const int) const;
		String &operator=(const String &);
		String &operator=(const char*);

		bool operator==(const String &) const;
		bool operator==(char*) const;
		bool operator>(const String &) const;
		bool operator>(char*) const;
		bool operator<(const String &) const;
		bool operator<(char*) const;

		String &operator+(const String &);
		String &operator+(char*);

		String &operator++();		// перфиксный
		String &operator--(int);	// постфиксный
		String &operator-(char);	// все 3 бесполезны :D

		void* operator new(size_t);
		void* operator new[](size_t);
		void operator delete(void*);
		void operator delete[](void*);

		operator char*();
		operator int();

		int getSize() const;
		int compare(const String &) const;
		int compare(const char*) const;
	private:
		char* string_;
		int size_;
};

int stringLen(const char*);