##Quick Guide for installing and setting up my project

###Dependencies:
Any OS that runs the latest version of Python, I use Linux, but it shouldn't
matter.

- Python	
  - I used 2.5.2-5 Linux 64bit, but all 2.4 and 2.5 versions shoud work
  - Downloads and Installation instructions on: http://python.org/

- Ply
  - 2.5-1 Cross-platform, Required by Pyke and is a tool for lex yacc parsing
  - Downloads and Installation instructions on: http://www.dabeaz.com/ply/

- Pyke
  - 0.3-1 Cross-platform, Main libraries for my project.
  - Downloads and Installation instructions on: http://pyke.sourceforge.net/installing_pyke.html
  - I recommend downloading the examples from the same site and making sure that
  the family_relations code works. Type 'python' in that examples folder, then in the
  interpreter type the following lines:
```
import test
test.fc_test()
```
  
  - You should see something that looks like a family tree with no errors.

###Files:
- pr1_bdi8241.py – Main application and control code
- beerrules.krb – Rules file in KRB syntax
- README – User manual to help install and run my project
- IO_Examples – Some examples of user input and the corresponding output


###Running my code:
- Extract the pr1_bdi8241.zip file (Since you're reading this I'll assume that worked)
- There are only four files (pr1_bdi8241.py, beerrules.krb, README, IO_Examples)
- In a terminal/console change to where ever the extracted files are.
- Run 'python pr1_bdi8241.py'
- This will compile the python code and krb file and run the application
- Just follow the prompts to test it out. Enjoy!

If you have any further questions, you can contact me by email either through myCourses or
at bdi8241@cs.rit.edu. I would also like to hear any suggestions or new features that you
have.
