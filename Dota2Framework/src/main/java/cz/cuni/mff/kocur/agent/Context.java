package cz.cuni.mff.kocur.agent;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.World;
import cz.cuni.mff.kocur.world.WorldUpdate;

/**
 * Abstract class that represents a context. This class is extended by
 * TeamContext and AgentContext.
 * 
 * @author kocur
 *
 */
public abstract class Context {

	protected World world = new World();

	public abstract void update(WorldUpdate u);

	/**
	 * Finds entitites in radius around specified Location.
	 * 
	 * @param l
	 *            The location around which we are looking.
	 * @param dist
	 *            The radius.
	 * @return Returns all the entities that have distance to specified location
	 *         less that the given radius.
	 */
	public List<BaseEntity> findEntitiesInRadius(Location l, double dist) {
		if (world == null)
			return new LinkedList<BaseEntity>();
		return world.findEntitiesInRadius(l, dist);
	}
	
	/**
	 * 
	 * @return Returns map containing all the entities.
	 */
	public ConcurrentHashMap<Integer, BaseEntity> getEntites() {
		return world.getEntities();
	}
	
	/**
	 * Returns world converted to string.
	 */
	public String toString() {
		return world.toString();
	}
	
	/**
	 * 
	 * @return Returns world representation.
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * Sets a new world.
	 * 
	 * @param world
	 *            New world.
	 */
	public void setWorld(World world) {
		this.world = world;
	}

}
