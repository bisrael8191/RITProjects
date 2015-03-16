'''
File: pr1_bdi8241

Main test program that should recommend different beer
styles based on a user's preferences.

Author: Brad Israel (bdi8241@cs.rit.edu)
Date: 08/01/2008
Intro to AI
Project 1

Run: python pr1_bdi8241.py
See README for more setup information
'''
#System libraries
import sys
import time

#Import pyke
from pyke import knowledge_engine

#Vars
user = 'user'		#Needed for proofs and future work

# Compile and load .krb files in '.' directory (recursively).
engine = knowledge_engine.engine('.')

#Introduction
print "###################################################################################"
print "###                     Beer Style Recommendation System                        ###"
print "###                                                                             ###"
print "###                  Author: Brad Israel (bdi8241@cs.rit.edu)                   ###"
print "###                      Introduction to A.I. - Project 1                       ###"
print "###################################################################################"

#Put in while loop, reset engine every time, ask for exit
engine.reset()

#Prompt the user for their beer preferences
print "Please follow the prompts to select a beer style"
print "Available input is inside the brackets, separated by ','"
print "Code currently doesn't error check input, so it will probably error"
print " or not return anything if you mistype something."
print ""

'''
For reference:
	ltdk - light/dark fact
	bs - bitter/sweet fact
	rh - refreshing/hoppy fact
	abv - alcohol content high/low fact
'''
ltdk = raw_input("Do you like darker or lighter beer? [dark,light]  ")
#Create a new fact based on the user's input
engine.add_case_specific_fact('beer', 'choice', (user, 'ltdk', ltdk, 'bs'))

bs = raw_input("Do you like more bitter or more sweet beer? [bitter,sweet]  ")
#Create a new fact based on the user's input
engine.add_case_specific_fact('beer', 'choice', (user, 'bs', bs, 'rh'))

rh = raw_input("Do you like beer that's refreshing (crisp) or hoppy (dry)? [refreshing,hoppy]  ")
#Create a new fact based on the user's input
engine.add_case_specific_fact('beer', 'choice', (user, 'rh', rh, 'abv'))

abv = raw_input("Do you like beer with a higher or lower alcohol content? [high,low]  ")
#Create a new fact based on the user's input
engine.add_case_specific_fact('beer', 'choice', (user, 'abv', abv, 'recommendation'))

#Activate the knowledge engine, which applies the rules
# to the newly entered facts to create more rules.
#Name is based on the file name (beerrules.krb).
ruleTime = time.time()
engine.activate('beerrules')
ruleTime = time.time() - ruleTime

#Just here for testing (will dump out all facts)
#engine.get_kb('beer').dump_specific_facts()

#Do the proof and get the results in list format
proof = []
invalidProof = False
proofTime = time.time()
for (val1, val2, opt1, opt2), plan \
in engine.prove_n('beer', 'proof', (user,), 4):
	proof.append([val1, val2, opt1, opt2])
	if val2 == "":
		invalidProof = True	#Mistyped/blank entry

proofTime = time.time() - proofTime

if len(proof) < 4:
	invalidProof = True	#Couldn't find a beer style

if invalidProof == False:
	#Print proof in english
	print
	print "You have decided on a %ser beer style that is both %s and %s with a %ser alcoholic content." % \
		(proof[0][0], proof[1][0], proof[2][0], proof[3][0])
	print
	print "I would look for a good %s" % (proof[3][1].upper()),
	
	if len(proof) > 4:
		for x in range (4, len(proof)):
			print "or %s" % (proof[x][1].upper()),
else:
	print
	print "Sorry, your choices didn't return a beer style ... if all else fails try a good LAGER!"

print
print

fullProof = raw_input("Do you want to see the full proof output and statistics? [y,n]  ")
if fullProof == "y":
	print "Proof:"
	for subProof in proof:
		print "%s -> %s for options [%s] and [%s]" % (subProof[0], subProof[1], subProof[2], subProof[3])
	print "\n\nStatistics:"
	print "Time to generate rules: %.5f secs, %.0f asserts/sec" % \
          (ruleTime, engine.get_kb('beer').get_stats()[2] / ruleTime)
	print "Time for proof: %.5f secs" % (proofTime)
	engine.print_stats()
