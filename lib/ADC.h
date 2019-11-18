#ifndef ADC_H
#define ADC_H

#ifndef ADC_CS
    #define ADC_CS		P2_0
#endif

#ifndef ADC_RD
    #define ADC_RD		P2_1
#endif

#ifndef ADC_WR
    #define ADC_WR		P2_2
#endif

#ifndef ADC_PORT
    #define ADC_PORT	P0
#endif

void adc_initialize(){
    EX0 = 1;
    EA = 1;
    ADC_PORT = 0xFF;
}

__bit adc_busy;

unsigned char adc_get(void){
static unsigned char adc_data;
ADC_CS=0;ADC_WR=0;ADC_WR=1;ADC_CS=1;
while(adc_busy==1);
ADC_CS=0;ADC_RD=0;adc_data=ADC_PORT;ADC_RD=1;ADC_CS=1;
return adc_data;
}

void INT0_ISR(void) __interrupt 0 {
    adc_busy=0;
}

#endif