#include "sorts.h"

#define LIMIT 150

void hoarSort(int* ms, int size)
{
	int mid, i , j, temp;
	mid = ms[(size-1)/2];
	i = 0;
	j = size-1;
	do
	{
		while (ms[i] < mid) i++;
		while (ms[j] > mid) j--;
		if(i <= j)
		{
			temp = ms[i];
			ms[i] = ms[j];
			ms[j] = temp;
			i++;
			j--;
		}
	}while(i<=j);
	if(i < size-1)
		hoarSort(ms+i, size-i);
	if(j > 0)
		hoarSort(ms, j+1);
}

void optimizedHoar(int* ms, int size)
{
	if (size-1 < LIMIT)						// если подмассив слишком мал, то сортируем его методом вставок
	{
		shellSort(ms, size);
		return;
	}
	else
	{
		int mid, i , j, temp;
		mid = ms[randNum(size-1)];    		// выбираем случайный элемент
		i = 0;
		j = size-1;
		do
		{
			while (ms[i] < mid) i++;
			while (ms[j] > mid) j--;
			if(i <= j)
			{
				temp = ms[i];
				ms[i] = ms[j];
				ms[j] = temp;
				i++;
				j--;
			}
		}while(i<=j);
		if(j > 0)
			optimizedHoar(ms, j+1);
		if(i < size-1)
			optimizedHoar(ms+i, size-i);
	}
}

void shellSort(int* ms, int size)
{
	int i, j, temp;
	int step;
	int flag;
	for(step = size/2; step > 0; step /= 2)
		do
		{
			flag=0;
			for(i = 0, j = step; j < size; i++, j++)
			{
				if(ms[i]>ms[j])
				{
					temp = ms[j];
					ms[j] = ms[i];
					ms[i] = temp;
					flag = 1;
				}
			}
		}while(flag);
}

void insertSort(int* ms, int size)
{
	int i, j, temp;
	for(i = 1; i < size; i++)
	{
		j=i-1;
		temp = ms[i];
		while(j >= 0 && temp < ms[j])
			ms[j--+1] = ms[j];
		ms[j+1] = temp;
	}
}

void selectionSort(int* ms, int size)
{
	int i, j, sel, temp; 
	for(i = 0; i < size-1; i++)
	{
		sel = i;
		for(j = size-1; j > i; j--)
			if (ms[sel] > ms[j]) sel=j;
		if(i != sel)
		{
			temp = ms[i];
			ms[i] = ms[sel];
			ms[sel] = temp;
		}
	}
}

void bubbleSort(int* ms, int size)
{
	int i, j, temp;
	for(i = 0; i < size-1; i++)
		for(j = size-1; j > i; j--)
		{
			if(ms[j-1] > ms[j])
			{
				temp = ms[j-1];
				ms[j-1] = ms[j];
				ms[j] = temp;
			}
		}
}

void shakerSort(int* ms, int size)
{
	int i, temp, border_element, lower_border, upper_border;
	lower_border = 1;
	border_element = size-1;
	upper_border = size-1;
	do
	{
		for(i = upper_border; i >= lower_border; --i)
		{
			if(ms[i-1] > ms[i])
			{
				temp = ms[i-1];
				ms[i-1] = ms[i];
				ms[i] = temp;
				border_element = i;
			}
		}
		lower_border = border_element+1;
		for(i = lower_border; i <= upper_border; ++i)
		{
			if(ms[i-1] > ms[i])
			{
				temp = ms[i-1];
				ms[i-1] = ms[i];
				ms[i] = temp;
				border_element = i;
			}
		}
		upper_border = border_element-1;
	}while(lower_border <= upper_border);
}

float timeCheck(int *ms, int size, void (*sort)(int*, int)) // функция измерения времнеи сортировки,
{															// которая возвращает отрицательное значение,
	float time;												// если сортировка не удалась
	int* local_ms;

	assert(size > 0);
	local_ms = (int*)malloc(size*sizeof(int));
	assert(local_ms != nullptr);

	for (int i = 0; i < size; i++)
		local_ms[i] = ms[i];

	clock_t cycles = clock();
	
	sort(local_ms, size);

	cycles = clock() - cycles;
	time = (float)cycles/CLOCKS_PER_SEC;

	if (!checkSort(local_ms, size)) return -1;

	free(local_ms);
	return time;
}

int checkSort(int* ms, int size)
{
	for(int i = 1; i < size; i++)
	{
		if(ms[i-1] <= ms[i]) continue;
		else return 0;
	}
	return 1;
}

void randInit(int* ms, int size)
{
	srand(time(NULL));

	for (int i=0 ; i < size; i++)
	{
		ms[i]=rand()%2000-rand()%1000+rand()%100*rand()%20;
	}
}

int randNum(int size)
{
	srand(time(NULL));
	int a;
	a = rand()%(size);
	return a;
}