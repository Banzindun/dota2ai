import matplotlib.pyplot as plt
import matplotlib.ticker as ticker
import numpy as np
import math


class Function:
	def __init__(self, m, k, c, b):
		self.m = m
		self.k = k
		self.c = c 
		self.b = b

	def compute(self, x):
		return x

	def toString():
		return ""

class Poly(Function):
	def __init__(self, m, k, c, b):
		Function.__init__(self, m,k,c,b)

	def compute(self, x):
		return self.m * np.power(x-self.c, self.k) + self.b

	def toString(self):
		return "Poly [{}, {}, {}, {}]".format(self.m, self.k, self.c, self.b)

class Logit(Function):
	def __init__(self, m, k, c, b):
		Function.__init__(self, m,k,c,b)

	def compute(self, x):
		z = self.x / self.k - self.c
		return 0.5 * np.log(z/(1-z), np.power(100, self.m)) + self.b + 0.5

	def toString(self):
		return "Logit [{}, {}, {}, {}]".format(self.m, self.k, self.c, self.b)


class Logistic(Function):
	def __init__(self, m, k, c, b):
		Function.__init__(self, m,k,c,b)

	def compute(self, x):
		z = 10*self.m*(x-self.c-0.5)
		return self.k * (1/( 1 + np.exp(-z))) + self.b

	def toString(self):
		return "Logistic [{}, {}, {}, {}]".format(self.m, self.k, self.c, self.b)

class Sinusoid(Function):
	def __init__(self, m, k, c, b):
		Function.__init__(self, m,k,c,b)

	def compute(self, x):
		return self.m * np.sin(self.k * x + self.c) + self.b

	def toString(self):
		return "Sinusoid [{}, {}, {}, {}]".format(self.m, self.k, self.c, self.b)

def getFunction(functionType, m, k, c, b):
	if functionType == "poly":
		return Poly(m, k, c, b)
	elif functionType == "logit":
		return Logit(m, k, c, b)
	elif functionType == "sinusoid":
		return Sinusoid(m,k,c,b)
	else:
		return Logistic(m, k, c, b) 


def plot(function):
	x = np.arange(0.0, 1.0, 0.001)

	plt.style.use('fivethirtyeight')
	plt.figure(1, figsize=(16, 9), facecolor='w', edgecolor='k')
	plt.title(function.toString())

	plt.rcParams['agg.path.chunksize'] = 10000
	plt.plot(x, function.compute(x), linewidth=2, label="average") # Plot the average

	axes = plt.gca()

	leg=plt.legend(loc='best', numpoints=6, fancybox=True)

	plt.savefig("graph.png", bbox_inches='tight', dpi=400)

def main():
	arg = raw_input("Please type in: function_type m k c b\n")
	args = arg.split(" ")

	if len(args) < 5:
		return -1
	

	function_type = args[0]
	m = int(args[1])
	k = int(args[2])
	c = int(args[3])
	b = int(args[4])

	print m,k,c,b

	plot(getFunction(function_type, m, k, c, b))


if __name__ == "__main__":
	print "Plotting the function"
	main()