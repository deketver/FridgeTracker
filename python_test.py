import os
import sys

workingdirectory=os.getcwd()

print("Current working directory" + workingdirectory)

number_arguments = len(sys.argv)

print("Number of arguments:", len(sys.argv), ' arguments.')

for item in range(number_arguments):
	print("Arguemnt: ", sys.argv[item])

