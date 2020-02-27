#include <msp430.h>

volatile int short CNANGED_FREQUENCY_MODE = 0;
volatile int short LOW_POWER_MODE_CORE_4_MODE = 0;
volatile int short DEFAULT_DELAY_IN_INTERRUPT = 1000;

#
pragma vector = PORT1_VECTOR
__interrupt void PORT1_S1(void) {
    volatile int i;
    volatile int k = 1;

    for (i = 0; i < DEFAULT_DELAY_IN_INTERRUPT; i++) {
        k++;
    }

    if (LOW_POWER_MODE_CORE_4_MODE) {
        _bic_SR_register_on_exit(LPM4_bits);
        LOW_POWER_MODE_CORE_4_MODE = 0;
    } else {
        LOW_POWER_MODE_CORE_4_MODE = 1;
        _bis_SR_register_on_exit(LPM4_bits);

    }

    P1IFG &= ~BIT7;
}

# pragma vector = PORT2_VECTOR
__interrupt void PORT2_S2(void) {
    volatile int i;
    volatile int k = 1;

    for (i = 0; i < DEFAULT_DELAY_IN_INTERRUPT; i++) {
        k++;
    }

    if (CNANGED_FREQUENCY_MODE) {
        UCSCTL4 = SELM__DCOCLK;
        UCSCTL5 = DIVM__32;
        CNANGED_FREQUENCY_MODE = 0;
    } else {
        UCSCTL4 = SELM__XT1CLK;
        UCSCTL5 = DIVM__16;
        CNANGED_FREQUENCY_MODE = 1;
    }

    P2IFG &= ~BIT2;
}

int main(void) {
    WDTCTL = WDTPW | WDTHOLD;

    P1DIR &= ~BIT7;
    P1OUT |= BIT7;
    P1REN |= BIT7;

    P2DIR &= ~BIT2;
    P2OUT |= BIT2;
    P2REN |= BIT2;

    __bis_SR_register(GIE);

    P1IES |= BIT7;
    P1IFG &= ~BIT7;
    P1IE |= BIT7;

    P2IES |= BIT2;
    P2IFG &= ~BIT7;
    P2IE |= BIT2;

    P7DIR |= BIT7;
    P7SEL |= BIT7;

    UCSCTL3 |= SELREF__REFOCLK;
    UCSCTL4 = SELM__DCOCLK;
    UCSCTL1 = DCORSEL_0;
    UCSCTL2 = 102;
    UCSCTL3 |= FLLREFDIV__8;
    UCSCTL5 |= DIVM__32;

    return 0;
}