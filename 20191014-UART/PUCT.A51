DIV_SWITCH EQU P1
DIV_SAVE   EQU 20h
ORG	0000h
	SJMP	START
;
;Interrupt Vector Table
	ORG	0003h
	LCALL	INT0_ISR
	RETI
	ORG	000Bh
	LCALL	T0_ISR
	RETI
	ORG	0013h
	LCALL	INT1_ISR
	RETI
	ORG	001Bh
	LCALL	T1_ISR
	RETI
	ORG	0023h
	LCALL	UART_ISR
	RETI
;
	ORG	0030h
START:
    MOV SCON,#0D3h
    MOV TMOD, #00100000b
    MOV TH1, #0FDh
    MOV DIV_SWITCH, #0FFh
    SETB TR1
DONE:	
    MOV A, DIV_SWITCH
    CPL A
    CJNE A, DIV_SAVE, DONE_BACK
    AJMP DONE
DONE_BACK:
    LCALL UART_PUTC
    MOV DIV_SAVE, A
    SJMP	DONE
;		
INT0_ISR:
;	Service Routine for External Interrupt 0
	RET
T0_ISR:
;	Service Routine for Timer 0 Interrupt
	RET
INT1_ISR:
;	Service Routine for External Interrupt 1
	RET
T1_ISR:
;       Service Routine for Timer 1 Interrupt
	RET
UART_ISR:
;	Service Routine for UART Interrupt
	RET
;
;====================== function =========================


UART_PUTC:
	MOV SBUF, A
UART_PUTC_WAIT:
	JNB TI, UART_PUTC_WAIT
	CLR TI
	RET
;==========================================================
	END