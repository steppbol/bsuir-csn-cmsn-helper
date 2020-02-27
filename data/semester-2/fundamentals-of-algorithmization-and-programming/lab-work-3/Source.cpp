#include <stdio.h>
#include <conio.h>
#include <stdlib.h>
#include <string.h>
#include "Header.h"

int main()
{
	printf("%d\n", sizeof(BITMAPINFOHEADER));
	_getch();

	int amount = 0;
	bool initialized = false;
	IMAGE_24* image_st = nullptr;

	for (;;)
	{
		output_menu();
		switch (_getch())
		{
			case '1':
			{
				if (initialized)
				{
					deleteStruct(image_st);
					amount = 0;
					initialized = false;
				}

				image_st = CUI_performOpening(initialized, amount);

				break;
			}
			case '2':
			{
				CUI_performGreyConvertion(image_st, initialized);
				break;
			}
			case '3':
			{
				CUI_performNegativeConvertion(image_st, initialized);
				break;
			}
			case '4':
			{
				CUI_perform_filtrarion(image_st, initialized);
				break;
			}
			case '5':
			{
				CUI_perform_correction(image_st, initialized);
				break;
			}
			case '6':
			{
				CUI_saveProgress(image_st, initialized, amount);
				break;
			}
			case '7':
			{
				CUI_perform_Deletenig(image_st, initialized, amount);
				break;
			}
			case '0':
			{
				CUI_exit(image_st, initialized);
			}
		}
	}
	return 0;
}