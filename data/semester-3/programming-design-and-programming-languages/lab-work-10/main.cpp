#include "Queue.h"

int main()
{
	try
	{
		Queue<int> queueUnit;

		queueUnit.push(12);
		queueUnit.push(32);
		queueUnit.push(47);
		queueUnit.push(99);
		queueUnit.push(666);
		queueUnit.push(54);
		queueUnit.push(13);
		queueUnit.push(1999);

		cout  << "Created queue" << endl;
		queueUnit.show();

		queueUnit.pop();
		queueUnit.emplace(2, 100);
		queueUnit.remove(3);

		cout << endl << endl << "Modified queue" << endl;
		queueUnit.show();

		cout << endl << queueUnit.find(10) << endl;

		/*
		Queue<char> queueUnit;

		queueUnit.push('a');
		queueUnit.push('b');
		queueUnit.push('c');
		queueUnit.push('d');
		queueUnit.push('e');
		queueUnit.push('f');
		queueUnit.push('g');
		queueUnit.push('h');

		cout  << "Created queue" << endl;
		queueUnit.show();

		queueUnit.pop();
		queueUnit.emplace(2, 'i');
		queueUnit.remove(3);

		cout << endl << endl << "Modified queue" << endl;
		queueUnit.show();

		cout << endl << queueUnit.find('j') << endl;*/

		Queue<int>::iterator queueIterator;

		cout << endl << "Iterator output" << endl;

		for (queueIterator = queueUnit.begin(); queueIterator != queueUnit.end(); ++queueIterator)
			cout << *queueIterator << " ";
		cout << endl;

		cin.ignore();
	}
	catch (QueueSizeException &exception)
	{
		cout << exception.what() << endl;
		cin.ignore();
	}
	catch (IteratorException &exception)
	{
		cout << exception.what() << endl;
		cin.ignore();
	}

	return EXIT_SUCCESS;
}