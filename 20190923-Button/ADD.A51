BUTTON_P EQU P3.0
AUGENG_LED_P EQU P0.0
ADDENG_LED_P EQU P0.1
SUM_LED_P    EQU P0.2
CARRY_LED_P  EQU P0.7
ADD_LED_P    EQU P0.3
MULT_LED_P   EQU P0.4
SWITCH_P EQU P1
LED_P    EQU P2
AUG_ADDEAR EQU 30h
ADD_ADDEAR EQU 31h


IFF       EQU 20h

        ORG    000h    

START:
        MOV    LED_P,    #0FFh
        MOV    SWITCH_P, #0FFh ;DIP switch
        MOV    P0, #11100111b

        SETB   BUTTON_P ;BUTTON

WAIT_IS_ADD_MULT:
        MOV IFF, SWITCH_P
        ;CPL A
        ;MOV IFF, A
        
        JB BUTTON_P, WAIT_IS_ADD_MULT

WAIT_IS_ADD_MULT_RELEASE:
        JNB BUTTON_P, WAIT_IS_ADD_MULT_RELEASE
        
        CLR  AUGENG_LED_P
        
        CLR  MULT_LED_P
        SETB ADD_LED_P 
        
        JNB P1.0, WAIT_AUGEND
        
        SETB MULT_LED_P 
        CLR  ADD_LED_P


WAIT_AUGEND:
        MOV A, SWITCH_P
        MOV LED_P, A
        CPL A
        MOV AUG_ADDEAR, A
        JB BUTTON_P, WAIT_AUGEND

WAIT_AUGEND_RELEASE:
        JNB BUTTON_P, WAIT_AUGEND_RELEASE
        SETB AUGENG_LED_P
        SETB SUM_LED_P
        CLR  ADDENG_LED_P

WAIT_ADDEND:
        MOV A, SWITCH_P
        MOV LED_P, A
        CPL A
        MOV ADD_ADDEAR, A

        JB BUTTON_P, WAIT_ADDEND

WAIT_ADDEND_RELEASE:
        JNB BUTTON_P, WAIT_ADDEND_RELEASE
        SETB AUGENG_LED_P
        SETB ADDENG_LED_P
        CLR SUM_LED_P

        ACALL ADD_FUN
        JB 00h, PRINT
        ACALL MUL_FUN
        

PRINT:
        MOV AUG_ADDEAR, A
        CPL A

        MOV LED_P, A
        ;MOV LED_P, A
        JNC WAIT_SUM
        CLR CARRY_LED_P

WAIT_SUM:
        JB BUTTON_P, WAIT_SUM

WAIT_SWM_RELEASE:
        JNB BUTTON_P, WAIT_SWM_RELEASE
        JNB P1.0, WAIT_AUGEND_RELEASE
        SJMP START




;--------------- function -----------------------
ADD_FUN:
    MOV A, AUG_ADDEAR
    ADD A, ADD_ADDEAR
RET

MUL_FUN:
    CLR P0.5
    MOV A, ADD_ADDEAR
    MOV B, AUG_ADDEAR
    MUL AB

    ;MOV A, #00h
    ;MOV R1, ADD_ADDEAR
;POW: ADD A, AUG_ADDEAR    
 ;    DJNZ   R1, POW

RET

;----------------------------------------------
END