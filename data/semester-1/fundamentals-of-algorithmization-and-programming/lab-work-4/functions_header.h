#ifndef FUNCTION_HEADER_H_
#define FUNCTION_HEADER_H_

int* create_array(int size);
int delete_array(int*ms);
int delete_element(int* ms, int size, int element);
void random_init(int* ms, int size);
void keyboard_init(int* ms, int size);
void output(int* ms, int size);
void sort (int* ms, int size, int choice);
void shellSort(int* ms, int size);
void hoarSort(int* ms, int left,int right);
void find_max(int* ms, int size, int* number_of_max, int* max);
void find_min(int* ms, int size, int* number_of_min, int* min);

#endif FUNCTION_HEADER_H_