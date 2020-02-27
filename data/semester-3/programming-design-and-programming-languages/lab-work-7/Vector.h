#include "Template.h"

template <typename Type>
class Vector : public Array<Type>
{
	public:
		typedef Type iterator;

		Type& begin()
		{
			return this->_ptr[0];
		}
		Type& end()
		{
			return this->_ptr[this->_size];
		}
		bool empty()
		{
			if (this->_size == 0) return true;
			else return false;
		}
/*		void push_back(const Type element)
		{
			if (this->_ptr == nullptr)
			{
				this->_size = 1;
				this->_ptr = new Type[1];
				this->_ptr[0] = element;
				return;
			}
			else
			{
				Array<Type> temp(this->_size);
				for (int i = 0; i < this->_size; i++)
				{
					temp._ptr[i] = this->_ptr[i];
				}

				delete[] this->_ptr;

				this->_size += 1;
				this->_ptr = new Type[this->_size];

				for (int i = 0; i < this->_size - 1; i++)
					this->_ptr[i] = temp._ptr[i];

				this->_ptr[this->_size - 1] = element;
				return;
			}
		}*/
		void pop_back()
		{
			if (this->_size == 0) return;
			if (this->_size == 1)
			{
				delete[] Array<Type>::getPtr();
				this->_size = 0;
				Array<Type>::getPtr() = nullptr;
			}
			Array<Type> temp(this->_size);
			for (int i = 0; i < this->_size; i++)
			{
				temp._ptr[i] = Array<Type>::getPtr()[i];
			}

			delete[] Array<Type>::getPtr();

			this->_size -= 1;
			Array<Type>::getPtr() = new Type[this->_size];

			for (int i = 0; i < this->_size; i++)
				Array<Type>::getPtr()[i] = temp._ptr[i];
		}
/*		void insert(Type element, const int position)
		{
			if (this->_ptr == nullptr || position >= this->_size || position < 0)
			{
				this->_size = 1;
				this->_ptr = new Type[1];
				this->_ptr[0] = element;
				return;
			}
			else
			{
				Array<Type> temp(this->_size);
				for (int i = 0; i < this->_size; i++)
				{
					temp._ptr[i] = this->_ptr[i];
				}

				delete[] this->_ptr;

				this->_size += 1;
				this->_ptr = new Type[this->_size];

				for (int i = 0, j = 0; i < this->_size; i++, j++)
				{
					if (i == position)
					{
						this->_ptr[i] = element;
						j--;
					}
					else this->_ptr[i] = temp._ptr[j];
				}

				return;
			}
		}*/

		Vector() : Array<Type>()
		{
		}
		Vector(const int size) : Array<Type>(size)
		{
		}
		~Vector()
		{
		}
};
