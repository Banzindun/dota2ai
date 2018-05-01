package cz.cuni.mff.kocur.influence;

public class PropagationPoint {
	private int x;

	private int y; 
	
	private int distance = 0;
	
	public PropagationPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public PropagationPoint(int x, int y, int distance) {
		this.x = x;
		this.y = y;
		this.distance = distance;
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

	public int getDistance() {
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
	 * @param maxDistance Maximum distance.
	 * @return Returns distance/maxDistance, that is maximally one.
	 */
	public double getNormalizedDistance(double maxDistance) {
		if (distance > maxDistance)
			return 1;
		return (double) distance/maxDistance;
	}
	
}
