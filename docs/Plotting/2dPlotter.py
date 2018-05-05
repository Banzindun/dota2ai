import matplotlib.pyplot as plt
import matplotlib.ticker as ticker
import numpy as np
import math




@np.vectorize
def clamp(x):
	if x < 0:
		x = 2
	
	if x > 1:
		x = 2

	return x


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
		z = x / self.k - self.c
		z+=0.0001
		power = np.power(100, self.m)
		divided = z/(1-z)
		return 0.5 * np.log(divided)/np.log(power) + self.b + 0.5

	def toString(self):
		return "Logit [{}, {}, {}, {}]".format(self.m, self.k, self.c, self.b)


class Logistic(Function):
	def __init__(self, m, k, c, b):
		Function.__init__(self, m,k,c,b)

	def compute(self, x):
		z = 10*self.m*(x-self.c-0.5)
		return self.k * (1.0/( 1 + np.exp(np.negative(z)))) + self.b
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
	if arg == "plot":
		plot_four_functions()
		return 

	args = arg.split(" ")

	if len(args) < 5:
		return -1
	

	function_type = args[0]
	m = int(args[1])
	k = int(args[2])
	c = int(args[3])
	b = int(args[4])

	plot(getFunction(function_type, m, k, c, b))


def plot_four_functions():
	fcs = {"poly" :  [4.5, 3, 0.4, 0.4],
		"sinusoid" : [0.2, 10, 0.7, 0.5],
		"logistic" : [1, 0.9, 0, 0.05],
		"logit" : [1.1, 0.66, 0.5, -0.1]}

	x = np.arange(0.0, 1.0, 0.001)
	#plt.style.use('fivethirtyeight')
	

	plt.figure(1, figsize=(16, 9), facecolor='w', edgecolor='k')
	
	i = 1
	for key, value in fcs.iteritems(): 
		plt.subplot(2,2,i)
		plt.grid(True)

		axes = plt.gca()
		axes.set_xlim([-0.01, 1.01])
		axes.set_ylim([-0.01, 1.01])

		axes.set_xticks(np.arange(0, 1, 0.1))
		axes.set_yticks(np.arange(0, 1., 0.1))

		function = getFunction(key, value[0], value[1], value[2], value[3])

		plt.title(function.toString())
		plt.plot(x, function.compute(x), linewidth=2, label="") # Plot the average

		i+=1

	#plt.show()
	plt.savefig("plotted_functions.png", bbox_inches='tight', dpi=400)
	plt.savefig("utilities.pdf", bbox_inches='tight')


if __name__ == "__main__":
	print "Plotting the function"
	main()