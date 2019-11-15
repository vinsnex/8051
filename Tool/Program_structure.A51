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
;	Initialization
;
;	Task
;
DONE:	SJMP	DONE
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
	END