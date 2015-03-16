/*
**
** File:	main.c
**
** Author:	Brad Israel - bdi8241
**
** Description:	Main program for project 3, Watch program
*/
#include <x86arch.h>
#include <uart.h>

#include "c_io.h"
#include "support.h"
#include "startup.h"

/*
**Definitions
*/
#define	MAX_BUF 512		//Max input/output buffer size
#define TICKS_PER_DECISECOND 2	//Really its 1.8, but 18 ticks per second is also rounded
#define	CLR_SCR "\x1b[H\x1b[J"	//Clear screen escape code

/*
**Prototypes
*/
void sti(void);
void cli(void);
void send_eoi(void);
void setInterrupt(int state);
int strcat(char *buf, char *append, int start, int last);
void itoa(char *ret, int i);
int atoi(char tens, char units);
void mystery_isr_init(void);
void mystery_isr(int vector, int code);
void timer_isr_stub(int vector, int code);
void timer_isr_init(void);
void timer_isr(int vector, int code);
void terminal_isr_stub(int vector, int code);
void terminal_isr_init(void);
void terminal_isr(int vector, int code);
void chWrite(char ch);
void bufferWrite(char *user_buf);
void displayTime(int time);
int getCorrectTime(char *set_input, int default_time);
void processInput(void);

/*
**Global Vars
*/
//Number of clock ticks that have passed
int tick = 0;

//State of the IER
int interrupt_state = 0;

//Output buffer, used to write to the device
char output_buf[MAX_BUF];
int out_start = 0;
int out_end = 0;

//Input buffer, used to read from the device
char input_buf[MAX_BUF];
int in_start = 0;
int in_end = 0;

//Current mode
//n - normal, s - set, a -alarm, t - timer
char mode = 'n';

//Current displayed time, in deciseconds
unsigned long int decisecs = 0;
//Saved normal time, in deciseconds
unsigned long int normal_time = 0;
//Saved alarm time, in deciseconds
unsigned long int alarm_time = 0;
//Saved timer time, in deciseconds
unsigned long int timer_time = 0;
//Saved lap time, in deciseconds
unsigned long int lap_time = 0;

//If 1 - print full display, 0 - print only changed chars
int print_full = 0;

//Initial display
char *initial = "00:00:00      ";

//If the timer is running or not
int timer_running = 0;
//If the timer is in lap mode
int timer_lap = 0;
//If the alarm is enabled
int alarm_on = 0;


/*
** Useful functions
*/
//Turn on interrupts
void sti(){
	asm("sti");
}

//Turn off interrupts
void cli(){
	asm("cli");
}

//Acknowledge the interrupt
void send_eoi(){
	__outb(PIC_MASTER_CMD_PORT, PIC_EOI);
}

//Turn on or off rx/tx interrupts
void setInterrupt(int state){
	__outb(UA4_IER, state);
}

//Append a string to a buffer at a starting position in the buffer.
//If the last var is set, add a null to end of buffer.
int strcat(char *buf, char *append, int start, int last){
	int len = start;
	buf += start;	//Move the buffer to the starting point
	while(*append != '\0'){
		*buf = *append;
		len++;	//Increase overall length of string
		buf++;	//Go to the next char
		append++;
	}
	if(last == 1){
		*buf = '\0';
		len++;
	}
	return len;	//return length of string, also where to start next append
}

//Convert an int into a max of two chars
void itoa(char *ret, int i){
	ret[0] = (char)((i/10) + 48);
	ret[1] = (char)((i%10) + 48);
	ret[2] = '\0';
}

//Convert a char to an int
int atoi(char tens, char units){
	return ((((int)(tens - 48)) * 10) + ((int)(units - 48)));
}

/*
**Mystery interrupt 27
*/
//Install the interrupt
void mystery_isr_init(){
	__install_isr(0x27, mystery_isr);
}

//If the interrupt occurs, just acknowledge and return
void mystery_isr(int vector, int code){
	send_eoi();
}

/*
**Timer
*/
//Install interrupt
void timer_isr_init(){
	__install_isr(INT_VEC_TIMER, timer_isr);
}

//Increase the time for every decisecond
void timer_isr(int vector, int code){
	//Stop the clock in Set mode, or the timer is stopped
	//Let it go through for all other modes
	if((mode != 's') && !(mode == 't' && timer_running == 0)){
		tick++;
		if((tick%TICKS_PER_DECISECOND) == 0){
			decisecs++;		//Update time
			displayTime(decisecs);	//Display new time
			tick = 0;		//Reset ticks, so it doesn't overflow
		}
	}

	send_eoi();	//Acknowledge interrupt
}

/*
**Terminal
*/
//Setup the serial controller, and install the interrupt
void terminal_isr_init(void){
	//Switch to bank 1
	__outb(UA4_LCR, UA4_LCR_BANK1);

	//Set the baud rate
	__outb(UA4_LBGD_L, BAUD_LOW_BYTE(BAUD_9600));
	__outb(UA4_LBGD_H, BAUD_HIGH_BYTE(BAUD_9600));

	//Set the character format in bank 0
	__outb(UA4_LCR, UA4_LCR_BANK0 | UA4_LCR_BITS_8 | UA4_LCR_1_STOP_BIT | UA4_LCR_NO_PARITY);

	//Turn off the enable bits in IER for all interrupts
	interrupt_state = 0x00;
	setInterrupt(interrupt_state);

	//Turn on the ISEN bit in the MCR
	__outb(UA4_MCR, UA4_MCR_INT_SIGNAL_ENABLE);

	__install_isr(INT_VEC_SERIAL_PORT_1, terminal_isr);
}

//When there are chars to read from the serial device,
// append them to an input buffer. When allowed to write to device,
// write another byte from the output buffer. 
void terminal_isr(int vector, int code){
	//Get device status
	char status = __inb(UA4_EIR);
	status &= UA4_EIR_INT_PRI_MASK;

	//While there are things to do
	while(status != UA4_EIR_NO_INT){

		//Handle reading
		if(status == UA4_EIR_RX_INT_PENDING){
			//Get a character, strip parity
			char nextch = __inb(UA4_RX_DATA) & 0x7f;

			//Append to input buffer
			input_buf[in_end] = nextch;
			in_end++;

			//If the buffer fills, turn off the rx int
			if(in_end == MAX_BUF-1){
				interrupt_state &= ~UA4_IER_RX_INT_ENABLE;
				setInterrupt(interrupt_state);
			}
		}

		//Handle writing
		if(status == UA4_EIR_TX_INT_PENDING){
			//Get character from output buffer
			char nextch = output_buf[out_start];

			//If there are things in the buffer, write them
			if(out_start != out_end){
				out_start++;
				if(nextch != '\0')
					__outb(UA4_TX_DATA, nextch);
			}else{
				//Reset buffer
				out_start = 0;
				out_end = 0;

				//No more input, turn off the interrupt
				interrupt_state &= ~UA4_IER_TX_INT_ENABLE;
				setInterrupt(interrupt_state);

				//Go back to read mode
				interrupt_state |= UA4_IER_RX_INT_ENABLE;
				setInterrupt(interrupt_state);
			}
		}

		//Get the new status
		status = __inb(UA4_EIR);
		status &= UA4_EIR_INT_PRI_MASK;
	}

	send_eoi();	//Acknowledge interrupt
}

//Write a character to the device by adding to output buffer
void chWrite(char ch){
	cli();	//Stop ints
	output_buf[out_end] = ch;	//Append to buffer
	output_buf[out_end+1] = '\0';	//Append a null, but don't increase out_end
	out_end++;			//Increase end
	sti(); //Restart ints

	//Enable tx bit
	interrupt_state |= UA4_IER_TX_INT_ENABLE;
	setInterrupt(interrupt_state);
}

//Write a buffer to the device by adding it to a buffer
void bufferWrite(char *user_buf){
	cli();	//Stop ints

	//Loop through and write
	char ch;
	while( (ch = *user_buf) ){
		output_buf[out_end] = ch;	//Append to buffer
		out_end++;			//Increase end
		user_buf++;			//Get next character
	}
	output_buf[out_end] = '\0'; 		//Re-add the null char to the end

	sti(); //Restart ints

	//Enable tx bit
	interrupt_state |= UA4_IER_TX_INT_ENABLE;
	setInterrupt(interrupt_state);

}

//Display the time, in deciseconds, on the terminal
//Setting print_full to 1, allows the whole time to be displayed
void displayTime(int time){
	int tmp = time;
	int mod, catLen;
	char itoaBuf[3];
	char concat[14];

	//If in timer lap mode or alarm mode 
	//don't update display
	if((mode == 't' && timer_lap == 1 && timer_running == 1) || (mode == 'a'))
		return;

	//If more than 10 decisecs have passed, need to change secs
	if((mod = tmp % 10) == 0 || print_full == 1){
		//Only update this section in timer mode
		if(mode == 't'){
			catLen = 0;
			itoa(itoaBuf, mod);
			catLen = strcat(concat, "\x1b[6D", catLen, 0);	//Jump 6 spaces back
			catLen = strcat(concat, ".", catLen, 0);	//Out put the '.'
			itoaBuf[0] = itoaBuf[1];
			itoaBuf[1] = '\0';
			catLen = strcat(concat, itoaBuf, catLen, 0);	//Output the decisecond
			catLen = strcat(concat, "\x1b[4C", catLen, 1);	//Jump back to the end
			bufferWrite(concat);
		//Not in timer mode, so output spaces
		}else{
			catLen = 0;
			catLen = strcat(concat, "\x1b[6D  \x1b[4C", catLen, 1);
			bufferWrite(concat);
		}
		tmp /= 10;	//Divide out the deciseconds

		//If more than 60 secs have passed, need to change minutes
		if((mod = tmp % 60) == 0 || print_full == 1){
			catLen = 0;
			itoa(itoaBuf, mod);
			catLen = strcat(concat, "\x1b[8D", catLen, 0);	//Jump 8 spaces back
			catLen = strcat(concat, itoaBuf, catLen, 0);	//Output the seconds
			catLen = strcat(concat, "\x1b[6C", catLen, 1);	//Jump back to the end
			bufferWrite(concat);
			tmp /= 60;	//Divide out the seconds
			
			//If more than 60 mins have passed, need to change hours
			if((mod = tmp % 60) == 0 || print_full == 1){
				catLen = 0;
				itoa(itoaBuf, mod);
				catLen = strcat(concat, "\x1b[11D", catLen, 0);
				catLen = strcat(concat, itoaBuf, catLen, 0);
				catLen = strcat(concat, ":", catLen, 0);
				catLen = strcat(concat, "\x1b[8C", catLen, 1);
				bufferWrite(concat);
				tmp /= 60;

				//If more than 24 hours have passed, reset to 0
				if((mod = tmp % 24) == 0 || print_full == 1){
					catLen = 0;
					itoa(itoaBuf, mod);
					catLen = strcat(concat, "\x1b[14D", catLen, 0);
					catLen = strcat(concat, itoaBuf, catLen, 0);
					catLen = strcat(concat, ":", catLen, 0);
					catLen = strcat(concat, "\x1b[11C", catLen, 1);
					bufferWrite(concat);
					
					//Reset the time at 24 hours, so it doesn't overflow
					if(print_full != 1)
						decisecs = 0;
	
				//Process the hours
				}else{
					catLen = 0;
					itoa(itoaBuf, mod);
					catLen = strcat(concat, "\x1b[14D", catLen, 0);
					catLen = strcat(concat, itoaBuf, catLen, 0);
					catLen = strcat(concat, ":", catLen, 0);
					catLen = strcat(concat, "\x1b[11C", catLen, 1);
					bufferWrite(concat);
				}

			//Process the minutes
			}else{
				catLen = 0;
				itoa(itoaBuf, mod);
				catLen = strcat(concat, "\x1b[11D", catLen, 0);
				catLen = strcat(concat, itoaBuf, catLen, 0);
				catLen = strcat(concat, ":", catLen, 0);
				catLen = strcat(concat, "\x1b[8C", catLen, 1);
				bufferWrite(concat);
			}

		//Process the seconds
		}else{
			catLen = 0;
			itoa(itoaBuf, mod);
			catLen = strcat(concat, "\x1b[8D", catLen, 0);
			catLen = strcat(concat, itoaBuf, catLen, 0);
			catLen = strcat(concat, "\x1b[6C", catLen, 1);
			bufferWrite(concat);
		}

	//Process deciseconds
	}else{
		//Only update this section in timer mode
		if(mode == 't'){
			catLen = 0;
			itoa(itoaBuf, mod);
			catLen = strcat(concat, "\x1b[6D", catLen, 0);
			catLen = strcat(concat, ".", catLen, 0);
			itoaBuf[0] = itoaBuf[1];
			itoaBuf[1] = '\0';
			catLen = strcat(concat, itoaBuf, catLen, 0);
			catLen = strcat(concat, "\x1b[4C", catLen, 1);
			bufferWrite(concat);
		//Not in timer mode, so output spaces
		}else{
			catLen = 0;
			catLen = strcat(concat, "\x1b[6D  \x1b[4C", catLen, 1);
			bufferWrite(concat);
		}
	}

	print_full = 0;	//Reset print_full if enabled

	//Sound the alarm
	if((decisecs == alarm_time) && (alarm_on == 1))
		chWrite(0x07);
}

//Returns correct deciseconds from string
int getCorrectTime(char *set_input, int default_time){
	int ret = 0;
	int tmp = default_time;

	//Hours tens place
	int place = (tmp/360000)*360000;
	if(set_input[0] == 'r'){
		return default_time; 				//No updates made, return the old time
	}else if(set_input[0] != ' '){
		ret += ((int)(set_input[0] - 48)) * 360000; 	//Not a space, so get int in decisecs
	}else{
		ret += place; 					//Space, so don't change
	}
	tmp -= place;

	//Hours units place
	place = (tmp/36000)*36000;
	if(set_input[1] == 'r'){
		ret += place; 					//Space, so don't change
		set_input[2] = ' '; 				//Set the rest to spaces, so that they aren't changed
		set_input[3] = ' ';
		set_input[4] = ' ';
		set_input[5] = ' ';
	}else if(set_input[1] != ' '){
		ret += ((int)(set_input[1] - 48)) * 36000; 	//Not a space, so get int in decisecs
	}else{
		ret += place; 					//Space, so don't change
	}
	tmp -= place;

	//Minutes tens place
	place = (tmp/6000)*6000;
	if(set_input[2] == 'r'){
		ret += place; 					//Space, so don't change
		set_input[3] = ' '; 				//Set the rest to spaces, so that they aren't changed
		set_input[4] = ' ';
		set_input[5] = ' ';
	}else if(set_input[2] != ' '){
		ret += ((int)(set_input[2] - 48)) * 6000; 	//Not a space, so get int in decisecs
	}else{
		ret += place; 					//Space, so don't change
	}
	tmp -= place;

	//Minutes units place
	place = (tmp/600)*600;
	if(set_input[3] == 'r'){
		ret += place; 					//Space, so don't change
		set_input[4] = ' '; 				//Set the rest to spaces, so that they aren't changed
		set_input[5] = ' ';
	}else if(set_input[3] != ' '){
		ret += ((int)(set_input[3] - 48)) * 600; 	//Not a space, so get int in decisecs
	}else{
		ret += place; 					//Space, so don't change
	}
	tmp -= place;

	//Seconds tens place
	place = (tmp/100)*100;
	if(set_input[4] == 'r'){
		ret += place; 					//Space, so don't change
		set_input[5] = ' '; 				//Set the rest to spaces, so that they aren't changed
	}else if(set_input[4] != ' '){
		ret += ((int)(set_input[4] - 48)) * 100; 	//Not a space, so get int in decisecs
	}else{
		ret += place; 					//Space, so don't change
	}
	tmp -= place;

	//Seconds units place
	place = (tmp/10)*10;
	if(set_input[5] == 'r'){
		ret += place; 					//Space, so don't change
	}else if(set_input[5] != ' '){
		ret += ((int)(set_input[5] - 48)) * 10; 	//Not a space, so get int in decisecs
	}else{
		ret += place; 					//Space, so don't change
	}
	tmp -= place;

	return ret;
}

//Process the input buffer
void processInput(){

	while(in_start != in_end){
		char cmd = input_buf[in_start];
		//Normal mode
		if(mode == 'n' && cmd == 'a'){
			print_full = 1;		//Print the full time
			alarm_on = 1;		//Turn the alarm on
			bufferWrite("\x1b[3D*\x1b[2C");//Display the alarm flag
			displayTime(alarm_time);//Display the saved alarm time
			mode = 'a';		//Change mode to alarm
			in_start++;
		}else if(mode == 'n' && cmd == 's'){
			cli();
			mode = 's';		//Change mode to Set, stops clock
			normal_time = decisecs;	//Save current time
			in_start++;		//Increase the start point
			sti();
		}else if(mode == 'n' && cmd == 't'){
			cli();			//Stop interrupts so the time doesn't change
			normal_time = decisecs;	//Save the current normal time
			decisecs = timer_time;	//Restore the timer time
			mode = 't';		//Change the mode to timer
			print_full = 1;
			timer_running = 0;	//Initially stop the timer
			if(timer_lap == 1)
				displayTime(lap_time);	//Display the saved lap timer time
			else
				displayTime(decisecs);	//Display the saved timer time
			in_start++;		//Increase the start point
			sti();			//Reenable interrupts
	
		//Set mode
		}else if(mode == 's'){
			//Busy wait until 6 chars or an 'r' is inputted
			char set_input[6];
			int i = 0;
			
			//Jump back to beginning
			bufferWrite("\x1b[14D");

			while(i < 6){
				//If the user hasn't entered enough, wait .5sec
				while(in_start == in_end){
					__delay(5);
				}

				//Set the char in set_input
				set_input[i] = input_buf[in_start];

				//If an 'r' was entered, stop looping
				if(input_buf[in_start] == 'r'){
					in_start++;
					break;
				}

				//Write out a ':' in the correct spot
				if(i == 2 || i == 4){
					chWrite(':');
				}

				//Echo the char, if space jump forward 1
				if(set_input[i] == ' '){
					bufferWrite("\x1b[1C");
				}else{		
					chWrite(set_input[i]);
				}

				//Increase in_start and get next char
				in_start++;
				i++;
			}

			//Move cursor back to the end of the display
			bufferWrite("\x1b[14D\x1b[14C");

			//Set the correct time
			decisecs = getCorrectTime(set_input, normal_time);
			//decisecs = atoi(set_input[0], set_input[1]) * 36000;
			mode = 'n';		//Change the mode to normal
			print_full = 1;
			displayTime(decisecs);//Display the new time
		
		//Alarm mode
		}else if(mode == 'a'){
			//Busy wait until 6 chars or an 'r' or 'c' is inputted
			char alarm_input[6];
			int i = 0;			

			//Jump back to beginning
			bufferWrite("\x1b[14D");

			while(i < 6){
				//If the user hasn't entered enough, wait .5sec
				while(in_start == in_end){
					__delay(5);
				}

				//Set the char in set_input
				alarm_input[i] = input_buf[in_start];

				//If an 'r' was entered, stop looping
				if(alarm_input[i] == 'r'){
					in_start++;
					break;
				}else if(alarm_input[i] == 'c'){
					in_start++;
					alarm_on = 0;	//Stop the alarm
					alarm_input[i] = 'r';
					break;
				}

				//Write out a ':' in the correct spot
				if(i == 2 || i == 4){
					chWrite(':');
				}

				//Echo the char, if space jump forward 1
				if(alarm_input[i] == ' '){
					bufferWrite("\x1b[1C");
				}else{		
					chWrite(alarm_input[i]);
				}

				//Increase in_start and get next char
				in_start++;
				i++;
			}

			//Move cursor back to the end of the display
			bufferWrite("\x1b[14D\x1b[14C");

			//If alarm disabled, clear the alarm flag
			if(alarm_on == 0){
				bufferWrite("\x1b[3D \x1b[2C");
			}

			//Set the correct time
			alarm_time = getCorrectTime(alarm_input, alarm_time);
			mode = 'n';		//Change the mode to normal
			print_full = 1;
			//displayTime(decisecs);//Display the new time

		//Timer mode
		}else if(mode == 't' && cmd == 'c'){
			cli();
			decisecs = 0;		//Clear the timer time
			print_full = 1;
			displayTime(decisecs);	//Display the saved timer time
			in_start++;
			sti();
		}else if(mode == 't' && cmd == 'l'){
			//Set lap mode and lap flag
			if(timer_lap == 0){
				timer_lap = 1;
				lap_time = decisecs;
				bufferWrite("\bL");
				print_full = 1;		//Print full display next time
				displayTime(decisecs);
			}else{
				timer_lap = 0;
				bufferWrite("\b ");
				print_full = 1;		//Print full display next time
				displayTime(decisecs);
			}

			in_start++;
		}else if(mode == 't' && cmd == 'r'){
			cli();			//Stop interrupts so the time doesn't change
			timer_time = decisecs;	//Save the current time
			decisecs = normal_time;	//Restore the normal time
			mode = 'n';		//Change the mode to normal
			print_full = 1;
			displayTime(decisecs);	//Display the saved time
			in_start++;		//Increase the start point
			sti();			//Reenable interrupts
			
		}else if(mode == 't' && cmd == 's'){
			if(timer_running == 0)
				timer_running = 1;
			else
				timer_running = 0;
			in_start++;
		}else{
			in_start++;	//Ignore the character
		}
	}
	
	cli(); //Stop interrupts
	if(in_start == in_end){
		//Finished all input, clear the buffer
		in_start = 0;
		in_end = 0;
	}
	sti(); //Start interrupts

	//Enable rx interrupts
	interrupt_state |= UA4_IER_RX_INT_ENABLE;
	setInterrupt(interrupt_state);
}

int main( void ) {

	/*Initialize mystery interrupt*/
	mystery_isr_init();

	/*Initialize the timer*/
	timer_isr_init();

	/*Initialize the terminal*/
	terminal_isr_init();
	
	//Enable rx interrupts 
	interrupt_state |= UA4_IER_RX_INT_ENABLE;
	setInterrupt(interrupt_state);

	/*Enable Interrupts*/
	sti();

	//Clear the screen
	bufferWrite(CLR_SCR);

	//Show the initial display
	bufferWrite(initial);

	//Display welcome
	c_printf("PROJECT 3 - Digital Watch w/ stopwatch and alarm!\n Brad Israel - bdi8241\n");

	//Go into a loop to process the input buffer
	while(1){
		processInput();
	}
}
