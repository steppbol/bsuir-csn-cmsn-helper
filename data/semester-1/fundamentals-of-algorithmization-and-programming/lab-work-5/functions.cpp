#include "header.h"

int stringLen(char* str)
{
	int i=0;
	while(str[i]!='\0')
	i++;
	return i;
}

int stringCmp(char* str1, char* str2)
{
	int result, i=0;
	while(str1[i]==str2[i])
	{
		if(str1[i]=='\0' && str2[i]=='\0') return 0;
		i++;
	}
	return result=str1[i]>str2[i]?1:-1;
}

void stringCat(char* str1, char* str2)
{
	int i, j, border;
	i = stringLen(str1);
	border=stringLen(str2);
	for(j=0; j <= border; j++)
		str1[i+j]=str2[j];
}

void stringSort(char** str_list, int arg_num)
{
	int i, j;
	char* temp;
	for (i=2; i<arg_num; ++i)
	{
		j=i-1;
		temp=str_list[i];
		while(j>=1 && stringCmp(temp,str_list[j])<0)
			str_list[j--+1]=str_list[j];
		str_list[j+1]=temp;
	}
}