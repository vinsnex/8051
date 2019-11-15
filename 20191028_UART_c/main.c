// Template for SDCC 8051 C program

#include "8051.h" // Defining SFRs
#include "uart.h"

void delay(unsigned int ms);
// ISR Prototypes ===================================================
void INT0_ISR(void) __interrupt 0; // ISR for External Interrupt 0
void T0_ISR(void)   __interrupt 1; // ISR for Timer0/Counter0 Overflow Interrupt
void INT1_ISR(void) __interrupt 2; // ISR for External Interrupt 1
void T1_ISR(void)   __interrupt 3; // ISR for Timer1/Counter1 Overflow Interrupt
void UART_ISR(void) __interrupt 4; // ISR for UART Interrupt



void main(void){
    char read_str[10];
    int num = 0;
    int data;
    char state = 0;
    char input_state=0;
    P2 = 0xFF;
    uart_initialize();
    while (1)
    {
         uart_put_string("initial...\n");
    }
    
   
    while (1){
        uart_put_string(input_state ? "symbol : " : "num : ");
        uart_get_string(read_str);
        uart_put_string(read_str);
        uart_put_char('\n');
        
        if(read_str[0] == '+' ){
            state = 0;
            input_state = 0;
        }else if(read_str[0] == '-'){
            state = 1;
            input_state = 0;
        }else if(read_str[0] == '*'){
            state = 2;
            input_state = 0;
        }else if(read_str[0] == '/'){
            state = 3;
            input_state = 0;
        }else if(read_str[0] == '='){
            input_state = 0;
            uart_put_string("number = ");
            uart_put_integer(num);
            uart_put_string("\n\n");
            P2 = num ^ 0xFF ;
            num = 0;
        }else {
            input_state = 1;
            data = uart_s2i(read_str);
            if(state == 0) num += data;
            else if(state == 1) num -= data;
            else if(state == 2) num *= data;
            else if(state == 3) num /= data;
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

void delay(unsigned int ms){
    static unsigned int delay_ms;
	static char delay_1ms;
	for(delay_ms = 0;delay_ms<ms;delay_ms++)
		for(delay_1ms=0;delay_1ms<11;delay_1ms++);
}