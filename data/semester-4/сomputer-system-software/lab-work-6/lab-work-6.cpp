#include <string.h>
#include <stdio.h>
#include <iostream>

using namespace std;

#define countUseMemory 512 //Block memory
#define allMemory 32768 //32 kb

char allProgMemory[allMemory];
unsigned long avaibleMemory = allMemory;
char* blockBuff[countUseMemory];
unsigned int blockBuffCounter = 1;
unsigned int blockBuffSize[countUseMemory];

void* arrayOfPointer[64];

char* useBuff[countUseMemory];
unsigned int useBuffCounter = 0;
unsigned int block_size[countUseMemory];
unsigned int lastblockUsebuff;

void init();
void* myMalloc(unsigned long);
int myFree(void*);
void* myRealloc(void*, int );

int main()
{

    init();
    int choose;
    int size;
    int index;
    while(true)
    {
      cout << "1)malloc\n";
      cout << "2)free\n";
      cout << "3)realloc\n";
      cout << "4)quit\n";
      cin >> choose;
      switch (choose)
      {
        case 1:
        {
          cout << "Input index:";
          cin >> index;
          if(index > 64)
          {
            cout << "Wrong index!\n";
            break;
          }
          if(arrayOfPointer[index] == NULL)
          {
            cout << "Input size:";
            cin >> size;
            arrayOfPointer[index] = myMalloc(size);
            if(arrayOfPointer[index] == NULL)
            {
              cout << "Error! No memory!\n";
              return 1;
            }
            cout << "Use malloc:\n";
            printf("Use blocks - %d\n",useBuffCounter);
          } else {
            cout << "Error!\n";
            break;
          }
          break;
        }
        case 2:
        {
          cout << "Input index:";
          cin >> index;
          if(myFree(arrayOfPointer[index]) == -1)
          {
              cout << "Our block no found\n";
              break;
          }
          cout << "Use free:\n";
          printf("Use blocks - %d\n",useBuffCounter);
          break;
        }
        case 3:
        {
          cout << "Input index:";
          cin >> index;
          cout << "Input size:";
          cin >> size;
          arrayOfPointer[index] = myRealloc(arrayOfPointer[index], size);
          if(arrayOfPointer[index] == NULL)
          {
            cout << "Error! No memory!\n";
            return 1;
          }
          cout << "Use realloc:\n";
          printf("Use blocks - %d\n",useBuffCounter);
          break;
        }
        case 4:
        return 0;
        default:
        break;
      }
    }
}

void init()
{
    blockBuff[0] = allProgMemory;
    blockBuffSize[0] = avaibleMemory;
}

void* myMalloc(unsigned long size)
{
    unsigned long int last;
    char *tempArray;

    if (size > avaibleMemory)
        return NULL;

    tempArray = NULL;

    for (int i = 0; i < blockBuffCounter; i++)
        if (size <= blockBuffSize[i])
        {
            tempArray = blockBuff[i];
            last = i;
            break;
        }

    if (!tempArray)
        return NULL;

    useBuff[useBuffCounter] = tempArray;
    block_size[useBuffCounter] = size;
    useBuffCounter++;
    lastblockUsebuff++;
    blockBuff[last] = (char*)(last + size + 1);
    blockBuff[last] = blockBuff[last] - size;

    avaibleMemory -= size;
    return tempArray;
}

int myFree(void* memblock)
{
    unsigned int last;
    char* tempArray = 0;
    for (int i = 0; i < lastblockUsebuff; i++)
        if (memblock == useBuff[i])
        {
            tempArray = useBuff[i];
            last = i;
            break;
        }

    if (!tempArray)
      return -1;

    useBuff[last] = 0;
    useBuffCounter--;
    blockBuff[blockBuffCounter] = (char*)memblock;
    blockBuffSize[blockBuffCounter] = block_size[last];
    blockBuffCounter++;
    avaibleMemory += block_size[last];
    return 0;
}

void* myRealloc(void* memory_block, int size)
{
    unsigned int i, last;
    char* tempArray = 0;

    for (i = 0; i < lastblockUsebuff; i++)
        if (memory_block == useBuff[i])
            if (useBuff[i] == NULL)
                return myMalloc(size);


    if (size == 0)
    {
        myFree(memory_block);
        return NULL;
    }

    for (i = 0; i < lastblockUsebuff; i++)
        if (memory_block == useBuff[i])
        {
            tempArray = useBuff[i];
            last = i;
            break;
        }

    if (!tempArray)
        return NULL;

    myFree(memory_block);
    return myMalloc(size);
}
