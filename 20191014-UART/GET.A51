NUMBER	EQU 30h
LED_PIN EQU P2
BUZZER  EQU P1.7
GET_DATA EQU 20h
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
    MOV TMOD, #00100001b
    MOV TH1, #0FDh
    SETB TR1
	SETB BUZZER
	SETB	ET0
	SETB	TR0
	MOV TH0, #00h
	MOV TL0, #00h
	SETB 	EA
	SETB PT0
DONE:	
	LCALL UART_GETC
	;ANL GET_DATA, #07h
	;CPL 21h
	MOV NUMBER, GET_DATA
	XRL GET_DATA, #0FFh

	MOV LED_PIN, GET_DATA

    SJMP	DONE
;		
	;MOV 21h, GET_DATA
INT0_ISR:
;	Service Routine for External Interrupt 0
	RET
T0_ISR:
	MOV A, NUMBER
	MOV DPTR, #THDATA
	MOVC A, @A+DPTR
	MOV TH0, A

	MOV A, NUMBER
	MOV DPTR, #TLDATA
	MOVC A, @A+DPTR
	MOV TL0, A

	CPL BUZZER
	RET
THDATA:   DB      0FEh,0FEh,0FEh,0FEh,0FEh,0FEh,0FEh,0FEh

TLDATA:   DB      022h,057h,085h,09Bh,085h,0C2h,0E4h,03Eh
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
UART_GETC:
    JNB RI, UART_GETC
    CLR RI
    MOV GET_DATA, SBUF
    RET


;=========================================================
	END