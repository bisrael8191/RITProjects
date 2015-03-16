#
#Project 2 - AOS Assembler
# File: project2.s
# Compile: make
# Run: ./project2 filename
# Description: Assembles AOS assembly into the op codes
#		and builds the symbol table.
# Author: Brad Israel - bdi8241
#
.section .data
	#Static Variables (sys calls, etc)
	.equ EXIT, 1
#	.equ READ, 3
	.equ WRITE, 4
	.equ OPEN, 5
	.equ CLOSE, 6
	.equ LSEEK, 19
	.equ STDOUT, 1
	.equ O_RDONLY, 0
	.equ MAX_LINE, 80	#Max number of characters on a line

pc:
	.int 0	#Program counter variable, starts at 0

symElem:
	.int 0	#Number of elements currently stored in the symbol table

HALT:
	.ascii "HALT"
LA:
	.ascii "LA"
L:
	.ascii "L"
ST:
	.ascii "ST"
STAL:
	.ascii "STAL"
NG:
	.ascii "NG"
OP:
	.ascii "OP"
READ:
	.ascii "READ"
PRT:
	.ascii "PRT"
JP:
	.ascii "JP"
JZ:
	.ascii "JZ"
CALL:
	.ascii "CALL"
RET:
	.ascii "RET"
BSS:
	.ascii "BSS"
END:
	.ascii "END"

tmpLabel:
	.ascii "    "

tmpInst:
	.ascii "    "

tmpEeee:
	.ascii "    "

tmpPcHex:
	.ascii "00000000"

space:
	.ascii " "

newline:
	.ascii "\n"

symTableOutput:
	.ascii "ssss   vvvv\n"

startatOutput:
	.ascii "\n\nStart at: vvvv\n\n\n"

finalOutput:
	.ascii "                  "

error:
	.ascii "An error has occurred!\n"

.section bss
	.lcomm filehandle, 4
	.lcomm lineBuf, 80
	.lcomm symTable, 800

.section .text
.globl main
.type main, @function
main:
	#Prologue
	pushl	%ebp
        movl	%esp, %ebp

	#Open the specified file
	#Put the cmdln arg in eax
        movl    12(%ebp), %eax
        addl    $4, %eax

	#Push the args and do the syscall
	pushl	$O_RDONLY
	pushl	(%eax)
	pushl	$0
	movl	$OPEN, %eax
	lcall	$0x27, $0
	addl	$12, %esp

	cmpl	$2, %eax		#Check if a file desc was returned (must be >2)
	jle	error_exit		#If neg result, jump to exit
	movl	%eax, filehandle	#Mov the file desc into the filehandle mem

	call	pass1			#Start pass 1, which builds and prints the symbol table

	movl	$0, pc			#Reset program counter

	#lseek back to the beginning of file
	pushl $0
	pushl $0
	pushl filehandle
	pushl $0
	movl	$LSEEK, %eax
	lcall	$0x27, $0
	addl	$12, %esp

	call	pass2			#Start pass 2, which processes displays the assembled AOS code
	
close_file:
	pushl	filehandle
	pushl	$0
	movl	$CLOSE, %eax
	lcall	$0x27, $0
	addl	$8, %esp
	jmp	exit

error_exit:
	#Write error message
	pushl	$23
	pushl	$error
	pushl	$STDOUT
	pushl	$0
	movl	$WRITE, %eax
	lcall	$0x27, $0
	addl	$16, %esp

exit:
	#Exit
	pushl	$0
	movl	$EXIT, %eax
	lcall	$0x27, $0
	addl	$4, %esp

#Pass 1 is used to build the symbol table that is used in Pass 2.
#Symbol table is stored in an array called symTable.
.type pass1, @function
pass1:
	#Prologue
	pushl	%ebp
	movl	%esp, %ebp
	pushl	%edi
	pushl	%esi
	pushl	%ebx
	subl	$0, %esp

read_loop:
	#Call to getline function
	pushl	$lineBuf
	pushl	filehandle
	call	getline
	addl	$8, %esp

	cmpl	$-1, %eax		#Check if file is done
	je	pass1_done		#Jump out of the loop
	movl	%eax, %ecx		#Move line length to %ecx

	leal	lineBuf, %esi		#Put the addr of the new line in %esi
	cld
	lodsb				#Load the first char into %al

	#If the first char is a #, loop
	cmpb	$'#', %al
	je	cont_loop		#Skip lines that are comments

	#Else, line must be in the other format
	pushl	%ecx
	call	parseLine		#Call function that will return the pc increment(push ecx)
	addl	$4, %esp

	movl	%eax, %edx		#Store pc increment count

	cld
	leal	tmpLabel, %esi
	lodsl				#Load the label into eax
	cmpl	$0x20202020, %eax	#Check if there is a label
	jne	store_label		#If the label exists, store it in the symbol table
	addl	%edx, pc		#No label, so increment the program counter by the correct amount
	jmp	cont_loop		#Continue looping
	
store_label:
	movl	symElem, %edi			#Get number of elements in the symbol table
	movl	%eax, symTable(, %edi, 8)	#Store the new symbol after the last one

	pushl	%edx				#Make sure that edx doesn't get destroyed
	pushl	$tmpPcHex
	pushl	pc
	call	convertx			#Convert current pc to hex
	addl	$8, %esp
	popl	%edx				#Restore edx

	cld
	leal	tmpPcHex, %esi
	lodsl					#The upper 4 bytes are ignored
	lodsl					#This is the needed lower 4 bytes of hex
	movl	$4, %ebx
	movl	%eax, symTable(%ebx, %edi, 8)	#Store the hex location after the symbol

	addl	%edx, pc			#Increment the pc
	incl	symElem				#Increment the number of elements in the symbol table	

	#Write out one line of the symbol table
	cld
	leal	tmpLabel, %esi
	leal	symTableOutput, %edi
	movsl

	leal	tmpPcHex, %esi
	leal	symTableOutput+7, %edi
	lodsl					#Ignore the first 4 bytes
	movsl					#Move the needed 4 bytes into the string

	pushl	$12
	pushl	$symTableOutput
	pushl	$STDOUT
	pushl	$0
	movl	$WRITE, %eax
	lcall	$0x27, $0
	addl	$16, %esp

cont_loop:
	jmp	read_loop			#Keep looping

pass1_done:
	#Print Start at address
	pushl	$19
	pushl	$startatOutput
	pushl	$STDOUT
	pushl	$0
	movl	$WRITE, %eax
	lcall	$0x27, $0
	addl	$16, %esp

	#epilogue
	addl	$0, %esp
	popl	%ebx
	popl	%esi
	popl	%edi
	leave
	ret

#Pass 2 looks up label operands in the symbol table
# and prints out the program counter, the AOS op code,
# and the original statement
.type pass2, @function
pass2:
	#Prologue
	pushl	%ebp
	movl	%esp, %ebp
	pushl	%edi
	pushl	%esi
	pushl	%ebx
	subl	$0, %esp

read_loop2:
	#Reset finalOutput to 18 space chars
	leal	space, %esi
	leal	finalOutput, %edi
	movl	$18, %ecx
	cld
	lodsb
	rep	stosb

	#Call to getline function
	pushl	$lineBuf
	pushl	filehandle
	call	getline
	addl	$8, %esp

	cmpl	$-1, %eax		#Check if file is done
	je	pass2_done		#Jump out of the loop
	movl	%eax, %ebx		#Move line length to %ebx

	leal	lineBuf, %esi		#Put the addr of the new line in %esi
	cld
	lodsb				#Load the first char into %al

	#If the first char is a #, loop
	cmpb	$'#', %al
	jne	format2			#Line isn't a comment, so must be the other format
	#Print finalOutput
	pushl	$18
	pushl	$finalOutput
	pushl	$STDOUT
	pushl	$0
	movl	$WRITE, %eax
	lcall	$0x27, $0
	addl	$16, %esp

	#Print the original statement
	pushl	%ebx
	pushl	$lineBuf
	pushl	$STDOUT
	pushl	$0
	movl	$WRITE, %eax
	lcall	$0x27, $0
	addl	$16, %esp

	#Print newline char
	pushl	$1
	pushl	$newline
	pushl	$STDOUT
	pushl	$0
	movl	$WRITE, %eax
	lcall	$0x27, $0
	addl	$16, %esp

	jmp cont_pass2

format2:
	#Else, line must be in the other format
	pushl	%ecx
	call	parseLine		#Call function that will return the pc increment(push ecx)
	addl	$4, %esp

	addl	%eax, pc		#Increment the program counter by the correct amount

	#Print finalOutput
	pushl	$18
	pushl	$finalOutput
	pushl	$STDOUT
	pushl	$0
	movl	$WRITE, %eax
	lcall	$0x27, $0
	addl	$16, %esp

	#Print the original statement
	pushl	%ebx
	pushl	$lineBuf
	pushl	$STDOUT
	pushl	$0
	movl	$WRITE, %eax
	lcall	$0x27, $0
	addl	$16, %esp

	#Print newline char
	pushl	$1
	pushl	$newline
	pushl	$STDOUT
	pushl	$0
	movl	$WRITE, %eax
	lcall	$0x27, $0
	addl	$16, %esp

cont_pass2:
	jmp	read_loop2		#Keep looping

pass2_done:
	#epilogue
	addl	$0, %esp
	popl	%ebx
	popl	%esi
	popl	%edi
	leave
	ret

#Parses one line of AOS code
#
#Sets tmpLabel, if there is a label on the statement
#Sets tmpInst to the Mnenomic for the op code
#Sets tmpEeee, if there is an operand
#Sets finalOutput to the program counter and op code
#Returns how many bytes to increment program counter
#
.type parseLine, @function
parseLine:
	#Prologue
	pushl	%ebp
	movl	%esp, %ebp
	pushl	%edi
	pushl	%esi
	pushl	%ebx
	subl	$0, %esp

	#Make sure tmpLabel is reset to four spaces
	leal	space, %esi
	leal	tmpLabel, %edi
	movl	$4, %ecx
	cld
	lodsb
	rep	stosb

	#Make sure tmpInst is reset to four spaces
	leal	space, %esi
	leal	tmpInst, %edi
	movl	$4, %ecx
	cld
	lodsb
	rep	stosb

	#Make sure tmpEeee is reset to four spaces
	leal	space, %esi
	leal	tmpEeee, %edi
	movl	$4, %ecx
	cld
	lodsb
	rep	stosb

	movl	8(%ebp), %ecx		#Move length of string to ecx
	leal	lineBuf, %esi		#Put the addr of the line in %esi
	leal	tmpLabel, %edi		#Put the addr of the possible label in %edi
	
	#If first char isn't a space, create the label
label_create:
	cld
	lodsb				#Load the first char into %al
	cmpb	$' ', %al
	je	calculate		#If space, go to the byte calculating loop
	cmpb	$0x9, %al
	je	calculate		#If tab, also jump
	stosb				#If not, store the char in tmpLabel
	loop	label_create		#Loop until there is a space
	
	#Else, calculate how many bytes to increment the pc
calculate:
	leal	tmpInst, %edi		#Load the instruction buffer in %edi

ignore_space1:
	cld
	lodsb				#Load the next char into %al
	cmpb	$' ', %al		#If space char, ignore and keep looping
	je	ignore_space1
	cmpb	$0x9, %al
	jne	inst_create		#If not a tab either, jump to next step
	loop	ignore_space1

inst_create:
	cld
	stosb				#Store the first op char in tmpInst
	lodsb				#Load next char
	cmpb	$' ', %al		#If space char, means end of inst
	je	find_eeee
	cmpb	$0x9, %al
	je	find_eeee		#If tab char, also means end of inst
	cmpb	$0x0, %al
	je	inst_compare		#If null byte, line is finished
	loop	inst_create		#Keep looping until op is in tmpInst, or end of line

	jcxz	inst_compare		#If end of line is reached, skip trying to get an eeee
find_eeee:
	leal	tmpEeee, %edi		#Load the eeee buffer in %edi
ignore_space2:
	cld
	lodsb				#Load the next char into %al
	cmpb	$' ', %al		#If space char, ignore and keep looping
	je	ignore_space2
	cmpb	$0x9, %al
	jne	eeee_create		#If not a tab either, jump to next step
	loop	ignore_space2		#Or until end of line

	jcxz	inst_compare		#If end of line is reached, skip trying to get an eeee
eeee_create:
	cld
	stosb				#Store the first char in tmpEeee
	lodsb				#Load next char
	cmpb	$' ', %al		#If space char, means end of eeee
	je	inst_compare
	cmpb	$0x9, %al		#If space char, means end of eeee
	je	inst_compare
	cmpb	$0x0, %al		#If tab char, also means end of eeee
	je	inst_compare		#If null byte, line is finished
	loop	eeee_create		#Keep looping until label/op/n is in tmpEeee, or end of line
	
#Compare the instructions to figure how many bytes to increment the pc
inst_compare:
inst_halt:
	#HALT
	pushl	$4
	pushl	$HALT
	pushl	$tmpInst
	call	ncompare		#Compare HALT to the line instruction
	addl	$12, %esp
	cmpl	$0, %eax		#Check if they match
	jne	inst_la			#If they don't match, check next instruction
	
	#Set the output to display for pass 2
	call	pcToHex			#Convert pc to 4 byte hex
	movl	%eax, finalOutput	#Put it in the final output
	movw	$0x3030, finalOutput+7	#Put the op code in the final output

	movl	$1, %eax		#They do match, so increment pc by 1
	jmp	parseLine_done		#Jump to the end of the function
	
inst_la:
	#LA
	pushl	$2
	pushl	$LA
	pushl	$tmpInst
	call	ncompare
	addl	$12, %esp
	cmpl	$0, %eax
	jne	inst_l

	#Set the output to display for pass 2
	call	pcToHex			#Convert pc to 4 byte hex
	movl	%eax, finalOutput	#Put it in the final output
	movw	$0x3031, finalOutput+7	#Put the op code in the final output
	call	findSymbol		#Find the symbol 4 byte hex
	movl	%eax, finalOutput+10	#Set the symbol hex

	movl	$3, %eax
	jmp	parseLine_done
inst_l:
	#L
	pushl	$1
	pushl	$L
	pushl	$tmpInst
	call	ncompare
	addl	$12, %esp
	cmpl	$0, %eax
	jne	inst_stal

	#Set the output to display for pass 2
	call	pcToHex			#Convert pc to 4 byte hex
	movl	%eax, finalOutput	#Put it in the final output
	movw	$0x3131, finalOutput+7	#Put the op code in the final output
	call	findSymbol		#Find the symbol 4 byte hex
	movl	%eax, finalOutput+10	#Set the symbol hex

	movl	$3, %eax
	jmp	parseLine_done
inst_stal:
	#STAL
	pushl	$4
	pushl	$STAL
	pushl	$tmpInst
	call	ncompare
	addl	$12, %esp
	cmpl	$0, %eax
	jne	inst_st

	#Set the output to display for pass 2
	call	pcToHex			#Convert pc to 4 byte hex
	movl	%eax, finalOutput	#Put it in the final output
	movw	$0x3331, finalOutput+7	#Put the op code in the final output
	
	movl	$1, %eax
	jmp	parseLine_done
inst_st:
	#ST
	pushl	$2
	pushl	$ST
	pushl	$tmpInst
	call	ncompare
	addl	$12, %esp
	cmpl	$0, %eax
	jne	inst_ng

	#Set the output to display for pass 2
	call	pcToHex			#Convert pc to 4 byte hex
	movl	%eax, finalOutput	#Put it in the final output
	movw	$0x3231, finalOutput+7	#Put the op code in the final output

	movl	$1, %eax
	jmp	parseLine_done
inst_ng:
	#NG
	pushl	$2
	pushl	$NG
	pushl	$tmpInst
	call	ncompare
	addl	$12, %esp
	cmpl	$0, %eax
	jne	inst_op

	#Set the output to display for pass 2
	call	pcToHex			#Convert pc to 4 byte hex
	movl	%eax, finalOutput	#Put it in the final output
	movw	$0x3034, finalOutput+7	#Put the op code in the final output

	movl	$1, %eax
	jmp	parseLine_done
inst_op:
	#OP
	pushl	$2
	pushl	$OP
	pushl	$tmpInst
	call	ncompare
	addl	$12, %esp
	cmpl	$0, %eax
	jne	inst_read

	#Set the output to display for pass 2
	call	pcToHex			#Convert pc to 4 byte hex
	movl	%eax, finalOutput	#Put it in the final output

	#Figure out the op code (4n)
	leal	tmpEeee, %esi
	lodsb
	cmpb	$'+', %al
	jne	op_minus
	movw	$0x3134, finalOutput+7	#Put the op code in the final output
	jmp	op_done
op_minus:
	cmpb	$'-', %al
	jne	op_mult
	movw	$0x3234, finalOutput+7	#Put the op code in the final output
	jmp	op_done
op_mult:
	cmpb	$'*', %al
	jne	op_div
	movw	$0x3334, finalOutput+7	#Put the op code in the final output
	jmp	op_done
op_div:
	cmpb	$'/', %al
	jne	op_mod
	movw	$0x3434, finalOutput+7	#Put the op code in the final output
	jmp	op_done
op_mod:
	movw	$0x3534, finalOutput+7	#Put the op code in the final output
op_done:

	movl	$1, %eax
	jmp	parseLine_done
inst_read:
	#READ
	pushl	$4
	pushl	$READ
	pushl	$tmpInst
	call	ncompare
	addl	$12, %esp
	cmpl	$0, %eax
	jne	inst_prt

	#Set the output to display for pass 2
	call	pcToHex			#Convert pc to 4 byte hex
	movl	%eax, finalOutput	#Put it in the final output
	movw	$0x3037, finalOutput+7	#Put the op code in the final output

	movl	$1, %eax
	jmp	parseLine_done
inst_prt:
	#PRT
	pushl	$3
	pushl	$PRT
	pushl	$tmpInst
	call	ncompare
	addl	$12, %esp
	cmpl	$0, %eax
	jne	inst_jp

	#Set the output to display for pass 2
	call	pcToHex			#Convert pc to 4 byte hex
	movl	%eax, finalOutput	#Put it in the final output
	movw	$0x3137, finalOutput+7	#Put the op code in the final output

	movl	$1, %eax
	jmp	parseLine_done
inst_jp:
	#JP
	pushl	$2
	pushl	$JP
	pushl	$tmpInst
	call	ncompare
	addl	$12, %esp
	cmpl	$0, %eax
	jne	inst_jz

	#Set the output to display for pass 2
	call	pcToHex			#Convert pc to 4 byte hex
	movl	%eax, finalOutput	#Put it in the final output
	movw	$0x3039, finalOutput+7	#Put the op code in the final output		
	call	findSymbol		#Find the symbol 4 byte hex
	movl	%eax, finalOutput+10	#Set the symbol hex
	
	#movl	$finalOutput, %edi
	#call	pcToHex
	#movl	%eax, (%edi)
	#movw	$0x3039, 7(%edi)
	#call	findSymbol
	#movl	%eax, 10(%edi)

	movl	$3, %eax
	jmp	parseLine_done
inst_jz:
	#JZ
	pushl	$2
	pushl	$JZ
	pushl	$tmpInst
	call	ncompare
	addl	$12, %esp
	cmpl	$0, %eax
	jne	inst_call

	#Set the output to display for pass 2
	call	pcToHex			#Convert pc to 4 byte hex
	movl	%eax, finalOutput	#Put it in the final output
	movw	$0x3139, finalOutput+7	#Put the op code in the final output
	call	findSymbol		#Find the symbol 4 byte hex
	movl	%eax, finalOutput+10	##Set the symbol hex

	movl	$3, %eax
	jmp	parseLine_done
inst_call:
	#CALL
	pushl	$4
	pushl	$CALL
	pushl	$tmpInst
	call	ncompare
	addl	$12, %esp
	cmpl	$0, %eax
	jne	inst_ret

	#Set the output to display for pass 2
	call	pcToHex			#Convert pc to 4 byte hex
	movl	%eax, finalOutput	#Put it in the final output
	movw	$0x3239, finalOutput+7	#Put the op code in the final output
	call	findSymbol		#Find the symbol 4 byte hex
	movl	%eax, finalOutput+10	#Set the symbol hex

	movl	$3, %eax
	jmp	parseLine_done
inst_ret:
	#RET
	pushl	$3
	pushl	$RET
	pushl	$tmpInst
	call	ncompare
	addl	$12, %esp
	cmpl	$0, %eax
	jne	inst_bss

	#Set the output to display for pass 2
	call	pcToHex			#Convert pc to 4 byte hex
	movl	%eax, finalOutput	#Put it in the final output
	movw	$0x3339, finalOutput+7	#Put the op code in the final output

	movl	$1, %eax
	jmp	parseLine_done
inst_bss:
	#BSS
	pushl	$3
	pushl	$BSS
	pushl	$tmpInst
	call	ncompare
	addl	$12, %esp
	cmpl	$0, %eax
	jne	inst_end

	#Set the output to display for pass 2
	call	pcToHex			#Convert pc to 4 byte hex
	movl	%eax, finalOutput	#Put it in the final output
	

	#Use the number after to figure out eax = n*2
	pushl	$tmpEeee
	call	evaluate
	addl	$4, %esp
	imull	$2, %eax
	jmp	parseLine_done
inst_end:
	#END
	pushl	$3
	pushl	$END
	pushl	$tmpInst
	call	ncompare
	addl	$12, %esp

	cmpl	$0, %eax
	jne	parseLine_done

	#Set the output to display for pass 2
	call	pcToHex			#Convert pc to 4 byte hex
	movl	%eax, finalOutput	#Put it in the final output

	#Find the Label memory address
	call	findSymbol
	movl	%eax, startatOutput+12	#Set the start at output
	
	movl	$0, %eax		#This is the last line, don't increment counter
	jmp	parseLine_done

	
parseLine_done:
	#epilogue
	addl	$0, %esp
	popl	%ebx
	popl	%esi
	popl	%edi
	leave
	ret

#Finds a symbol in the symbol table
#
#Returns the hex program counter location for the symbol
.type findSymbol, @function
findSymbol:
	#Prologue
	pushl	%ebp
	movl	%esp, %ebp
	pushl	%edi
	pushl	%esi
	pushl	%ebx
	subl	$0, %esp

	leal	symTable, %edi		#Put the symbol table in %edi
	leal	tmpEeee, %esi		#Put the symbol to find in %esi
	
	#Find the max number of 4 byte iterations (symElem * 2)
	movl	symElem, %ecx
	imull	$2, %ecx

	#Scan over the symbol table array 4 bytes at a time
	lodsl
	cld
	repne	scasl
	jne	symbol_dne

	#Set the location of the hex code in ecx
	movl	symElem, %ebx
	imull	$2, %ebx
	subl	%ebx, %ecx
	neg	%ecx

	#Put the hex string into eax
	movl	symTable(, %ecx, 4), %eax

	jmp findSymbol_done

symbol_dne:
	#Only gets here during pass 1

findSymbol_done:
	#epilogue
	addl	$0, %esp
	popl	%ebx
	popl	%esi
	popl	%edi
	leave
	ret

#Convert the program counter to hex
#
#Returns the program counter in hex
.type pcToHex, @function
pcToHex:
	#Prologue
	pushl	%ebp
	movl	%esp, %ebp
	pushl	%edi
	pushl	%esi
	pushl	%ebx
	subl	$0, %esp

	#Call the convert function
	pushl	$tmpPcHex
	pushl	pc
	call	convertx			#Convert current pc to hex
	addl	$8, %esp

	#Remove uneeded part
	cld
	leal	tmpPcHex, %esi
	lodsl					#The upper 4 bytes are ignored
	lodsl					#Puts the needed lower 4 bytes of hex in eax

	#epilogue
	addl	$0, %esp
	popl	%ebx
	popl	%esi
	popl	%edi
	leave
	ret
