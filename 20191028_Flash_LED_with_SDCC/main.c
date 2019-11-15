// Template for SDCC 8051 C program

#include <8051.h> // Defining SFRs

// ISR Prototypes ===================================================
void INT0_ISR(void) __interrupt 0; // ISR for External Interrupt 0
void T0_ISR(void)   __interrupt 1; // ISR for Timer0/Counter0 Overflow Interrupt
void INT1_ISR(void) __interrupt 2; // ISR for External Interrupt 1
void T1_ISR(void)   __interrupt 3; // ISR for Timer1/Counter1 Overflow Interrupt
void UART_ISR(void) __interrupt 4; // ISR for UART Interrupt
void delay(unsigned int ms);
char list[2][4] = {{0b10000001, 0b01000010, 0b00100100, 0b00011000 },
                   {0b00011000, 0b00100100, 0b01000010, 0b10000001 }};
void main(void){
    int i;
    P1 = 0xFF;
    while (1){
    	if(P1 == 0xFF){
    	    for(i=0;i<8;i++){
    	        P2 = ~(0b00000001<<i);
    	        delay(10000);
    	    }
    	}else if(P1 == 0xFE){
    	    for(i=0;i<8;i++){
    	        P2 = ~(0b10000000>>i);
    	        delay(1000);
    	    }
    	}else if(P1 == 0xFD){
    	    for(i=0;i<8;i++){
    	        P2 = ~list[0][i];
    	        delay(1000);
    	    }
    	}else if(P1 == 0xFB){
    	    for(i=0;i<8;i++){
    	        P2 = ~list[1][i];
    	        delay(1000);
    	    }
    	}else{
    	
    	}
 
    }
    
    
    
}

void INT0_ISR(void) __interrupt 0
{}
void T0_ISR(void) __interrupt 1
{}
void INT1_ISR(void) __interrupt 2
{}
void T1_ISR(void) __interrupt 3
{}
void UART_ISR(void) __interrupt 4
{}

//**************************************
void delay(unsigned int ms){
    unsigned int delay_ms;
	// char delay_1ms;
	for(delay_ms = 0;delay_ms<ms*11;delay_ms++);
		// for(delay_1ms=0;delay_1ms<11;delay_1ms++);
}