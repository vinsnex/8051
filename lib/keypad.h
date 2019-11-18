// Keypad functions (with 74C922) by Chin-Shiuh Shieh on 2011-04-04
#ifndef KEYPAD_DO
	#define KEYPAD_DO P1
#endif

#ifndef KEYPAD_DA
	#define KEYPAD_DA P1_4
#endif

#ifndef KEYPAD_NULL
	#define KEYPAD_NULL 0x10
#endif

// Function Prototypes
unsigned char keypad_get_key(void);
unsigned char keypad_get_key_echo(void);
unsigned char keypad_check_key(void);
unsigned char keypad_get_byte(void);
unsigned int  keypad_get_word(void);
unsigned int  keypad_get_integer();

unsigned char keypad_get_key(void)
{
unsigned char key;
KEYPAD_DO|=0x1F;
while(KEYPAD_DA==0);
key=KEYPAD_DO;key&=0x0F;
while(KEYPAD_DA==1);
return key;
}

unsigned char keypad_get_key_echo(void)
{
unsigned char key;
KEYPAD_DO|=0x1F;
while(KEYPAD_DA==0);
key=KEYPAD_DO;key&=0x0F;
// uart_put_char(ASCII[key]);
// clcd_put_char(ASCII[key]);
while(KEYPAD_DA==1);
return key;
}

unsigned char keypad_get_byte(void)
{
return(keypad_get_key_echo()*16+keypad_get_key_echo());
}

unsigned int keypad_get_word(void)
{
return(keypad_get_byte()*256+keypad_get_byte());
}

unsigned char keypad_check_key(void)
{
unsigned char key;
KEYPAD_DO|=0x1F;
if(KEYPAD_DA==1)
	{key=KEYPAD_DO;key&=0x0F;while(KEYPAD_DA==1);return key;}
else
	return KEYPAD_NULL;
}

unsigned int keypad_get_integer(void)
{
unsigned int value=0;
unsigned char key;
while((key=keypad_get_key())<0x0A)
	{
//	uart_put_char(ASCII[key]);
//	clcd_put_char(ASCII[key]);
	value=value*10+key;
	}
return value;
}