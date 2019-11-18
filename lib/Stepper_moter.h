#ifndef SETPPER_MOTER_H
#define SETPPER_MOTER_H

#include "delay.h"

#ifndef Setpper_moter_pin0
#define Setpper_moter_pin0 P0_0
#endif

#ifndef Setpper_moter_pin1
#define Setpper_moter_pin1 P0_1
#endif

#ifndef Setpper_moter_pin2
#define Setpper_moter_pin2 P0_2
#endif

#ifndef Setpper_moter_pin3
#define Setpper_moter_pin3 P0_3
#endif


void Stepper_Motor_run_delay(__bit direction, unsigned delay_s);
void Stepper_Motor_run(__bit direction);

void Stepper_Motor_run_delay(__bit direction, unsigned int delay_s){
    Stepper_Motor_run(direction);
    delay(delay_s);
}

void Stepper_Motor_run(__bit direction){
    
    delay(200);
    if(direction){
        Setpper_moter_pin0 = 1;
        Setpper_moter_pin1 = 0;
        Setpper_moter_pin2 = 0;
        Setpper_moter_pin3 = 0;
        delay(200);
        Setpper_moter_pin0 = 0;
        Setpper_moter_pin1 = 1;
        delay(200);
        Setpper_moter_pin1 = 0;
        Setpper_moter_pin2 = 1;
        delay(200);
        Setpper_moter_pin2 = 0;
        Setpper_moter_pin3 = 1;
        delay(200);
        Setpper_moter_pin3 = 0;
    }else{
        Setpper_moter_pin0 = 0;
        Setpper_moter_pin1 = 0;
        Setpper_moter_pin2 = 0;
        Setpper_moter_pin3 = 1;
        delay(200);
        Setpper_moter_pin3 = 0;
        Setpper_moter_pin2 = 1;
        delay(200);
        Setpper_moter_pin2 = 0;
        Setpper_moter_pin1 = 1;
        delay(200);
        Setpper_moter_pin1 = 0;
        Setpper_moter_pin0 = 1;
        delay(200);
        Setpper_moter_pin0 = 0;
    }
}

#endif