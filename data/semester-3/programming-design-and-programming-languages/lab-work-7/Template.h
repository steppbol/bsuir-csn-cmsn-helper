#pragma once
#include <iostream>
#include <ctime>
#include <cassert>
#include <cstdarg>
#include <stdexcept>
#include "ExceptionFunctionsHeader.h"
#include "SubtractingException.h"
#include "AddingException.h"
#include "InvalidSizeException.h"
#include "DeletingException.h"



template <typename Type>
class Array
{
public:
	void operator delete(void* ptr)
	{
		if (this->_ptr == nullptr) throw DeletingException("Can not delete nullptr");

		free(this->_ptr);
		this->_ptr = nullptr;
	}
	void operator delete[](void* ptr)
	{
		if (this->_ptr == nullptr) throw DeletingException("Can not delete nullptr");

		free(this->_ptr);
		this->_ptr = nullptr;
	}
	Array operator()(int size, Type element...)
	{
		if (size == 0) return *this;
		if (size < 0) throw InvalidSizeException("Array's size can not be negative");

		if (this->_ptr != nullptr) delete this->_ptr;

		this->_size = size;
		this->_ptr = new Type[this->_size];

		Type temp = element;
		va_list temp_ptr;
		va_start(temp_ptr, element);

		this->_ptr[0] = element;
		for (int i = 1; i < this->_size; i++)
		{
			this->_ptr[i] = va_arg(temp_ptr, Type);
		}

		va_end(temp_ptr);
		return *this;
	}
	Type& operator[](const int index) const
	{
		if (!this->_size) throw domain_error("Can not watch empty array");

		if (index < 0 || index >= _size) throw out_of_range("The value is out of range");

		return this->_ptr[index];
	}
	Array& operator=(Array &object)
	{
		if (&object != this)
		{
			if (this->_size != object._size)
			{
				if (this->_ptr != nullptr) delete this->_ptr;
				this->_size = object._size;
				this->_ptr = new Type[this->_size];
			}

			for (int i = 0; i < this->_size; i++)
				this->_ptr[i] = object._ptr[i];
		}
		return *this;
	}
	bool operator==(const Array &object) const
	{
		if (this->size_ != object.size_) return false;
		if (this->_size == 0 && object._size == 0) return true;
		if (this->_size == 0 || object._size == 0) return false;

		for (int i = 0; i < this->size_; i++)
			if (this->ptr_[i] != object.ptr_[i]) return false;

		return true;
	}
	Array& operator+=(const Type element)
	{
		if (this->_ptr == nullptr)
		{
			this->_size = 1;
			this->_ptr = new Type[1];
			this->_ptr[0] = element;
			return *this;
		}
		else
		{
			Array temp(this->_size);
			for (int i = 0; i < this->_size; i++)
			{
				temp._ptr[i] = this->_ptr[i];
			}

			delete this->_ptr;

			this->_size += 1;
			this->_ptr = new Type[this->_size];

			for (int i = 0; i < this->_size - 1; i++)
				this->_ptr[i] = temp._ptr[i];

			this->_ptr[this->_size - 1] = element;
			return *this;
		}
	}
	Array operator+(const Array &object)
	{
		if (this->_size == 0 && object._size == 0) throw AddingException("Can not add two empty arrays");
		int max_size = (this->_size > object._size ? this->_size : object._size);

		Array result(max_size);

		int i = 0;
		if(max_size == this->_size)
		{
			for (; i < object._size; i++)
				result._ptr[i] = this->_ptr[i] + object._ptr[i];
			for (; i < this->_size; i++)
				result._ptr[i] = this->_ptr[i];
		}
		else
		{
			for (; i < this->_size; i++)
				result._ptr[i] = this->_ptr[i] + object._ptr[i];
			for (; i < object._size; i++)
				result._ptr[i] = object._ptr[i];
		}
		return result;
	}
	Array& operator-=(const Type key)
	{
		if (!this->_size) return *this;

		int position = -1;

		for (int i = 0; i < this->_size; i++)
		{
			if (this->_ptr[i] == key)
			{
				position = i;
				break;
			}
		}

		if (position == -1) return *this;

		if (this->_size == 1)
		{
			delete this->_ptr;
			this->_ptr = nullptr;
			this->_size = 0;
		}

		Array temp(this->_size);
		for (int i = 0, j = 0; i < this->_size; i++, j++)
		{
			if (i == position)
			{
				j--;
				continue;
			}
			temp._ptr[j] = this->_ptr[i];
		}

		delete this->_ptr;
		this->_size--;

		this->_ptr = new Type[this->_size];

		for (int i = 0; i < this->_size; i++)
			this->_ptr[i] = temp._ptr[i];

		return *this;
	}
	Array& operator-(const Type key)
	{
		if (!this->_size) return *this;

		Type temp = 0;
		int index = 0;
		int offset = 1;

		for (int i = 0; i < this->_size; i++)
			if (this->_ptr[i] == key) index++;

		if (!index) return *this;

		else
		{
			for (int i = 0; i <= this->_size - offset; i++)
				if (this->_ptr[i] == key)
				{
					for (int j = i; j < this->_size - offset; j++)
					{
						temp = this->_ptr[j];
						this->_ptr[j] = this->_ptr[j+1];
						this->_ptr[j + 1] = temp;
					}
					offset++;
				}
		}

		Array temp_arr(this->_size);
		for (int i = 0; i < this->_size; i++)
		{
			temp_arr._ptr[i] = this->_ptr[i];
		}
		delete[] this->_ptr;

		this->_size -= offset;
		this->_size += 1;
		this->_ptr = new Type[this->_size];

		for (int i = 0; i < this->_size; i++)
			this->_ptr[i] = temp_arr._ptr[i];

		if (!this->_size) this->_ptr = nullptr;

		return *this;
	}
	Array& operator-(const Array &object)
	{
		if (this->_size == 0 && object._size == 0) throw SubtractingException("Can not subtract empty arrays");
		int max_size = (this->_size > object._size ? this->_size : object._size);

		Array result(max_size);

		int i = 0;
		if (max_size == this->_size)
		{
			for (; i < object._size; i++)
				result._ptr[i] = this->_ptr[i] - object._ptr[i];
			for (; i < this->_size; i++)
				result._ptr[i] = this->_ptr[i];
		}
		else
		{
			for (; i < this->_size; i++)
				result._ptr[i] = this->_ptr[i] - object._ptr[i];
			for (; i < object._size; i++)
				result._ptr[i] = (-1)*object._ptr[i];
		}
		return result;
	}

	void set(int size = 0)
	{
		if (this->_ptr)
		{
			delete this->_ptr;
			this->_ptr = nullptr;
			if (size) this->_size = size;
		}
		if(!this->_size)
		{
			if (!size)
			{
				cout << "Please input the size of the array";
				cin >> this->_size;
			}
			else this->_size = size;
		}

		this->_ptr = new Type[this->_size];

		cout << "Please input the elements one by one";
		for (int i = 0; i < this->_size; i++)
			cin >> this->_ptr[i];
	}
	void randomSet(int size = 0)
	{
		if (this->_ptr) delete this->_ptr;
		if (size) this->_size = size;

		this->_ptr = new Type[this->_size];

		srand(time(NULL));

		for (int i = 0; i < this->_size; i++)
		{
			this->_ptr[i] = 11 + rand() % 10000;	//rand() % 2000 - rand() % 1000 + rand() % 100 * rand() % 20;
		}
	}
	int getSize() const
	{
		return this->_size;
	}
	Type* getPtr()
	{
		Type* temp = this->_ptr;
		return temp;
	}
	void show() const
	{
		for (int i = 0; i < _size; i++)
			cout << _ptr[i] << " ";
		cout << endl;
	}

	void sortFall()
	{
		Type temp = 0;
		int i = 0, j = 0;

		for (i = 1; i < this->_size; ++i)
		{
			j = i - 1;
			temp = this->_ptr[i];
			while (j >= 0 && this->_ptr[j] < temp)
			{
				this->_ptr[j + 1] = this->_ptr[j];
				j--;
			}
			this->_ptr[j + 1] = temp;
		}
	}
	void sortGrow()
	{
		Type temp = 0;
		int i = 0, j = 0;

		for (i = 1; i < this->_size; ++i)
		{
			j = i - 1;
			temp = this->_ptr[i];
			while (j >= 0 && this->_ptr[j] > temp)
			{
				this->_ptr[j + 1] = this->_ptr[j];
				j--;
			}
			this->_ptr[j + 1] = temp;
		}
	}

	Array()
	{
		this->_size = 0;
		this->_ptr = nullptr;
	}
	Array(const int size)
	{
		if (size < 0) throw InvalidSizeException("Array's size can not be negative");

		if (size == 0)
		{
			this->_size = 0;
			this->_ptr = nullptr;
		}
		else
		{
			this->_size = size;
			this->_ptr = new Type[size];

			for (int i = 0; i < this->_size; i++)
			{
				this->_ptr[i] = 0;
			}
		}
	}
	Array(const Array &object)
	{
		this->_size = object._size;
		this->_ptr = new Type[this->_size];

		for (int i = 0; i < this->_size; i++)
			this->_ptr[i] = object._ptr[i];
	}
	~Array()
	{
		if (this->_ptr != nullptr)
			delete this->_ptr;
		this->_ptr = nullptr;
	}
private:
	int _size;
	Type* _ptr;
};

template <class Type>
Type& findElement(Type* arr, int size, Type key)
{
	for (int i = 0; i < size; i++)
	{
		if (arr[i] == key) return arr[i];
	}
	cout << "Array does not contain this elemnt.\nThe first element is returned" << endl;
	return arr[0];
}

template<>
char& findElement<char>(char* arr, int size, char key)
{
	for (int i = 0; (arr[i] != '\0' && arr[i] != '\n') || (i < size); i++)
	{
		if (arr[i] == key) return arr[i];
	}
	std::cout << "String does not contain this elemnt.\nThe first element is returned" << std::endl;
	return arr[0];
}