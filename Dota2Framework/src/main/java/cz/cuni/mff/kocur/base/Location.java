package cz.cuni.mff.kocur.base;

import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Class that represents location. Each location has x,y,z coordinates and it
 * can have a entity id. This will be extended by base entity and interest
 * objects.
 * 
 * @author kocur
 *
 */
public class Location {
	/**
	 * Id of our location/entity.
	 */
	protected int entid = -1;

	/**
	 * x coordinate.
	 */
	protected double x = -1;

	/**
	 * y coordinate.
	 */
	protected double y = -1;

	/**
	 * z coordinate.
	 */
	protected double z = 0;

	public Location() {

	}

	/**
	 * Creates the location from passed coordinates.
	 * 
	 * @param x2
	 *            X coordinate.
	 * @param y2
	 *            Y coordinate.
	 * @param z2
	 *            Z coordinate.
	 */
	public Location(double x2, double y2, double z2) {
		x = x2;
		y = y2;
		z = z2;
	}

	/**
	 * Creates the location using array of coordinates. Array should have 2 or 3
	 * fields with doubles. If 2 supplied then z = 0.
	 * 
	 * @param coords
	 *            Coordinates from which we should initialize the location.
	 */
	public Location(double[] coords) {
		x = coords[0];
		y = coords[1];

		if (coords.length == 2)
			z = 0;
		else
			z = coords[2];
	}
	
	/**
	 * Creates the location using two coordinates.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 */
	public Location(double x, double y) {
		this.x = x;
		this.y = y;

		this.z = 0;
	}

	/**
	 * 
	 * @return Returns X coordinate.
	 */
	public double getX() {
		return x;
	}

	/**
	 * 
	 * @param x
	 *            New x coordinate.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * 
	 * @return Returns y coordinate.
	 */
	public double getY() {
		return y;
	}

	/**
	 * 
	 * @return Returns z coordinate.
	 */
	public double getZ() {
		return z;
	}

	/**
	 * 
	 * @param z
	 *            New z coordinate.
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * 
	 * @param y
	 *            New y coordinate.
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * 
	 * @return Returns id of this entity.
	 */
	public int getEntid() {
		return entid;
	}

	/**
	 * 
	 * @param entid
	 *            Sets this entity id.
	 */
	public void setEntid(int entid) {
		this.entid = entid;
	}

	/**
	 * Changes coordinates to the ones passed in argument. This is used for example
	 * when deserializing JSON data, to initialize x,y,z.
	 * 
	 * @param origin
	 *            New coordinates of this location.
	 */
	@JsonSetter("origin")
	public void setOrigin(double[] origin) {
		x = origin[0];
		y = origin[1];
		z = origin[2];
	}

	/**
	 * 
	 * @return Returns true if this object has location set.
	 */
	public boolean isLocationSet() {
		return x != -1 && y != -1 && z != -1;
	}

	/**
	 * 
	 * @return Returns true if the id of this object is set.
	 */
	public boolean idSet() {
		return entid != -1;
	}

	/**
	 * Updates the location. Changes its x,y,z, and entity id to correspond to
	 * location that is passed as argument.
	 * 
	 * @param e
	 *            Location to copy from.
	 */
	public void update(Location e) {
		this.entid = e.getEntid();
		this.x = e.getX();
		this.y = e.getY();
		this.z = e.getZ();
	}

}
