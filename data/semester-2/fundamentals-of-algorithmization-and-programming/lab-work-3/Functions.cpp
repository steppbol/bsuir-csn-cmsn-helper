#include <stdio.h>
#include <conio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include "Header.h"

IMAGE_24* fileToStruct(FILE* original)
{
	rewind(original);

	IMAGE_24* image_st = (IMAGE_24*)calloc(1, sizeof(IMAGE_24));
	if (image_st == nullptr) return nullptr;

	image_st->indent = 0;

	fread(&(image_st->f_header), sizeof(BITMAPFILEHEADER), 1, original);
	fread(&(image_st->i_header), sizeof(BITMAPINFOHEADER), 1, original);

	if (image_st->i_header.biWidth % 4 != 0)
	{
		image_st->indent = (image_st->i_header.biWidth * image_st->i_header.biBitCount / 8) % 4;
	}

	image_st->raster = createMatrix(image_st->i_header.biHeight, image_st->i_header.biWidth);
	if (image_st->raster == nullptr) return nullptr;
	
	for (int i = 0; i < image_st->i_header.biHeight; i++)
	{
		for (int j = 0; j < image_st->i_header.biWidth; j++)
		{
			fread(&(image_st->raster[i][j]), sizeof(RGBTRIPLE), 1, original);
		}
		fseek(original, image_st->indent, SEEK_CUR);
	}

	rewind(original);

	return image_st;
}

void structToFile(IMAGE_24* image_st, FILE* new_file)
{
	BYTE zero = 0;

	rewind(new_file);

	fwrite(&(image_st->f_header), sizeof(BITMAPFILEHEADER), 1, new_file);
	fwrite(&(image_st->i_header), sizeof(BITMAPINFOHEADER), 1, new_file);

	for (int i = 0; i < image_st->i_header.biHeight; i++)
	{
		for (int j = 0; j < image_st->i_header.biWidth; j++)
		{
			fwrite(&(image_st->raster[i][j]), sizeof(RGBTRIPLE), 1, new_file);
		}
		if (image_st->indent) fwrite(&zero, sizeof(BYTE), image_st->indent, new_file);
	}

	rewind(new_file);
}

RGBTRIPLE** createMatrix(int Height, int Width)
{
	int i;

	RGBTRIPLE** matrix = (RGBTRIPLE**)calloc(Height, sizeof(RGBTRIPLE*));
	if (matrix == nullptr) emergency_Exit();

	for (i = 0; i < Height; i++)
	{
		matrix[i] = (RGBTRIPLE*)calloc(Width, sizeof(RGBTRIPLE));
		if (matrix[i] == nullptr)
		{
			i--;
			for (; i >= 0; i--)
			{
				free(matrix[i]);
			}
			free(matrix);
			return nullptr;
		}
	}

	return matrix;
}
 
void convertToNegative(IMAGE_24* image_st)
{
	for (int i = 0; i < image_st->i_header.biHeight; i++)
	{
		for (int j = 0; j < image_st->i_header.biWidth; j++)
		{
			image_st->raster[i][j].rgbRed = 255 - image_st->raster[i][j].rgbRed;
			image_st->raster[i][j].rgbGreen = 255 - image_st->raster[i][j].rgbGreen;
			image_st->raster[i][j].rgbBlue = 255 - image_st->raster[i][j].rgbBlue;
		}
	}
}

void convertToGrey(IMAGE_24* image_st)
{
	BYTE bright = 0;
	BYTE temp_red = 0;
	BYTE temp_green = 0;
	BYTE temp_blue = 0;

	for (int i = 0; i < image_st->i_header.biHeight; i++)
	{
		for (int j = 0; j < image_st->i_header.biWidth; j++)
		{
			bright = (((image_st->raster[i][j].rgbRed * MAGIC_RED) + (image_st->raster[i][j].rgbGreen * MAGIC_GREEN) + (image_st->raster[i][j].rgbBlue * MAGIC_BLUE)) >> MAGIC_BYTES);

			image_st->raster[i][j].rgbRed = bright;
			image_st->raster[i][j].rgbGreen = bright;
			image_st->raster[i][j].rgbBlue = bright;
		}
	}
}

void median_filtration(IMAGE_24* image_st)		// размер аперутуры в данной функции 3х3
{
	int k = 0;
	int row = 0;
	int coloumn = 0;
	int size = 0;
	int row_limit = 3;
	int coloumn_limit = 3;
	int amount = 0;
	BYTE red_array[MS_SIZE];
	BYTE green_array[MS_SIZE];
	BYTE blue_array[MS_SIZE];

	for (int i = 0; i < image_st->i_header.biHeight; i++)
	{
		for (int j = 0; j < image_st->i_header.biWidth; j++)
		{
			row = i - 1;										
			row_limit = 3;

			if (i == image_st->i_header.biHeight - 1 || i == 0) // если ряд крайний
			{
				row_limit = 2;
				if(i == 0) row = i;
			}

			for (int x = row, k = 0; row_limit--; x++)
			{
				coloumn = j - 1;
				coloumn_limit = 3;

				if (j == image_st->i_header.biWidth - 1 || j == 0)	// если столбец крайний
				{
					coloumn_limit = 2;
					if (j == 0) coloumn = j;
				}

				for (int y = coloumn; coloumn_limit--; y++)
				{
					red_array[k] = image_st->raster[x][y].rgbRed;		// заполняем апертру
					green_array[k] = image_st->raster[x][y].rgbGreen;	// для каждого цвета
					blue_array[k] = image_st->raster[x][y].rgbBlue;		// по отдельности

					k++;		// индекс одномерных массивов, хранящих цвета пикселей
					size = k;
				}
			}

			image_st->raster[i][j].rgbRed = find_median(red_array, size);
			image_st->raster[i][j].rgbGreen = find_median(green_array, size);
			image_st->raster[i][j].rgbBlue = find_median(blue_array, size);

			memset(red_array, 0, MS_SIZE * sizeof(BYTE));
			memset(green_array, 0, MS_SIZE * sizeof(BYTE));
			memset(blue_array, 0, MS_SIZE * sizeof(BYTE));

			size = 0;
		}
	}
}

BYTE find_median(BYTE ms[], int size)
{
	int i;
	int j;
	int median_index;
	BYTE temp;
	BYTE median;

	for (i = 1; i < size; i++)
	{
		j = i - 1;
		temp = ms[i];
		while (j >= 0 && temp < ms[j])
		{
			ms[j + 1] = ms[j];
			j--;
		}
		ms[j + 1] = temp;
	}

	median_index = (int)(size / 2);
	median = ms[median_index];

	return median;
}

void gamma_correction(IMAGE_24* image_st, double gamma)
{
	for (int i = 0; i < image_st->i_header.biHeight; i++)
	{
		for (int j = 0; j < image_st->i_header.biWidth; j++)
		{
			image_st->raster[i][j].rgbRed = pow(double(image_st->raster[i][j].rgbRed) / 255, gamma) * 255;
			image_st->raster[i][j].rgbGreen = pow(double(image_st->raster[i][j].rgbGreen) / 255, gamma) * 255;
			image_st->raster[i][j].rgbBlue  = pow(double(image_st->raster[i][j].rgbBlue) / 255, gamma) * 255;
		}
	}
}

void deleteStruct(IMAGE_24* image_st)
{
	for (int i = 0; i < image_st->i_header.biHeight; i++)
	{
		free(image_st->raster[i]);
	}

	free(image_st->raster);

	free(image_st);
}

bool check_File(FILE* file)
{
	if (file == nullptr) return false;

	fseek(file, 0, SEEK_END);
	if (!ftell(file))
	{
		fclose(file);
		return false;
	}

	rewind(file);
	BITMAPFILEHEADER header;
	const WORD bmp_type = 19778;

	fread(&header, sizeof(BITMAPFILEHEADER), 1, file);
	if (header.bfType == bmp_type)
	{
		rewind(file);
		return true;
	}
	else return false;
}

void securedNumerInput(char* input_message, TYPE data...) // первым аргументом в списке пиременных параметров является
{														  // нижняя граница ввода, после нее идет верхняя, а затем указатель
	int i = 0;											  // на вводимую переменную
	int check = 1;
	va_list ptr;

	__crt_va_start(ptr, data);

	switch (data)
	{
		case INT:
		{
			int lower_border = __crt_va_arg(ptr, int);
			int upper_border = __crt_va_arg(ptr, int);
			int* number = __crt_va_arg(ptr, int*);

			do
			{
				printf("%s:\n", input_message);
				if (i > 0)
				{
					printf("\aError! Please repeat the input\n");
				}
				rewind(stdin);
				check = scanf_s("%d", number);
				system("cls");

				i++;

			} while (!check || *number <= lower_border || *number > upper_border);

			return;
		}

		case FLOAT:
		{
			float lower_border = __crt_va_arg(ptr, float);
			float upper_border = __crt_va_arg(ptr, float);
			float* number = __crt_va_arg(ptr, float*);

			do
			{
				printf("%s:\n", input_message);
				if (i > 0)
				{
					printf("\aError! Please repeat the input\n");
				}
				rewind(stdin);
				check = scanf_s("%f", number);
				system("cls");

				i++;

			} while (!check || *number <= lower_border || *number > upper_border);

			return;
		}

		case DOUBLE:
		{
			double lower_border = __crt_va_arg(ptr, double);
			double upper_border = __crt_va_arg(ptr, double);
			double* number = __crt_va_arg(ptr, double*);

			do
			{
				printf("%s:\n", input_message);
				if (i > 0)
				{
					printf("\aError! Please repeat the input\n");
				}
				rewind(stdin);
				check = scanf_s("%lf", number);
				system("cls");
				
				i++;

			} while (!check || *number <= lower_border || *number > upper_border);

			return;
		}
	}
}

void emergency_Exit()
{
	system("cls");
	printf("The memory has not been allocated or the file is corrupted!\nPress any key to exit the programm...");
	_getch();
	exit(1);
}

void CUI_perform_filtrarion(IMAGE_24* image_st, bool &initialized)
{
	int precision = 0;

	system("cls");

	if (!initialized)
	{
		system("cls");
		printf("The original file is not initialized!");
		printf("\n\nPress any key to continue...");
		_getch();
		return;
	}

	for (;;)
	{
		system("cls");
		printf("Choose filtering degree:\n\n1. Great\n2. Average\n3. Small");
		switch (_getch())
		{
		case '1': precision = BIG_FILTER; break;
		case '2': precision = AVERAGE_FILTER; break;
		case '3': precision = SMALL_FILTER; break;
		default: continue;
		}
		break;
	}
	system("cls");
	printf("Proceeding...");

	do
		median_filtration(image_st);
	while (precision--);

	system("cls");
	printf("Success!\n\nPress any key to continue...");
	_getch();
}

void CUI_perform_correction(IMAGE_24* image_st, bool &initialized)
{
	char str[BUFF_SIZE] = { 0 };

	system("cls");

	double gamma;
	if (!initialized)
	{
		system("cls");
		printf("The original file is not initialized!");
		printf("\n\nPress any key to continue...");
		_getch();
		return;
	}

	strcpy_s(str, BUFF_SIZE, "Please enter the value of gamma.\nRemember, that the bigger gamma is, the darker image will get");

	securedNumerInput(str, DOUBLE, (double)SMALL_GAMMA, (double)BIG_GAMMA, &gamma);

	gamma_correction(image_st, gamma);

	system("cls");
	printf("Success!\n\nPress any key to continue...");
	_getch();
}

void CUI_performGreyConvertion(IMAGE_24* image_st, bool &initialized)
{
	system("cls");

	if (!initialized)
	{
		system("cls");
		printf("The original file is not initialized!");
		printf("\n\nPress any key to continue...");
		_getch();
		return;
	}

	convertToGrey(image_st);

	system("cls");
	printf("Success!\n\nPress any key to continue...");
	_getch();
}

void CUI_performNegativeConvertion(IMAGE_24* image_st, bool &initialized)
{
	system("cls");

	if (!initialized)
	{
		system("cls");
		printf("The original file is not initialized!");
		printf("\n\nPress any key to continue...");
		_getch();
		return;
	}

	convertToNegative(image_st);

	system("cls");
	printf("Success!\n\nPress any key to continue...");
	_getch();
}

void CUI_perform_Deletenig(IMAGE_24* image_st, bool &initialized, int &amount)
{
	system("cls");

	if (!initialized)
	{
		system("cls");
		printf("The original file is not initialized!");
		printf("\n\nPress any key to continue...");
		_getch();
		return;
	}

	deleteStruct(image_st);
	
	amount = 0;
	initialized = false;

	system("cls");
	printf("Success!\n\nPress any key to continue...");
	_getch();
}

IMAGE_24* CUI_performOpening(bool &initialized, int &amount)
{
	IMAGE_24* image_st;
	FILE* original_image;
	char str[BUFF_SIZE] = { 0 };
	int i = 0;

	do
	{																					
		system("cls");
		if (i > 0) printf("Erorr! No file found...\n");
		printf("Please enter the full name of the appropriate file\n");
		fgets(str, BUFF_SIZE, stdin);
		str[strlen(str) - 1] = '\0';
		fopen_s(&original_image, str, "rb");
		i++;
	} while (!check_File(original_image));

	image_st = fileToStruct(original_image);
	if (image_st == nullptr)
	{
		fclose(original_image);
		emergency_Exit();
	}

	fclose(original_image);
	initialized = true;

	system("cls");
	printf("Success!\n\nPress any key to continue...");
	_getch();

	return image_st;
}

void CUI_saveProgress(IMAGE_24* image_st, bool &initialized, int &amount)
{
	FILE* new_image;
	char str[BUFF_SIZE] = { 0 };

	system("cls");

	if (!initialized)
	{
		system("cls");
		printf("The original file is not initialized!");
		printf("\n\nPress any key to continue...");
		_getch();
		return;
	}
	amount++;

	memset(str, '\0', BUFF_SIZE);
	_itoa_s(amount, str, BUFF_SIZE, DECIMAL);
	strcat_s(str, BUFF_SIZE, "_modified.bmp");

	fopen_s(&new_image, str, "wb");
	if (new_image == nullptr)
	{
		deleteStruct(image_st);
		emergency_Exit();
	}

	structToFile(image_st, new_image);
	fclose(new_image);

	system("cls");
	printf("Success!\n\nPress any key to continue...");
	_getch();

}

void CUI_exit(IMAGE_24* image_st,  bool &initialized)
{
	if (!initialized)
	{
		exit(EXIT_SUCCESS);
	}

	deleteStruct(image_st);
	exit(EXIT_SUCCESS);
}

void output_menu()
{
	system("cls");
	printf("Please select the action to perform\n\n");
	printf("================================================================================\n\n");
	printf("1. Open file and initialize structure\n");
	printf("2. Convert to shades of grey\n");
	printf("3. Convert to negative\n");
	printf("4. Perform median filtration of the image\n");
	printf("5. Perform gamma correction\n");
	printf("6. Save the result (new file will be created)\n");
	printf("7. Delete all current proress\n");
	printf("0. Exit the program\n\n");
	printf("================================================================================\n\n");
}




















































/*void convertToGrey(IMAGE_24* image_st)
{
	BYTE bright = 0;
	BYTE temp_red = 0;
	BYTE temp_green = 0;
	BYTE temp_blue = 0;

	for (int i = 0; i < image_st->i_header.biHeight; i++)
	{
		for (int j = 0; j < image_st->i_header.biWidth; j++)
		{
			temp_red = image_st->raster[i][j].rgbRed * 0.299;
			temp_green = image_st->raster[i][j].rgbGreen * 0.587;
			temp_blue = image_st->raster[i][j].rgbBlue * 0.114;

			bright = temp_red + temp_green + temp_blue;

			image_st->raster[i][j].rgbRed = bright;
			image_st->raster[i][j].rgbGreen = bright;
			image_st->raster[i][j].rgbBlue = bright;
		}
	}
}*/