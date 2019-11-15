#include "8051.h"
#include "uart.h"
#include "keypad.c"

int main(){
    unsigned char put_number;
    uart_initialize();
    while(1){
        put_number = keypad_get_key();
        uart_put_integer(put_number);
    }
    return 0;
}