# https://jakevdp.github.io/PythonDataScienceHandbook/04.12-three-dimensional-plotting.html
# https://matplotlib.org/users/customizing.html
# http://www.randalolson.com/2014/06/28/how-to-make-beautiful-data-visualizations-in-python-with-matplotlib/
# https://python-graph-gallery.com/3d/
# https://matplotlib.org/mpl_toolkits/mplot3d/tutorial.html
# https://matplotlib.org/examples/mplot3d/surface3d_demo.html

from mpl_toolkits.mplot3d import axes3d
import matplotlib.pyplot as plt
from matplotlib import cm

from matplotlib.ticker import LinearLocator, FormatStrFormatter
import numpy as np

import csv as ccsv
import sys 

def main():
	if len(sys.argv) < 2:
		print "Please pass name of the csv file you want plot as command line argument."
		sys.exit(1)

	fileName = sys.argv[1]
	
	#values = parse_grid(fileName)
	#Z = np.array(values)
	Z = np.loadtxt(fileName)

	#stepsX = len(values[0])
	#stepsY = len(values)
	stepsX, stepsY = Z.shape;
	# Make data.
	X = np.arange(-10, 10, 20.0/stepsX)
	Y = np.arange(-10, 10, 20.0/stepsY)
	


	X, Y = np.meshgrid(X, Y)
	Z = Z.T
	print X.shape 
	print Y.shape 
	print Z.shape

	colors = cm.viridis(Z)
	rcount, ccount, _ = colors.shape

	fig = plt.figure()
	ax = fig.gca(projection='3d')
	#ax.plot_wireframe(X, Y, Z, rstride=2, cstride=2)

	#surf = ax.plot_surface(X, Y, Z, rcount=rcount, ccount=ccount,
	#                       facecolors=colors, shade=False)
	#surf.set_facecolor((0,0,0,0))

	#ax.plot_surface(X, Y, Z, rstride=1, cstride=1, cmap=cm.jet)
	#ax.plot_wireframe(X, Y, Z, rstride=5, cstride=5)

	surf = ax.plot_surface(X, Y, Z, cmap=cm.coolwarm,
                       linewidth=0, antialiased=False)

	# Add a color bar which maps values to colors.
	fig.colorbar(surf, shrink=0.5, aspect=5)

	# Customize the z axis.
	#ax.set_zlim(-0.01, 1.01)
	#ax.zaxis.set_major_locator(LinearLocator(10))
	#ax.zaxis.set_major_formatter(FormatStrFormatter('%.02f'))
	plt.savefig("spread.pdf", bbox_inches='tight')
	plt.show()

if __name__ == "__main__":
	main()