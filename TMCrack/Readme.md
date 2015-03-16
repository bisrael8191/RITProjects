#**Security Measurement and Testing**
##Rainbow Table Java Framework
The goal for this project was to research time-memory trade-off techniques, as they relate to cracking Windows LanMan 
hashes, and then implement a framework that is more useful for academic purposes. The framework is written and 
documented in Java so that the code is easier to understand and follow and so that it is easy to modify or replace 
elements of the algorithm, such as the reduction function. The framework will also allow the user to generate 
statistics and benchmarks before or/and after generating and searching the tables, which should allow the user to 
compare their modifications in terms of accuracy and performance for Windows password cracking. This should make 
rainbow tables easier to understand and "play" with for a wider audience. 

Final Report: [PDF](Rainbow_Tables_Project_Report.pdf)

Java Code [ver. 1.00]: [JAR](TMCrack_1_00.jar)

###Works Cited
Philippe Oechslin: Making a Faster Cryptanalytic Time-Memory Trade-Off. CRYPTO 2003: 617-630.[http://lasecwww.epfl.ch/pub/lasec/doc/Oech03.pdf](http://lasecwww.epfl.ch/pub/lasec/doc/Oech03.pdf)

M. E. Hellman. A cryptanalytic time-memory trade-off. IEEE Transactions on Information Theory, IT-26:401-406, 1980.[http://www-ee.stanford.edu/~hellman/publications/36.pdf](http://www-ee.stanford.edu/~hellman/publications/36.pdf)

###Useful Websites
* How Rainbow Tables Work - [http://kestas.kuliukas.com/RainbowTables](http://kestas.kuliukas.com/RainbowTables/)
* Ophcrack - [http://lasecwww.epfl.ch/~oechslin/projects/ophcrack/](http://lasecwww.epfl.ch/~oechslin/projects/ophcrack/)
* Parameter Optimization in RainbowCrack - [http://www.antsight.com/zsl/rainbowcrack/optimization/optimization.htm](http://www.antsight.com/zsl/rainbowcrack/optimization/optimization.htm)
* Plain-text Basics - [http://www.plain-text.info/Rainbowtables_Basics/](http://www.plain-text.info/Rainbowtables_Basics/)
* RainbowCrack - [http://www.antsight.com/zsl/rainbowcrack/](http://www.antsight.com/zsl/rainbowcrack/)
* Rainbow Tables Explained - [https://www.isc2.org/cgi-bin/content.cgi?page=738](https://www.isc2.org/cgi-bin/content.cgi?page=738)
* Wikipedia - Rainbow Table - [http://en.wikipedia.org/wiki/Rainbow_table](http://en.wikipedia.org/wiki/Rainbow_table)