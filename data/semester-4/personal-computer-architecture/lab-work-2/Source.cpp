#define ITERATIONS 10000000
#define REGISTER_IN_BYTES 8
#define OVERALL_AMOUNT_MMX 8
#define OVERALL_AMOUNT_PURE 16
#define CUSTOM_SEED 154320
#define TWO_BYTES 2
#define SOME_SEED1 7
#define SOME_SEED2 13
#define ARR_SIZE 32

#include <stdlib.h>
#include <stdio.h>
#include <conio.h>
#include <windows.h>
#include <time.h>

void randInit(short int arr[], int size, unsigned int seed);
void printArr(short int arr[], int size);

void main(void)
{
	LARGE_INTEGER frequency, st, finish;
	float execution_time;

	short int first_array[ARR_SIZE] = { 0 };
	short int second_array[ARR_SIZE] = { 0 };
	short int result_array[ARR_SIZE] = { 0 };

	randInit(first_array, ARR_SIZE, (unsigned int)time(NULL));
	randInit(second_array, ARR_SIZE, (unsigned int)(time(NULL) / SOME_SEED1 * SOME_SEED2));

	printf("First array: \n");
	printArr(first_array, ARR_SIZE);
	printf("\n\nSecond array: \n");
	printArr(second_array, ARR_SIZE);

	// C block

	QueryPerformanceFrequency(&frequency);
	QueryPerformanceCounter(&st);

	for (int i = 0; i < ITERATIONS; i++)
	{
		for (int j = 0; j < ARR_SIZE; j++)
		{
			result_array[j] = max(first_array[j], second_array[j]);
		}
	}

	QueryPerformanceCounter(&finish);
	execution_time = (finish.QuadPart - st.QuadPart) * 1000.0f / frequency.QuadPart;

	printf("\n\nC time : %f\n\n", execution_time);

	printArr(result_array, ARR_SIZE);

	// Pure ASM block

	QueryPerformanceFrequency(&frequency);
	QueryPerformanceCounter(&st);

	for (int i = 0; i < ITERATIONS; i++)
	{
		_asm {
			pusha

			xor esi, esi
			xor ecx, ecx

			mov ecx, OVERALL_AMOUNT_PURE

			comparison:
				
					mov ax, first_array[esi]
					mov bx, second_array[esi]
				
				first_number:
					cmp al, bl
					jae greater1
					mov dl, bl
					jmp second_number
				greater1 :
					mov dl, al

				second_number:
					cmp ah, bh
					jae greater2
					mov dh, bh
					jmp write_result
				greater2 :
					mov dh, ah

				write_result:
					mov result_array[esi], dx
					add esi, TWO_BYTES

			loop comparison

			popa
		}
	}

	QueryPerformanceCounter(&finish);
	execution_time = (finish.QuadPart - st.QuadPart) * 1000.0f / frequency.QuadPart;

	printf("\n\nPure ASM time : %f\n\n", execution_time);

	printArr(result_array, ARR_SIZE);

	// MMX ASM block

	QueryPerformanceFrequency(&frequency);
	QueryPerformanceCounter(&st);

	for (int i = 0; i < ITERATIONS; i++)
	{
		_asm {
			xor ecx, ecx
			xor esi, esi

			mov ecx, OVERALL_AMOUNT_MMX

			arr_comparison :

				movq MM0, first_array[esi]
				movq MM1, second_array[esi]
				pmaxsw MM0, MM1
				movq result_array[esi], MM0

				add esi, REGISTER_IN_BYTES

			loop arr_comparison

			emms
		}
	}

	QueryPerformanceCounter(&finish);
	execution_time = (finish.QuadPart - st.QuadPart) * 1000.0f / frequency.QuadPart;

	printf("\n\nMMX ASM time : %f\n\n", execution_time);

	printArr(result_array, ARR_SIZE);

	_getch();
}

void randInit(short int arr[], int size, unsigned int seed)
{
	srand(seed);

	for (int i = 0; i < size; i++)
	{
		arr[i] = rand() * rand() % 100 + rand() % 5 * rand() % 13;
	}
}

void printArr(short int arr[], int size)
{
	putc('\n', stdin);
	for (int i = 0; i < size; i++)
	{
		printf("%4d", arr[i]);
	}
	putc('\n', stdin);
}
