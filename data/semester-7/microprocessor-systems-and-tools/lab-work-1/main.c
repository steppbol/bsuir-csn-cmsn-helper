#include <msp430.h>

int main(void) {
    WDTCTL = WDTPW | WDTHOLD;
    // setup leds
    P1DIR = BIT0;
    P1OUT = 0;
    P8DIR = BIT1;
    P8OUT = 0;
    //setup buttons
    P1REN |= BIT7;
    P1OUT |= BIT7;

    P2REN |= BIT2;
    P2OUT |= BIT2;

    int butt1_flag = 0;
    int butt2_flag = 0;
    int led1_state = 0;

    while (1) {
        int butt1 = (P1IN & BIT7) == 0;
        int butt2 = (P2IN & BIT2) == 0;
        if (butt1 == 1 && butt1_flag == 0) {
            butt1_flag = 1;
        }

        if (butt2 == 1 && butt2_flag == 0) {
            if (butt1 == 1) {
                if (!led1_state) {
                    P1OUT |= BIT0;
                    led1_state = 1;
                } else {
                    led1_state = 0;
                    P1OUT &= ~BIT0;
                }
            }
            if (butt1 == 0) {
                P8OUT &= ~BIT1;
            }
            butt2_flag = 1;
        }

        if (butt2 == 0 && butt2_flag == 1) {
            butt2_flag = 0;
        }

        if (butt1 == 0 && butt1_flag == 1) {
            P8OUT |= BIT1;
            butt1_flag = 0;
        }
    }
    return 0;
}