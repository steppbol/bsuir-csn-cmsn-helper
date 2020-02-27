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

#pragma comment(lib, "cudart") 

#define BLOCK_SIZE_X 1024
//#define IMAGE_WIDTH 17374
//#define IMAGE_HEIGHT 27472
#define IMAGE_WIDTH 6000
#define IMAGE_HEIGHT 4000
#define MAX_BLOCKS 200000

using namespace std;

void cpu_filterImage(BYTE*, BYTE*, int, int);
void cuda_filterImage(BYTE*, BYTE*, bool);
void cudaCheckStatus(cudaError_t);
void resizeImage(BYTE*, BYTE*, int, int);
bool checkEquality(BYTE*, BYTE*, int, int);
__global__ void cuda_filterImage(BYTE*, BYTE*, size_t, size_t);
__global__ void cuda_filterImageShared(BYTE*, BYTE*, size_t, size_t);
__device__ BYTE sumPixels(BYTE, BYTE, BYTE, BYTE, BYTE, BYTE, BYTE, BYTE, BYTE);
__device__ WORD pack(uchar2);
__device__ uchar2 unpack(WORD);

int main() {
	unsigned int imageWidth = 0, imageHeight = 0, channels = 0;

	const char primaryImagePath[] = "D:\\image.pgm";
	const char outputImageCpuPath[] = "D:\\imageCPU.pgm";
	const char outputImageGpuPath[] = "D:\\imageGPU.pgm";
	const char outputImageGpuSharedPath[] = "D:\\imageGPUshared.pgm";

	BYTE *primaryImage = NULL;

	__loadPPM(primaryImagePath, &primaryImage, &imageWidth, &imageHeight, &channels);

	auto* outputImageCpu = (BYTE*)malloc(imageWidth * imageHeight * sizeof(BYTE));
	auto* outputImageGpu = (BYTE*)malloc(imageWidth * imageHeight * sizeof(BYTE));
	auto* outputImageGpuShared = (BYTE*)malloc(imageWidth * imageHeight * sizeof(BYTE));
	auto* resizedImage = (BYTE*)malloc((imageWidth + 2) * (imageHeight + 2) * sizeof(BYTE));

	resizeImage(primaryImage, resizedImage, imageWidth, imageHeight);

	cuda_filterImage(resizedImage, outputImageGpuShared, true);
	cuda_filterImage(resizedImage, outputImageGpu, false);
	cpu_filterImage(resizedImage, outputImageCpu, imageWidth, imageHeight);

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

		cudaCheckStatus(cudaMallocPitch((void**)&device_inMatrix, &pitchInMatrix, IMAGE_WIDTH + 2, gridSizeY + 2));
		cudaCheckStatus(cudaMallocPitch((void**)&device_outMatrix, &pitchOutMatrix, IMAGE_WIDTH, gridSizeY));
		cudaCheckStatus(cudaMemcpy2D(
			device_inMatrix, pitchInMatrix,
			inMatrix, IMAGE_WIDTH + 2,
			IMAGE_WIDTH + 2, gridSizeY + 2,
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
			outMatrix, IMAGE_WIDTH,
			device_outMatrix, pitchOutMatrix,
			IMAGE_WIDTH, gridSizeY,
			cudaMemcpyDeviceToHost)
		);

		inMatrix = &inMatrix[(IMAGE_WIDTH + 2) * gridSizeY * times];
		outMatrix = &outMatrix[IMAGE_WIDTH * gridSizeY * times];

		cudaCheckStatus(cudaFree(device_inMatrix));
		cudaCheckStatus(cudaFree(device_outMatrix));
	}
}

void cpu_filterImage(BYTE* primaryImage, BYTE* outputImage, int imageWidth, int imageHeight) {
	primaryImage = &primaryImage[imageWidth + 2 + 1];

	clock_t startTime, endTime;
	startTime = clock();
	for (auto i = 0; i < imageHeight; i++) {
		for (auto j = 0; j < imageWidth; j++) {
			short sum = 0;

			sum += primaryImage[i * (imageWidth + 2) + j];
			sum += primaryImage[i * (imageWidth + 2) + j + 1];
			sum += primaryImage[i * (imageWidth + 2) + j - 1];

			sum += primaryImage[(i + 1) * (imageWidth + 2) + j];
			sum += primaryImage[(i + 1) * (imageWidth + 2) + j + 1];
			sum += primaryImage[(i + 1) * (imageWidth + 2) + j - 1];

			sum += primaryImage[(i - 1) * (imageWidth + 2) + j];
			sum += primaryImage[(i - 1) * (imageWidth + 2) + j + 1];
			sum += primaryImage[(i - 1) * (imageWidth + 2) + j - 1];

			sum = sum / 9;

			if (sum > 255) {
				sum = 255;
			}
			else if (sum < 0) {
				sum = 0;
			}

			outputImage[i * imageWidth + j] = (unsigned char)sum;
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

	WORD *startOfProcessingRow = (WORD*)&inMatrix[pitchInMatrix * blockIdx.y + blockIdx.x * blockDim.x * 2 + threadIdx.x * 2];

	WORD a1 = startOfProcessingRow[0];
	WORD a2 = startOfProcessingRow[1];
	WORD b1 = startOfProcessingRow[pitchInMatrix / 2];
	WORD b2 = startOfProcessingRow[pitchInMatrix / 2 + 1];
	WORD c1 = startOfProcessingRow[pitchInMatrix];
	WORD c2 = startOfProcessingRow[pitchInMatrix + 1];

	uchar2 aa1 = unpack(a1);
	uchar2 aa2 = unpack(a2);
	uchar2 bb1 = unpack(b1);
	uchar2 bb2 = unpack(b2);
	uchar2 cc1 = unpack(c1);
	uchar2 cc2 = unpack(c2);

	BYTE firstPixel = sumPixels(aa1.x, aa1.y, aa2.x, bb1.x, bb1.y, bb2.x, cc1.x, cc1.y, cc2.x);
	BYTE secondPixel = sumPixels(aa1.y, aa2.x, aa2.y, bb1.y, bb2.x, bb2.y, cc1.y, cc2.x, cc2.y);

	outMatrix[blockIdx.y * pitchOutMatrix + blockIdx.x * blockDim.x * 2 + threadIdx.x * 2] = firstPixel;
	outMatrix[blockIdx.y * pitchOutMatrix + blockIdx.x * blockDim.x * 2 + threadIdx.x * 2 + 1] = secondPixel;
}

__global__ void cuda_filterImageShared(BYTE* inMatrix, BYTE* outMatrix, size_t pitchInMatrix, size_t pitchOutMatrix) {
	int remainderElements = (IMAGE_WIDTH % (blockDim.x * 2)) / 2;

	if (remainderElements != 0 && (blockIdx.x + 1) % gridDim.x == 0 && threadIdx.x >= remainderElements) {
		return;
	}

	__shared__ WORD sharedMemoryIn[3][BLOCK_SIZE_X + 1];
	__shared__ WORD sharedMemoryOut[BLOCK_SIZE_X];

	WORD *startOfProcessingRow = (WORD*)&inMatrix[blockIdx.y * pitchInMatrix + blockIdx.x * blockDim.x * 2 + threadIdx.x * 2];
	WORD *outputRow = (WORD*)&outMatrix[blockIdx.y * pitchOutMatrix + blockIdx.x * blockDim.x * 2];

	if (threadIdx.x == 0) {
		sharedMemoryIn[0][threadIdx.x] = startOfProcessingRow[0];
		sharedMemoryIn[1][threadIdx.x] = startOfProcessingRow[pitchInMatrix / 2];
		sharedMemoryIn[2][threadIdx.x] = startOfProcessingRow[pitchInMatrix];
	}

	sharedMemoryIn[0][threadIdx.x + 1] = startOfProcessingRow[1];
	sharedMemoryIn[1][threadIdx.x + 1] = startOfProcessingRow[pitchInMatrix / 2 + 1];
	sharedMemoryIn[2][threadIdx.x + 1] = startOfProcessingRow[pitchInMatrix + 1];

	__syncthreads();

	uchar2 aa1 = unpack(sharedMemoryIn[0][threadIdx.x]);
	uchar2 aa2 = unpack(sharedMemoryIn[0][threadIdx.x + 1]);

	uchar2 bb1 = unpack(sharedMemoryIn[1][threadIdx.x]);
	uchar2 bb2 = unpack(sharedMemoryIn[1][threadIdx.x + 1]);

	uchar2 cc1 = unpack(sharedMemoryIn[2][threadIdx.x]);
	uchar2 cc2 = unpack(sharedMemoryIn[2][threadIdx.x + 1]);

	uchar2 pixels;
	pixels.x = sumPixels(aa1.x, aa1.y, aa2.x, bb1.x, bb1.y, bb2.x, cc1.x, cc1.y, cc2.x);
	pixels.y = sumPixels(aa1.y, aa2.x, aa2.y, bb1.y, bb2.x, bb2.y, cc1.y, cc2.x, cc2.y);

	sharedMemoryOut[threadIdx.x] = pack(pixels);

	outputRow[threadIdx.x] = sharedMemoryOut[threadIdx.x];
}

__device__ WORD pack(uchar2 pixels)
{
	return (pixels.y << 8) | pixels.x;
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
			resizedImage[n * (imageWidth + 2) + m] = primaryImage[i * imageWidth + j];

			if (j == 0 || j == imageWidth - 1) {
				m++;
				resizedImage[n * (imageWidth + 2) + m] = primaryImage[i * imageWidth + j];
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
