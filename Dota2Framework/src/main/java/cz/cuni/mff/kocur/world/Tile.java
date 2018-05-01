package cz.cuni.mff.kocur.world;

/**
 * Class representing simple tiles that should be used inside grids. (influence mapping, navigation)
 * @author Banzindun
 *
 */
public class Tile {
	/**
	 * Tile is blocked and traversable.
	 */
	final public static byte TRAVERSABLE = 1;
	
	/**
	 * Tile is blocked and not traversable.
	 */
	final public static byte BLOCKED = 2;
	
	/**
	 * If tile is not traversable (not traversabela and not blocked).
	 */
	final public static byte NOTTRAVERSABLE = 3;
	
	/**
	 * Tile is walkable.
	 */
	final public static byte OK = 0; 
	
	/**
	 * Type of this field. 
	 */
	public byte type = OK;
	
	/**
	 * Height of the tile.
	 */
	public short height = -1;
	
	public Tile(byte type) {
		this.type = type;
	}
}
