#include<at89x52.h>

void delay(unsigned int _s){
    while(_s--);
}

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

void main(){
    while(1){
        Stepper_Motor_run(0);
    }
}