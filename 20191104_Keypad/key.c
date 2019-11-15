#include "8051.h" // Defining SFRs

// #include "./lib/uart.c"
#include "keypad.c"

int main(){
    while(1){
        P2 = ~keypad_get_key();
    }
    return 0;
}