#include "MatrixService.h"
#include <Windows.h>
#include <iostream>

using namespace std;

int main() {
	MatrixService matrixService;

	T**** matrixA = matrixService.allocation(MATRIX_HEIGHT, MATRIX_WIDTH, CELL_HEIGHT, CELL_WIDTH);
	T**** matrixB = matrixService.allocation(MATRIX_HEIGHT, MATRIX_WIDTH, CELL_HEIGHT, CELL_WIDTH);
	T**** matrixC;
	T**** matrixD;

	matrixService.generateValues(matrixA, MATRIX_HEIGHT, MATRIX_WIDTH, CELL_HEIGHT, CELL_WIDTH);
	matrixService.generateValues(matrixB, MATRIX_HEIGHT, MATRIX_WIDTH, CELL_HEIGHT, CELL_WIDTH);

	ULONGLONG resultTimeSSE;
	ULONGLONG resultTime;
	ULONGLONG resultTimeNotVectorized;

	ULONGLONG endTime;

	ULONGLONG startTime = GetTickCount64();
	matrixC = matrixService.multiply(matrixA, matrixB, MATRIX_HEIGHT, MATRIX_WIDTH, CELL_HEIGHT, CELL_WIDTH);
	endTime = GetTickCount64();

	resultTime = endTime - startTime;

	startTime = GetTickCount64();
	matrixD = matrixService.multiplySSE(matrixA, matrixB, MATRIX_HEIGHT, MATRIX_WIDTH, CELL_HEIGHT, CELL_WIDTH);
	endTime = GetTickCount64();

	resultTimeSSE = endTime - startTime;

	startTime = GetTickCount64();
	matrixD = matrixService.multiplyNotVectorized(matrixA, matrixB, MATRIX_HEIGHT, MATRIX_WIDTH, CELL_HEIGHT, CELL_WIDTH);
	endTime = GetTickCount64();

	resultTimeNotVectorized = endTime - startTime;

	cout << "Victorization Ticks: " << resultTime << "." << endl;
	cout << "Not Victorized Ticks: " << resultTimeNotVectorized << "." << endl;
	cout << "SSE Ticks: " << resultTimeSSE << ".\n" << endl;

	cout << "Vectorized speed up: x" << (double)resultTimeNotVectorized / (double)resultTime << "." << endl;
	cout << "SSE speed up: x" << (double)resultTimeNotVectorized / (double)resultTimeSSE << ".\n" << endl;

	if (matrixService.equals(matrixC, matrixD, MATRIX_HEIGHT, MATRIX_WIDTH, CELL_HEIGHT, CELL_WIDTH)) {
		cout << "Matrices by ASM and by Victorization are equal." << endl;
	}
	else {
		cout << "Matrices by ASM and by Victorization are not equal." << endl;
	}

	matrixService.destroy(matrixA, MATRIX_HEIGHT, MATRIX_WIDTH, CELL_HEIGHT, CELL_WIDTH);
	matrixService.destroy(matrixB, MATRIX_HEIGHT, MATRIX_WIDTH, CELL_HEIGHT, CELL_WIDTH);
	matrixService.destroy(matrixC, MATRIX_HEIGHT, MATRIX_WIDTH, CELL_HEIGHT, CELL_WIDTH);
	matrixService.destroy(matrixD, MATRIX_HEIGHT, MATRIX_WIDTH, CELL_HEIGHT, CELL_WIDTH);

	system("pause");
	return 0;
}