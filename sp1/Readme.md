#**Systems Programming**
##ISR clock/timer/alarm
Bootstrapped C code that boots from a floppy disk, runs without an operating system, and handles the interrupt service 
routines (ISRs) required to operate a VT220 serial terminal. Once running, the program displays a running clock on the 
terminal that allows the time to be modified and has four modes; clock, set, timer, and alarm. The clock mode displays 
the current time down to the second. Set mode allows the display to be set in any mode, which allows you to adjust the 
clock time or alarm countdown. Timer mode acts like a common stopwatch with start, stop, and lap functions. Finally, 
the alarm mode will countdown from a set time and sound a audible beep when the countdown hits zero.

*Note for students currently enrolled in SP1: While the projects may look the same, the professor changes them 
slightly so that he can tell if you copy it. SO DON'T CHEAT! Figuring out how this works on your own is the 
only way to understand it!

Code: [ZIP](SP1_ISR_Project.zip)

##AOS Compiler in x86 Assembly
AOS is a made up language with a simple set of instructions and our project uses x86 (AT&T syntax) assembly to 
create a 2-pass compiler for AOS input. My project assembles AOS assembly into the op codes and then builds the 
symbol table. There is example input and output for the project in the zip file and the code is documented for 
"easier" (it's still assembly :) ) reading.

Code: [ZIP](SP1_AOS_Project.zip)