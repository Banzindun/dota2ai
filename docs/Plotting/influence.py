import matplotlib.pyplot as plt
import numpy as np
import matplotlib.ticker as ticker


def influence(origin, maxDistance, x, y):
	dist = np.abs(origin[0] - x) + np.abs(origin[1] - y)
	if dist > maxDistance: 
		return 0

	return 1-float(dist)/maxDistance


data = np.ones((10, 10))

origin1 = [3,3]

maxDistance1 = 5

origin2 = [6, 6]
maxDistance2 = 4


for x in range(data.shape[0]):
	for y in range(data.shape[1]):
		data[x,y] = influence(origin1, maxDistance1, x, y)



fig, (ax1, ax2) = plt.subplots(1, 2)
# Using matshow here just because it sets the ticks up nicely. imshow is faster.
#ax.matshow(data)

ax1.matshow(data, cmap='Greens')

ax1.tick_params(axis="y",direction="in", length=0)
ax1.tick_params(axis="x",direction="in", length=0)

ax1.xaxis.set_ticks([])
ax1.set_xticklabels([i for i in range(11)])

ax1.set_yticklabels([i for i in range(11)])
ax1.set_yticks([])

ax1.xaxis.set_major_locator(ticker.MultipleLocator(1))
ax1.yaxis.set_major_locator(ticker.MultipleLocator(1))



for (i, j), z in np.ndenumerate(data):
    ax1.text(j, i, '{:0.1f}'.format(z), ha='center', va='center')





for x in range(data.shape[0]):
	for y in range(data.shape[1]):
		data[x,y] += influence(origin2, maxDistance2, x, y)


ax2.matshow(data, cmap='Greens')

ax2.tick_params(axis="y", direction="in", length=0)
ax2.tick_params(axis="x", direction="in", length=0)

ax2.set_xticklabels([i for i in range(11)])
ax2.set_yticklabels([i for i in range(11)])

ax2.xaxis.set_major_locator(ticker.MultipleLocator(1))
ax2.yaxis.set_major_locator(ticker.MultipleLocator(1))



for (i, j), z in np.ndenumerate(data):
    ax2.text(j, i, '{:0.1f}'.format(z), ha='center', va='center')


plt.title("")
plt.show()