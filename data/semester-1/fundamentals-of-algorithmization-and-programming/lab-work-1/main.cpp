#define _USE_MATH_DEFINES
#include <stdio.h>
#include <math.h>
#include <locale.h>
#include <conio.h>
#include <iostream>
double power(double number, int power)		// функция возведения числа в положительную степень
{
	int i;
	double num=number;						//задаем переменную 'num', необходимую для возведения числа в положительную степень//
	switch (power)
	{
		case 0:num=1; break;				// если степень = 0, то результат = 1
		case 1:num=number; break;		 	// если степень = 1, то результат равен исходному числу
		default: for (i=1; i<power; i++) 	// цикл возведения в положительную степень
		num=num*number;
	}
	return num;
}
double factorial(int value) 			// функция подсчета факториала
{
	int i;
	double result=1;					//задаем переменную 'result', необходимую для подсчета факториала числа//
	for (i=1; i<=value; i++)			// цикл подсчета факториала
	{
		result=result*(double)i;
	}
	return result;
}
double module(double number)			// фунция нахождения модуля числа
{
	if (number<0)
		number=number*(-1);
	return number;
}
double adduction(double angle)			// функция приведения угла к значениям от 0 до двух pi
{
	double pi=180;
	if (angle<0)
	{
		angle=angle*(-1);
	}
	while (angle>2*pi)
		angle=angle-(2*pi);
	return angle;
}
double function_cos(double angle, double precision) // функция подсчета косинуса угла, измеряемого в радианах, с задваемой точностью
{
	int n=(-1);			// задаем переменную, которая будет обозначать номер слагаемогов в ряде Маклорена для функции "косинус"
	double summand1;	// переменная, которой будут присваиваться значения слагаемых в ряде Маклорена
	double summand2=0;	// переменная, которой будет присваиваться значение результата вычислений
	do
	{
		n++;
		summand1=power((-1), n)*power(angle, 2*n)/factorial(2*n);
		summand2+=summand1;
	}while (module(summand1)>precision); // для соблюдения точночти вычисления косинуса, модуль каждого слагаемого ряда сравнивается со значением точности, введенным с клавиатуры
	return summand2;
}
int main()
 {
	 setlocale(0,"");
	 int digit_amount;					// чилсо знаков после запятой в значении косинуса
	 int i;								// переменная для работы с циклом
	 int choice;						// выбор единиц измерения
	 double angle;						// угол, вводимый с клавитуры
	 double helping_angle;				// вспомогательный угол, измеряемый в градусах
	 double cos_angle;					// косинус угла
	 double precision=1;				// точность подсчета косинуса (число, с которым будут сравниваться слагаемые в ряде Маклорена)
	 double rad_coeff;					// коэффициент, указывающий, сколько градсусов в 1 pi
	 double PI=M_PI;					// число pi
	 rad_coeff=180/PI;
	 do									// делает программу цикличной
	 {
		 do
		 {
			 system("cls");
			 printf ("Укажите единицы измерения нужного вам угла : Радианы(1) ИЛИ Градусы(2) ");
			 fflush(stdin);
		 }while(scanf_s("%d",&choice)!=1 || (choice!=1 && choice!=2)); // продолжать цикл, пока не введено 1 или 2
		 do
		 {
			 system("cls");
			 printf ("Введите численное значение угла : ");
			 fflush(stdin);
		 }while(scanf_s("%12lf",&angle)!=1); 
		 if(choice==1) angle*=rad_coeff;	// перевод угла в градусы, если он был введен в радианах
		 angle=adduction(angle);			// приведение угла к значению от 0 до 360 градусов
		 helping_angle=angle;				// сохранение значения угла в градусах
		 angle/=rad_coeff;					// перевод значения угла в радианы
		 do
		 {
			 system("cls");
			 printf ("Введите нужную точность подсчета косинусa угла \n(количество знаков после запятой, не больше 15):");
			 fflush(stdin);
		 }while(scanf_s("%d", &digit_amount)!=1 || digit_amount>15 || digit_amount<=0);
		 system("cls");
		 for (i=1; i<=digit_amount; i++)
		 {
			 precision/=10;	
		 }
		 cos_angle=function_cos(angle,precision); // нахождение косинуса угла
		 printf("============================================================================\n\n");
		 printf("Значение введенного угла: %lf радиан ИЛИ %lf градусов",angle,helping_angle);
		 printf("\n\nКосинус угла = %.*lf\n\nКосинус, подсчитанный при помощи библиотечной функции = %.*lf",digit_amount,cos_angle,digit_amount,cosl(angle));// сosl(angle) -- библиотечная функция подсчета косинуса угла
		 printf("\n\n============================================================================\n");
		 printf("\nЧтобы продолжить работу с программой нажмите (1)\nИЛИ\nЧтобы выйти нажмите любую другую кнопку");
	 }while(_getch()=='1');	 // условие продолжения работы с программой
	 return 0;
 }