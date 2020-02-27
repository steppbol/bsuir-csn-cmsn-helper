#include "cuda_runtime.h"
#include "device_launch_parameters.h"
#include "helper_image.h"

#include <iostream> 
#include <cuda_runtime.h> 
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <intrin.h>
#include <windows.h>
#include <ctime>
#include <cmath>
#include <cstdlib>
#include <tuple>

#pragma comment(lib, "cudart") 

#define BLOCK_SIZE_X 1024
#define BLOCK_SIZE_Y 1
//#define IMAGE_WIDTH 11880
//#define IMAGE_HEIGHT 8648
#define IMAGE_WIDTH 6000
#define IMAGE_HEIGHT 4000
#define MAX_BLOCKS 50000

using namespace std;

void cpu_filterImage(BYTE*, BYTE*, int, int);
void cuda_filterImage(BYTE*, BYTE*, bool);
void cudaCheckStatus(cudaError_t);
void resizeImage(BYTE*, BYTE*, int, int);
bool checkEquality(BYTE*, BYTE*, int, int);
__global__ void cuda_filterImage(BYTE*, BYTE*, size_t, size_t);
__global__ void cuda_filterImageShared(BYTE*, BYTE*, size_t, size_t);
__device__ BYTE sumPixels(BYTE, BYTE, BYTE, BYTE, BYTE, BYTE, BYTE, BYTE, BYTE);
__device__ WORD pack(uchar3);
__device__ uchar2 unpack(WORD);

int main() {
	unsigned int imageWidth = 0, imageHeight = 0, channels = 0;

	const char primaryImagePath[] = "D:\\image.ppm";
	const char outputImageCpuPath[] = "D:\\imageCPU.ppm";
	const char outputImageGpuPath[] = "D:\\imageGPU.ppm";
	const char outputImageGpuSharedPath[] = "D:\\imageGPUshared.ppm";

	BYTE *primaryImage = NULL;

	__loadPPM(primaryImagePath, &primaryImage, &imageWidth, &imageHeight, &channels);

	auto* outputImageCpu = (BYTE*)malloc(imageWidth * imageHeight * sizeof(BYTE) * 3);
	auto* outputImageGpu = (BYTE*)malloc(imageWidth * imageHeight * sizeof(BYTE) * 3);
	auto* outputImageGpuShared = (BYTE*)malloc(imageWidth * imageHeight * sizeof(BYTE) * 3);
	auto* resizedImage = (BYTE*)malloc((imageWidth + 2) * (imageHeight + 2) * sizeof(BYTE) * 3);

	resizeImage(primaryImage, resizedImage, imageWidth, imageHeight);

	cpu_filterImage(resizedImage, outputImageCpu, imageWidth, imageHeight);
	cuda_filterImage(resizedImage, outputImageGpu, false);
	cuda_filterImage(resizedImage, outputImageGpuShared, true);

	__savePPM(outputImageCpuPath, outputImageCpu, imageWidth, imageHeight, channels);
	__savePPM(outputImageGpuPath, outputImageGpu, imageWidth, imageHeight, channels);
	__savePPM(outputImageGpuSharedPath, outputImageGpuShared, imageWidth, imageHeight, channels);

	cout << "Start compare" << endl;

	if (checkEquality(outputImageCpu, outputImageGpu, IMAGE_WIDTH, IMAGE_HEIGHT)
		&& checkEquality(outputImageGpu, outputImageGpuShared, IMAGE_WIDTH, IMAGE_HEIGHT)) {
		cout << "Results are equals!" << endl;
	}
	else {
		cout << "Results are NOT equals!" << endl;
	}

	free(primaryImage);
	free(resizedImage);
	free(outputImageCpu);
	free(outputImageGpu);
	free(outputImageGpuShared);
}

void cuda_filterImage(BYTE* inMatrix, BYTE* outMatrix, bool optimizationFlag) {
	float resultTime;

	BYTE* device_inMatrix;
	BYTE* device_outMatrix;

	cudaEvent_t cuda_startTime;
	cudaEvent_t cuda_endTime;

	cudaCheckStatus(cudaEventCreate(&cuda_startTime));
	cudaCheckStatus(cudaEventCreate(&cuda_endTime));

	int numOfBlocksInRow = (int)ceil((double)(IMAGE_WIDTH) / (BLOCK_SIZE_X * 2));
	int numOfBlockInColumn = IMAGE_HEIGHT;
	int blocksNeeded = numOfBlockInColumn * numOfBlocksInRow;
	int maxBlocksPerIteration = MAX_BLOCKS - MAX_BLOCKS % numOfBlocksInRow;

	for (int i = 0, int times = 1; i < blocksNeeded; i += maxBlocksPerIteration, times++) {
		int blocksInIteration = (blocksNeeded - i) < maxBlocksPerIteration ? blocksNeeded - i : maxBlocksPerIteration;
		size_t pitchInMatrix = 0, pitchOutMatrix = 0;
		int gridSizeY = blocksInIteration / numOfBlocksInRow;
		int gridSizeX = numOfBlocksInRow;

		cudaCheckStatus(cudaMallocPitch((void**)&device_inMatrix, &pitchInMatrix, (IMAGE_WIDTH + 2) * 3, gridSizeY + 2));
		cudaCheckStatus(cudaMallocPitch((void**)&device_outMatrix, &pitchOutMatrix, IMAGE_WIDTH * 3, gridSizeY));
		cudaCheckStatus(cudaMemcpy2D(
			device_inMatrix, pitchInMatrix,
			inMatrix, (IMAGE_WIDTH + 2) * 3,
			(IMAGE_WIDTH + 2) * 3, gridSizeY + 2,
			cudaMemcpyHostToDevice));

		dim3 blockSize(BLOCK_SIZE_X);
		dim3 gridSize(gridSizeX, gridSizeY);

		cudaCheckStatus(cudaEventRecord(cuda_startTime, NULL));

		if (optimizationFlag) {
			cuda_filterImageShared << < gridSize, blockSize >> > (device_inMatrix, device_outMatrix, pitchInMatrix, pitchOutMatrix);
		}
		else {
			cuda_filterImage << < gridSize, blockSize >> > (device_inMatrix, device_outMatrix, pitchInMatrix, pitchOutMatrix);
		}

		cudaCheckStatus(cudaPeekAtLastError());
		cudaCheckStatus(cudaEventRecord(cuda_endTime, NULL));
		cudaCheckStatus(cudaEventSynchronize(cuda_endTime));

		cudaCheckStatus(cudaEventElapsedTime(&resultTime, cuda_startTime, cuda_endTime));

		if (optimizationFlag) {
			printf("%d: CUDA time with optimization: %lf seconds\n", times, (double)resultTime / CLOCKS_PER_SEC);
		}
		else {
			printf("%d: CUDA time: %lf seconds\n", times, (double)resultTime / CLOCKS_PER_SEC);
		}

		cudaCheckStatus(cudaMemcpy2D(
			outMatrix, IMAGE_WIDTH * 3,
			device_outMatrix, pitchOutMatrix,
			IMAGE_WIDTH * 3, gridSizeY,
			cudaMemcpyDeviceToHost)
		);

		inMatrix = &inMatrix[(IMAGE_WIDTH + 2) * gridSizeY * times * 3];
		outMatrix = &outMatrix[IMAGE_WIDTH * gridSizeY * times * 3];

		cudaCheckStatus(cudaFree(device_inMatrix));
		cudaCheckStatus(cudaFree(device_outMatrix));
	}
}

void cpu_filterImage(BYTE* primaryImage, BYTE* outputImage, int imageWidth, int imageHeight) {
	primaryImage = &primaryImage[(imageWidth + 2 + 1) * 3];

	clock_t startTime, endTime;
	startTime = clock();
	for (auto i = 0; i < imageHeight; i++) {
		for (auto j = 0; j < imageWidth; j++) {
			for (auto k = 0; k < 3; k++) {
				short sum = 0;
				int index = 0;

				index = (i * (imageWidth + 2) + j) * 3 + k;
				sum += primaryImage[(i * (imageWidth + 2) + j) * 3 + k];
				sum += primaryImage[(i * (imageWidth + 2) + j + 1) * 3 + k];
				sum += primaryImage[(i * (imageWidth + 2) + j - 1) * 3 + k];

				sum += primaryImage[((i + 1) * (imageWidth + 2) + j) * 3 + k];
				sum += primaryImage[((i + 1) * (imageWidth + 2) + j + 1) * 3 + k];
				sum += primaryImage[((i + 1) * (imageWidth + 2) + j - 1) * 3 + k];

				sum += primaryImage[((i - 1) * (imageWidth + 2) + j) * 3 + k];
				sum += primaryImage[((i - 1) * (imageWidth + 2) + j + 1) * 3 + k];
				sum += primaryImage[((i - 1) * (imageWidth + 2) + j - 1) * 3 + k];

				sum = sum / 9;

				if (sum > 255) {
					sum = 255;
				}

				outputImage[(i * imageWidth + j) * 3 + k] = (BYTE)sum;
			}
		}
	}
	endTime = clock();
	printf("-  CPU time: %lf seconds\n", (double)(endTime - startTime) / CLOCKS_PER_SEC);
}

__global__ void cuda_filterImage(BYTE* inMatrix, BYTE* outMatrix, size_t pitchInMatrix, size_t pitchOutMatrix) {
	int remainderElements = (IMAGE_WIDTH % (blockDim.x * 2)) / 2;

	if (remainderElements != 0 && (blockIdx.x + 1) % gridDim.x == 0 && threadIdx.x >= remainderElements) {
		return;
	}

	WORD *startOfProcessingRow = (WORD*)&inMatrix[pitchInMatrix * blockIdx.y + blockIdx.x * blockDim.x * 2 * 3 + threadIdx.x * 2 * 3];

	WORD a1 = startOfProcessingRow[0];
	WORD a2 = startOfProcessingRow[1];
	WORD a3 = startOfProcessingRow[2];
	WORD a4 = startOfProcessingRow[3];
	WORD a5 = startOfProcessingRow[4];
	WORD a6 = startOfProcessingRow[5];

	WORD b1 = startOfProcessingRow[pitchInMatrix / 2];
	WORD b2 = startOfProcessingRow[pitchInMatrix / 2 + 1];
	WORD b3 = startOfProcessingRow[pitchInMatrix / 2 + 2];
	WORD b4 = startOfProcessingRow[pitchInMatrix / 2 + 3];
	WORD b5 = startOfProcessingRow[pitchInMatrix / 2 + 4];
	WORD b6 = startOfProcessingRow[pitchInMatrix / 2 + 5];

	WORD c1 = startOfProcessingRow[pitchInMatrix];
	WORD c2 = startOfProcessingRow[pitchInMatrix + 1];
	WORD c3 = startOfProcessingRow[pitchInMatrix + 2];
	WORD c4 = startOfProcessingRow[pitchInMatrix + 3];
	WORD c5 = startOfProcessingRow[pitchInMatrix + 4];
	WORD c6 = startOfProcessingRow[pitchInMatrix + 5];

	uchar2 aa1 = unpack(a1);
	uchar2 aa2 = unpack(a2);
	uchar2 aa3 = unpack(a3);
	uchar2 aa4 = unpack(a4);
	uchar2 aa5 = unpack(a5);
	uchar2 aa6 = unpack(a6);

	uchar2 bb1 = unpack(b1);
	uchar2 bb2 = unpack(b2);
	uchar2 bb3 = unpack(b3);
	uchar2 bb4 = unpack(b4);
	uchar2 bb5 = unpack(b5);
	uchar2 bb6 = unpack(b6);

	uchar2 cc1 = unpack(c1);
	uchar2 cc2 = unpack(c2);
	uchar2 cc3 = unpack(c3);
	uchar2 cc4 = unpack(c4);
	uchar2 cc5 = unpack(c5);
	uchar2 cc6 = unpack(c6);


	uchar3 firstPixel, secondPixel;
	firstPixel.x = sumPixels(aa1.x, aa2.y, aa4.x, bb1.x, bb2.y, bb4.x, cc1.x, cc2.y, cc4.x);
	firstPixel.y = sumPixels(aa1.y, aa3.x, aa4.y, bb1.y, bb3.x, bb4.y, cc1.y, cc3.x, cc4.y);
	firstPixel.z = sumPixels(aa2.x, aa3.y, aa5.x, bb2.x, bb3.y, bb5.x, cc2.x, cc3.y, cc5.x);
	secondPixel.x = sumPixels(aa2.y, aa4.x, aa5.y, bb2.y, bb4.x, bb5.y, cc2.y, cc4.x, cc5.y);
	secondPixel.y = sumPixels(aa3.x, aa4.y, aa6.x, bb3.x, bb4.y, bb6.x, cc3.x, cc4.y, cc6.x);
	secondPixel.z = sumPixels(aa3.y, aa5.x, aa6.y, bb3.y, bb5.x, bb6.y, cc3.y, cc5.x, cc6.y);

	outMatrix = &outMatrix[blockIdx.y * pitchOutMatrix + threadIdx.x * 2 * 3 + blockIdx.x * blockDim.x * 2 * 3];
	outMatrix[0] = firstPixel.x;
	outMatrix[1] = firstPixel.y;
	outMatrix[2] = firstPixel.z;
	outMatrix[3] = secondPixel.x;
	outMatrix[4] = secondPixel.y;
	outMatrix[5] = secondPixel.z;
}

__global__ void cuda_filterImageShared(BYTE* inMatrix, BYTE* outMatrix, size_t pitchInMatrix, size_t pitchOutMatrix) {
	int remainderElements = (IMAGE_WIDTH % (blockDim.x * 2)) / 2;

	if (remainderElements != 0 && (blockIdx.x + 1) % gridDim.x == 0 && threadIdx.x >= remainderElements) {
		return;
	}

	__shared__ WORD sharedMemoryIn[3][(BLOCK_SIZE_X + 1) * 3];
	__shared__ WORD sharedMemoryOut[BLOCK_SIZE_X * 3];

	WORD *startOfProcessingRow = (WORD*)&inMatrix[blockIdx.y * pitchInMatrix + blockIdx.x * blockDim.x * 2 * 3];
	WORD *outputRow = (WORD*)&outMatrix[blockIdx.y * pitchOutMatrix + blockIdx.x * blockDim.x * 2 * 3];

	if (threadIdx.x == 0) {
		WORD *tempPointer = &startOfProcessingRow[threadIdx.x];

		sharedMemoryIn[0][threadIdx.x] = tempPointer[0];
		sharedMemoryIn[0][threadIdx.x + 1] = tempPointer[1];
		sharedMemoryIn[0][threadIdx.x + 2] = tempPointer[2];

		sharedMemoryIn[1][threadIdx.x] = tempPointer[pitchInMatrix / 2];
		sharedMemoryIn[1][threadIdx.x + 1] = tempPointer[pitchInMatrix / 2 + 1];
		sharedMemoryIn[1][threadIdx.x + 2] = tempPointer[pitchInMatrix / 2 + 2];

		sharedMemoryIn[2][threadIdx.x] = tempPointer[pitchInMatrix];
		sharedMemoryIn[2][threadIdx.x + 1] = tempPointer[pitchInMatrix + 1];
		sharedMemoryIn[2][threadIdx.x + 2] = tempPointer[pitchInMatrix + 2];
	}

	startOfProcessingRow = &startOfProcessingRow[(threadIdx.x + 1) * 3];

	sharedMemoryIn[0][threadIdx.x * 3 + 3] = startOfProcessingRow[0];
	sharedMemoryIn[0][threadIdx.x * 3 + 4] = startOfProcessingRow[1];
	sharedMemoryIn[0][threadIdx.x * 3 + 5] = startOfProcessingRow[2];

	sharedMemoryIn[1][threadIdx.x * 3 + 3] = startOfProcessingRow[pitchInMatrix / 2];
	sharedMemoryIn[1][threadIdx.x * 3 + 4] = startOfProcessingRow[pitchInMatrix / 2 + 1];
	sharedMemoryIn[1][threadIdx.x * 3 + 5] = startOfProcessingRow[pitchInMatrix / 2 + 2];

	sharedMemoryIn[2][threadIdx.x * 3 + 3] = startOfProcessingRow[pitchInMatrix];
	sharedMemoryIn[2][threadIdx.x * 3 + 4] = startOfProcessingRow[pitchInMatrix + 1];
	sharedMemoryIn[2][threadIdx.x * 3 + 5] = startOfProcessingRow[pitchInMatrix + 2];


	__syncthreads();

	WORD a1 = sharedMemoryIn[0][threadIdx.x * 3];
	WORD a2 = sharedMemoryIn[0][threadIdx.x * 3 + 1];
	WORD a3 = sharedMemoryIn[0][threadIdx.x * 3 + 2];
	WORD a4 = sharedMemoryIn[0][threadIdx.x * 3 + 3];
	WORD a5 = sharedMemoryIn[0][threadIdx.x * 3 + 4];
	WORD a6 = sharedMemoryIn[0][threadIdx.x * 3 + 5];

	WORD b1 = sharedMemoryIn[1][threadIdx.x * 3];
	WORD b2 = sharedMemoryIn[1][threadIdx.x * 3 + 1];
	WORD b3 = sharedMemoryIn[1][threadIdx.x * 3 + 2];
	WORD b4 = sharedMemoryIn[1][threadIdx.x * 3 + 3];
	WORD b5 = sharedMemoryIn[1][threadIdx.x * 3 + 4];
	WORD b6 = sharedMemoryIn[1][threadIdx.x * 3 + 5];

	WORD c1 = sharedMemoryIn[2][threadIdx.x * 3];
	WORD c2 = sharedMemoryIn[2][threadIdx.x * 3 + 1];
	WORD c3 = sharedMemoryIn[2][threadIdx.x * 3 + 2];
	WORD c4 = sharedMemoryIn[2][threadIdx.x * 3 + 3];
	WORD c5 = sharedMemoryIn[2][threadIdx.x * 3 + 4];
	WORD c6 = sharedMemoryIn[2][threadIdx.x * 3 + 5];

	uchar2 aa1 = unpack(a1);
	uchar2 aa2 = unpack(a2);
	uchar2 aa3 = unpack(a3);
	uchar2 aa4 = unpack(a4);
	uchar2 aa5 = unpack(a5);
	uchar2 aa6 = unpack(a6);

	uchar2 bb1 = unpack(b1);
	uchar2 bb2 = unpack(b2);
	uchar2 bb3 = unpack(b3);
	uchar2 bb4 = unpack(b4);
	uchar2 bb5 = unpack(b5);
	uchar2 bb6 = unpack(b6);

	uchar2 cc1 = unpack(c1);
	uchar2 cc2 = unpack(c2);
	uchar2 cc3 = unpack(c3);
	uchar2 cc4 = unpack(c4);
	uchar2 cc5 = unpack(c5);
	uchar2 cc6 = unpack(c6);

	uchar3 firstPixel, secondPixel;
	firstPixel.x = sumPixels(aa1.x, aa2.y, aa4.x, bb1.x, bb2.y, bb4.x, cc1.x, cc2.y, cc4.x);
	firstPixel.y = sumPixels(aa1.y, aa3.x, aa4.y, bb1.y, bb3.x, bb4.y, cc1.y, cc3.x, cc4.y);
	firstPixel.z = sumPixels(aa2.x, aa3.y, aa5.x, bb2.x, bb3.y, bb5.x, cc2.x, cc3.y, cc5.x);
	secondPixel.x = sumPixels(aa2.y, aa4.x, aa5.y, bb2.y, bb4.x, bb5.y, cc2.y, cc4.x, cc5.y);
	secondPixel.y = sumPixels(aa3.x, aa4.y, aa6.x, bb3.x, bb4.y, bb6.x, cc3.x, cc4.y, cc6.x);
	secondPixel.z = sumPixels(aa3.y, aa5.x, aa6.y, bb3.y, bb5.x, bb6.y, cc3.y, cc5.x, cc6.y);

	WORD *tempSharedOut = &sharedMemoryOut[threadIdx.x * 3];

	tempSharedOut[0] = ((firstPixel.y << 8) | firstPixel.x);
	tempSharedOut[1] = ((secondPixel.x << 8) | firstPixel.z);
	tempSharedOut[2] = ((secondPixel.z << 8) | secondPixel.y);

	outputRow = &outputRow[threadIdx.x * 3];

	outputRow[0] = tempSharedOut[0];
	outputRow[1] = tempSharedOut[1];
	outputRow[2] = tempSharedOut[2];
}

__device__ WORD pack(uchar3 pixelLine)
{
	return (pixelLine.y << 8) | pixelLine.x;
}

__device__ uchar2 unpack(WORD c)
{
	uchar2 pixelLine;
	pixelLine.x = (BYTE)(c & 0xFF);
	pixelLine.y = (BYTE)((c >> 8) & 0xFF);

	return pixelLine;
}

__device__ BYTE sumPixels(BYTE a1, BYTE a2, BYTE a3, BYTE a4, BYTE a5, BYTE a6, BYTE a7, BYTE a8, BYTE a9)
{
	uint32_t result = 0;

	result = a1 + a2 + a3 + a4 + a5 + a6 + a7 + a8 + a9;

	result /= 9;

	if (result > 255) {
		result = 255;
	}

	return (BYTE)result;
}

void resizeImage(BYTE* primaryImage, BYTE* resizedImage, int imageWidth, int imageHeight) {
	for (int i = 0, int n = 0; i < imageHeight; i++, n++) {
		for (int j = 0, int m = 0; j < imageWidth; j++, m++) {
			resizedImage[(n * (imageWidth + 2) + m) * 3] = primaryImage[(i * imageWidth + j) * 3];
			resizedImage[(n * (imageWidth + 2) + m) * 3 + 1] = primaryImage[(i * imageWidth + j) * 3 + 1];
			resizedImage[(n * (imageWidth + 2) + m) * 3 + 2] = primaryImage[(i * imageWidth + j) * 3 + 2];

			if (j == 0 || j == imageWidth - 1) {
				m++;
				resizedImage[(n * (imageWidth + 2) + m) * 3] = primaryImage[(i * imageWidth + j) * 3];
				resizedImage[(n * (imageWidth + 2) + m) * 3 + 1] = primaryImage[(i * imageWidth + j) * 3 + 1];
				resizedImage[(n * (imageWidth + 2) + m) * 3 + 2] = primaryImage[(i * imageWidth + j) * 3 + 2];
			}
		}

		if (n == 0 || n == imageHeight) {
			i--;
		}
	}
}

void cudaCheckStatus(cudaError_t cudaStatus) {
	if (cudaStatus != cudaSuccess) {
		cout << "CUDA return error code: " << cudaStatus;
		cout << " " << cudaGetErrorString(cudaStatus) << endl;
		exit(-1);
	}
}

bool checkEquality(BYTE* firstImage, BYTE* secondImage, int imageWidth, int imageHeight) {
	for (int i = 0; i < imageWidth * imageHeight; i++) {
		if (fabs(firstImage[i] - secondImage[i]) > 1) {
			return false;
		}
	}
	return true;
}