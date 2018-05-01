package cz.cuni.mff.kocur.events;

/**
 * Class that represents a GameEvent. Typically the event occurs on some
 * location or to some entity. And is usually team dependent. Those attributes
 * are suppported by the implementation.
 * 
 * @author kocur
 *
 */
public class GameEvent extends Event {

	/**
	 * I want to know who triggered the event. If someone did, here will be his id.
	 */
	protected int id = -1;

	/**
	 * X coordinate of the place where the event occurred.
	 */
	protected double x;

	/**
	 * Y coordinate of the place where this event occured.
	 */
	protected double y;

	/**
	 * Z coordinate of place where the event occurred.
	 */
	protected double z;

	/**
	 * Int for storing type of for example object that triggered this event.
	 */
	protected int type = 0;

	/**
	 * Number of the team that this event happened to.
	 */
	protected int team = 0;

	/**
	 * 
	 * @return Returns the id of the GAME object that triggered this event.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id of GAME object that triggered this event.
	 * 
	 * @param id
	 *            Id of the object that triggered this event.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * 
	 * @return Returns the X coordinate of the spot, where the event occurred.
	 */
	public double getX() {
		return x;
	}

	/**
	 * 
	 * @param x
	 *            X coordinate of the place where this event took place.
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * 
	 * @return Returns the Y coordinate of the spot, where the event occurred.
	 */

	public double getY() {
		return y;
	}

	/**
	 * 
	 * @param y
	 *            Y coordinate of the place where this event took place.
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * 
	 * @return Returns the Z coordinate of the spot, where the event occurred.
	 */
	public double getZ() {
		return z;
	}

	/**
	 * 
	 * @param z
	 *            Z coordinate of the place where this event took place.
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * Sets the [x,y,z] coordinates of the place where the event occured.
	 * 
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param z Z cooridnate.
	 */
	public void setXYZ(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Sets a new type of this event.
	 * @param type New type.
	 */
	public void setType(int type) {
		this.type = type;

	}

	/**
	 * Sets a new team to this event.
	 * @param team new team.
	 */
	public void setTeam(int team) {
		this.team = team;

	}

	/**
	 * 
	 * @return Returns type of this event.
	 */
	public int getType() {
		return type;
	}

	/**
	 * 
	 * @return Returns team of this event.
	 */
	public int getTeam() {
		return team;
	}

}
