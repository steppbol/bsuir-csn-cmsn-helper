#include "MatrixService.h"

T**** MatrixService::allocation(int matrixHeight, int matrixWidth, int cellHeight, int cellWidth) {
	T**** matrix = nullptr;

	try {
		matrix = new T***[matrixHeight];

		for (int i = 0; i < matrixHeight; i++) {
			matrix[i] = new T**[matrixWidth];

			for (int j = 0; j < matrixWidth; j++) {
				matrix[i][j] = new T*[cellHeight];

				for (int k = 0; k < cellHeight; k++) {
					matrix[i][j][k] = new T[cellWidth];

					for (int m = 0; m < cellWidth; m++) {
						matrix[i][j][k][m] = 0;
					}
				}
			}
		}
	}
	catch (...) {
		return nullptr;
	}

	return matrix;
}

bool MatrixService::generateValues(T**** matrix, int matrixHeight, int matrixWidth, int cellHeight, int cellWidth) {
	srand((unsigned)time(NULL));

	try {
		for (int i = 0; i < matrixHeight; i++) {
			for (int j = 0; j < matrixWidth; j++) {
				for (int k = 0; k < cellHeight; k++) {
					for (int m = 0; m < cellWidth; m++) {
						matrix[i][j][k][m] = (T)(rand() % 100);
					}
				}
			}
		}
	}
	catch (...) {
		return false;
	}

	return true;
}

T**** MatrixService::multiply(T**** matrixA, T**** matrixB, int matrixHeight, int matrixWidth, int cellHeight, int cellWidth) {
	T**** matrixC = nullptr;
	T* __restrict tempÑ = nullptr;
	T* __restrict tempB = nullptr;

	try {
		matrixC = allocation(matrixHeight, matrixWidth, cellHeight, cellWidth);

		for (int m = 0; m < matrixHeight; m++) {

			for (int n = 0; n < matrixWidth; n++) {

				for (int i = 0; i < cellHeight; i++) {
					tempÑ = matrixC[m][n][i];

					for (int j = 0; j < cellWidth; j++) {
						tempB = matrixB[m][n][j];

						for (int k = 0; k < cellWidth; k++) {
							tempÑ[k] += matrixA[m][n][i][j] * tempB[k];
						}

					}
				}
			}
		}
	}
	catch (...) {
		return nullptr;
	}

	return matrixC;
}

T**** MatrixService::multiplyNotVectorized(T**** matrixA, T**** matrixB, int matrixHeight, int matrixWidth, int cellHeight, int cellWidth) {
	T**** matrixC = nullptr;
	T* tempÑ = nullptr;
	T* tempB = nullptr;

	try {
		matrixC = allocation(matrixHeight, matrixWidth, cellHeight, cellWidth);

		for (int m = 0; m < matrixHeight; m++) {

			for (int n = 0; n < matrixWidth; n++) {

				for (int i = 0; i < cellHeight; i++) {
					tempÑ = matrixC[m][n][i];

					for (int j = 0; j < cellWidth; j++) {
						tempB = matrixB[m][n][j];

#pragma loop(no_vector)
						for (int k = 0; k < cellWidth; k++) {
							tempÑ[k] += matrixA[m][n][i][j] * tempB[k];
						}

					}
				}
			}
		}
	}
	catch (...) {
		return nullptr;
	}

	return matrixC;
}

T**** MatrixService::multiplySSE(T**** matrixA, T**** matrixB, int matrixHeight, int matrixWidth, int cellHeight, int cellWidth) {
	T**** matrixC = nullptr;
	T tempA = 0;
	T *tempB = nullptr;
	T* tempC = nullptr;

	__m128d reg0;
	__m128d reg1;
	__m128d reg2;

	try {
		matrixC = allocation(matrixHeight, matrixWidth, cellHeight, cellWidth);

		for (int m = 0; m < matrixHeight; m++) {
			for (int n = 0; n < matrixWidth; n++) {
				for (int i = 0; i < cellHeight; i++)
				{
					tempC = matrixC[m][n][i];

					for (int j = 0; j < cellWidth; j++)
					{
						tempA = matrixA[m][n][i][j];
						tempB = matrixB[m][n][j];
						reg1 = _mm_set1_pd(tempA);

						for (int k = 0; k < cellWidth; k += 4)
						{
							reg0 = _mm_load_pd(tempC + k);
							reg2 = _mm_load_pd(tempB + k);
							reg0 = _mm_add_pd(reg0, _mm_mul_pd(reg1, reg2));
							_mm_store_pd(tempC + k, reg0);
						}
					}
				}
			}
		}
	}
	catch (...) {
		return nullptr;
	}

	return matrixC;
}

bool MatrixService::equals(T**** matrixA, T**** matrixB, int matrixHeight, int matrixWidth, int cellHeight, int cellWidth) {
	try {
		for (int i = 0; i < matrixHeight; i++) {

			for (int j = 0; j < matrixWidth; j++) {

				for (int k = 0; k < cellHeight; k++) {

					for (int l = 0; l < cellWidth; l++) {

						if (matrixA[i][j][k][l] != matrixB[i][j][k][l]) {
							return false;
						}
					}
				}
			}
		}
	}
	catch (...) {
		return false;
	}

	return true;
}

bool MatrixService::destroy(T**** matrix, int matrixHeight, int matrixWidth, int cellHeight, int cellWidth) {
	try {

		for (int i = 0; i < matrixHeight; i++) {

			for (int j = 0; j < matrixWidth; j++) {

				for (int k = 0; k < cellHeight; k++) {
						delete[] matrix[i][j][k];
				}
			}
		}
		for (int i = 0; i < matrixHeight; i++) {

			for (int j = 0; j < matrixWidth; j++) {
					delete[] matrix[i][j];
			}
		}
		for (int i = 0; i < matrixHeight; i++) {
				delete[] matrix[i];
		}

		delete[] matrix;
	}
	catch (...) {
		return false;
	}

	return true;
}
