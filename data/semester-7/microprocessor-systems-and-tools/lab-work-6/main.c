#include <msp430.h>
#include "CTS_Layer.h"
#include <math.h>


typedef unsigned char uchar;

/* LCD --------------------------------------------------------------------------------------------------------------------- */

#define SCROL_CTL					0x40  //Scroll image up by SL rows (SL = last 5 bits), range:0-63

#define SET_MIRROR_COL				0xA0  //Normal mirror SEG (column) mapping (set bit0 to mirror columns)

#define SET_MIRROR_ROW				0xC0  //Normal mirror COM (row) mapping (set bit3 to mirror rows)

#define ALL_PIXEL_ON				0xA4  //Disable all pixel on (last bit 1 to turn on all pixels - does not affect memory)

#define LCD_INVERSE					0xA6  //Inverse display off (last bit 1 to invert display - does not affect memory)

#define BIAS_RATIO_VCC				0xA2  //Set voltage bias ratio (BR = bit0)

#define POW_CTL						0x2F  //Set Power control - booster, regulator, and follower on

#define SET_CONTRAST_RESISTOR		0x27  //Set internal resistor ratio Rb/Ra to adjust contrast
#define MSB_SELECT_VOLUME			0x81  //Set Electronic Volume "PM" to adjust contrast
#define LSB_SELECT_VOLUME			0x10  //Set Electronic Volume "PM" to adjust contrast (PM = last 5 bits)

#define ADV_CTL_MSB					0xFA  //Set temp. compensation curve to -0.11%/C
#define ADV_CTL_LSB					0x90

#define COLUMN_ADR_MSB				0x10  //Set SRAM col. addr. before write, last 4 bits = ca4-ca7
#define COLUMN_ADR_LSB				0x00  //Set SRAM col. addr. before write, last 4 bits = ca0-ca3
#define PAGE_ADR					0xB0  //Set SRAM page addr (pa = last 4 bits), range:0-8

#define LCD_EN						0xAF  //Enable display (exit sleep mode & restore power)

/* LCD --------------------------------------------------------------------------------------------------------------------- */


/* Temperature sensor ------------------------------------------------------------------------------------------------------ */

#define CALADC12_15V_30C *((unsigned int *)0x1A1A) // Temperature Sensor Calibration-30 C
#define CALADC12_15V_85C *((unsigned int *)0x1A1C) // Temperature Sensor Calibration-85 C

/* Temperature sensor ------------------------------------------------------------------------------------------------------ */


#define ROWS 9
#define COLUMNS 6
#define PAGES 2
#define DELAY 1000
#define COLUMN_OFFSET_BIG 30
#define COLUMN_OFFSET_NONE 0


void Delay(long int value);

void SetupADC();
void SetupLCD();

void __LCD_SetAddress(uchar page, uchar column);
void Dogs102x6_writeData(uchar *sData, uchar i);
void Dogs102x6_writeCommand(uchar *sCmd, uchar i);

void Clear(void);
void ShowNumber(long int number);

int GetS1State();
void SetupControllButton();

void SetupTimer();


// 4 columns (+2 offset) and 9 rows. Each byte => 8 rows == 1 page
uchar plus[PAGES][COLUMNS]  = {{0x18, 0x7E, 0x7E, 0x18, 0x00}, {0x00, 0x00, 0x00, 0x00, 0x00}};
uchar minus[PAGES][COLUMNS] = {{0x18, 0x18, 0x18, 0x18, 0x00}, {0x00, 0x00, 0x00, 0x00, 0x00}};
uchar digits[10][PAGES][COLUMNS] = {
  {{0x7F, 0xC1, 0xC1, 0x7F, 0x00, 0x00}, {0x00, 0x80, 0x80, 0x00, 0x00, 0x00}}, // digit 0
  {{0xFF, 0xFF, 0x06, 0x04, 0x00, 0x00}, {0x80, 0x80, 0x00, 0x00, 0x00, 0x00}}, // digit 1
  {{0xCF, 0xDD, 0xF9, 0xF3, 0x00, 0x00}, {0x00, 0x80, 0x80, 0x00, 0x00, 0x00}}, // digit 2
  {{0x77, 0xDD, 0xC9, 0x63, 0x00, 0x00}, {0x00, 0x80, 0x80, 0x00, 0x00, 0x00}}, // digit 3
  {{0xFF, 0x08, 0x08, 0x0F, 0x00, 0x00}, {0x80, 0x00, 0x00, 0x80, 0x00, 0x00}}, // digit 4
  {{0xF9, 0xC9, 0xC9, 0xCF, 0x00, 0x00}, {0x80, 0x80, 0x80, 0x80, 0x00, 0x00}}, // digit 5
  {{0x73, 0xC9, 0xC9, 0x7F, 0x00, 0x00}, {0x00, 0x80, 0x80, 0x00, 0x00, 0x00}}, // digit 6
  {{0x03, 0x0C, 0x30, 0xC0, 0x00, 0x00}, {0x80, 0x80, 0x80, 0x80, 0x00, 0x00}}, // digit 7
  {{0x77, 0xC9, 0xC9, 0x77, 0x00, 0x00}, {0x00, 0x80, 0x80, 0x00, 0x00, 0x00}}, // digit 8
  {{0x7F, 0xC9, 0xC9, 0x67, 0x00, 0x00}, {0x00, 0x80, 0x80, 0x00, 0x00, 0x00}}  // digit 9
};

int column_offset = COLUMN_OFFSET_BIG; // 0 - default is COLUMN_OFFSET_BIG, 1 - mirror horizonta is COLUMN_OFFSET_NONE

uchar LCD_INIT_COMMANDS_PART_1[7] = {
	SCROL_CTL,
	SET_MIRROR_COL,
	SET_MIRROR_ROW,
	ALL_PIXEL_ON,
	LCD_INVERSE,
	BIAS_RATIO_VCC,
	POW_CTL
};
uchar LCD_INIT_COMMANDS_PART_2[6] = {
	SET_CONTRAST_RESISTOR,
	MSB_SELECT_VOLUME,
	LSB_SELECT_VOLUME,
	ADV_CTL_MSB,
	ADV_CTL_LSB,
	LCD_EN,
};
int AMOUNT_OF_COMMANDS_1 = 7;
int AMOUNT_OF_COMMANDS_2 = 6;

/* PADS ------------------------------------------------------------------------------------------------------ */

#define NUM_KEYS    5
#define LED4        BIT5
#define LED5        BIT4
#define LED6        BIT3
#define LED7        BIT2
#define LED8        BIT1

//uint16_t dCnt[NUM_KEYS];
struct Element* keypressed;

const struct Element* address_list[NUM_KEYS] = {
		&PAD1,
		&PAD2,
		&PAD3,
		&PAD4,
		&PAD5
};
const uint8_t ledMask[NUM_KEYS] = {
LED4,
LED5,
LED6,
LED7,
LED8
};

/* PADS ------------------------------------------------------------------------------------------------------ */

void SetupControllButton()
{
	P1DIR &= ~BIT7;
	P1OUT |= BIT7;
	P1REN |= BIT7;
	P1IES |= BIT7;
	P1IE |= BIT7;
	P1IFG |= BIT7;
}

int GetS1State()
{
	return (P1IN & BIT7) ? 0 : 1;
}

void SetupLCD()
{
	// Reset LCD
	P5DIR |= BIT7;	// port init for LCD operations
	P5OUT &= ~BIT7;	// set RST (active low)
	P5OUT |= BIT7;	// reset RST (inactive is high)

	// Delay for at least 5ms
	_delay_cycles(550);

	// Choosing slave
	P7DIR |= BIT4;	// select LCD for chip
	P7OUT &= ~BIT4;	// CS is active low

	// Setting up LCD_D/C
	P5DIR |= BIT6;	// Command/Data for LCD
	P5OUT &= ~BIT6;	// CD low for command

	// Set up P4.1 -- SIMO, P4.3 -- SCLK (select PM_UCB1CLK)
	P4SEL |= BIT1 | BIT3;
	P4DIR |= BIT1 | BIT3;

	// Set up back light
	P7DIR |= BIT6;	// initializing
	P7OUT |= BIT6;	// back light
	P7SEL &= ~BIT6; // USE PWM to control brightness

	// Deselect slave
	P7OUT |= BIT4;	// CS = 1 (Deselect LCD) (stop setting it up)

	UCB1CTL1 |= UCSWRST;	// set UCSWRST bit to disable USCI and change its control registers

	UCB1CTL0 = (
		UCCKPH 	&	// UCCKPH - 1: change out on second signal change, capture input on first one)
		~UCCKPL |	// UCCKPL - 0: active level is 1
		UCMSB 	|	// MSB comes first, LSB is next
		UCMST 	|	// Master mode
		UCSYNC 	|	// Synchronous mode
		UCMODE_0	// 3 pin SPI mode
	);

	// set SMCLK as source and keep RESET
	UCB1CTL1 = UCSSEL_2 | UCSWRST;

	// set frequency divider
	UCB1BR0 = 0x01;	// LSB to 1
	UCB1BR1 = 0;	// MSB to 0

	UCB1CTL1 &= ~UCSWRST;	// enable USCI
	UCB1IFG &= ~UCRXIFG;	// reset int flag (which is set after input shift register gets data)
	Dogs102x6_writeCommand(LCD_INIT_COMMANDS_PART_1, AMOUNT_OF_COMMANDS_1);

	// delay to wait at least 120 ms
	_delay_cycles(12500);

	Dogs102x6_writeCommand(LCD_INIT_COMMANDS_PART_2, AMOUNT_OF_COMMANDS_2);

	Clear();
}

void __LCD_SetAddress(uchar page, uchar column)
{
	uchar cmd[1];

	if (page > 7)
	{
		page = 7;
	}

	if (column > 101)
	{
		column = 101;
	}

	cmd[0] = PAGE_ADR + (7 - page);
	uchar command_high = 0x00;
	uchar command_low = 0x00;
	uchar column_address[] = { COLUMN_ADR_MSB, COLUMN_ADR_LSB };

	command_low = (column & 0x0F);
	command_high = (column & 0xF0);
	command_high = (command_high >> 4);

	column_address[0] = COLUMN_ADR_LSB + command_low;
	column_address[1] = COLUMN_ADR_MSB + command_high;

	Dogs102x6_writeCommand(cmd, 1);
	Dogs102x6_writeCommand(column_address, 2);
}

void Dogs102x6_writeCommand(uchar *sCmd, uchar i)
{
	// CS Low
	P7OUT &= ~BIT4;

	// CD Low
	P5OUT &= ~BIT6;
	while (i)
	{
		// USCI_B1 TX buffer ready?
		while (!(UCB1IFG & UCTXIFG)) ;

		// Transmit data
		UCB1TXBUF = *sCmd;

		// Increment the pointer on the array
		sCmd++;

		// Decrement the Byte counter
		i--;
	}

	// Wait for all TX/RX to finish
	while (UCB1STAT & UCBUSY);

	// Dummy read to empty RX buffer and Clear any overrun conditions
	UCB1RXBUF;

	// CS High
	P7OUT |= BIT4;
}

void Dogs102x6_writeData(uchar *sData, uchar i)
{
	// CS Low
	P7OUT &= ~BIT4;
	//CD High
	P5OUT |= BIT6;

	while (i)
	{
		// USCI_B1 TX buffer ready?
		while (!(UCB1IFG & UCTXIFG));

		// Transmit data and increment pointer
		UCB1TXBUF = *sData++;

		// Decrement the Byte counter
		i--;
	}

	// Wait for all TX/RX to finish
	while (UCB1STAT & UCBUSY);

	// Dummy read to empty RX buffer and Clear any overrun conditions
	UCB1RXBUF;

	// CS High
	P7OUT |= BIT4;
}

void ShowNumber(long int number)
{
	volatile long int length = 1;
	volatile long int digit = 0;
	volatile long int i = 0;

	long int temp = number > 0 ? number : (-1) * number;
	while(1)
	{
		if (temp < 10)
		{
			break;
		}

		temp /= 10;

		length++;
	}

	temp = number;
	for(i = 0; i < length; i++)
	{
		digit = (long int)(temp % 10);

		digit = digit < 0 ? (-1) * digit : digit;

		if (digit < 10)
			{
			__LCD_SetAddress(0, column_offset + i * COLUMNS);
			Dogs102x6_writeData(digits[digit][0], COLUMNS);
			__LCD_SetAddress(1, column_offset + i * COLUMNS);
			Dogs102x6_writeData(digits[digit][1], COLUMNS);
		}

		temp /= 10;
	}

	if (number >= 0)
	{
		__LCD_SetAddress(0, column_offset + length * COLUMNS);
		Dogs102x6_writeData(plus[0], COLUMNS);
		__LCD_SetAddress(1, column_offset + length * COLUMNS);
		Dogs102x6_writeData(plus[1], COLUMNS);
	}
	else
	{
		__LCD_SetAddress(0, column_offset + length * COLUMNS);
		Dogs102x6_writeData(minus[0], COLUMNS);
		__LCD_SetAddress(1, column_offset + length * COLUMNS);
		Dogs102x6_writeData(minus[1], COLUMNS);
	}
}

void Clear(void)
{
	uchar lcd_data[] = {0x00};
	uchar page, column;

	for (page = 0; page < 8; page++)
	{
		__LCD_SetAddress(page, 0);
		for (column = 0; column < 132; column++)
		{
			Dogs102x6_writeData(lcd_data, 1);
		}
	}
}

void SetupTimer()
{
	// doesn't affect sample/conversion!!!, only triggers the beginning

    // setup timer
    TA0CTL = TASSEL__SMCLK | MC__UP | ID__1 | TACLR;     // SMCLK, UP-mode
    long int second = 32768;
    long int period = second / 2;
    TA0CCR0 = second;
    TA0CCR1 = period;
    TA0CCTL1 = OUTMOD_3;
}

void SetupADC()
{
	// leds setup
    P8DIR |= BIT1;
    P8OUT &= ~BIT1;

    REFCTL0 &= ~REFMSTR;		// turn of REF block

	ADC12CTL0 =
			ADC12SHT0_8
			+ ADC12REFON
			+ ADC12ON;

	// Internal ref = 1.5V

	ADC12CTL1 =
			ADC12SHP
			+ ADC12SHS_1
			+ ADC12SSEL_0;	// enable sample timer

	ADC12MCTL0 =
			ADC12SREF_1
			 + ADC12INCH_10;	// ADC i/p ch A10 = temp sense i/p

	ADC12IE = ADC12IE0;							// ADC_IFG upon conv result-ADCMEMO

	__delay_cycles(100);						// delay to allow Ref to settle

	ADC12CTL0 |= ADC12ENC;

//	ADC12CTL1 =
//			ADC12CONSEQ_0 		// single-channel single-conversion mode
//			| ADC12SHS_1		// choose TA0 as source
//			| ADC12SHP			// enable pulse mode
//			| ADC12SSEL_0		// clock as ADC12OSC (default is 5 MHz)
//			| ADC12CSTARTADD_0; // start with MEM0
//
//    ADC12IE |= ADC12IE0;		// ADC interrupt enable
//
//    ADC12CTL0 &= ~ADC12REF2_5V;				// set ref to 1.5 V
//    ADC12CTL0 |= ADC12REFON | ADC12SHT0_6; 	// 256 ADC12CLK cycles to be bigger than 30 mcs (64 is enough, but example used 256)
//
//    ADC12MCTL0 =
//			ADC12INCH_10 	// measure temperature sensor
//			| ADC12SREF_1;	// reference voltage is V(R+) = VREF+ and V(R-) = AVss
//
//    ADC12CTL2 |= ADC12RES_2;	// max range with 12 bit resolution
//
//    ADC12CTL0 = ADC12ON  | ADC12ENC;	// turn ADC on and enable conversion
}

void Delay(long int value)
{
	volatile long int i = 0;
	volatile long int temp = 0;
	for (; i < value; i++)
	{
		temp++;
	}
}

#define CALADC12_15V_30C *((unsigned int *)0x1A1A) // Temperature Sensor Calibration-30 C
#define CALADC12_15V_85C *((unsigned int *)0x1A1C) // Temperature Sensor Calibration-85 C


#pragma vector = ADC12_VECTOR
__interrupt void ADC12_ISR() {
	P8OUT ^= BIT1;

	TA0CTL &= ~MC__UP;
	TA0CTL |= MC__STOP | TACLR;
	TA0CCTL1 &= ~BIT2;

	// also clears ADC12IFG0 flag
	unsigned short int probe = ADC12MEM0 & 0x0FFF;
	ADC12MEM0 = 0;

	volatile long int temp1 = probe;
	volatile long int temp2 = temp1 - CALADC12_15V_30C;
	volatile float temp3 = temp2 * (85 - 30);
	volatile float temp4 = temp3 / (CALADC12_15V_85C - CALADC12_15V_30C);
	temp4 += 30;
	volatile long int deg_c = (long int)temp4;

	Clear();
	ShowNumber(deg_c);

	ADC12CTL0 &= ~ADC12ENC;
}

#pragma vector = PORT1_VECTOR
__interrupt void HandleS1ISR()
{
	Delay(12500);

	if (GetS1State())
	{
		if (!(ADC12CTL1 & ADC12BUSY)) // if there is no active operation
		{
			SetupTimer();
			ADC12CTL0 |= ADC12ENC;
		}
	}

	P1IFG &= ~BIT7;
}

/*
 *  ======== SetVcorUp(uint16_t) ========
 *  Taken from MSP430F55xx_UCS_10.c code example.
 */
void SetVcoreUp(uint16_t level)
{
    // Open PMM registers for write
    PMMCTL0_H = PMMPW_H;
    // Set SVS/SVM high side new level
    SVSMHCTL = SVSHE + SVSHRVL0 * level + SVMHE + SVSMHRRL0 * level;
    // Set SVM low side to new level
    SVSMLCTL = SVSLE + SVMLE + SVSMLRRL0 * level;
    // Wait till SVM is settled
    while ((PMMIFG & SVSMLDLYIFG) == 0)
        ;
    // Clear already set flags
    PMMIFG &= ~(SVMLVLRIFG + SVMLIFG);
    // Set VCore to new level
    PMMCTL0_L = PMMCOREV0 * level;
    // Wait till new level reached
    if ((PMMIFG & SVMLIFG))
        while ((PMMIFG & SVMLVLRIFG) == 0)
            ;
    // Set SVS/SVM low side to new level
    SVSMLCTL = SVSLE + SVSLRVL0 * level + SVMLE + SVSMLRRL0 * level;
    // Lock PMM registers for write access
    PMMCTL0_H = 0x00;
}

int main(void)
{
	WDTCTL = WDTPW | WDTHOLD;

	uint8_t i;

	SetupLCD();

	/* Initialize IO */
	P1DIR = 0xFF;
	P2DIR = 0xFF;
	P8DIR = 0xFF;
	P1OUT = 0;
	P2OUT = 0;
	P8OUT = 0;

	/*
	*  Set DCO to 25Mhz and SMCLK to DCO. Taken from MSP430F55xx_UCS_10.c code
	*  example.
	*/
	// Increase Vcore setting to level3 to support fsystem=25MHz
	// NOTE: Change core voltage one level at a time..
	SetVcoreUp(0x01);
	SetVcoreUp(0x02);
	SetVcoreUp(0x03);

	UCSCTL3 = SELREF_2;                       // Set DCO FLL reference = REFO
	UCSCTL4 |= SELA_2;                        // Set ACLK = REFO

	__bis_SR_register(SCG0);                  // Disable the FLL control loop
	UCSCTL0 = 0x0000;                         // Set lowest possible DCOx, MODx
	UCSCTL1 = DCORSEL_7;    
	// Select DCO range 50MHz operation
	UCSCTL2 = FLLD_1 + 762;                   // Set DCO Multiplier for 25MHz
											// (N + 1) * FLLRef = Fdco
											// (762 + 1) * 32768 = 25MHz
											// Set FLL Div = fDCOCLK/2
	__bic_SR_register(SCG0);                  // Enable the FLL control loop

	// Worst-case settling time for the DCO when the DCO range bits have been
	// changed is n x 32 x 32 x f_MCLK / f_FLL_reference. See UCS chapter in 5xx
	// UG for optimization.
	// 32 x 32 x 25 MHz / 32,768 Hz ~ 780k MCLK cycles for DCO to settle
	__delay_cycles(782000);
	// Loop until XT1,XT2 & DCO stabilizes - In this case only DCO has to stabilize
	do
	{
	  UCSCTL7 &= ~(XT2OFFG + XT1LFOFFG + DCOFFG);
	  // Clear XT2,XT1,DCO fault flags
	  SFRIFG1 &= ~OFIFG;                      // Clear fault flags
	}
	while (SFRIFG1 & OFIFG);                   // Test oscillator fault flag

	/*  establish baseline */
	TI_CAPT_Init_Baseline(&keypad);
	TI_CAPT_Update_Baseline(&keypad, 5);

	SetupControllButton();
	// SetupLCD();
	SetupADC();

	  __bis_SR_register(GIE);

	while (1)
	{
	  P1OUT &= ~(LED4 + LED5 + LED6 + LED7 + LED8);
	  keypressed = (struct Element *) TI_CAPT_Buttons(&keypad);
	  //TI_CAPT_Custom(&keypad,dCnt);
	  __no_operation();
	  if (keypressed)
	  {
		  for (i = 0; i < NUM_KEYS; i++)
		  {
			  if (keypressed == address_list[3])
			  {
				  P1OUT |= BIT4;

				  if (!(ADC12CTL1 & ADC12BUSY)) // if there is no active operation
				  		{
				  			SetupTimer();
				  			ADC12CTL0 |= ADC12ENC;
				  		}
			  }
		  }
	  }
	  /* 32ms delay. This delay can be replaced with other application tasks. */
	  __delay_cycles(900000);
	} // while loop


  return 0;
}
