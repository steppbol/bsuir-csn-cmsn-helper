#include "MyString.h"
#include <iostream>
#include <assert.h>

using std::cout;
using std::endl;
using std::bad_alloc;

String::String()						//коструктор по умолчанию
{
	this->string_ = nullptr;
	this->size_ = 0;
}

String::String(const char* input_str)	//коструктор инициализации
{
	assert(stringLen(input_str) > 0);

	this->size_ = stringLen(input_str);
	this->string_ = new char[size_];
	memset(this->string_, 0, this->size_ * sizeof(char));

	for (int i = 0; i < this->size_; i++)
	{
		this->string_[i] = input_str[i];
	}
}

String::String(const String& object)	//коструктор копирования
{
	this->size_ = object.size_;
	this->string_ = new char[this->size_];
	for (int i = 0; i < this->size_; i++)
	{
		this->string_[i] = object.string_[i];
	}
}

String::~String()
{
	if (this->string_ != nullptr)
	{
		delete[] this->string_;
		this->string_ = nullptr;
	}
}

int String::getSize() const
{
	return size_;
}

int String::compare(const String &object) const
{
	int result, i = 0;
	while (this->string_[i] == object.string_[i])
	{
		if (this->string_[i] == '\0' && object.string_[i] == '\0') return 0;
		if (this->string_[i] == '\n' && object.string_[i] == '\n') return 0;
		i++;
	}
	return result = this->string_[i] > object.string_[i] ? 1 : -1;
}

int String::compare(const char*str) const
{
	int result, i = 0;
	while (this->string_[i] == str[i])
	{
		if (this->string_[i] == '\0' && str[i] == '\0') return 0;
		if (this->string_[i] == '\n' && str[i] == '\n') return 0;
		i++;
	}
	return result = this->string_[i] > str[i] ? 1 : -1;
}

String &String::operator()(const char* input_str)
{
	assert(stringLen(input_str) > 0);

	this->size_ = stringLen(input_str);
	this->string_ = new char[this->size_];
	memset(this->string_, 0, this->size_*sizeof(char));

	for (int i = 0; i < this->size_; i++)
	{
		this->string_[i] = input_str[i];
	}
	return *this;
}

String::operator char*()
{
	return this->string_;
}

String::operator int()
{
	return this->size_;
}

char& String::operator[](const int index) const
{
	if (index < 0 || index >= size_)
	{
		cout << "Error! Wrong index! The array does not contain one.\nThe first element is returned" << endl;
		return this->string_[0];
	}
	return this->string_[index];
}

String &String::operator=(const String &object)
{
	assert(&object != this && object.string_ != nullptr);
	if (this->string_ != nullptr) delete[] this->string_;

	this->size_ = object.size_;
	this->string_ = new char[this->size_];
	memset(this->string_, 0, this->size_ * sizeof(char));

	for (int i = 0; i < this->size_; i++)
	{
		this->string_[i] = object.string_[i];
	}
	return *this;
}

String &String::operator=(const char* str)
{
	assert(str != nullptr);
	if (this->string_ != nullptr) delete[] this->string_;

	this->size_ = stringLen(str);
	this->string_ = new char[this->size_];
	memset(this->string_, 0, this->size_ * sizeof(char));

	for (int i = 0; i < this->size_; i++)
	{
		this->string_[i] = str[i];
	}
	return *this;
}

bool String::operator==(const String &object) const
{
	int i = 0;
	while (this->string_[i] == object.string_[i])
	{
		if (this->string_[i] == '\0' && object.string_[i] == '\0') return true;
		if (this->string_[i] == '\n' && object.string_[i] == '\n') return true;
		i++;
	}
	return false;
}

bool String::operator==(char* str) const
{
	int i = 0;
	while (this->string_[i] == str[i])
	{
		if (this->string_[i] == '\0' && str[i] == '\0') return true;
		if (this->string_[i] == '\n' && str[i] == '\n') return true;
		i++;
	}
	return false;
}

bool String::operator>(const String &object) const
{
	int result, i = 0;
	while (this->string_[i] == object.string_[i])
	{
		if (this->string_[i] == '\0' && object.string_[i] == '\0') return false;
		if (this->string_[i] == '\n' && object.string_[i] == '\n') return false;
		i++;
	}
	return result = this->string_[i] > object.string_[i] ? true : false;
}

bool String::operator>(char* str) const
{
	int result, i = 0;
	while (this->string_[i] == str[i])
	{
		if (this->string_[i] == '\0' && str[i] == '\0') return false;
		if (this->string_[i] == '\n' && str[i] == '\n') return false;
		i++;
	}
	return result = this->string_[i] > str[i] ? true : false;
}

bool String::operator<(const String &object) const
{
	int result, i = 0;
	while (this->string_[i] == object.string_[i])
	{
		if (this->string_[i] == '\0' && object.string_[i] == '\0') return false;
		if (this->string_[i] == '\n' && object.string_[i] == '\n') return false;
		i++;
	}
	return result = this->string_[i] < object.string_[i] ? true : false;
}

bool String::operator<(char* str) const
{
	int result, i = 0;
	while (this->string_[i] == str[i])
	{
		if (this->string_[i] == '\0' && str[i] == '\0') return false;
		if (this->string_[i] == '\n' && str[i] == '\n') return false;
		i++;
	}
	return result = this->string_[i] < str[i] ? true : false;
}

String &String::operator+(const String &object)
{
	assert(&object != this && object.string_ != nullptr);

	if (this->string_ == nullptr)
	{
		*this = object;
		return *this;
	}
	else
	{
		String temp = *this;
		delete[] this->string_;

		this->size_ = temp.size_ + object.size_ - 1;
		this->string_ = new char[this->size_];
		memset(this->string_, 0, this->size_ * sizeof(char));

		int i = 0;
		for (; i < temp.size_ - 1; i++)
		{
			this->string_[i] = temp.string_[i];
		}
		for (int j = 0; i < this->size_; j++, i++)
		{
			this->string_[i] = object.string_[j];
		}

		return *this;
	}
}

String &String::operator+(char *str)
{
	assert(str != nullptr);
	
	if (this->string_ == nullptr)
	{
		*this = str;
		return *this;
	}
	else
	{
		String temp = *this;
		delete[] this->string_;

		this->size_ = temp.size_ + stringLen(str) - 1;
		this->string_ = new char[this->size_];
		memset(this->string_, 0, this->size_ * sizeof(char));

		int i = 0;
		for (; i < temp.size_ - 1; i++)
		{
			this->string_[i] = temp.string_[i];
		}
		for (int j = 0; i < this->size_; j++, i++)
		{
			this->string_[i] = str[j];
		}

		return *this;
	}
}

String &String::operator++()
{
	String temp = *this;
	 
	delete[] this->string_;

	this->size_++;
	this->string_ = new char[this->size_];
	memset(this->string_, 0, this->size_ * sizeof(char));

	int i = 0;
	for (; i < temp.size_; i++)
	{
		this->string_[i] = temp.string_[i];
	}
	i--;
	this->string_[i] = '!';

	return *this;
}

String &String::operator--(int empty)
{
	String temp = *this;

	delete[] this->string_;

	this->size_--;
	this->string_ = new char[this->size_];
	memset(this->string_, 0, this->size_ * sizeof(char));

	int i = 0;
	for (; i < this->size_ - 1; i++)
	{
		this->string_[i] = temp.string_[i];
	}

	return *this;
}

String &String::operator-(char symbol)
{
	if (this->string_ == nullptr)
	{
		*this = "Error! This string_ is empty";
		return *this;
	}
	else
	{
		int difference = 0;
		for (int index = 0; index < this->size_; index++)
		{
			if (this->string_[index] == symbol) difference++;
		}

		if (difference == 0) return *this;

		String temp = *this;
		delete[] this->string_;

		this->size_ -= difference;
		this->string_ = new char[this->size_];
		memset(this->string_, 0, this->size_ * sizeof(char));

		int i = 0, j = 0;
		while(i < this->size_)
		{
			if (temp.string_[j] == symbol)
			{
				j++;
				continue;
			}
			this->string_[i] = temp.string_[j];

			i++;
			j++;
		}
		return *this;
	}
}

ostream &operator<<(ostream & output, const String &object)
{
	output << object.string_ << endl;
	return output;
}

void* String::operator new(size_t memory_size)
{
	void *ptr = nullptr;

	ptr = malloc(memory_size);
	if (ptr == nullptr) throw bad_alloc();

	return ptr;
}

void* String::operator new[](size_t memory_size)
{
	void *ptr = nullptr;

	ptr = malloc(memory_size);
	if (ptr == nullptr)	throw bad_alloc();

	return ptr;
}

void String::operator delete(void* ptr)
{
	assert(ptr != nullptr);

	free(ptr);
	ptr = nullptr;
}

void String::operator delete[](void* ptr)
{
	assert(ptr != nullptr);

	free(ptr);
	ptr = nullptr;
}

int stringLen(const char* str)
{
	if (str == nullptr) return 0;

	int i = 0;

	for (; str[i] != '\0' && str[i] != '\n'; i++);
	if (i != 0) i++;

	return i;
}