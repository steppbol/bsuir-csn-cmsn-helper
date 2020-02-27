#include <msp430.h>

int butt1_flag = 0;
int butt2_flag = 0;
int led_state = 0;

#pragma vector = PORT1_VECTOR
__interrupt void buttonPush1(void) {
    if (butt1_flag == 0) {
        P1IES &= ~BIT7;
        butt1_flag = 1;
    } else {
        P1IES |= BIT7;
        P8OUT |= BIT1;
        butt1_flag = 0;
    }
    volatile int i = 0;
    for (i = 0; i < 1000; i++) {}
    P1IFG = 0;
}

#pragma vector = PORT2_VECTOR
__interrupt void buttonPush2(void) {
    if (butt2_flag == 0) {
        if (butt1_flag) {
            if (!led_state) {
                P1OUT |= BIT0;
                led_state = 1;
            } else {
                led_state = 0;
                P1OUT &= ~BIT0;
            }
        }
        if (butt1_flag == 0) {
            P8OUT &= ~BIT1;
        }
        P2IES &= ~BIT2;
        butt2_flag = 1;
    } else {
        P2IES |= BIT2;
        butt2_flag = 0;
    }
    volatile int i = 0;
    for (i = 0; i < 1000; i++) {}
    P2IFG = 0;
}

int main(void) {
    WDTCTL = WDTPW | WDTHOLD;
    P1DIR = BIT0;
    P1OUT = 0;
    P8DIR = BIT1;
    P8OUT = 0;

    P1REN |= BIT7;
    P1OUT |= BIT7;

    P2REN |= BIT2;
    P2OUT |= BIT2;

    __bis_SR_register(GIE);
    P1IE |= BIT7;
    P2IE |= BIT2;
    P1IES |= BIT7;
    P2IES |= BIT2;
    P1IFG = 0;
    P2IFG = 0;

    __no_operation();

    return 0;
}