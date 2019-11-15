; Example program 04: 7-Segment LED scanned by Time 0
; Chin-Shiuh Shieh
; 2003-03-09
;
; Memory Allocation
LED_DATA   		EQU		30h     ;Data Byte to be displayed
NIBBLE  		EQU		00h     ;Bit for Nibble Control
; Pin Assignment
LED_PORT		EQU		P0
HIGH_NIBBLE		EQU		P2.0
LOW_NIBBLE		EQU		P2.1
BUTTON_start		EQU		P2.3
BUTTON_Time		EQU		P2.4
TIME_RAM                EQU             21h
START_RAM               EQU             20h
DELAY_1S                EQU             29h
BUZZER                  EQU             P2.2
;
PROG	EQU		0000h
		ORG		PROG+0000h
		SJMP	START
;Interrupt Vector Table
		ORG		PROG+0003h
		LCALL	INT0_ISR
		RETI
		ORG		PROG+000Bh
		LCALL	T0_ISR
		RETI
		ORG		PROG+0013h
		LCALL	INT1_ISR
		RETI
		ORG		PROG+001Bh
		LCALL	T1_ISR
		RETI
		ORG		PROG+0023h
		LCALL	UART_ISR
		RETI
;
		ORG		PROG+0030h
START:
        MOV     DELAY_1S, #00h
	MOV	LED_DATA,#00h
        MOV     TMOD,#00010001b	; Set Timer to work in Mode 1 (16-bit Timer)
        MOV	START_RAM,#00h
        MOV     TIME_RAM ,#00h
        SETB    BUZZER

        SETB	ET0				; Enable Timer 0 Interrupt
        SETB	TR0				; Start Timer 0
        SETB	ET1				; Enable Timer 0 Interrupt
        SETB	TR1				; Start Timer 0
        
        SETB	EA				; Enable Interrupt

        SETB    BUTTON_Time
        SETB    BUTTON_start
       ; SETB	IT0		
	;SETB	EX0
	;SETB	IT1		
	;SETB	EX1
LOOP:
        LCALL BUTTION_CLICK
        SJMP	LOOP

;
T0_ISR: ;Timer 0
        MOV     TH0,#0FCh   ; Timer 0 Interrupt will be generated
        MOV     TL0,#018h   ; for every 1mS
        CPL     NIBBLE
        JB      NIBBLE,SHOW_HIGH
SHOW_LOW:
        SETB    HIGH_NIBBLE
        MOV     A,LED_DATA
        ANL     A,#0Fh
        MOV     DPTR,#FONT
        MOVC    A,@A+DPTR
        MOV     LED_PORT,A
        CLR     LOW_NIBBLE
        RET
SHOW_HIGH:
        SETB    LOW_NIBBLE
        MOV     A,LED_DATA
        SWAP    A
        ANL     A,#0Fh
        MOV     DPTR,#FONT
        MOVC    A,@A+DPTR
        MOV     LED_PORT,A
        CLR     HIGH_NIBBLE
        RET
FONT:   DB      0C0h,0F9h,0A4h,0B0h,099h,092h,082h,0F8h
        DB      080h,090h,088h,083h,0A7h,0A1h,086h,08Eh
;-----------------------------------------------------------------------
T1_ISR: ;Timer 1
        MOV     TH1,#3Ch   ; Timer 0 Interrupt will be generated
        MOV     TL1,#0B0h   ; for every 1mS
        
        JB START_RAM, T1_ELSE
        MOV LED_DATA, TIME_RAM
        MOV DELAY_1S, #00h
        SETB BUZZER

        RET
T1_ELSE:
        LCALL TIME_1S
        LCALL SET_BUZZER
        MOV LED_DATA, TIME_RAM
        RET
;-----------------------------------------------------------------------

INT0_ISR:
        MOV R1, START_RAM
        
INT0_ELSE: RET

INT1_ISR:
        
		RET
UART_ISR:
		RET

;================================= function ===================================================

SET_BUZZER:
        MOV A, TIME_RAM
        CJNE A, #00h, TIME_RAM_not_EUQAL_TO_0
        ;JNZ TIME_RAM, TIME_RAM_not_EUQAL_TO_0
        CLR BUZZER
        RET

TIME_RAM_not_EUQAL_TO_0: 
        SETB BUZZER

        RET

;------------------------------------------------------------
TIME_1S:
        MOV R1, DELAY_1S
        CJNE R1, #14h, DELAY1s_ELSE
        
        MOV DELAY_1S, #00h
        MOV A, TIME_RAM
        JZ  T1_RET
        DEC TIME_RAM
        RET
DELAY1s_ELSE:
        INC DELAY_1S

T1_RET: RET
;------------------------------------------------------------
BUTTION_CLICK:
        JB BUTTON_start, CBRET1
        JB START_RAM, WAIT_BUTTON_RELEASE
        INC TIME_RAM
WAIT_BUTTON_RELEASE: 
        JNB BUTTON_start, WAIT_BUTTON_RELEASE
CBRET1:
        

        JB BUTTON_Time, CBRET2
        CPL START_RAM
WAIT_BUTTON_RELEASE2: 
        JNB BUTTON_Time, WAIT_BUTTON_RELEASE2
CBRET2:
        LCALL BUTTON_DELAY
        RET
;------------------------------------------------------------
BUTTON_DELAY:
        MOV R6, #1Fh
L1:     MOV R7, #0FFh
L2:     DJNZ R7, L2
        DJNZ R6, L1

        RET

;------------------------------------------------------------
        END
