#include <dos.h>
#include <stdio.h>
#include <conio.h>
#include <io.h>

#define PLAY_SOUND() _asm {
	_asm mov bx,0
	_asm mov ax, 0E07h
	_asm int 10h
}

unsigned int globalCount = 0;

void _interrupt _far(*oldVector)();

void _interrupt _far newVector();

void interrupt(*oldAlarm) ();

void reset() 
{

	if (oldAlarm != NULL) {
		setvect(0x4A, oldAlarm);
		outp(0xA1, (inp(0xA0) | 0x01)); // deny real time clock interrupt

		outp(0x70, 0x05);	//hours
		outp(0x71, 0x00);	//reset

		outp(0x70, 0x03);	//minutes
		outp(0x71, 0x00);	//reset

		outp(0x70, 0x01);	//seconds
		outp(0x71, 0x00);	//reset

		outp(0x70, 0xB);
		outp(0x71, (inp(0x71) & 0xDF)); // block alarm interrupt
	}
}

void _interrupt _far newVector()
{
	globalCount++;

	outp(0x70, 0x0C);
	//inp(0x71);

	outp(0x20, 0x20);
	outp(0xA0, 0x20);
}

void interrupt newAlarm()
{
	write(1, "\nALARM TRIGGERED!", 17);

	//PLAY_SOUND();
	//PLAY_SOUND();
	//PLAY_SOUND();
	//PLAY_SOUND();
	//PLAY_SOUND();
	//PLAY_SOUND();
	//PLAY_SOUND();
}

int bcdToDecimal(int number)
{
	return ((number / 16 * 10) + (number % 16));
}

int decimalToBCD(int number)
{
	return ((number / 10 * 16) + (number % 10));
}

void getTime()
{

	outp(0x70, 0x04); //hours
	printf("%02d:", bcdToDecimal(inp(0x71)));
	outp(0x70, 0x02); //minutes
	printf("%02d:", bcdToDecimal(inp(0x71)));
	outp(0x70, 0x00); //seconds
	printf("%02d\n", bcdToDecimal(inp(0x71)));
}

void getDate()
{
	outp(0x70, 0x07); //day
	printf("%02d.", bcdToDecimal(inp(0x71)));
	outp(0x70, 0x08); //month
	printf("%02d.", bcdToDecimal(inp(0x71)));
	outp(0x70, 0x32); //century
	printf("%02d", bcdToDecimal(inp(0x71)));
	outp(0x70, 0x09); //year
	printf("%02d\n", bcdToDecimal(inp(0x71)));
}

void setTime(int flag)
{
	int hours;
	do 
	{
		rewind(stdin);
		printf("H: ");
		scanf("%i", &hours);
	} while ((hours > 23 || hours < 0));
	hours = decimalToBCD(hours);

	int minutes;
	do
	{
		rewind(stdin);
		printf("M: ");
		scanf("%i", &minutes);
	} while (minutes > 59 || minutes < 0);
	minutes = decimalToBCD(minutes);

	int seconds;
	do 
	{
		rewind(stdin);
		printf("S: ");
		scanf("%i", &seconds);
	} while (seconds > 59 || seconds < 0);
	seconds = decimalToBCD(seconds);

	if (flag)
	{
		int i = 0;
		outp(0x70, 0x0A);
		for (; (inp(0x71) & 0x80) && i < 10000; i++)  //seventh A bit signals that time data are redy for extraction if 0
		{
			outp(0x70, 0x0A);
		}
		if (i == 10000)
		{
			printf("error!");
			return;
		}

		outp(0x70, 0xB);
		outp(0x71, inp(0x71) | 0x80); // deny time update

		outp(0x70, 0x04);
		outp(0x71, hours);
		outp(0x70, 0x02);
		outp(0x71, minutes);
		outp(0x70, 0x00);
		outp(0x71, seconds);

		outp(0x70, 0xB);
		outp(0x71, inp(0x71) & 0x7F); // allow RTC update
	}
	else
	{
		outp(0x70, 0x05);
		outp(0x71, hours);
		outp(0x70, 0x03);
		outp(0x71, minutes);
		outp(0x70, 0x01);
		outp(0x71, seconds);

		outp(0x70, 0xB);
		outp(0x71, (inp(0x71) | 0x20)); // allow alarm interrupt

		oldAlarm = getvect(0x4A);
		setvect(0x4A, newAlarm);
		outp(0xA1, (inp(0xA0) & 0xFE));	// allow RTC interrupt
	}
}

void delay(unsigned int ms)
{

	disable();

	oldVector = _dos_getvect(0x70);
	_dos_setvect(0x70, newVector);

	enable();
	//Unmask slave IRQ line
	outp(0xA1, inp(0xA1) & 0xFE); // allow RTC int

	outp(0x70, 0xB);
	outp(0x71, inp(0x71) | 0x40);  //allow periodic int

	globalCount = 0;

	while (globalCount <= ms);

	disable();

	_dos_setvect(0x70, oldVector);

	enable();

	return;
}

int main()
{
	while (1) 
	{
		getDate();
		getTime();
		printf("1. Set Time\n");
		printf("2. Set Alarm\n");
		printf("3. Sleep\n");
		printf("0. Exit\n\n");

		switch (getch())
		{
		case '1':
			setTime(1);
			break;

		case '2':
			setTime(0);
			break;

		case '3':
			int ms;
			rewind(stdin);
			printf("Input ms: ");
			scanf("%d", &ms);
			printf("Sleep...\n");
			delay(ms);
			printf("Continue working\n");
			break;

		case '0':
			reset();
			return 0;
		}
		default:
			continue;
	}
}