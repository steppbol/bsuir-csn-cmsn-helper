#include <Windows.h>
#include <iostream>
#include "Matrix.h"
using namespace std;

typedef int TYPE;

#define CACHE_NUM 43 // sqrt(cacheSize * 0.7 / 3 / 4)
#define MAIN_WIDTH 40*CACHE_NUM
#define MAIN_HEIGHT 40*CACHE_NUM

int main() {
	Matrix<TYPE> matrixA(MAIN_WIDTH, MAIN_HEIGHT),
		matrixB(MAIN_WIDTH, MAIN_HEIGHT),
		matrixC, matrixD, matrixF, matrixG;

	matrixA.generateValues();
	matrixB.generateValues();

	ULONGLONG resultTime;
	ULONGLONG resultTimeNotVectorized;
	ULONGLONG resultTimeCached;
	ULONGLONG resultTimeAsUsual;

	ULONGLONG endTime;

	ULONGLONG startTime = GetTickCount64();
	matrixC = matrixA.multiplyVectorized(matrixB);
	endTime = GetTickCount64();

	resultTime = endTime - startTime;

	startTime = GetTickCount64();
	matrixD = matrixA.multiplyNotVectorized(matrixB);
	endTime = GetTickCount64();

	resultTimeNotVectorized = endTime - startTime;

	startTime = GetTickCount64();
	matrixF = matrixA.multiplyMatrixCache(matrixB, CACHE_NUM);
	endTime = GetTickCount64();

	resultTimeCached = endTime - startTime;

	startTime = GetTickCount64();
	matrixG = matrixA.multiplyMatrixAsUsual(matrixB);
	endTime = GetTickCount64();

	resultTimeAsUsual = endTime - startTime;

	cout << "Victorization Ticks: " << resultTime << "." << endl;
	cout << "Cached Ticks: " << resultTimeCached << "." << endl;
	cout << "Usual Ticks: " << resultTimeAsUsual << "." << endl;
	cout << "Not Victorized Ticks: " << resultTimeNotVectorized << "." << endl << endl;

	cout << "Vectorized speed up: x" << (double)resultTimeAsUsual / (double)resultTime << "." << endl;
	cout << "Cached speed up: x" << (double)resultTimeAsUsual / (double)resultTimeCached << "." << endl;
	cout << "Not vectorized speed up: x" << (double)resultTimeAsUsual / (double)resultTimeNotVectorized << "." << endl;

	if (matrixC.equals(matrixF) && matrixC.equals(matrixD) && matrixC.equals(matrixG)) {
		cout << "Matrices are equal." << endl;
	}
	else {
		cout << "Matrices are equal." << endl;
	}

	system("pause");
	return 0;
}