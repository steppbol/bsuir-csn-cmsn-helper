#include "sorts.h"
#include <stdio.h>
#include <conio.h>
#include <locale.h>

int main()
{
	setlocale(LC_ALL,"RUS");
	int* ms, size;
	do
	{
		fflush(stdin);
		printf("Введите размерность массива с клавиатуры : ");
	}while(!scanf("%d", &size) || size<1);
	system("cls");
	ms=(int*)malloc(size*sizeof(int));
	assert(ms != nullptr);
	randInit(ms, size);
	printf("Если значение времени отрицательно, значит сортировка массива провалилась");
	printf("\n\nРезультаты :\n\nЧисло элементов : %d", size);
	printf("\n\nВремя в секундах hoar %f", timeCheck(ms, size, hoarSort));
	printf("\n\nВремя в секундах optimizedHoar %f", timeCheck(ms, size, optimizedHoar));
	printf("\n\nВремя в секундах shell %f", timeCheck(ms, size, shellSort));
	printf("\n\nВремя в секундах insert %f", timeCheck(ms, size, insertSort));
	printf("\n\nВремя в секундах selection %f", timeCheck(ms, size, selectionSort));
	printf("\n\nВремя в секундах shaker %f", timeCheck(ms, size, shakerSort));
	printf("\n\nВремя в секундах bubble %f", timeCheck(ms, size, bubbleSort));
	_getch();
	free(ms);
	return 0;
}