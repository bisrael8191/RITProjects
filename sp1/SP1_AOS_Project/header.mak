SUPPORTDIR =	/usr/local/pub/wrc/courses/sp1/project2
CC =		gcc
AS =		gas

#
# For testing purposes, if you want to use the C library, 
# comment out the next two lines and uncomment the following
# two lines (which will use an alternate version of the
# support library that will play nicely with the C library).
#
CFLAGS =	-nodefaultlibs
LIBFLAGS =	-L$(SUPPORTDIR) -lsupport
# CFLAGS =	
# LIBFLAGS =	-L$(SUPPORTDIR) -lsupport1

CLIBFLAGS =	$(LIBFLAGS)

.s.o:
		gas --gstabs $< -o $@
