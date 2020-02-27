#ifndef QUEUE_H
#define QUEUE_H

#include <iostream>
#include "IteratorException.h"
#include "QueueSizeException.h"
#include "AbscentElementException.h"

using namespace std;

template <typename Type>
class Queue
{
public:
	typedef Type value_type;
	typedef size_t size_type;
	typedef value_type& reference;
	typedef const value_type& const_reference;
	typedef Queue container_type;

private:
	class Node
	{
	public:
		friend ostream& operator<<(ostream& stream, const Node &node)
		{
			stream << node._value << " ";

			return stream;
		}

		void showValue()
		{
			cout << this->_value << " ";
		}
		void setValue(value_type value)
		{
			this->_value = value;
		}
		value_type getValue()
		{
			return this->_value;
		}

		Node()
		{
		}
		Node(value_type value) : _value(value)
		{
		}
		~Node()
		{
		}

	public:
		Node *_next;
		value_type _value;
	};

public:
	class iterator
	{
	private:
		friend class Queue<value_type>;

	public:
		iterator& operator=(const iterator &iteratorUnit)
		{
			this->_currentNode = iteratorUnit._currentNode;
			return *this;
		}
		bool operator==(const iterator &iteratorUnit) const
		{
			return (this->_currentNode == iteratorUnit._currentNode);
		}
		bool operator!=(const iterator &iteratorUnit) const
		{
			return !(iteratorUnit == *this);
		}
		iterator& operator++()
		{
			if (this->_currentNode == nullptr)
				throw IteratorException("Trying to increment an empty iterator");

			if (this->_currentNode->_next == nullptr)
				throw IteratorException("Trying to increment past the tail of the queue!");

			this->_currentNode = this->_currentNode->_next;
			return *this;
		}

		reference operator*() const
		{
			if (this->_currentNode == nullptr)
				throw IteratorException("Trying to get reference of an empty iterator");
			return this->_currentNode->_value;
		}

		iterator() : _currentNode(nullptr)
		{
		}
		iterator(Node *node) : _currentNode(node)
		{
		}
		iterator(const iterator &iteratorUnit) : _currentNode(iteratorUnit._currentNode)
		{
		}

	private:
		Node *_currentNode;
	};

public:
	reference front()
	{
		return this->*_head;
	}
	const_reference front() const
	{
		return this->*_head;
	}
	reference back()
	{
		return this->*tail;
	}
	const_reference back() const
	{
		return this->*tail;
	}

	iterator begin()
	{
		return iterator(this->_head);
	}
	iterator end()
	{
		return iterator(this->_tail);
	}

	void emplace(const int position, const value_type value)
	{
		if (position > static_cast<int>(this->size()) || position < 0)
			throw QueueSizeException("The position of new element is invalid");
		
		Node* currentNode = this->_head;
		for (int i = 0; i < position; i++, currentNode = currentNode->_next);

		Node* tempNode = new Node;
		*tempNode = *currentNode;
		currentNode->setValue(value);
		currentNode->_next = tempNode;

		if (position == 0)
		{
			this->_head = currentNode;
		}
		else if (position == (static_cast<int>(size())))
		{
			this->_tail = currentNode->_next;
		}
	}
	void pop()
	{
		if (this->isEmpty()) return;

		Node* tempNode;

		tempNode = this->_head;
		this->_head = this->_head->_next;

		delete tempNode;
	}
	void push(value_type value)
	{
		if (this->isEmpty())	// if the queue is empty
		{
			Node *tempNode = new Node(value);
		
			tempNode->_next = this->_head;
			this->_head = tempNode;

			return;
		}
		else
		{
			Node* tempNode = new Node(value);
			
			*(this->_tail) = *tempNode;
			this->_tail->_next = new Node;
			this->_tail = this->_tail->_next;
		}
	}
	void remove(const int position)
	{
		if (position >= static_cast<int>(this->size()) || position < 0)
			throw QueueSizeException("The position of the element wich is being deleted is invalid");

		Node* currentNode = this->_head;
		for (int i = 0; i < position; i++, currentNode = currentNode->_next);

		if (position == 0)
		{
			this->_head = currentNode;
		}
		else if (position == (static_cast<int>(size()) - 1))
		{
			this->_tail = currentNode;
		}

		Node* tempNode = currentNode->_next;
		*currentNode = *(currentNode->_next);
		delete tempNode;
	}
	
	Node& find(value_type key)
	{
		try
		{
			for (Node* tempNode = this->_head;
				tempNode != this->_tail;
				tempNode = tempNode->_next)
				if (tempNode->getValue() == key) return *tempNode;

			throw AbscentElementException("The element is abscent! First node will be returned...");
		}
		catch (AbscentElementException &exception)
		{
			cout << endl << exception.what() << endl;
		}
		return *(this->_head);
	}

	size_type size() const
	{
		size_type size = 0;

		for (Node* tempNode = this->_head;
			tempNode != this->_tail;
			tempNode = tempNode->_next)
			size++;

		return size;
	}
	bool isEmpty() const
	{
		return (this->_head == this->_tail);
	}

	void show() const
	{
		for (Node* tempNode = this->_head;
			tempNode != this->_tail;
			tempNode = tempNode->_next)
			cout << *tempNode << " ";
	}

	Queue()
	{
		this->_head = this->_tail = new Node;
		this->_tail->_next = nullptr;
	}
	Queue(value_type value)
	{
		this->_head = this->_tail = new Node;
		this->_tail->_next = nullptr;

		push(value);
	}
	~Queue()
	{
		Node *currentNode = _head;
		for (Node *tempNode = this->_head; tempNode != this->_tail;)
		{
			tempNode = tempNode->_next;
			delete currentNode;
			currentNode = tempNode;
		}
		delete currentNode;
	}

private:
	Node* _head;	
	Node* _tail;	// фиктивный элемент
};

#endif QUEUE_H