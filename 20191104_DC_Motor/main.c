#include<at89x52.h>
#include "uart.h"
// #include "Delay.h"

void T0_ISR(void)   __interrupt 1;
void DC_Moter_run(char _strtus);

struct DC_Moter{
      int speed_dealy;
    char direction;
}dcm = {100,1};

void main(){
    uart_initialize();
    TMOD &= 0xF0;
    TMOD |= 0x01;
    EA=1;			// Enable Interrupt
    ET0=1;			// Enable Timer 0 Interrupt
    TR0=1;			// Start Timer 0
    // PS = 1;
    TH0 = 0;
    TL0 = 0;
    char get_fun;
    char save_direction;
    while (1){
        /*
        // DC_Moter_run(1);
        get_fun = uart_get_char();
        if(get_fun == 'S'){
            dcm.speed_dealy = uart_get_char();
            // uart_put_integer(dcm.speed_dealy);
            // uart_put_char('\n');

        }
        else if(get_fun == 'P'){
            if(dcm.direction == 0){
                dcm.direction = save_direction;
            }else{
                save_direction = dcm.direction;
                dcm.direction = 0;
            }
            
        }else if(get_fun == 'R')
            dcm.direction = dcm.direction == 1 ? 2 : dcm.direction == 2 ? 1 : 0;
*/
        P2_0 = 0;
        P2_1 = 1;
        P2_2 = 0;
        P2_3 = 1;
    }
}


void DC_Moter_run(char _strtus){
    if(_strtus == 1){
        P2_0 = 1;
        P2_1 = 0;
        P2_2 = 1;
        P2_3 = 0;
    }else if(_strtus == 2){
        P2_1 = 1;
        P2_2 = 0;
        P2_0 = 0;
        P2_3 = 1;
    }else{
        if(dcm.direction == 0 || dcm.speed_dealy == 0){
            P2_0 = 1;
            P2_1 = 1;
            P2_2 = 0;
            P2_3 = 0;
        }else
        if(dcm.direction == 1){
            P2_3 = 1;
        }else if(dcm.direction == 2){
            P2_0 = 1;
        }
        
    }
}

void T0_ISR(void) __interrupt 1
{
    static unsigned int data;
    static int _delay = 0;
    DC_Moter_run(_delay ==1 ? dcm.direction : 0);
    // DC_Moter_run(0);
    data = 0xFFFF - ((_delay == 1 ? 30000 : 0 ) - (dcm.speed_dealy*300));
    // data = 0xFF - dcm.speed_dealy;
    TH0= data / 0xFF;
    TL0= data % 0xFF;
    
    // _delay = dcm.speed_dealy == 30000 ? 0 : (_delay == 0 ? 1 : 0);
    _delay = dcm.speed_dealy==0?0: (_delay == 1 ? 0 :_delay+1);

}