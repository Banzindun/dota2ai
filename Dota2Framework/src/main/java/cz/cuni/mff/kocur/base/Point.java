package cz.cuni.mff.kocur.base;

/**
 * Class that represents a Point. That is a object that has x and y coordinate.
 * @author kocur
 *
 */
public class Point {

	/**
	 * X coordinate.
	 */
	public int x;
	
	/**
	 * Y coordinate.
	 */
	public int y;
	
	/**
	 * Creates point from passed x, y. 
	 * @param y2 X coordinate of the point.
	 * @param x2 Y coordinate of the point.
	 */
	public Point(int y2, int x2) {
		x = x2;
		y = y2;
	}

	/**
	 * 
	 * @param x2 X coordinate to compare.
	 * @param y2 Y coordinate to compare.
	 * @return Returns true if x, y of this object are equal to specified x, y.
	 */
	public boolean sameCoordinates(int x2, int y2) {
		return x == x2 && y == y2;
	}
}
