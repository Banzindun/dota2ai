package cz.cuni.mff.kocur.world;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import cz.cuni.mff.kocur.base.CustomStringBuilder;
import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.interests.InterestsBase;

/**
 * This class represents a world object. It stores entities by their in-game
 * identifiers. It allows user to search in them, get them by distance, id or
 * name. The class also supplies functions, that handle the update and cleanup.
 * 
 * @author kocur
 *
 */
public class World {
	/**
	 * Map of stored entities.
	 */
	@JsonProperty("entities")
	protected ConcurrentHashMap<Integer, BaseEntity> entities = new ConcurrentHashMap<>(1024);

	/**
	 * Constructor.
	 */
	public World() {
	}

	/**
	 * 
	 * @return Returns list of entitites inside this world.
	 */
	public ConcurrentHashMap<Integer, BaseEntity> getEntities() {
		return entities;
	}

	/**
	 * 
	 * @param id
	 *            Id of the entity we want to find.
	 * @return Returns an entity with given id.
	 */
	public BaseEntity getEntity(Integer id) {
		return entities.get(id);
	}

	/**
	 * Searches for entity by id.
	 * 
	 * @param name
	 *            Name of the entity.
	 * @return Returns the entity with given name.
	 */
	public int searchIdByName(String name) {
		final Entry<Integer, BaseEntity> e = entities.entrySet().stream()
				.filter(p -> p.getValue().getName().equals(name)).findAny().orElse(null);
		if (e == null) {
			return -1;
		} else {
			return e.getKey();
		}
	}

	/**
	 * Finds entities in radius around a location.
	 * 
	 * @param l
	 *            Location. Center of our radius.
	 * @param dist
	 *            Radius.
	 * @return Returns list of entities inside the radius around the passed
	 *         location.
	 */
	public List<BaseEntity> findEntitiesInRadius(Location l, double dist) {
		return entities.values().stream().filter(p -> GridBase.distance(p, l) < dist && GridBase.distance(p, l) != 0)
				.sorted((e1, e2) -> Double.compare(GridBase.distance(e1, l), GridBase.distance(e2, l)))
				.collect(Collectors.toList());
	}

	/**
	 * Converts the ids of two entities to the index in distance HashMap.
	 * 
	 * @param e1
	 *            Id of the first entity.
	 * @param e2
	 *            If of the second entity.
	 * @return Id in distance hash map.
	 */
	public Double toIndex(int e1, int e2) {
		if (e1 < e2)
			return e1 + e2 / 10000.0;
		else
			return e2 + e1 / 10000.0;
	}

	/**
	 * Sets entities inside this world.
	 * 
	 * @param entities
	 *            New entities.
	 */
	public void setEntities(ConcurrentHashMap<Integer, BaseEntity> entities) {
		this.entities = entities;
	}

	/**
	 * Updates this world. Updates puts all entities to world representation.
	 * 
	 * @param u
	 *            WorldUpdate - world that we are updating from.
	 */
	public void update(WorldUpdate u) {
		for (Entry<Integer, BaseEntity> e : u.getEntities().entrySet()) {
			BaseEntity local = entities.get(e.getKey());
			if (local == null) {
				entities.put(e.getKey(), e.getValue()); // Inserting new
				e.getValue().born(); // First time I saw this (or after a time) .. call born function
			} else {
				local.update(e.getValue()); // Else we update the old value
			}
		}

		for (Integer i : u.getEntities().keySet()) {
			// And we set u to reference entity we created.
			u.getEntities().put(i, entities.get(i));
		}

		// If there ares some interests, like fountain etc. then update those
		if (u.getInterests() != null)
			InterestsBase.getInstance().update(u.getInterests());
	}
	
	/**
	 * Updates this world. Updates puts all entities to world representation. 
	 * 
	 * @param u
	 *            WorldUpdate - world that we are updating from.
	 */
	public void voidUpdate(WorldUpdate u) {
		for (Entry<Integer, BaseEntity> e : u.getEntities().entrySet()) {
			BaseEntity local = entities.get(e.getKey());
			if (local == null) {
				entities.put(e.getKey(), e.getValue()); // Inserting new
				e.getValue().updateTime();
			} else {
				local.update(e.getValue());	// Else we update the old value
			}
		}
	}

	/**
	 * Cleans up the world.
	 * 
	 * This removes entities that should disappear.
	 * 
	 * @return Returns number of entities that were removed.
	 */
	public int cleanup() {
		int c = 0;
		for (Iterator<Entry<Integer, BaseEntity>> it = entities.entrySet().iterator(); it.hasNext();) {
			BaseEntity e = it.next().getValue();
			if (e.shouldDie()) {
				e.dying();
				it.remove();
				c++;
			}
		}
		return c;
	}

	/**
	 * Cleans up the world, but doesn't alert entities that they are dying.
	 * 
	 * This removes entities that should disappear.
	 * 
	 * @return Returns number of entities that were removed.
	 */
	public int voidCleanup() {
		int c = 0;
		for (Iterator<Entry<Integer, BaseEntity>> it = entities.entrySet().iterator(); it.hasNext();) {
			BaseEntity e = it.next().getValue();
			if (e.shouldDie()) {
				it.remove();
				c++;
			}
		}
		return c;
	}

	@Override
	public String toString() {
		CustomStringBuilder builder = new CustomStringBuilder();
		for (Entry<Integer, BaseEntity> e : entities.entrySet()) {
			builder.appendLine(e.toString());
		}

		return builder.toString();
	}
}
