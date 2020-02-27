#ifndef SORTS_H
#define SORTS_H
#include <time.h>
#include <stdlib.h>
#include <assert.h>
void hoarSort(int* ms, int right);
void optimizedHoar(int* ms, int right);
void shellSort(int* ms, int size);
void insertSort(int* ms, int size);
void selectionSort(int* ms, int size);
void bubbleSort(int* ms, int size);
void shakerSort(int* ms, int size);
float timeCheck(int *ms, int size, void (*sort)(int*, int));
int checkSort(int* ms, int size);
void randInit(int* ms, int size);
int randNum(int size);
#endif SORTS_H