package cz.cuni.mff.kocur.world;

import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.base.Pair;

/**
 * Class that represents a gridBase. GridBase handles the map that consists of
 * tiles. Each tile represents a few in-game tiles. Each one of them stores
 * information about the height and passability of the in-game tile. The map
 * stores
 * 
 * @author kocur
 *
 */
public class GridBase extends GridSystem {
	/**
	 * Logger registered for GridBase.
	 */
	private Logger logger = LogManager.getLogger(GridSystem.class.getName());

	/**
	 * The instance.
	 */
	private static GridBase instance = null;

	/**
	 * 
	 * @return Returns instance of GridBase.
	 */
	public static GridBase getInstance() {
		if (instance == null)
			instance = new GridBase();

		return instance;
	}

	/**
	 * Array of tiles. This is essentially the grid I am storing.
	 */
	private static Tile[][] tiles;

	/**
	 * Minimum value the x can have.
	 */
	private int minX;

	/**
	 * Maximum value the x can have.
	 */
	private int maxX;

	/**
	 * Minimum value the y can have.
	 */
	private int minY;

	/**
	 * Maximum value the y can have.
	 */
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
	 *            x
	 * @param y
	 *            y
	 * @param type
	 *            Type of tile (traversable, nontraversable, blocked)
	 */
	public void setTile(int x, int y, byte type) {
		tiles[y][x] = new Tile(type);
	}

	/**
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @return Returns tile on position x, y.
	 */
	public Tile getTile(int x, int y) {
		return tiles[y][x];
	}

	/**
	 * 
	 * @param xy
	 *            Array of x and y coordinates. [x, y]
	 * @return Returns the tile on position x, y.
	 */
	public Tile getTile(int[] xy) {
		return tiles[xy[0]][xy[1]];
	}

	/**
	 * Sets a height of tile on x and y position.
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @param height
	 *            Height.
	 */
	public void setTileHeight(int x, int y, short height) {
		tiles[y][x].height = height;
	}

	/**
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @return Returns the height of the tile on position [x, y].
	 */
	public short getTileHeight(int x, int y) {
		return tiles[y][x].height;
	}

	/**
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @return Returns true, if the tile at [x, y] is passable.
	 */
	public boolean passable(int x, int y) {
		return tiles[y][x].type == Tile.OK;
	}

	/**
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @return Returns true, if the tile at [x, y] is not traversable.
	 */
	public boolean nottraversable(int x, int y) {
		return tiles[y][x].type == Tile.NOTTRAVERSABLE;
	}

	@Override
	public double resolveY(double y) {
		if (y < 0) {
			return (maxY + 1 + (-y)) / resolution;
		}

		return (maxY - y) / resolution;
	}

	@Override
	public double resolveYBack(double y) {
		if (y < maxY) {
			return -(y * resolution - maxY);
		} else {
			return -(y * resolution - maxY - 1);
		}
	}

	/**
	 * Measures a distance from tile to tile, given by their coordinates.
	 * 
	 * @param x1
	 *            x1
	 * @param y1
	 *            y1
	 * @param x2
	 *            x2
	 * @param y2
	 *            y2
	 * @return Returns the distance.
	 */
	public static double distanceTileToTile(int x1, int y1, int x2, int y2) {
		return distance(x1, y1, x2, y2);
	}

	/**
	 * Measures a distance from tile to tile, given by their coordinates.
	 * 
	 * @param x1
	 *            x1
	 * @param y1
	 *            y1
	 * @param x2
	 *            x2
	 * @param y2
	 *            y2
	 * @return Returns the distance.
	 */
	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	/**
	 * 
	 * @param s
	 *            Source.
	 * @param target
	 *            Target.
	 * @return Returns distance between source and target.
	 */
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

	public static double distance(Location l, double d, double e) {
		return distance(l.getX(), l.getY(), d, e);
	}

	/**
	 * Finds closest tile, that can be walked on, that is near [x, y] and in a
	 * specified radius.
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @param distance
	 *            Radius.
	 * @return Returns the coordinates of the closest passable tile.
	 */
	public int[] findClosestPassableTile(int x, int y, int distance) {
		LinkedList<Pair<Double, Location>> tts = new LinkedList<>();

		for (int i = y - distance; i < y + distance && i < height; i++) {
			if (i < 0)
				i = 0;

			for (int j = x - distance; j < x + distance && j < width; j++) {
				if (j < 0)
					j = 0;

				if (passable(j, i))
					tts.add(new Pair<Double, Location>(distance(x, y, j, i), new Location(j, i)));

			}
		}

		if (tts.size() > 0) {
			Location l = tts.stream().min((first, second) -> Double.compare(first.getKey(), second.getKey())).get()
					.getValue();

			return new int[] { (int) l.getX(), (int) l.getY() };
		}

		return new int[] { x, y };
	}

}
