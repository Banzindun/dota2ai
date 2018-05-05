package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.world.Ability;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.Item;

/**
 * Class that represents a target, that we will be using in our decisions.
 * Target can be a location, entity, item, name or ability stored inside. These
 * are all the things we can care about.
 * 
 * @author kocur
 *
 */
public class Target {

	/**
	 * Name of the target.
	 */
	protected String name = null;

	/**
	 * Location of the target.
	 */
	protected Location location = null;

	/**
	 * Target's entity.
	 */
	protected BaseEntity entity = null;

	/**
	 * Item we target.
	 */
	protected Item item = null;

	/**
	 * Ability we target.
	 */
	protected Ability ability = null;

	/**
	 * A target's value (for influence and other values).
	 */
	protected double value = Double.NEGATIVE_INFINITY;

	/**
	 * Stores location inside this target.
	 * 
	 * @param l
	 *            Location.
	 */
	public Target(Location l) {
		location = l;
	}

	/**
	 * Stores location and string name in this target.
	 * 
	 * @param l
	 *            Location.
	 * @param name
	 *            Name.
	 */
	public Target(Location l, String name) {
		location = l;
		this.name = name;
	}

	/**
	 * Stores base entity in this target.
	 * 
	 * @param e
	 *            Base entity.
	 */
	public Target(BaseEntity e) {
		entity = e;
	}

	/**
	 * Stores base entity and name in this target.
	 * 
	 * @param e
	 *            Base entity.
	 * @param name
	 *            Name.
	 */
	public Target(BaseEntity e, String name) {
		entity = e;
		this.name = name;
	}

	/**
	 * Stores item in this target.
	 * 
	 * @param i
	 *            Item.
	 */
	public Target(Item i) {
		this.item = i;
	}

	/**
	 * Stores a value in this target.
	 * 
	 * @param value
	 *            The value.
	 */
	public Target(double value) {
		this.value = value;
	}

	public Target() {

	}

	/**
	 * 
	 * @return Returns name stored inside this target.
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return Returns location stored inside this target.
	 */
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * 
	 * @return Returns base entity stored inside this target.
	 */
	public BaseEntity getEntity() {
		return entity;
	}

	public void setEntity(BaseEntity entity) {
		this.entity = entity;
	}

	/**
	 * 
	 * @return Returns item stored inside this target.
	 */
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public void setValue(double value) {
		this.value = value;

	}

	/**
	 * 
	 * @return Returns value stored inside this target.
	 */
	public double getValue() {
		return value;
	}

	/**
	 * 
	 * @return Returns ability stored inside this target.
	 */
	public Ability getAbility() {
		return ability;
	}

	public void setAbility(Ability a) {
		ability = a;
	}

}
