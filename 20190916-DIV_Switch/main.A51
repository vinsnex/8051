        ORG    0000h
INIT:
        MOV    P1, #0FFh

REPEAT: 
        MOV P2, #0FFh
        MOV A, #1000b
        ;CPL #00h
        CPL A;

        JNB P1.0, P10_ON
        JNB P1.1, P11_ON
        JNB P1.2, P12_ON
        
        AJMP JP34
        
        ;SJMP   REPEAT

P10_ON:
        MOV P2, #00h
        LCALL DELAY
        MOV P2, #0FFh
        LCALL DELAY
        
        SJMP REPEAT

P11_ON:  
        MOV P2,  #01111111b
        LCALL DELAY
        MOV P2, #10111111b
        LCALL DELAY
        MOV P2, #11011111b
        LCALL DELAY
        MOV P2, #11101111b
        LCALL DELAY
        MOV P2, #11110111b
        LCALL DELAY
        MOV P2, #11111011b
        LCALL DELAY
        MOV P2, #11111101b
        LCALL DELAY
        MOV P2, #11111110b
        LCALL DELAY
        MOV P2, #0FFh
        LCALL DELAY

        SJMP REPEAT

P12_ON:  
        MOV P2, #11111110b
        LCALL DELAY
        MOV P2, #11111101b
        LCALL DELAY
        MOV P2, #11111011b
        LCALL DELAY
        MOV P2, #11110111b
        LCALL DELAY
        MOV P2, #11101111b
        LCALL DELAY
        MOV P2, #11011111b
        LCALL DELAY
        MOV P2, #10111111b
        LCALL DELAY
        MOV P2, #01111111b
        LCALL DELAY
        MOV P2, #0FFh
        LCALL DELAY

        AJMP REPEAT

JP34:  
        JNB P1.3, P13_ON
        JNB P1.4, P14_ON

        AJMP REPEAT

P13_ON:  
        MOV P2, #01111110b
        LCALL DELAY
        MOV P2, #10111101b
        LCALL DELAY
        MOV P2, #11011011b
        LCALL DELAY
        MOV P2, #11100111b
        LCALL DELAY
        MOV P2, #0FFh
        LCALL DELAY

        AJMP REPEAT

P14_ON:
        MOV P2, #11100111b
        LCALL DELAY
        MOV P2, #11011011b
        LCALL DELAY
        MOV P2, #10111101b
        LCALL DELAY
        MOV P2, #01111110b
        LCALL DELAY
        MOV P2, #0FFh
        LCALL DELAY

        AJMP REPEAT

DELAY:  MOV    R0, #0FFh
LOOP1:  MOV    R1, #0FFh
LOOP2:  DJNZ   R1, LOOP2
        DJNZ   R0, LOOP1
        RET
        END