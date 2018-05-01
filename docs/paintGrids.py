from PIL import Image, ImageColor
import math


step = 0

xsteps = 0
ysteps = 0


minX = 0
minY = 0
maxX = 0
maxY = 0


def handleHeader(i):
	global minX, minY, maxX, maxY, xsteps, ysteps, step

	while "#" not in lines[i]:
		fields = lines[i].split(":")
		if "Step" == fields[0]:
			step = int(fields[1])
		elif "X" == fields[0]:
			minX, maxX = parseInts(fields[1])
		elif "Y" == fields[0]:
			minY, maxY = parseInts(fields[1])
		i+=1
	
	xsteps = (abs(minX) + abs(maxX))/step + 1
	ysteps = (abs(minY) + abs(maxY))/step + 1

	print minX, maxX
	print minY, maxY
	print xsteps, ysteps
	print step

	return i


def parseInts(f):
	f = f[1:-2] # Take away []
	ints = f.split(",")
	return (int(ints[0]), int(ints[1]))

def handleBuildings(i):
	return len(lines) # finish

def handleNav(i):
	global xsteps, ysteps

	im = Image.new('RGB', (xsteps, ysteps))
	 
	green = ImageColor.getrgb('#8fd053')
	red = ImageColor.getrgb('#e26f6f')
	grey = ImageColor.getrgb('#616161')
	white = ImageColor.getrgb('white')

	y = 0
	while "#" not in lines[i]:
		for x in range(xsteps):
			if lines[i][x] == "1":
				im.putpixel((x,y), green) # Blocked
	 		elif lines[i][x] == "2":
				im.putpixel((x,y), grey) # Blocked and not transversable
			elif lines[i][x] == "3": # Not traverseable
				im.putpixel((x,y), red)
			elif lines[i][x] == "0":
				im.putpixel((x,y), white)

		y+=1
		i+=1
		

	im.save('gridMap.png') # or any image format

	return i


def handleHeight(i):
	global xsteps, ysteps

	im = Image.new('RGB', (xsteps, ysteps)) 
	
	y=0
	while "#" not in lines[i]:
		heights = lines[i].split(" ")

		for x in range(len(heights)):
			if heights[x].strip() == "": continue
			
			if heights[x] == "x": f_height = 1000
			else: f_height = int(heights[x])

			if f_height > 1000:
				f_height = 1000
			elif f_height < 0:
				f_height = 0

			percent = int(100-math.floor(f_height/10.0))
			color = ImageColor.getrgb("hsl(1, 0%, " + str(percent) + "%)")

			im.putpixel((x,y), color)
		i+=1
		y+=1
	im.save('heightMap.png') # or any image format

	return i

if __name__ == "__main__":

	f = open("grid.data", "r")

	lines = f.readlines()

	# Split lines to navigation grid and to height grid
	i = 0
	while i < len(lines): 
		if "#header" in lines[i]:
			i = handleHeader(i+1)
		elif "#navmap" in lines[i]:
			i = handleNav(i+1)
		elif "#heightmap" in lines[i]:
			i = handleHeight(i+1)
		elif "#buildings" in lines[i]:
			i = handleBuildings(i+1)
		
		i+=1

	
	
