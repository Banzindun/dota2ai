#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
Entry point of tests, that are based on python.
These should test the server functionalitty and correctness of responses.

"""

import sys
import testServer
import testClient

if __name__ == "__main__":
	# Run main function
	main()


OPTION = "SERVER"


def main():
	if (len(sys.argv) > 1):
		OPTION = sys.argv[1].upper()
		if not (OPTION == "SERVER" || OPTION == "CLIENT"):
			print "Unknown supplied argument: " + OPTION
			help()
			return 
	else: 
		help()
		return

	if (OPTION == "CLIENT"):
		pass
	else if (OPTION == "SERVER"):
		pass


def help():
	print """
		Run as: python run.py OPTION
		
		where OPTION is: 
			SERVER - to test the server side
			CLIENT - to test the game side
		"""





