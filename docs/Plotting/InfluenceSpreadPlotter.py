# https://jakevdp.github.io/PythonDataScienceHandbook/04.12-three-dimensional-plotting.html
# https://matplotlib.org/users/customizing.html
# http://www.randalolson.com/2014/06/28/how-to-make-beautiful-data-visualizations-in-python-with-matplotlib/
# https://python-graph-gallery.com/3d/
# https://matplotlib.org/mpl_toolkits/mplot3d/tutorial.html
# https://matplotlib.org/examples/mplot3d/surface3d_demo.html

from mpl_toolkits.mplot3d import Axes3D
import matplotlib.pyplot as plt
from matplotlib import cm
from matplotlib.ticker import LinearLocator, FormatStrFormatter
import numpy as np

import csv
import sys 

def main():
	if len(sys.argv) < 2:
		print "Please pass name of the csv file you want plot as command line argument."
		sys.exit(1)

	fileName = sys.argv[1]
	print "Using file: " + fileName

	values = parse_grid(fileName)


	fig = plt.figure()
	ax = fig.gca(projection='3d')

	stepsX = len(values[0])
	stepsY = len(values)

	# Make data.
	X = np.linspace(0, 1, num=stepsX)
	Y = np.linspace(0, 1, num=stepsY)

	X, Y = np.meshgrid(X, Y)

	Z = np.array(values)

	# Plot the surface.
	surf = ax.plot_surface(X, Y, Z, rstride=1, cstride=1, cmap=cm.jet, antialiased=True)
	ax.plot_wireframe(X, Y, Z, rstride=5, cstride=5)

	# Customize the z axis.
	ax.set_zlim(minValue-0.01, maxValue+0.01)
	ax.zaxis.set_major_locator(LinearLocator(10))
	ax.zaxis.set_major_formatter(FormatStrFormatter('%.02f'))

	# Add a color bar which maps values to colors.
	fig.colorbar(surf, shrink=0.5, aspect=5)

	plt.show()

def parse_grid(fileName):
	values = []


	maxValue = 0
	minValue = 1000

	with open(fileName) as csvfile:
		data = csv.reader(csvfile, delimiter=',')
		
		for row in data:
			values.append([])
			for number in row:
				if (len(number) > 0):
					value = np.abs(float(number))
					values[-1].append(value)
					
					if value < minValue:
						minValue = value

					if value > maxValue:
						maxValue = value

	return normalize(values, minValue, maxValue)

def normalize(arr, minValue, maxValue):
	for i in range(len(arr)):
		for j in range(len(arr[0])):
			value = arr[i][j]
			arr[i][j] = (value - minValue)/(maxValue - minValue)

	return arr

if __name__ == "__main__":
	main()