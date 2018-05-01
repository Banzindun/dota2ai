package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.world.Ability;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.Item;

public class Target {

	protected String name = null;

	protected Location location = null;

	protected BaseEntity entity = null;

	protected Item item = null;
	
	protected Ability ability = null;

	protected double value = Double.NEGATIVE_INFINITY;

	public Target(Location l) {
		location = l;
	}

	public Target(Location l, String name) {
		location = l;
		this.name = name;
	}

	public Target(BaseEntity e) {
		entity = e;
	}

	public Target(BaseEntity e, String name) {
		entity = e;
		this.name = name;
	}

	public Target(Item i) {
		this.item = i;
	}

	public Target(double value) {
		this.value = value;
	}

	public Target() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public BaseEntity getEntity() {
		return entity;
	}

	public void setEntity(BaseEntity entity) {
		this.entity = entity;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public void setValue(double value) {
		this.value = value;

	}

	public double getValue() {
		return value;
	}

	public Ability getAbility() {
		return ability;
	}
	
	public void setAbility(Ability a) {
		ability = a;
	}
	
}
