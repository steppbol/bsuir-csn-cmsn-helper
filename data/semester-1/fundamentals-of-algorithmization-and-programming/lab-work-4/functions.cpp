#include "functions_header.h"
#include <stdlib.h>
#include <time.h>
#include <stdio.h>
#include <iostream>
int* create_array(int size)
{
	int* ms;
	ms=(int*)malloc(size*sizeof(int));
	return ms;
}
int delete_array(int*ms)
{
	free(ms);
	return 0;	//возвращает значение размера массива (т.е. 0)
}
int delete_element(int* ms, int size, int element)
{
	for (int i=0; i<size; i++ )
	{
		if(ms[i]==element && i==(size-1)) 
		{
			size--;
			break;
		}
		if (ms[i]==element)
		{
			for(int j=i+1; j<size; j++)
				ms[j-1]=ms[j];
			size--;
			i--;
			if (size==0) break;
		}
	}
	return size;
}
void keyboard_init(int* ms, int size)
{
	int scanf_result=1;
	for (int i=0;i<size;i++)
	{
		do
		{
			system("cls");
			if (!scanf_result) printf ("Ошибка! Повторите ввод\n\n");
			printf(" Введите элемент [%d]:\t",i);
			fflush(stdin);
			scanf_result=scanf_s("%9d",ms+i);
		}while(!scanf_result);
	}
}
void random_init(int* ms, int size)
{
	srand(time(NULL));

	for (int i=0;i<size;i++)
	{
		ms[i]=rand()%200;
	}
}
void sort(int* ms, int size, int choice)
{
	int i, j, n=0;
	switch(choice)
	{
			case 1:
			{
				for(i=1;i<size;++i)
				{
					j=i-1;
					n=ms[i];
					while(j>=0 && n>ms[j])
					{
						ms[j--+1]=ms[j];
						ms[j+1]=n;
					}
					continue;
				}
			}
			case 2:
			{
				for(i=1;i<size;++i)
				{
					j=i-1;
					n=ms[i];
					while(j>=0 && n<ms[j])
					{
						ms[j--+1]=ms[j];
						ms[j+1]=n;
					}
					continue;
				}
			}
	}
}
void output(int* ms, int size)
{
	printf(" Массив : [");
	for(int i=0;i< size;i++)
	{
		printf("%6d", ms[i]);
	}
	printf("     ]");
}
void find_max(int* ms, int size, int* number_of_max, int* max)
{
	*max=ms[0];
	for(int i=1;i<size;i++)
	{
		if (*max<ms[i])
		{
			*max=ms[i];
		}
	}
	for(int i=0;i<size;i++)
	{
		if (*max==ms[i])
		{
			*number_of_max+=1;
		}
	}
}
void find_min(int* ms, int size, int* number_of_min, int* min)
{
	*min=ms[0];
	for(int i=1;i<size;i++)
	{
		if (*min>ms[i])
		{
			*min=ms[i];
		}
	}
	for(int i=0;i<size;i++)
	{
		if (*min==ms[i])
		{
			*number_of_min+=1;
		}
	}
}
void hoarSort(int* ms, int left,int right)
{

}
void shellSort(int* ms, int size)
{
	int i, j, temp;
	int step;
	int flag;
	for(step=size/2;step>0;step/=2)
		do
		{
			flag=0;
			for(i=0, j=step; j<size; i++, j++)
			{
				if(*(ms+i)>*(ms+j))
				{
					temp=*(ms+j);
					*(ms+j)=*(ms+i);
					*(ms+i)=temp;
					flag=1;
				}
			}
		}while(flag);
}