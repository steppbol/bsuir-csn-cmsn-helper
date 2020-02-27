#include <stdio.h>
#include <conio.h>
#include <math.h>
#include <locale.h>
#include <iostream>
#include <stdlib.h>
int main()
{
	setlocale(0,"");
	int dimension_value=0;											// размерность квадратной матрицы
	int primes_amount=0;											// число элементов массива простых чисел
	int prime=0;													// переменная, обозначающая простое число
	int i=0;
	int modulo=0;													// остаток от деления
	int natural_number=0;											// переменная для перебора натуральных чисел
	int *ms;														// указатель на массив простых чисел
	int line=0;
	int column=0;
	int dynamic_dimension=0;       						    		// граница заполнения строки вправо / столбца вниз
	int mat_edge=0;													// граница заполнения строки влево / столбца вверх
	int **mat;														// матрица (указатель на указатели на строки)
		do															// цикл проверки ввода
		{
			system("cls");
			puts("Введите размерность матрицы (от 1 до 10)");
			fflush(stdin);
		}while(!scanf_s("%d", &dimension_value) || dimension_value<=0 || dimension_value>10 );
		system("cls");
		primes_amount=dimension_value*dimension_value; 				// задаем число элементов массива
		ms=(int*)malloc(primes_amount*sizeof(int));
		for(i=0;i<primes_amount;i++)								// цикл заполнения массива простыми числами
		{
			prime++;
			for (natural_number=2;prime>natural_number/2;natural_number++) // проверка того, простое ли число
			{
				modulo=prime%natural_number;
				if (!modulo) break;
			}
			if (!modulo && prime!=natural_number)  
				{
					i--;
					continue;
				}
			ms[i]=prime;
		}
		mat=(int**)calloc(dimension_value,sizeof(int*));			// выделение памяти под массив строк (указателей на них)
		for(line=0;line<dimension_value;line++)						// цикл выделение памяти 
			mat[line]=(int*)calloc(dimension_value,sizeof(int));	// под каждую строку
		i=0;
		line=0;
		dynamic_dimension=dimension_value;
		dynamic_dimension++;
		while (dynamic_dimension>0)									// цикл заполнения матрицы
		{
			if (dynamic_dimension>2)
				{
					dynamic_dimension--;							// сужаем матрицу
				}
			if (dynamic_dimension==1)    							// если размерность матрицы 1х1, то просто заполняем 1 элемент
			{
				mat[line][column]=ms[i];
				break;
			}
			for (;column<dynamic_dimension;column++, i++)			// заполнение строки влево
				mat[line][column]=ms[i];
			column--;												// возвращаемся на последний элемент крайнего правого столбца
			line++;													// сдвигаемся на 1 строку вниз
			for (;line<dynamic_dimension;line++, i++)				// заполнение столбца вниз
				mat[line][column]=ms[i];
			line--;													// возвращаемся на последний элемент крайней нижней строки
			column--;												// сдвигаемся на 1 столбец влево
			for (;column>=mat_edge;column--, i++)					// заполнение строки вправо
				mat[line][column]=ms[i];
			column++;												// возвращаемся в крайний левый столбец
			line--;													// сдвигаемся на 1 строку вверх
			if (dynamic_dimension==2) break;						// если размерность матрицы 2х2, то цикл прерывается
			for (;line>(mat_edge);line--, i++)						// заполнение столбца вверх
				mat[line][column]=ms[i];
			line++;													// возвращаемся на крайнюю верхнюю строку
			column++;												// сдвигаемся на 1 столбец вправо
			mat_edge++;												// наращиваем перменную, ограничивающую заполнение крайней нижней строки и крайнего левого столбца
		}
		printf("\n Матрица[%d]х[%d]:\n",dimension_value,dimension_value);
		for (line=0;line<dimension_value;line++)					// цикл вывода матрицы на экран
		{
			printf("\n\n| ",dimension_value);
			for (column=0;column<dimension_value;column++)
			{
				printf("%4d", mat[line][column]);
				if (column==dimension_value-1) printf("   |");
			}
		}
		for(line=0;line<dimension_value;line++)
			free(mat[line]);
		free(mat);
		free(ms);
		_getch();
		return 0;
}