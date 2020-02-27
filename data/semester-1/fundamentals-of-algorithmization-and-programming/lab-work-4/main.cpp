#include "functions_header.h"
#include <stdio.h>
#include <locale.h>
#include <conio.h>
#include <stdlib.h>
int main()
{
	setlocale(0,"");
	int *ms;
	int size=0;
	int scanf_check=1;			//переменная для проверки результата функции scanf_s
	printf("\t      О великий пользователь!\n\n Чтобы начать работу с программой, нажмите (1)\n\t\t      ИЛИ\n Чтобы выйти, нажмите любую другую клавишу");
	if(_getch()!='1')
		{
			return 0;
		}
	for(;;)
	{
	do
		{
			system("cls");
			if (!scanf_check) printf("\aОшибка повторите ввод\n\n");
			puts(" Введите число элементов массива:");
			fflush(stdin);
			scanf_check=scanf_s("%d",&size);
			if(size<=0 || size>101000) scanf_check=0;
		}while(!scanf_check);
	ms=create_array(size);
	system("cls");
	printf("\tВыберите тип инициализации массива\n\n");
	printf("===================================================================================================\n\n");
	printf("(1) : Инициализация массива с клавиатуры\n\n");
	printf("(2) : Инициализация массива случайными числами\n\n");
	printf("===================================================================================================\n\n");
	for(;;)
	{
		switch(_getch())
		{
		case '1': 
			{
				system("cls");
				keyboard_init(ms, size);
				system("cls");
				printf (" Массив успешно заполнен\n\n Чтобы продолжить работу, нажмите любую клавишу");
				_getch();
				break;
			}
		case '2':
			{
				system("cls");
				random_init(ms, size);
				printf (" Массив успешно заполнен\n\n Чтобы продолжить работу, нажмите любую клавишу");
				_getch();
				break;
			}
		default : continue;
		}							// конец switch
		break;
	}								// конец цикла выбора типа инициализации
	for(;;)
	{
		int special=0;				// особый элемент (минимальный/максимальный/удаляемый)
		int number_of_special=0;	// число минимальных/максимальных элементов
		int choice=0;				// переменная для выбора типа сортировки
		system("cls");
		printf("\n\tВыберите действие:\n\n");
		printf("===================================================================================================\n\n");
		printf("(1) : Просмотр массива (вывод на экран) \t\t\t(Число элементов массива [%d])\n",size);
		printf("(2) : Поиск и вывод максимального значения в массиве\n");
		printf("(3) : Поиск и вывод минимального значения в массиве\n");
		printf("(4) : Сортировка массива\n");
		printf("(5) : Удаление элементов, совпадающих с заданным значением\n");
		printf("(6) : Удаление массива и создание нового\n\n");
		printf("===================================================================================================\n\n");
		printf("Чтобы выйти, нажмите (0)");
		switch(_getch())
		{
		case '0':
			{
				size=delete_array(ms);
				return 0;
			}
		case '1': 
			{
				system("cls");
				output(ms, size);
				printf ("\n\n Чтобы продолжить работу, нажмите любую клавишу");
				_getch();
				continue;
			}
		case '2':
			{
				system("cls");
				find_max(ms, size, &number_of_special, &special);
				printf("Максимальный элемент массива = [%d] Число максимальных элемнтов = [%d]",special,number_of_special);
				printf ("\n\n Чтобы продолжить работу, нажмите любую клавишу");
				_getch();
				continue;
			}
		case '3':
			{
				system("cls");
				find_min(ms, size, &number_of_special, &special);
				printf("Минимальный элемент массива = [%d] Число минимальных элемнтов = [%d]",special,number_of_special);
				printf ("\n\n Чтобы продолжить работу, нажмите любую клавишу");
				_getch();
				continue;
			}
		case '4':
			{
				system("cls");
				do
				{
					system("cls");
					printf("Сортировка по (1) убыванию (2) возростанию ");
					fflush(stdin);
				}while(!scanf_s("%d",&choice) || (choice!=1 && choice!=2));
				sort (ms, size, choice);
				system("cls");
				printf (" Массив успешно отсортирован\n\n Чтобы продолжить работу, нажмите любую клавишу");
				_getch();
				continue;
			}
		case '5':
			{
				do
				{
					system("cls");
					printf("Введите элемент, который вы желаете удалить :");
				}while(!scanf_s("%d", &special));
				system("cls");
				size=delete_element(ms, size, special);
				if (size==0)
				{
					delete_array(ms);
					printf (" Массив успешно удален\n\n Нажмите любую клавишу, чтобы создать новый массив");
					_getch();
					break;
				}
				else 
				{
					ms=(int*)realloc(ms, size*sizeof(int));   // сокращение длины массива
					printf (" Указанные элементы успешно удалены\n\n Чтобы продолжить работу, нажмите любую клавишу");
					_getch();
					continue;
				}
			}
			case '6':
			{
				size=delete_array(ms);
				break;
			}
			case '7':
			{
				system("cls");
				shellSort(ms, size);
				printf (" Массив успешно отсортирован\n\n Чтобы продолжить работу, нажмите любую клавишу");
				_getch();
				continue;
			}
			default : continue;
		}
		break;
		}			// конец цикла манипуляций над массивом (просмотр, удаление элементов, сортировка, удаление)
	}				// конец цикла работы с массивом  (создание, инициализация и вышеперечисленные действия)
}					// конец тела main