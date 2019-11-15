        ORG    0000h
;INIT: 
        MOV    P1, #0FFh ;DIP switch

        MOV    20h, #00h
        SETB   P3.2
        
LOOP: 
        LCALL BUTTON_fun
        AJMP   LOOP

BUTTON_fun:
        JB P3.2, WAIT_CLICK
        MOV R0, A
        MOV A , P1
        CPL A
        
        ADD A, 20h 
        MOV 20h, A
        LCALL LED_FUN

        WAIT_BUTTON_RELASE:
            JNB P3.2, WAIT_BUTTON_RELASE
        
        WAIT_CLICK:
        RET

LED_FUN:
        CPL A
        MOV P2, A

        RET

        END