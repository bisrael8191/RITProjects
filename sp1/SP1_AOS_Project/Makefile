#
# Created by makemake (i386 Aug  4 2005) on Sun Oct 14 09:42:38 2007
#

#
# Definitions
#

.SUFFIXES:
.SUFFIXES:	$(SUFFIXES:.cc=.C .cpp) .s

.C:
		$(LINK.cc) -o $@ $< $(LDLIBS)
.cpp:
		$(LINK.cc) -o $@ $< $(LDLIBS)
.C.o:
		$(COMPILE.cc) $(OUTPUT_OPTION) $<
.cpp.o:
		$(COMPILE.cc) $(OUTPUT_OPTION) $<
.C.a:
		$(COMPILE.cc) -o $% $<
		$(AR) $(ARFLAGS) $@ $%
		$(RM) $%
.cpp.a:
		$(COMPILE.cc) -o $% $<
		$(AR) $(ARFLAGS) $@ $%
		$(RM) $%

CC =		cc

########## Flags from header.mak
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
########## End of flags from header.mak


CPP_FILES =	
C_FILES =	
ASSY_FILES =	project2.s
H_FILES =	
SOURCEFILES =	$(H_FILES) $(CPP_FILES) $(C_FILES) $(ASSY_FILES)
.precious:	$(SOURCEFILES)
OBJFILES =	

#
# Main targets
#

all:	 project2

project2:	project2.o $(OBJFILES)
	$(CC) $(CFLAGS) -o project2 project2.o $(OBJFILES) $(LIBFLAGS)

#
# Dependencies
#


#
# Housekeeping
#

Archive:	archive.tgz

archive.tgz:	$(SOURCEFILES) Makefile
	tar cf - $(SOURCEFILES) Makefile | gzip > archive.tgz

clean:
	-/bin/rm -r $(OBJFILES) ptrepository SunWS_cache .sb ii_files core 2> /dev/null

realclean:	clean
	/bin/rm -rf  
