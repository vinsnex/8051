#include<at89x52.h>
#include "uart.h"
#include "Delay.h"
struct DC_Moter{
    char speed_delay;
    char direction;
} stm = {0, 0};

void Stepper_Motor_run(__bit direction){
    char i = 3;
    P0 = direction ? 0x01 : 0x08;
    delay(200);
    while(i--) {
        P0 = direction ? P0 << 1 : P0 >> 1;
        delay(200);
    }
    P0 = 0x00;
}

void get_uart(){
    static char uart_get;
    static int get_t;
    static int count;
    uart_get = uart_get_char();
    if(uart_get == 's'){
        stm.speed_delay = uart_get_integer();
    }else if(uart_get == 'r'||uart_get == 'l'){
        count = uart_get_integer() + (stm.speed_delay >> 1);
        while(count--){
            Stepper_Motor_run(uart_get == 'r' ? 1:0);
            delay(stm.speed_delay*10);
        }
    }
    
}

void main(){
    uart_initialize();
    while(1){
        get_uart();
        // DC_Motor_run(0);
    }
}