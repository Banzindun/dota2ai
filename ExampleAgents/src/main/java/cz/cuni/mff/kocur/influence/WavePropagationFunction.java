package cz.cuni.mff.kocur.influence;

import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.world.GridBase;

/**
 * This is a representation of "bfs" like propagation function. It should spread
 * to some distance regarding with regards to obstacles.
 * 
 * @author kocur
 *
 * @param <T>
 *            Type of object that will be passed during propagation. (an object
 *            that we are doing the propagation for)
 */
public class WavePropagationFunction<T extends Location> implements PropagationFunction<T> {
	private static final Logger logger = LogManager.getLogger(WavePropagationFunction.class);

	/**
	 * Maximum distance I can Travel.
	 */
	protected double maxDistance = 0;

	/**
	 * Current distance.
	 */
	protected int distance = 0;

	/**
	 * Sign of the influence we are spreading.
	 */
	protected int sign = 1;

	/**
	 * Maximum influence.
	 */
	protected double maxInfluence = 1.0;

	/**
	 * Power that we will use to generate the influence.
	 */
	protected double power = 1.0;

	/**
	 * Minimum influence, that the propagation will generate.
	 */
	protected double minInfluence = 0;

	/**
	 * Array of already visited nodes.
	 */
	protected boolean[][] visited = null;

	/**
	 * X coordinate of our origin.
	 */
	protected int originX = 0;

	/**
	 * Y coordinate of our origin.
	 */
	protected int originY = 0;

	/**
	 * Queue of points, that we use for propagation.
	 */
	protected LinkedList<PropagationPoint> queue = new LinkedList<>();

	/**
	 * Neighbors, that the point can have. We will use them to spread to other
	 * tiles.
	 */
	protected static final LinkedList<PropagationPoint> neighbours = new LinkedList<>();

	static {
		neighbours.add(new PropagationPoint(1, 0));
		neighbours.add(new PropagationPoint(-1, 0));
		neighbours.add(new PropagationPoint(0, -1));
		neighbours.add(new PropagationPoint(0, 1));
		neighbours.add(new PropagationPoint(-1, -1));
		neighbours.add(new PropagationPoint(1, 1));
		neighbours.add(new PropagationPoint(-1, 1));
		neighbours.add(new PropagationPoint(1, -1));
	}

	/**
	 * Constructor, that initializes the WavePropagationFunction.
	 */
	public WavePropagationFunction() {

	}

	/**
	 * This propagates a passed influence layer. In WPF we spread in a wave-like
	 * manner. We decrease distance with each step and we spread the influence
	 * accordingly.
	 */
	@Override
	public void propagate(InfluenceLayer l, T e) {
		distance = 0;

		// Translate entity coordinates to layer coordinates.
		double[] xyz = l.getEntityCoordinates(e);

		originX = (int) xyz[0];
		originY = (int) xyz[1];

		if (!l.areInside(originX, originY)) {
			logger.warn("Coordinates " + e.getX() + ", " + e.getY() + " (" + originX + ", " + originY
					+ ") are not inside.");
			return;
		}

		// Create new visited array, will be false on init.
		visited = new boolean[l.getHeight()][l.getWidth()];

		// Create the queue.
		queue.clear();

		// Insert the first node.
		PropagationPoint startingPoint = new PropagationPoint(originX, originY);
		queue.add(startingPoint);
		visited[originY][originX] = true;

		// Spread the influence. Stop on empty queue (that means we have spread to the
		// distance we have needed).
		while (queue.size() > 0) {
			distance++;
			spread(l);
		}
	}

	/**
	 * Spreads the influence from given point to its surroundings.
	 * 
	 * @param l Influence layer.
	 * @param point Propagation point.
	 */
	protected void spreadInfluence(InfluenceLayer l, PropagationPoint point) {
		calculateInfluence(l, point);

		// Go through neighbors and if not visited and entity
		// can walk through them, then add them to queue
		for (PropagationPoint p : neighbours) {
			int _x = point.getX() + p.getX();
			int _y = point.getY() + p.getY();

			if (_x < 0 || _x >= l.getWidth())
				continue;
			if (_y < 0 || _y >= l.getHeight())
				continue;

			// If the tile is not passable set visited to true.
			if (!passable(l, _x, _y)) {
				visited[_y][_x] = true;
			}
			
			// If not visited create new PP and add it to queue
			if (!visited[_y][_x]) {
				// Create new point with incremented distance 
				PropagationPoint newPoint = new PropagationPoint(_x, _y, GridBase.distance(_x,  _y, originX, originY));
				if (newPoint.getDistance() < maxDistance) {
					queue.add(newPoint);
					visited[_y][_x] = true;
				}
			}
		}
	}

	/**
	 * Calculates influence on the passed point and adds it to the influence layer.
	 * 
	 * @param l
	 *            Influence layer.
	 * @param point
	 *            Point of the propagation.
	 */
	protected void calculateInfluence(InfluenceLayer l, PropagationPoint point) {
		double ratio = point.getNormalizedDistance(maxDistance);
		double influence = maxInfluence * (1 - ratio);

		// Add influence
		l.addInfluence(point.getX(), point.getY(), sign * minInfluence + sign * Math.pow(influence, power));
	}

	/**
	 * 
	 * @param l
	 *            Influence layer.
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @return Returns true if middle of tile at position x and y is passable in grid
	 *         coordinates.
	 */
	protected boolean passable(InfluenceLayer l, double x, double y) {
		int _x = (int) (l.resolveXBack(x)+l.getResolution()/2);
		int _y = (int) (l.resolveYBack(y)+l.getResolution()/2);

		if (!l.getParent().areInside(_x, _y)) {
			return false;
		}

		return GridBase.getInstance().passable(_x, _y);
	}

	/**
	 * Spreads the influence on this layer.
	 * 
	 * @param l
	 *            Influence layer.
	 */
	protected void spread(InfluenceLayer l) {
		LinkedList<PropagationPoint> newQueue = queue;
		queue = new LinkedList<PropagationPoint>();

		for (PropagationPoint p : newQueue) {
			spreadInfluence(l, p);
		}
	}

	public String toString() {
		IndentationStringBuilder builder = new IndentationStringBuilder();
		builder.appendLines("maxDistance " + maxDistance, "distance " + distance, "sign " + sign,
				"maxInfluence " + maxInfluence, "originX " + originX, "originY " + originY);

		return builder.toString();
	}

	public double getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(int distance) {
		this.maxDistance = distance;
	}

	public double getMaxInfluence() {
		return maxInfluence;
	}

	public void setMaxInfluence(double currentInfluence) {
		this.maxInfluence = currentInfluence;
	}

	public int getSign() {
		return sign;
	}

	public void setSign(int sign) {
		this.sign = sign;
	}

	public double getMinInfluence() {
		return minInfluence;
	}

	public void setMinInfluence(double minInfluence) {
		this.minInfluence = minInfluence;
	}

}
