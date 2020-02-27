#include "cuda_runtime.h"
#include "device_launch_parameters.h"

#include <iostream> 
#include <cuda_runtime.h> 
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <intrin.h>
#include <ctime>
#include <cmath>

#pragma comment(lib, "cudart") 

#define SIZE_M 123488
#define SIZE_N 1234
#define COUNT_OF_THREADS 1024
#define MAX_BLOCKS 200000

using namespace std;

void cpu_matrixOperation(short*, short*, int, int);
void cuda_matrixOperation(short*, short*, bool);
void cuda_checkStatus(cudaError_t);
void fillMatrix(short*, int, int);
bool checkEquality(short*, short*, int, int);

int main() {
	auto* initMatrix = (short*)malloc(SIZE_M * SIZE_N * sizeof(short));
	auto* cpu_outMatrix = (short*)malloc(SIZE_M * SIZE_N * sizeof(short));
	auto* cuda_outMatrix = (short*)malloc(SIZE_M * SIZE_N * sizeof(short));
	auto* cuda_outMatrixSharedMemory = (short*)malloc(SIZE_M * SIZE_N * sizeof(short));

	fillMatrix(initMatrix, SIZE_M, SIZE_N);

	/*for (auto i = 0; i < SIZE_N * SIZE_M; i++) {
		printf("%2d ", initMatrix[i]);

		if ((i + 1) % SIZE_M == 0) {
			cout << endl;
		}
	}*/

	cuda_matrixOperation(initMatrix, cuda_outMatrix, false);
	cuda_matrixOperation(initMatrix, cuda_outMatrixSharedMemory, true);

	cpu_matrixOperation(initMatrix, cpu_outMatrix, SIZE_M, SIZE_N);

	if (checkEquality(cuda_outMatrix, cpu_outMatrix, SIZE_M, SIZE_N)
		&& checkEquality(cuda_outMatrixSharedMemory, cuda_outMatrix, SIZE_M, SIZE_N)) {
		cout << "Results are equals!" << endl;
	}
	else {
		cout << "Results are NOT equals!" << endl;
	}

	/*cout << endl << "Not optimize" << endl;
	for (auto i = 0; i < SIZE_N * SIZE_M; i++) {
		printf("%3d ", cuda_outMatrix[i]);
		if ((i + 1) % (SIZE_M * 2) == 0) {
			cout << endl;
		}
	}
	cout << endl << "Shared memory" << endl;
	for (auto i = 0; i < SIZE_N * SIZE_M; i++) {
		printf("%3d ", cuda_outMatrixSharedMemory[i]);
		if ((i + 1) % (SIZE_M * 2) == 0) {
			cout << endl;
		}
	}
	cout << endl << "CPU" << endl;
	for (auto i = 0; i < SIZE_N * SIZE_M; i++) {
		printf("%3d ", cpu_outMatrix[i]);
		if ((i + 1) % (SIZE_M * 2) == 0) {
			cout << endl;
		}
	}*/

	free(initMatrix);
	free(cpu_outMatrix);
	free(cuda_outMatrix);
	free(cuda_outMatrixSharedMemory);
}

__global__ void cuda_matrixOperationKernel(int* inMatrix, short* outMatrix, int numOfBlocksInRow) {
	int remainderElements = SIZE_M % COUNT_OF_THREADS;

	if (remainderElements != 0 && (blockIdx.x + 1) % numOfBlocksInRow == 0 && threadIdx.x >= remainderElements) {
		return;
	}

	int *startOfResultRow = &inMatrix[SIZE_M * (blockIdx.x / numOfBlocksInRow)];
	outMatrix = &outMatrix[SIZE_M * (blockIdx.x / numOfBlocksInRow) * 2];

	int elements = 0;
	int countOfThreads = 0;

	if (remainderElements != 0 && (blockIdx.x + 1) % numOfBlocksInRow == 0) {
		countOfThreads = remainderElements;
	}
	else {
		countOfThreads = COUNT_OF_THREADS;
	}

	if (threadIdx.x < (countOfThreads / 2)) {
		elements = startOfResultRow[(blockIdx.x % numOfBlocksInRow) * COUNT_OF_THREADS / 2 + threadIdx.x];
	}
	else {
		elements = startOfResultRow[threadIdx.x % (countOfThreads / 2) + SIZE_M / 2 + (blockIdx.x % numOfBlocksInRow) * COUNT_OF_THREADS / 2];
	}

	short firstElement = (short)elements;
	short secondElement = (short)(elements >> 16);

	int offset = COUNT_OF_THREADS * 2 * (blockIdx.x % numOfBlocksInRow);

	if (threadIdx.x < (countOfThreads / 2)) {
		outMatrix[threadIdx.x * 2 * 2 + offset] = firstElement;
		outMatrix[(threadIdx.x * 2 + 1) * 2 + offset] = secondElement;
	}
	else {
		outMatrix[(threadIdx.x - countOfThreads / 2) * 2 * 2 + 1 + offset] = firstElement;
		outMatrix[((threadIdx.x - countOfThreads / 2) * 2 + 1) * 2 + 1 + offset] = secondElement;
	}
}

__global__ void cuda_matrixSharedMemoryOperationKernel(int* inMatrix, int* outMatrix, int numOfBlocksInRow) {
	int remainderElements = SIZE_M % COUNT_OF_THREADS;

	__shared__ int sharedMemory[COUNT_OF_THREADS];
	__shared__ short sharedMemoryOut[COUNT_OF_THREADS * 2];

	if (remainderElements != 0 && (blockIdx.x + 1) % numOfBlocksInRow == 0 && threadIdx.x >= remainderElements) {
		return;
	}

	int *startOfResultRow = &inMatrix[SIZE_M * (blockIdx.x / numOfBlocksInRow)];
	outMatrix = &outMatrix[SIZE_M * (blockIdx.x / numOfBlocksInRow)];

	int countOfThreads = 0;

	if (remainderElements != 0 && (blockIdx.x + 1) % numOfBlocksInRow == 0) {
		countOfThreads = remainderElements;
	}
	else {
		countOfThreads = COUNT_OF_THREADS;
	}

	if (threadIdx.x < (countOfThreads / 2)) {
		sharedMemory[threadIdx.x] = startOfResultRow[(blockIdx.x % numOfBlocksInRow) * COUNT_OF_THREADS / 2 + threadIdx.x];
	}
	else {
		sharedMemory[threadIdx.x] = startOfResultRow[threadIdx.x % (countOfThreads / 2) + SIZE_M / 2 + (blockIdx.x % numOfBlocksInRow) * COUNT_OF_THREADS / 2];
	}

	int elements = sharedMemory[threadIdx.x];
	short firstElement = (short)elements;
	short secondElement = (short)(elements >> 16);

	int offset = COUNT_OF_THREADS * 2 * (blockIdx.x % numOfBlocksInRow);

	if (threadIdx.x < (countOfThreads / 2)) {
		sharedMemoryOut[threadIdx.x * 2 * 2] = firstElement;
		sharedMemoryOut[(threadIdx.x * 2 + 1) * 2] = secondElement;
	}
	else {
		sharedMemoryOut[(threadIdx.x - countOfThreads / 2) * 2 * 2 + 1] = firstElement;
		sharedMemoryOut[((threadIdx.x - countOfThreads / 2) * 2 + 1) * 2 + 1] = secondElement;
	}

	__syncthreads();

	outMatrix[offset / 2 + threadIdx.x] = ((int*)sharedMemoryOut)[threadIdx.x];
}

void cuda_matrixOperation(short* inMatrix, short* outMatrix, bool optimizationFlag) {
	float resultTime;

	short* device_inMatrix;
	short* device_outMatrix;

	cudaEvent_t cuda_startTime;
	cudaEvent_t cuda_endTime;

	cuda_checkStatus(cudaEventCreate(&cuda_startTime));
	cuda_checkStatus(cudaEventCreate(&cuda_endTime));

	int numOfBlocksInRow = (int)ceil((double)SIZE_M / COUNT_OF_THREADS);
	int blocksNeeded = (SIZE_N * numOfBlocksInRow) / 2;
	int maxBlocksPerIteration = MAX_BLOCKS - MAX_BLOCKS % numOfBlocksInRow;

	for (int i = 0, int times = 0; i < blocksNeeded; i += maxBlocksPerIteration, times++) {
		int blocksInIteration = (blocksNeeded - i) < maxBlocksPerIteration ? blocksNeeded - i : maxBlocksPerIteration;

		int numOfRows =  (blocksInIteration / numOfBlocksInRow) * 2;

		cuda_checkStatus(cudaMalloc(&device_inMatrix, SIZE_M * numOfRows * sizeof(short)));
		cuda_checkStatus(cudaMalloc(&device_outMatrix, SIZE_M  * numOfRows * sizeof(short)));
		cuda_checkStatus(cudaMemcpy(
			device_inMatrix, 
			&inMatrix[SIZE_M * (maxBlocksPerIteration / numOfBlocksInRow) * 2 * times],
			SIZE_M * numOfRows * sizeof(short), cudaMemcpyHostToDevice)
		);

		dim3 blockSize(COUNT_OF_THREADS);
		dim3 gridSize(blocksInIteration);

		cuda_checkStatus(cudaEventRecord(cuda_startTime, NULL));

		if (optimizationFlag) {
			cuda_matrixSharedMemoryOperationKernel <<< gridSize, blockSize >>> ((int*)device_inMatrix, (int*)device_outMatrix, numOfBlocksInRow);
		}
		else {
			cuda_matrixOperationKernel <<< gridSize, blockSize >>> ((int*)device_inMatrix, device_outMatrix, numOfBlocksInRow);
		}

		cuda_checkStatus(cudaPeekAtLastError());
		cuda_checkStatus(cudaEventRecord(cuda_endTime, NULL));
		cuda_checkStatus(cudaEventSynchronize(cuda_endTime));

		cuda_checkStatus(cudaEventElapsedTime(&resultTime, cuda_startTime, cuda_endTime));

		if (optimizationFlag) {
			printf("%d: CUDA time with optimization: %lf seconds\n", times, (double)resultTime / CLOCKS_PER_SEC);
		}
		else {
			printf("%d: CUDA time: %lf seconds\n", times, (double)resultTime / CLOCKS_PER_SEC);
		}

		cuda_checkStatus(cudaMemcpy(
			&outMatrix[SIZE_M * (maxBlocksPerIteration / numOfBlocksInRow) * 2 * times],
			device_outMatrix,
			SIZE_M * numOfRows * sizeof(short), cudaMemcpyDeviceToHost)
		);

		cuda_checkStatus(cudaFree(device_inMatrix));
		cuda_checkStatus(cudaFree(device_outMatrix));
	}
}

void cpu_matrixOperation(short* inMatrix, short* outMatrix, int sizeOfM, int sizeOfN) {
	clock_t startTime, endTime;
	startTime = clock();
	for (auto i = 0; i < sizeOfM; i++) {
		for (auto j = 0; j < sizeOfN; j++) {
			int a = (j + 1) % 2 == 0 ? 1 : 0;
			outMatrix[(j / 2) * sizeOfM * 2 + a + i * 2] = inMatrix[i + sizeOfM * j];
		}
	}
	endTime = clock();
	printf("CPU time: %lf seconds\n", (double)(endTime - startTime) / CLOCKS_PER_SEC);
}

void fillMatrix(short* matrix, int sizeOfM, int sizeOfN) {
	for (int i = 0; i < sizeOfN; i++) {
		for (int j = 0; j < sizeOfM; j++) {
			matrix[sizeOfM * i + j] = rand() % 20 + 1;
		}
	}
}

void cuda_checkStatus(cudaError_t cudaStatus) {
	if (cudaStatus != cudaSuccess) {
		cout << "CUDA return error code: " << cudaStatus;
		cout << " " << cudaGetErrorString(cudaStatus) << endl;
		exit(-1);
	}
}

bool checkEquality(short* inMatrix, short* outMatrix, int sizeOfM, int sizeOfN) {
	for (int i = 0; i < sizeOfN * sizeOfM; i++) {
		if (inMatrix[i] != outMatrix[i]) {
			return false;
		}
	}
	return true;
}