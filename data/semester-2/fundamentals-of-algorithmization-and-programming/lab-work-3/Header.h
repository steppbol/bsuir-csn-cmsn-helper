#pragma once

#define MS_SIZE 9
#define BUFF_SIZE 150
#define MAGIC_RED 77
#define MAGIC_GREEN 151
#define MAGIC_BLUE 28
#define MAGIC_BYTES 8
#define BIG_FILTER 16
#define AVERAGE_FILTER 8
#define SMALL_FILTER 1
#define SMALL_GAMMA 0
#define BIG_GAMMA 200
#define DECIMAL 10

enum TYPE{INT, FLOAT, DOUBLE};

typedef unsigned long int DWORD;
typedef unsigned short int WORD;
typedef long int LONG;
typedef unsigned char BYTE;


#pragma pack(push, 1)
struct BITMAPINFOHEADER
{
	DWORD  biSize;
	LONG   biWidth;
	LONG   biHeight;
	WORD   biPlanes;
	WORD   biBitCount;
	DWORD  biCompression;
	DWORD  biSizeImage;
	LONG   biXPelsPerMeter;
	LONG   biYPelsPerMeter;
	DWORD  biClrUsed;
	DWORD  biClrImportant;
};
#pragma pack(pop)

#pragma pack(push, 1)
struct BITMAPFILEHEADER
{
	WORD    bfType;
	DWORD   bfSize;
	WORD    bfReserved1;
	WORD    bfReserved2;
	DWORD   bfOffBits;
};
#pragma pack(pop)

#pragma pack(push, 1)
struct RGBTRIPLE
{
	BYTE    rgbBlue;
	BYTE    rgbGreen;
	BYTE    rgbRed;
};
#pragma pack(pop)

struct IMAGE_24
{
	BITMAPFILEHEADER f_header;
	BITMAPINFOHEADER i_header;
	RGBTRIPLE** raster;
	int indent;
};

IMAGE_24* fileToStruct(FILE* original);
void structToFile(IMAGE_24* image_st, FILE* new_file);
RGBTRIPLE** createMatrix(int Height, int Width);
void convertToNegative(IMAGE_24* image_st);
void convertToGrey(IMAGE_24* image_st);
void median_filtration(IMAGE_24* image_st);
BYTE find_median(BYTE ms[], int size);
void gamma_correction(IMAGE_24* image_st, double gamma);
void deleteStruct(IMAGE_24* image_st);
bool check_File(FILE* file);
void output_menu();
void securedNumerInput(char* input_message, TYPE data...);
void emergency_Exit();
void CUI_perform_filtrarion(IMAGE_24* image_st, bool &initialized);
void CUI_perform_correction(IMAGE_24* image_st, bool &initialized);
void CUI_performGreyConvertion(IMAGE_24* image_st, bool &initialized);
void CUI_performNegativeConvertion(IMAGE_24* image_st, bool &initialized);
void CUI_perform_Deletenig(IMAGE_24* image_st, bool &initialized, int &amount);
IMAGE_24* CUI_performOpening(bool &initialized, int &amount);
void CUI_saveProgress(IMAGE_24* image_st, bool &initialized, int &amount);
void CUI_exit(IMAGE_24* image_st, bool &initialized);