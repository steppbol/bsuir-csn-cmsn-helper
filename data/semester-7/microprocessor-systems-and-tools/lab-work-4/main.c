#include <msp430.h>

typedef unsigned char uint8_t;

#define SET_COLUMN_ADDRESS_LSB        0x00
#define SET_COLUMN_ADDRESS_MSB        0x10
#define SET_PAGE_ADDRESS              0xB0

#define SET_SEG_DIRECTION             0xA0
#define SET_COM_DIRECTION             0xC0

#define SET_POWER_CONTROL             0x2F // Управление питанием. PC[0] – усилитель, PC[1] — регулятор, PC[2] — повторитель. 0 — отключено, 1 — включено
#define SET_SCROLL_LINE               0x40 // Установка начальной линии скроллинга SL=0..63
#define SET_VLCD_RESISTOR_RATIO       0x27 // Установка уровня внутреннего резисторного делителя PC = [0..7].Используется для управления контрастом.
#define SET_ELECTRONIC_VOLUME_MSB     0x81 // Регулировка контраста. Двухбайтная команда. PM[5..0] PM = 0..63.
#define SET_ELECTRONIC_VOLUME_LSB     0x0F
#define SET_ALL_PIXEL_ON              0xA4 // Включение всех пикселей. 0 – отображение содержимого памяти, 1 – все пиксели включены (содержимое памяти сохраняется).
#define SET_INVERSE_DISPLAY           0xA6 // Включение инверсного режима. 0 — нормальное отображение содержимого памяти, 1 — инверсное.
#define SET_DISPLAY_ENABLE            0xAF // Отключение экрана. 0 — экран отключен, 1 — включен.
#define SET_LCD_BIAS_RATIO            0xA2 // Смещение напряжения делителя: 0 – 1/9, 1 – 1/7.
#define SET_ADV_PROGRAM_CONTROL0_MSB  0xFA // Расширенное управление. ТС — температурная компенсация 0 = -0.05, 1 = -0.11 % / °С;
#define SET_ADV_PROGRAM_CONTROL0_LSB  0x90 // WC – циклический сдвиг столбцов 0 = нет, 1 = есть; WP –циклический сдвиг страниц 0 = нет, 1 = есть.

#define CD              BIT6
#define CS              BIT4

uint8_t Dogs102x6_initMacro[] = {
	SET_SCROLL_LINE,
	SET_SEG_DIRECTION,
	SET_COM_DIRECTION,
	SET_ALL_PIXEL_ON,
	SET_INVERSE_DISPLAY,
	SET_LCD_BIAS_RATIO,
	SET_POWER_CONTROL,
	SET_VLCD_RESISTOR_RATIO,
	SET_ELECTRONIC_VOLUME_MSB,
	SET_ELECTRONIC_VOLUME_LSB,
	SET_ADV_PROGRAM_CONTROL0_MSB,
	SET_ADV_PROGRAM_CONTROL0_LSB,
	SET_DISPLAY_ENABLE,
	SET_PAGE_ADDRESS,
	SET_COLUMN_ADDRESS_MSB,
	SET_COLUMN_ADDRESS_LSB
};

uint8_t MODE_COMMANDS[2][1] = { {SET_SEG_DIRECTION}, {SET_SEG_DIRECTION | 1} };

int CURRENT_ORIENTATION = 0; // 0 - default, 1 - mirror horizontal
int COLUMN_START_ADDRESS = 30; // 0 - default (30), 1 - mirror horizontal (0)
int CURRENT_NUMBER = -5417;
int SUM_NUMBER = +981;

uint8_t symbols[12][11] = {
		{0x20, 0x20, 0x20, 0x20, 0x20, 0xF8, 0x20, 0x20, 0x20, 0x20, 0x20}, // plus
		{0x00, 0x00, 0x00, 0x00, 0x00, 0xF8, 0x00, 0x00, 0x00, 0x00, 0x00}, // minus
		{0xF8, 0xF8, 0xD8, 0xD8, 0xD8, 0xD8, 0xD8, 0xD8, 0xD8, 0xF8, 0xF8}, // num0
		{0xF8, 0xF8, 0x30, 0x30, 0x30, 0x30, 0xF0, 0xF0, 0x70, 0x70, 0x30}, // num1
		{0xF8, 0xF8, 0xC0, 0xC0, 0xC0, 0xF8, 0xF8, 0x18, 0x18, 0xF8, 0xF8}, // num2
		{0xF8, 0xF8, 0x18, 0x18, 0x18, 0xF8, 0xF8, 0x18, 0x18, 0xF8, 0xF8}, // num3
		{0x18, 0x18, 0x18, 0x18, 0xF8, 0xF8, 0xD8, 0xD8, 0xD8, 0xD8, 0xD8}, // num4
		{0xF8, 0xF8, 0x18, 0x18, 0x18, 0xF8, 0xF8, 0xC0, 0xC0, 0xF8, 0xF8}, // num5
		{0xF8, 0xF8, 0xD8, 0xD8, 0xD8, 0xF8, 0xF8, 0xC0, 0xC0, 0xF8, 0xF8}, // num6
		{0xC0, 0xC0, 0xE0, 0x70, 0x38, 0x18, 0x18, 0x18, 0x18, 0xF8, 0xF8}, // num7
		{0xF8, 0xF8, 0xD8, 0xD8, 0xD8, 0xF8, 0xD8, 0xD8, 0xD8, 0xF8, 0xF8}, // num8
		{0xF8, 0xF8, 0x18, 0x18, 0xF8, 0xF8, 0xD8, 0xD8, 0xD8, 0xF8, 0xF8}  // num9
};

int lenHelper(int number);
int abs(int number);
int pow(int base, int exponent);
void printNumber(void);

void Dogs102x6_clearScreen(void);
void Dogs102x6_setAddress(uint8_t pa, uint8_t ca);
void Dogs102x6_writeData(uint8_t* sData, uint8_t i);
void Dogs102x6_writeCommand(uint8_t* sCmd, uint8_t i);
void Dogs102x6_backlightInit(void);
void Dogs102x6_init(void);

#pragma vector = PORT1_VECTOR
__interrupt void buttonS1(void)
{
	volatile int i = 0;

	for (i = 0; i < 2000; i++);

	if ((P1IN & BIT7) == 0) {
		CURRENT_NUMBER += SUM_NUMBER;

		Dogs102x6_clearScreen();

		printNumber();

		for (i = 0; i < 2000; i++);
	}

	P1IFG = 0;
}

#pragma vector = PORT2_VECTOR
__interrupt void buttonS2(void)
{
	volatile int i = 0;

	for (i = 0; i < 2000; i++);

	if ((P2IN & BIT2) == 0) {
		if (CURRENT_ORIENTATION == 0) {
			COLUMN_START_ADDRESS = 0;
			CURRENT_ORIENTATION = 1;
		}
		else {
			COLUMN_START_ADDRESS = 30;
			CURRENT_ORIENTATION = 0;
		}

		Dogs102x6_writeCommand(MODE_COMMANDS[CURRENT_ORIENTATION], 1);
		Dogs102x6_clearScreen();
		printNumber();

		for (i = 0; i < 2000; i++);
	}

	P2IFG = 0;
}

int main(void) {
	WDTCTL = WDTPW | WDTHOLD;

	P1DIR &= ~BIT7;
	P1OUT |= BIT7;
	P1REN |= BIT7;
	P1IE |= BIT7;
	P1IES |= BIT7;
	P1IFG = 0;

	P2DIR &= ~BIT2;
	P2OUT |= BIT2;
	P2REN |= BIT2;
	P2IE |= BIT2;
	P2IES |= BIT2;
	P2IFG = 0;

	Dogs102x6_init();
	Dogs102x6_backlightInit();
	Dogs102x6_clearScreen();
	printNumber();

	__bis_SR_register(GIE);

	return 0;
}

void printNumber(void) {
	int nDigits = lenHelper(CURRENT_NUMBER);
	int number = CURRENT_NUMBER;

	Dogs102x6_setAddress(0, COLUMN_START_ADDRESS);
	Dogs102x6_writeData(number > 0 ? symbols[0] : symbols[1], 11);

	int i = 0;
	int divider = pow(10, nDigits - 1);

	number = abs(number);

	for (i = 1; i <= nDigits; i++) {
		int digit = number / divider;

		Dogs102x6_setAddress(i, COLUMN_START_ADDRESS);
		Dogs102x6_writeData(symbols[digit + 2], 11);

		number = number % divider;
		divider /= 10;
	}
}

int lenHelper(int number) {
	number = abs(number);

	if (number >= 10000) return 5;
	if (number >= 1000) return 4;
	if (number >= 100) return 3;
	if (number >= 10) return 2;

	return 1;
}

int abs(int number) {
	return number > 0 ? number : number * (-1);
}

int pow(int base, int exponent) {
	int i = 0;
	int result = base;

	for (i = 0; i < exponent - 1; i++) {
		result *= base;
	}

	return result;
}

void Dogs102x6_clearScreen(void)
{
	uint8_t LcdData[] = { 0x00 };
	uint8_t p, c;

	// 8 total pages in LCD controller memory
	for (p = 0; p < 8; p++)
	{
		Dogs102x6_setAddress(p, 0);
		// 132 total columns in LCD controller memory
		for (c = 0; c < 132; c++)
		{
			Dogs102x6_writeData(LcdData, 1);
		}
	}
}

void Dogs102x6_setAddress(uint8_t pa, uint8_t ca)
{
	uint8_t cmd[1];

	if (pa > 7)
	{
		pa = 7;
	}

	if (ca > 101)
	{
		ca = 101;
	}

	cmd[0] = SET_PAGE_ADDRESS + (7 - pa);
	uint8_t H = 0x00;
	uint8_t L = 0x00;
	uint8_t ColumnAddress[] = { SET_COLUMN_ADDRESS_MSB, SET_COLUMN_ADDRESS_LSB };

	L = (ca & 0x0F);
	H = (ca & 0xF0);
	H = (H >> 4);

	ColumnAddress[0] = SET_COLUMN_ADDRESS_LSB + L;
	ColumnAddress[1] = SET_COLUMN_ADDRESS_MSB + H;

	Dogs102x6_writeCommand(cmd, 1);
	Dogs102x6_writeCommand(ColumnAddress, 2);
}

void Dogs102x6_writeData(uint8_t* sData, uint8_t i)
{
	P7OUT &= ~CS;
	P5OUT |= CD;

	while (i)
	{
		while (!(UCB1IFG & UCTXIFG));

		UCB1TXBUF = *sData;

		sData++;
		i--;
	}

	while (UCB1STAT & UCBUSY);
	// Dummy read to empty RX buffer and clear any overrun conditions
	UCB1RXBUF;

	P7OUT |= CS;
}

void Dogs102x6_writeCommand(uint8_t* sCmd, uint8_t i)
{
	P7OUT &= ~CS;
	P5OUT &= ~CD;

	while (i)
	{
		while (!(UCB1IFG & UCTXIFG));

		UCB1TXBUF = *sCmd;

		sCmd++;
		i--;
	}

	while (UCB1STAT & UCBUSY);
	// Dummy read to empty RX buffer and clear any overrun conditions
	UCB1RXBUF;

	P7OUT |= CS;
}

void Dogs102x6_backlightInit(void)
{
	P7DIR |= BIT6; // питание подсветки
	P7OUT |= BIT6;
	P7SEL &= ~BIT6;
}

void Dogs102x6_init(void)
{
	P5DIR |= BIT7; // установка на выход
	P5OUT &= BIT7;
	P5OUT |= BIT7;

	P7DIR |= CS;

	P5DIR |= CD;
	P5OUT &= ~CD;

	P4SEL |= BIT1; // передача данных LCD_SIMO
	P4DIR |= BIT1;

	P4SEL |= BIT3; // синхросигнал SCLK
	P4DIR |= BIT3;

	UCB1CTL1 = UCSSEL_2 + UCSWRST;
	//3-pin, 8-bit SPI master
	UCB1CTL0 = UCCKPH + UCMSB + UCMST + UCMODE_0 + UCSYNC;

	UCB1BR0 = 0x02;
	UCB1BR1 = 0;

	UCB1CTL1 &= ~UCSWRST;
	UCB1IFG &= ~UCRXIFG;

	Dogs102x6_writeCommand(Dogs102x6_initMacro, 13);
}
