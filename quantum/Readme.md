#**Extreme Theory**
##Quantum Computing Topics
Computing has seen many rapid advances in both hardware and software. Quantum computing is the next evolutionary step 
in the progression of computer technology. It is the implementation of quantum bits, or qubits, to solve computational 
problems in ways that are impossible with traditional computing methods. Quantum computing offers us many significant 
benefits, albeit with a plethora of challenges. The nature of quantum computing theoretically lends itself well to 
certain computational problems that are not easily solved using classical approaches. With quantum computing, it is 
theoretically possible to solve many problems, such as factorization, in polynomial time as opposed to exponential. 
Other unique advantages include entanglement and parallelism. Challenges that we must overcome include interference, 
reversibility, and measurement of states. These advantages and challenges are largely derived from the unique ability 
of a qubit to have a state of 0 and 1 simultaneously. This presentation will cover the history of quantum computing, 
the challenges it faces, the applications in which it may be applied, the unique implementation of programming in a 
quantum environment as well as the future of quantum computing.

### Grover's Algorithm
Grover's Algorithm was developed in 1996
by Lou Grover of Bell Labs to improve the search time of an unsorted database. His algorithm called
for an oracle or blackbox that would basically be able to store and retrieve bits and be able to achieve
a superposition. This oracle described a quantum computer. Using a quantum computer and Grover's 
algorithm, the averge search time of database with N entries was determined to be O(n^1/2). This means
that if the database had 100,000 unsorted entries, a specific entry could be found in 100 steps. On a classical
computer, this feat would take an average of 50,000 steps. Grover's algorithm has also been modified to solve
other problems like finding a key to decrypt an encrypted message, mean and median estimation, the collision
problem for many-to-one functions, and helping to solve NP-Complete problems. See the Resources page
to download many papers about Grover's Algorithm and many links to explain it in detail<br>

Download my slides: [PDF](grover_slides.pdf)

* [Quantum Computing and Grover's Algorithm](http://alumni.imsa.edu/~matth/quant/473/473proj/473proj.html)
* [Quantum-Quick Queries](http://www.sciencenews.org/pages/sn_arch/8_31_96/bob2.htm)
* [Grover's search algorithm](http://www.qtc.ecs.soton.ac.uk/lecture2/p11.html)
* [Grover's Algorithm](http://en.wikipedia.org/wiki/Grover's_algorithm)
* [Original Grover Paper](original_grover.pdf)
* [Fast quantum algorithm for estimating the median](estimate_median.pdf)
* [Quantum computers can search arbitrarily large databases by a single query](search_by_single_query.pdf)

### QCL
QCL is a high level language developed for the express purpose of having a language to develop programs that simulate 
quantum algorithms, while still being easily adapted for implementation on a real quantum computer. It is currently 
capable of many of the things you would expect from a classical language, as well being able to implement and emulate 
(albeit slowly) quantum algorithms. This is an important step towards quantum computing, as QCL gives us a platform 
from which we can test algorithms, and allows us to try out new algorithms while quantum computers themselves are not 
a reality. QCL is freely available for download, as is the source. The links can be found on the resources page.

* [QCL - Quantum Computation Language](http://tph.tuwien.ac.at/~oemer/qcl.html)
* [QCL for CS majors](http://tph.tuwien.ac.at/~oemer/doc/quprog/index.html)
* [QCL - Paper](http://tph.tuwien.ac.at/~oemer/doc/qcldoc/index.html)
