package cz.cuni.mff.kocur.world;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.base.Location;

public abstract class GridSystem {

	/**
	 * Static logger for GridSystem class.
	 */
	private static final Logger logger = LogManager.getLogger(GridSystem.class.getName());

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
	 * @param y
	 * @return
	 */
	public static Integer[] toTile(double x, double y) {
		return new Integer[] {(int) x, (int) y};
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
	 * Recursively multiplies x by resolution until it reaches the base. (that will be
	 * grid in usual scenario)
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
	 * @param y
	 * @return
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
	 * @param y
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
	 * @return
	 */
	public double resolveY(double y) {
		return (y - yOrigin) / resolution;
	}

	/**
	 * Resolves x, y. Transforms it to this bas.e
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public double[] resolveXY(double x, double y) {
		return new double[] { resolveX(x), resolveY(y) };
	}

	public double[] resolveXY(int[] xy) {
		return resolveXY(xy[0], xy[1]);
	}

	public double[] resolveXY(double[] xy) {
		return resolveXY(xy[0], xy[1]);
	}

	public double[] centerOfTile(double x, double y) {
		return new double[] { x + resolution / 2.0, y + resolution / 2.0 };
	}

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

	public double resolveXBack(double x) {
		return x * resolution + xOrigin;
	}

	public double resolveYBack(double y) {
		return y * resolution + yOrigin;
	}

	public double[] resolveXYBack(double x, double y) {
		return new double[] { resolveXBack(x), resolveYBack(y) };
	}

	public double[] resolveXYBack(double[] origin) {
		return resolveXYBack(origin[0], origin[1]);
	}
	
	

	public boolean setSize(int width, int height) {
		if (resolution <= 0) {
			logger.error("Grid resolution is less than or equal to 0.");
			return false;
		}
		this.height = height;
		this.width = width;

		return true;
	}

	public void setOrigin(double x, double y) {
		this.xOrigin = x;
		this.yOrigin = y;
	}

	public void setXOrigin(double x) {
		this.xOrigin = x;
	}

	public void setYOrigin(double y) {
		this.yOrigin = y;
	}

	public double[] getOrigin() {
		return new double[] { xOrigin, yOrigin };
	}

	public double getXOrigin() {
		return xOrigin;
	}

	public double getYOrigin() {
		return yOrigin;
	}

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

	public int getWidth() {
		return width;
	}

	public void setWidth(int w) {
		this.width = w;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int h) {
		this.height = h;
	}

	public double getResolution() {
		return resolution;
	}

	/**
	 * Checks if two coordinates of parent system are in this system.
	 * 
	 * @param x
	 * @param y
	 * @return
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
	 * @param x 
	 * @param y
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
		
		builder.appendLines("resolution " + resolution,
				"xOrigin " + xOrigin,
				"yOrigin " + yOrigin,
				"width " + width, 
				"height " + height
				);
		
		if (parent != null)
			builder.appendLine(parent.toString());
		
		return builder.toString();
	}

}
