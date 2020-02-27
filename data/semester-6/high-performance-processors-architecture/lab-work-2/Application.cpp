#include <iostream>
#include <Windows.h>
#include <stdio.h>
#include <intrin.h>

#pragma intrinsic(__rdtsc)
using namespace std;

struct CacheSize
{
	size_t size;
	size_t lineSize;
};

CacheSize getCacheSize(int level);
void fillBlock(int fragment, int offset, int n);
void show(double* timerArray);

int* block;
int blockSize = (int)(getCacheSize(1).size);
int offset = 1 * 1024 * 1024;
int const maxWay = 20;

int main() {
	cout << "Cache Size: " << blockSize << " Bytes." << endl;

	block = (int*)malloc(offset * maxWay * sizeof(int));

	double timerArray[maxWay];
	register unsigned long long startTicks = 0;
	register unsigned long long endTicks = 0;
	register unsigned long long resultTicks = 0;
	register int index = 0;

	for (int i = 1; i <= maxWay; i++) {
		startTicks = 0;
		endTicks = 0;
		index = 0;

		fillBlock(blockSize / i, offset, i);

		startTicks = __rdtsc();
		for (int i = 1; i <= maxWay; i++) {
			do {
				index = block[index];
			} while (index != 0);
		}
		endTicks = __rdtsc();

		resultTicks = endTicks - startTicks;
		timerArray[i - 1] = (double)(resultTicks / 32768);
	}

	show(timerArray);
	system("pause");
	return 0;
}

CacheSize getCacheSize(int level) {
	CacheSize cacheLevel;
	cacheLevel.lineSize = 0;
	cacheLevel.size = 0;
	DWORD bufferSize = 0;
	DWORD i = 0;
	SYSTEM_LOGICAL_PROCESSOR_INFORMATION * buffer = 0;

	GetLogicalProcessorInformation(0, &bufferSize);
	buffer = (SYSTEM_LOGICAL_PROCESSOR_INFORMATION *)malloc(bufferSize);
	GetLogicalProcessorInformation(&buffer[0], &bufferSize);

	for (i = 0; i != bufferSize / sizeof(SYSTEM_LOGICAL_PROCESSOR_INFORMATION); ++i) {
		if (buffer[i].Relationship == RelationCache && buffer[i].Cache.Level == level) {
			cacheLevel.size = buffer[i].Cache.Size;
			cacheLevel.lineSize = buffer[i].Cache.LineSize;
			break;
		}
	}

	free(buffer);
	return cacheLevel;
}

void fillBlock(int fragment, int offset, int n)
{
	for (int i = 0; i < fragment; i++) {
		for (int j = 0; j < n; j++) {
			if (j < n - 1) {
				block[j * offset + i] = (j + 1) * offset + i;
			}
			else if (i < fragment - 1) {
				block[j * offset + i] = i + 1;
			}
			else {
				block[j * offset + i] = 0;
			}
		}
	}
}

void show(double* timerArray){
	double value = 0;

	for (int i = 0; i < maxWay; i++) {
		printf("%02i) ", i + 1);

		value = 0.0;
		printf("Time - %.0lf.\n", timerArray[i]);
	}
}
