#pragma once
#include <stdlib.h>
#include <time.h>
#include <emmintrin.h>
#include <iostream>

template <typename T>
class Matrix
{
private:
	int matrixWidth;
	int matrixHeight;

	T** matrixPointer;
public:
	Matrix(int matrixWidth, int matrixHeight)
	{
		this->matrixWidth = matrixWidth;
		this->matrixHeight = matrixHeight;

		this->matrixPointer = nullptr;

		try {
			matrixPointer = new T*[matrixHeight];

			for (int i = 0; i < matrixHeight; i++) {
				matrixPointer[i] = new T[matrixWidth];
				for (int j = 0; j < matrixWidth; j++) {
					matrixPointer[i][j] = 0;

				}
			}
		}
		catch (...) {
			matrixPointer = nullptr;
		}
	}

	Matrix() {}

	bool equals(Matrix<T> &matrix) {
		for (int i = 0; i < matrixHeight; i++) {
			for (int j = 0; j < matrixWidth; j++) {
				if (this->matrixPointer[i][j] != matrix.getMatrixPointer()[i][j]) {
					return false;
				}
			}
		}
		return true;
	}

	Matrix<T> multiplyVectorized(Matrix<T> &matrixB) {
		if (this->matrixHeight != matrixB.getMatrixWidth())
		{
			return matrixB;
		}

		Matrix<T> resultMatrix(this->matrixWidth, matrixB.getMatrixHeight());

		mulMatrixVectorized(matrixB, resultMatrix);

		return resultMatrix;
	}

	void mulMatrixVectorized(Matrix<T> &matrixB, Matrix<T> &resultMatrix)
	{
		T* __restrict resultMatrixRow = nullptr;
		T* __restrict matrixBRow = nullptr;

		for (int i = 0; i < matrixB.getMatrixHeight(); i++) {
			resultMatrixRow = resultMatrix.getMatrixPointer()[i];
			for (int j = 0; j < this->matrixWidth; j++) {
				matrixBRow = matrixB.getMatrixPointer()[j];
				for (int k = 0; k < this->matrixWidth; k++) {
					resultMatrixRow[k] += this->matrixPointer[i][j] * matrixBRow[k];
				}
			}
		}
	}

	Matrix<T> multiplyNotVectorized(Matrix<T> &matrixB) {
		if (this->matrixHeight != matrixB.getMatrixWidth())
		{
			return matrixB;
		}

		Matrix<T> resultMatrix(this->matrixWidth, matrixB.getMatrixHeight());

		mulMatrixNotVectorized(matrixB, resultMatrix);

		return resultMatrix;
	}

	void mulMatrixNotVectorized(Matrix<T> &matrixB, Matrix<T> &resultMatrix)
	{
		T* __restrict resultMatrixRow = nullptr;
		T* __restrict matrixBRow = nullptr;

		for (int i = 0; i < matrixB.getMatrixHeight(); i++) {
			resultMatrixRow = resultMatrix.getMatrixPointer()[i];
			for (int j = 0; j < this->matrixWidth; j++) {
				matrixBRow = matrixB.getMatrixPointer()[j];
#pragma loop(no_vector) 
				for (int k = 0; k < this->matrixWidth; k++) {
					resultMatrixRow[k] += this->matrixPointer[i][j] * matrixBRow[k];
				}
			}
		}
	}

	bool generateValues() {
		srand((unsigned)time(NULL));

		try {
			for (int i = 0; i < this->matrixHeight; i++) {
				for (int j = 0; j < matrixWidth; j++) {
					matrixPointer[i][j] = (T)(rand() % 100);
				}
			}
		}

		catch (...) {
			return false;
		}

		return true;
	}
	Matrix<T> multiplyMatrixCache(Matrix<T> &matrixB, int cacheSize) {
		Matrix<T> resultMatrix(this->matrixWidth, matrixB.getMatrixHeight());

		mulMatrixCache(matrixB, resultMatrix, cacheSize);

		return resultMatrix;
	}

	void mulMatrixCache(Matrix<T> &matrixB, Matrix<T> &resultMatrix, int cacheSize) {
		int newSize = matrixB.getMatrixWidth() / cacheSize;

		for (int blockRow = 0; blockRow < newSize; blockRow++)
			for (int blockCol = 0; blockCol < newSize; blockCol++) {
				for (int blockTemp = 0; blockTemp < newSize; blockTemp++)
					for (int row = blockRow * cacheSize; row < cacheSize + blockRow * cacheSize; row++)
						for (int col = blockCol * cacheSize; col < cacheSize + blockCol * cacheSize; col++) {
							const T firstMatrixTemp = this->matrixPointer[row][col];
							for (int temp = blockTemp * cacheSize; temp < cacheSize + blockTemp * cacheSize; temp++)
								resultMatrix.getMatrixPointer()[row][temp] += firstMatrixTemp * matrixB.getMatrixPointer()[col][temp];
								//mulMatrixCacheVectorized(matrixB, resultMatrix, col, row, blockTemp, cacheSize, firstMatrixTemp);
						}

			}
	}

	void mulMatrixCacheVectorized(Matrix<T> &matrixB, Matrix<T> &resultMatrix, int col, int row, int blockTemp, int cacheSize, int firstMatrixTemp)
	{
		T** __restrict resultMatrixRow = resultMatrix.getMatrixPointer();
		T** __restrict matrixBRow = matrixB.getMatrixPointer();

		for (int temp = blockTemp * cacheSize; temp < cacheSize + blockTemp * cacheSize; temp++)
			resultMatrixRow[row][temp] += firstMatrixTemp * matrixBRow[col][temp];
	}

	Matrix<T> multiplyMatrixAsUsual(Matrix<T> &matrixB) {
		Matrix<T> resultMatrix(this->matrixWidth, matrixB.getMatrixHeight());

		for (int row = 0; row < this->matrixWidth; row++)
			for (int col = 0; col < this->matrixWidth; col++)
				for (int temp = 0; temp < this->matrixWidth; temp++)
					resultMatrix.getMatrixPointer()[row][col] += this->matrixPointer[row][temp] * matrixB.getMatrixPointer()[temp][col];

		return resultMatrix;
	}

	T** getMatrixPointer()
	{
		return this->matrixPointer;
	}

	int getMatrixWidth()
	{
		return this->matrixWidth;
	}

	int getMatrixHeight()
	{
		return this->matrixHeight;
	}
};