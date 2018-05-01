package cz.cuni.mff.kocur.world;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.interests.Fountain;

public class GridBase extends GridSystem {
	private Logger logger = LogManager.getLogger(GridSystem.class.getName());

	private static GridBase instance = null;

	public static GridBase getInstance() {
		if (instance == null)
			instance = new GridBase();

		return instance;
	}

	/**
	 * Array of tiles. This is essentially the grid I am storing.
	 */
	private static Tile[][] tiles;
	
	private int minX;
	private int maxX; 
	private int minY;
	private int maxY;

	/**
	 * Constructor.
	 */
	private GridBase() {
		
		
	}

	@Override
	public boolean setSize(int width, int height) {
		boolean r = super.setSize(width, height);

		if (r == true) {
			logger.info("Initializing grid with height and width: " + height + " " + width);
			tiles = new Tile[height][width];
		}

		return r;
	}

	/**
	 * Create a new tile on position x,y with given type.
	 * 
	 * @param x
	 * @param y
	 * @param type
	 *            Type of tile (traversable, nontraversable, blocked)
	 */
	public void setTile(int x, int y, byte type) {
		tiles[y][x] = new Tile(type);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return Returns tile on position x, y.
	 */
	public Tile getTile(int x, int y) {
		return tiles[y][x];
	}
	
	public Tile getTile(int[] xy) {
		return tiles[xy[0]][xy[1]];
	}

	public void setTileHeight(int x, int y, short height) {
		tiles[y][x].height = height;
	}

	public short getTileHeight(int x, int y) {
		return tiles[y][x].height;
	}

	public boolean passable(int x, int y) {
		return tiles[y][x].type == Tile.OK;
	}

	public boolean nottraversable(int x, int y) {
		return tiles[y][x].type == Tile.NOTTRAVERSABLE;
	}
	
	/**
	 * Resolves y. Transforms it to this base.
	 * 
	 * @param y
	 * @return
	 */
	@Override
	public double resolveY(double y) {
		if (y < 0) {
			return (maxY+1 + (-y))/resolution;
		}
		
		return (maxY-y)/resolution;
	}

	@Override
	public double resolveYBack(double y) {
		if (y < maxY) {
			return -(y*resolution-maxY);
		} else {
			return -(y*resolution - maxY - 1);
		}
	}

	public static double distanceTileToTile(int x1, int y1, int x2, int y2) {
		if (x1 == x2) // Same column
			return Math.abs(y2 - y1);
		else if (y1 == y2)
			return Math.abs(x2 - x1);

		return distance(x1, y1, x2, y2);
	}

	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	public static double distance(Location s, Location target) {
		return distance(s.getX(), s.getY(), target.getX(), target.getY());
	}

	public static double distanceTileToTile(double[] baseCoords, double[] xyz) {
		return distanceTileToTile(toTile(baseCoords), toTile(xyz));
	}

	private static double distanceTileToTile(Integer[] tile, Integer[] tile2) {
		return distanceTileToTile(tile[0], tile[1], tile2[0], tile2[1]);
	}

	public static double distanceTileToTile(int x, int y, double[] baseCoords) {
		return distanceTileToTile(x, y, (int) baseCoords[0], (int) baseCoords[1]);
	}
	
	public int getMinX() {
		return minX;
	}

	public void setMinX(int minX) {
		this.minX = minX;
	}

	public int getMaxX() {
		return maxX;
	}

	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}

	public int getMinY() {
		return minY;
	}

	public void setMinY(int minY) {
		this.minY = minY;
	}

	public int getMaxY() {
		return maxY;
	}

	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}

	public static double distance(Fountain f, double d, double e) {
		return distance(f.getX(), f.getY(), d, e);
	}

}
