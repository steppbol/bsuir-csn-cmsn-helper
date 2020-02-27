#pragma once
#include <stdlib.h>
#include <time.h>
#include <emmintrin.h>

#define MATRIX_HEIGHT	48
#define MATRIX_WIDTH 	48

#define CELL_HEIGHT	100
#define CELL_WIDTH 	100

typedef double T;

class MatrixService {
public:
	MatrixService() {}
	~MatrixService() {}

	T**** allocation(int, int, int, int);
	bool generateValues(T****, int, int, int, int);
	T**** multiply(T****, T****, int, int, int, int);
	T**** multiplyNotVectorized(T****, T****, int, int, int, int);
	T**** multiplySSE(T****, T****, int, int, int, int);
	bool equals(T****, T****, int, int, int, int);
	bool destroy(T****, int, int, int, int);
};
