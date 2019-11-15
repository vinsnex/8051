        ORG    0000h
;INIT: 
        MOV    P1, #0FFh ;DIP switch

        MOV    R0, #00h
        SETB   P3.2
        
LOOP: 

WAIT_BUTTON:   JB  P3.2, WAIT_BUTTON
WAIT_RELEASE:  JNB P3.2, WAIT_RELEASE
        MOV R0, A
        MOV A , P1
        CPL A

        ADD A, R0
        CPL A
        MOV P2, A
        CPL A
        
        AJMP   LOOP

        END