package cz.cuni.mff.kocur.world;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.base.Location;

/**
 * Abstract class that represents a GridSystem. GridSystem is a coordinate
 * system, that has a origin, resolution and a parent. This class is used to
 * translate coordinates from one base to another.
 * 
 * @author kocur
 *
 */
public abstract class GridSystem {

	/**
	 * Static logger for GridSystem class.
	 */
	private static final Logger logger = LogManager.getLogger(GridSystem.class.getName());

	/**
	 * Parent grid system.
	 */
	protected GridSystem parent = null;

	/**
	 * How many units one tile takes.
	 */
	protected double resolution = 1;

	/**
	 * First x coordinate.
	 */
	protected double xOrigin = 0;

	/**
	 * First y coordinate.
	 */
	protected double yOrigin = 0;

	/**
	 * How big is the grid in x direction. (maxX-minX)/resolution
	 */
	protected int width = 0;

	/**
	 * How big is the grid in y direction. (maxY-minY)/resolution
	 */
	protected int height = 0;

	public GridSystem() {

	}

	public GridSystem(GridSystem s) {
		this.parent = s.getParent();
		this.xOrigin = s.getXOrigin();
		this.yOrigin = s.getYOrigin();

		this.width = s.getWidth();
		this.height = s.getHeight();
		this.resolution = s.getResolution();
	}

	/**
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @return Returns array of x and y tile coordinates.
	 */
	public static Integer[] toTile(double x, double y) {
		return new Integer[] { (int) x, (int) y };
	}

	public static Integer[] toTile(double[] xy) {
		return toTile(xy[0], xy[1]);
	}

	/**
	 * Recursively divides x by resolution until it reaches the base. (that will be
	 * grid in usual scenario)
	 * 
	 * @param d
	 *            Value to recursively resolve.
	 * @return Returns value resolved to base grid system.
	 */
	public double reverseResolution(double d) {
		if (parent != null)
			return parent.reverseResolution(d / resolution);

		return d / resolution;
	}

	/**
	 * Recursively multiplies x by resolution until it reaches the base. (that will
	 * be grid in usual scenario)
	 * 
	 * For example attack and vision ranges can be resolved using this.
	 * 
	 * @param d
	 *            Value to recursively resolve.
	 * @return Returns value resolved to base grid system.
	 */
	public double toBaseResolution(double d) {
		if (parent != null)
			return parent.reverseResolution(d * resolution);

		return d * resolution;
	}

	/**
	 * Transforms coordinates x, y to base grid system (going through the parents).
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @return Returns passed x, y transformed to base coordinates.
	 */
	public double[] toBase(double x, double y) {
		// Translates x y coordinates up the GridSystem structure.
		if (parent != null)
			return parent.toBase(resolveXBack(x), resolveYBack(y));

		return new double[] { resolveXBack(x), resolveYBack(y) };
	}

	public double[] toBaseFromParent(double x, double y) {
		// Translates x y coordinates up the GridSystem structure.
		if (parent != null)
			return parent.toBase(x, y);

		return new double[] { resolveXBack(x), resolveYBack(y) };
	}

	public double[] toBaseFromParent(double[] origin) {
		return toBaseFromParent(origin[0], origin[1]);
	}

	/**
	 * Resolves x, y coordinates from the base to this grid system. Can be used to
	 * calculate grid coordinates from the in-game one.
	 * 
	 * @param x
	 *            X coordinate.
	 * @param y
	 *            Y coordinate.
	 * @return Returns grid coordinates, that correspond with x, y from the game and
	 *         resolutions.
	 */
	public double[] fromBase(double x, double y) {
		if (parent != null)
			return resolveXY(parent.fromBase(x, y));

		return resolveXY(x, y);
	}

	/**
	 * Resolves x, y coordinates from base to this coordinates without applying this
	 * layer's resolution.
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @return Returns array of ints that correspond to paren't resolution from his
	 *         base.
	 */
	public double[] fromBaseNoResolution(double x, double y) {
		if (parent != null)
			return parent.fromBase(x, y);
		return null;
	}

	/**
	 * Resolves x. Transforms it to this base.
	 * 
	 * @param x
	 *            X coordinate you want to resolve. (Should be coordinate in parent
	 *            system.
	 * @return Returns x coordinate with respect to this base.
	 */
	public double resolveX(double x) {
		return (x - xOrigin) / resolution;
	}

	/**
	 * Resolves y. Transforms it to this base.
	 * 
	 * @param y
	 *            y
	 * @return Returns resolved y.
	 */
	public double resolveY(double y) {
		return (y - yOrigin) / resolution;
	}

	/**
	 * Resolves x, y. Transforms it to this base.
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @return Returns resolved x.
	 */
	public double[] resolveXY(double x, double y) {
		return new double[] { resolveX(x), resolveY(y) };
	}

	/**
	 * 
	 * @param xy
	 *            Array containing x and y. [x, y]
	 * @return Returns the resolved array.
	 */
	public double[] resolveXY(int[] xy) {
		return resolveXY(xy[0], xy[1]);
	}

	/**
	 * 
	 * @param xy
	 *            Array containing x and y. [x, y]
	 * @return Returns the resolved array.
	 */
	public double[] resolveXY(double[] xy) {
		return resolveXY(xy[0], xy[1]);
	}

	/**
	 * Calculates the midpoint of the passed tile coordinates.
	 * 
	 * @param x
	 *            X
	 * @param y
	 *            Y
	 * @return Returns array containing x and y of the midpoint of the tile.
	 */
	public double[] centerOfTile(double x, double y) {
		return new double[] { x + resolution / 2.0, y + resolution / 2.0 };
	}

	/**
	 * Gets entity coordinates. It applies transformations and resolutions from the
	 * parents down to this class. If you use it in system, that has super parent
	 * set to GridBase, you can get coordinates from the game to your system.
	 * 
	 * @param e
	 *            Location.
	 * @return Returns this system's coordiantes, that correspond to the passed
	 *         entity.
	 */
	public double[] getEntityCoordinates(Location e) {
		if (parent != null) {
			double[] xyz = parent.getEntityCoordinates(e);

			xyz[0] = resolveX(xyz[0]);
			xyz[1] = resolveY(xyz[1]);
			xyz[2] = xyz[2];
			return xyz;
		}

		return new double[] { resolveX((int) e.getX()), resolveY((int) e.getY()), (int) e.getZ() };
	}

	/**
	 * Resolves the passed x back to the parents system.
	 * 
	 * @param x
	 *            x
	 * @return Returns x in parents system coordinates.
	 */
	public double resolveXBack(double x) {
		return x * resolution + xOrigin;
	}

	/**
	 * Resolves the passed y back to the parents system.
	 * 
	 * @param y
	 *            y
	 * @return Returns y in parents system coordinates.
	 */
	public double resolveYBack(double y) {
		return y * resolution + yOrigin;
	}

	/**
	 * Resolves the passed x and y back to the parents system.
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @return Returns x [0] and y [1] in parents system coordinates.
	 */
	public double[] resolveXYBack(double x, double y) {
		return new double[] { resolveXBack(x), resolveYBack(y) };
	}

	/**
	 * Resolves the passed x and y back to the parents system.
	 * 
	 * @param origin
	 *            Array of coordinates.
	 * @return Returns array of x and y in parents system coordinates.
	 */
	public double[] resolveXYBack(double[] origin) {
		return resolveXYBack(origin[0], origin[1]);
	}

	/**
	 * Sets the size of this grid system.
	 * 
	 * @param width
	 *            Width.
	 * @param height
	 *            Height.
	 * @return Returns true, if set correctly.
	 */
	public boolean setSize(int width, int height) {
		if (resolution <= 0) {
			logger.error("Grid resolution is less than or equal to 0.");
			return false;
		}
		this.height = height;
		this.width = width;

		return true;
	}

	/**
	 * Sets a new origin.
	 * 
	 * @param x
	 *            X coordinate of the origin.
	 * @param y
	 *            Y coordinate of the origin.
	 */
	public void setOrigin(double x, double y) {
		this.xOrigin = x;
		this.yOrigin = y;
	}

	/**
	 * Sets x origin.
	 * 
	 * @param x
	 *            x
	 */
	public void setXOrigin(double x) {
		this.xOrigin = x;
	}

	/**
	 * Sets y origin.
	 * 
	 * @param y
	 *            y
	 */
	public void setYOrigin(double y) {
		this.yOrigin = y;
	}

	/**
	 * 
	 * @return Returns array containing x and y origins.
	 */
	public double[] getOrigin() {
		return new double[] { xOrigin, yOrigin };
	}

	/**
	 * 
	 * @return Returns array containing x origin.
	 */
	public double getXOrigin() {
		return xOrigin;
	}

	/**
	 * 
	 * @return Returns array containing y origin.
	 */
	public double getYOrigin() {
		return yOrigin;
	}

	/**
	 * Sets a new resolution.
	 * 
	 * @param res
	 *            New resolution.
	 */
	public void setResolution(double res) {
		resolution = res;
	}

	/**
	 * Sets the parent of this grid system.
	 * 
	 * @param p
	 *            Parent of this system.
	 */
	public void setParent(GridSystem p) {
		parent = p;
	}

	/**
	 * 
	 * @return Returns parent grid system.
	 */
	public GridSystem getParent() {
		return parent;
	}

	/**
	 * 
	 * @return Returns width.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets a new width.
	 * 
	 * @param w
	 *            Width.
	 */
	public void setWidth(int w) {
		this.width = w;
	}

	/**
	 * 
	 * @return Returns a new height.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets a new height.
	 * 
	 * @param h
	 *            New height.
	 */
	public void setHeight(int h) {
		this.height = h;
	}

	/**
	 * 
	 * @return Returns the resolution.
	 */
	public double getResolution() {
		return resolution;
	}

	/**
	 * Checks if two coordinates of parent system are in this system.
	 * 
	 * @param x x
	 * @param y y
	 * @return Returns true, if the x and y are inside this system.
	 */
	public boolean areInsideSystem(double x, double y) {
		if (x < xOrigin || x >= xOrigin + width)
			return false;

		if (y < yOrigin || y >= yOrigin + height)
			return false;

		return true;
	}

	/**
	 * 
	 * @param x x
	 * @param y y
	 * @return Returns true if the two input integers are bigger than 0 and smaller
	 *         then size of this grid.
	 */
	public boolean areInside(double x, double y) {
		if (x < 0 || y < 0)
			return false;
		if (x >= width || y >= height)
			return false;

		return true;
	}

	public String toString() {
		IndentationStringBuilder builder = new IndentationStringBuilder();

		builder.appendLine("---------------------------------------------");

		builder.appendLines("resolution " + resolution, "xOrigin " + xOrigin, "yOrigin " + yOrigin, "width " + width,
				"height " + height);

		if (parent != null)
			builder.appendLine(parent.toString());

		return builder.toString();
	}

}
