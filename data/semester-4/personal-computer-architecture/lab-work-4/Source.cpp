#include <stdio.h>
#include <conio.h>
#include <dos.h>

#define COUNT 114
#define DELAY 10
#define TIME_RUN 65536

void SoundGeneration();
void DivisionCoefficientComputation();
void StatusWord();
void RandomNumber();

void main() 
{
	clrscr();
	char choice;
	do
	{
		system("cls");
		printf("\n\n1. Generate sound.\n2. Division coefficient.\n3. Status word.\n4. Random number.\n0. Exit.\n");
		printf("\n2. Division coefficient.\n3. Status word.\n4. Random number.\n0. Exit.\n");
		printf("\n3. Status word.\n4. Random number.\n0. Exit.\n");
		printf("\n4. Random number.\n0. Exit.\n");
		printf("\n0. Exit.\n");
		rewind(stdin);
		printf("\nYour choise: ");
		scanf("%c", &choice);

		switch (choice) 
		{
		case '1': SoundGeneration(); break;
		case '2': DivisionCoefficientComputation(); break;
		case '3': StatusWord(); break;
		case '4': RandomNumber(); break;
		}
	} while (choice != '0');
}

void SoundGeneration() {
	int frequency[COUNT] = { 165, 196, 165, 196, 220, 196, 220, 196, 233, 196, 220, 196, 233, 247, 294,
							165, 196, 165, 196, 220, 196, 220, 196, 233, 196, 220, 196, 233, 247, 294,
							165, 196, 165, 196, 220, 196, 220, 196, 233, 196, 220, 196, 233, 247, 294,
							165, 196, 165, 196, 220, 196, 220, 196, 233, 220, 196, 165,
							82, 98, 82, 98, 110, 98, 110, 98, 117, 98, 110, 98, 117, 124, 148,
							82, 98, 82, 98, 110, 98, 110, 98, 117, 98, 110, 98, 117, 124, 148,
							82, 98, 82, 98, 110, 98, 110, 98, 117, 98, 110, 98, 117, 124, 148,
							82, 98, 82, 98,110, 98, 110, 98, 117, 110, 98, 82 };
	int durability[COUNT] = { 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 300,
							130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 300,
							130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 300,
							130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 400,
							130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 300,
							130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 300,
							130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 300,
							130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 300 };
	int delayCounter[COUNT] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 ,
								1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 ,
								1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 ,
								1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 30,
								1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
								1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 ,
								1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 ,
								1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 100 };
	long unsigned base = 1193180;
	int frequencyCounter;
	int divisionCoefficient;

	for (frequencyCounter = 0; frequencyCounter < COUNT; frequencyCounter++)
	{
		outp(0x43, 0xB6);
		divisionCoefficient = base / frequency[frequencyCounter];
		outp(0x42, divisionCoefficient % 256);	//записываем младший байт
		divisionCoefficient /= 256;
		outp(0x42, divisionCoefficient);		//записываем старший байт

		outp(0x61, inp(0x61) | 3);				// 11 -- включение динамика и 2ого канала
		delay(durability[frequencyCounter]);
		outp(0x61, inp(0x61) & 0xFC);			// выключение динамика и 2ого канала
		delay(delayCounter[frequencyCounter]);
	}
}

void DivisionCoefficientComputation() 
{
	unsigned long j;
	int Channel;
	int ports[] = { 0x40, 0x41, 0x42 };
	int controlWords[] = { 0, 64, 128 };
	unsigned divisionCoefficientLow, divisionCoefficientHigh, divisionCoefficient, max;

	printf("\n\nCoefficient of division: \n");

	for (Channel = 0; Channel < 3; Channel++) 
	{
		divisionCoefficient = 0;
		max = 0;

		if (Channel == 2)
		{
			outp(0x61, inp(0x61) | 3);		//turn ON
		}

		for (j = 0; j < TIME_RUN; j++)
		{
			outp(0x43, controlWords[Channel]);
			divisionCoefficientLow = inp(ports[Channel]);
			divisionCoefficientHigh = inp(ports[Channel]);
			divisionCoefficient = divisionCoefficientHigh * 256 + divisionCoefficientLow;

			if (divisionCoefficient > max)
			{
				max = divisionCoefficient;
			}
		}
		if (Channel == 2) 
		{
			outp(0x61, inp(0x61) & 0xFC);	//turn OFF
		}
		printf("\nChannel %d: %4X\n", Channel, max);
	}
}

void StatusWord()
{
	printf("\n\nStatus word: \n");
	char stateWord[8];
	int Channel, state;
	int ports[] = { 0x40, 0x41, 0x42 };
	int controlWords[] = { 226, 228, 232 };		//command words,
												//acording to the rules

	for (Channel = 0; Channel < 3; Channel++)
	{
		outp(0x43, controlWords[Channel]);
		state = inp(ports[Channel]);

		for (int i = 7; i >= 0; i--) 
		{
			stateWord[i] = state % 2 + '0';
			state /= 2;

		}
		printf("\nChannel %d: ", Channel);
		for (i = 0; i < 8; i++) 
		{
			printf("%c", stateWord[i]);
		}
		printf("\n");
	}
}

void RandomNumber()
{
	char choice;
	unsigned int limit = TIME_RUN - 1, numberLow, numberHigh, number;

	do
	{
		printf("\n\n1. Set a limit.\n2. Get a number.\n0. Exit\n");
		fflush(stdin);
		printf("\nYour choise: ");
		scanf("%s", &choice);

		switch (choice)
		{
		case '1':
		{
			do
			{
				printf("\nEnter a limit in range [1...65635].\n");
				fflush(stdin);
			} while (!scanf("%d", &limit) || limit < 1);

			outp(0x43, 0xB4);
			outp(0x42, limit % 256);
			limit /= 256;
			outp(0x42, limit);
			outp(0x61, inp(0x61) | 1);		//не включая динамик, но включая канал 2
			break;
		}
		case '2':
		{
			outp(0x43, 128);
			numberLow = inp(0x42);
			numberHigh = inp(0x42);
			number = numberHigh * 256 + numberLow;

			printf("\nRandom number: %u\n", number);

			break;
		}
		}
	} while (choice != '0');
	outp(0x61, inp(0x61) & 0xFC);
}