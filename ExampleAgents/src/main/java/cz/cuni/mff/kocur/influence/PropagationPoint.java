package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.world.GridBase;

/**
 * Class that defines a point, where we spread during the wave propagation.
 * 
 * @author kocur
 *
 */
public class PropagationPoint {
	private int x;
	private int y;

	/**
	 * Distance from origin.
	 */
	private double distance = 0;

	public PropagationPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public PropagationPoint(int x, int y, double distance) {
		this.x = x;
		this.y = y;
		this.distance = distance;
	}

	/**
	 * Creates a new PropagationPoint. Sets coordinates and distance using the other
	 * point. Distance is set as distance stored inside the point and distance
	 * between the point and _x, _y is added.
	 * 
	 * @param _x
	 *            X coordinate.
	 * @param _y
	 *            Y coordinate.
	 * @param point
	 *            Point with coordinates and distance, from which we create this
	 *            point.
	 */
	PropagationPoint(int _x, int _y, PropagationPoint point) {
		this.x = _x;
		this.y = _y;
		
		this.distance = point.getDistance() + distance(point);
	}

	/**
	 * Measures distance from this point to the other.
	 * @param other Other point.
	 * @return Returns the distance between the two points.
	 */
	private double distance(PropagationPoint other) {
		return GridBase.distance(x, y, other.getX(), other.getY());
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public void add(PropagationPoint p) {
		this.x = p.getX();
		this.y = p.getY();
	}

	/**
	 * @param maxDistance
	 *            Maximum distance.
	 * @return Returns distance/maxDistance, that is maximally one.
	 */
	public double getNormalizedDistance(double maxDistance) {
		if (distance > maxDistance)
			return 1;
		return (double) distance / maxDistance;
	}

}
