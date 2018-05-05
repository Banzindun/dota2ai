package cz.cuni.mff.kocur.interests;

import java.awt.Graphics2D;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.world.Creep;

/**
 * Class that represents a object, that holds all the lanes.
 * 
 * @author kocur
 *
 */
public class Lanes {
	/**
	 * Logger registered for this class.
	 */
	private static final Logger logger = LogManager.getLogger(Lanes.class);

	/**
	 * Map of dire lanes addressed by their type.
	 */
	protected HashMap<Lane.TYPE, Lane> direLanes = new HashMap<>();

	/**
	 * Map of radiant lanes addressed by their type.
	 */
	protected HashMap<Lane.TYPE, Lane> radiantLanes = new HashMap<>();

	public Lanes() {
		direLanes.put(Lane.TYPE.BOT, new Lane(Lane.TYPE.BOT, Team.DIRE));
		direLanes.put(Lane.TYPE.MID, new Lane(Lane.TYPE.MID, Team.DIRE));
		direLanes.put(Lane.TYPE.TOP, new Lane(Lane.TYPE.TOP, Team.DIRE));

		radiantLanes.put(Lane.TYPE.BOT, new Lane(Lane.TYPE.BOT, Team.RADIANT));
		radiantLanes.put(Lane.TYPE.MID, new Lane(Lane.TYPE.MID, Team.RADIANT));
		radiantLanes.put(Lane.TYPE.TOP, new Lane(Lane.TYPE.TOP, Team.RADIANT));
	}

	/**
	 * Adds a lane to the lanes.
	 * 
	 * @param team
	 *            Team of the lane.
	 * @param type
	 *            Type of the lane.
	 * @param lane
	 *            The lane.
	 */
	public void addLane(int team, Lane.TYPE type, Lane lane) {
		if (team == Team.DIRE)
			direLanes.put(type, lane);
		else if (team == Team.RADIANT)
			radiantLanes.put(type, lane);
		else {
			logger.warn("Unknown lane type." + type);
		}
	}

	/**
	 * 
	 * @param team
	 *            Team number.
	 * @param type
	 *            Type of the lane.
	 * @return Returns lane of given team and type.
	 */
	public Lane getLane(int team, Lane.TYPE type) {
		if (team == Team.DIRE)
			return direLanes.get(type);
		else if (team == Team.RADIANT)
			return radiantLanes.get(type);

		logger.warn("No lane found for team and type: " + team + " " + type);
		return null;
	}

	/**
	 * 
	 * @param team
	 *            Team number.
	 * @return Returns all the stored lanes.
	 */
	public Lane[] getLanes(int team) {
		if (team == Team.DIRE)
			return direLanes.values().toArray(new Lane[direLanes.size()]);
		else
			return radiantLanes.values().toArray(new Lane[radiantLanes.size()]);
	}

	/**
	 * Sorts towers and path corners on the lanes using specified radiant and dire
	 * bases.
	 * 
	 * @param radiantBase
	 *            Radiant ancient (Fort).
	 * @param direBase
	 *            Dire ancient (Fort).
	 */
	public void sort(Fort radiantBase, Fort direBase) {
		for (Lane l : radiantLanes.values()) {
			l.sort(radiantBase);
		}

		for (Lane l : direLanes.values()) {
			l.sort(direBase);
		}
	}

	public String toString() {
		IndentationStringBuilder builder = new IndentationStringBuilder();

		builder.append("Dire lanes: ");
		builder.indent();

		for (Lane l : radiantLanes.values()) {
			builder.appendLine(l.toString(builder.getIndent()));
		}

		builder.deindent();
		builder.append("Radiant lanes: ");
		builder.indent();

		for (Lane l : direLanes.values()) {
			builder.appendLine(l.toString(builder.getIndent()));
		}

		return builder.toString();
	}

	public void paint(Graphics2D g) {
		for (Lane l : radiantLanes.values()) {
			l.paint(g);
		}

		for (Lane l : direLanes.values()) {
			l.paint(g);
		}
	}

	/**
	 * Should be called after a tower was destroyed. Removes the tower from on of
	 * the lanes.
	 * 
	 * @param entid
	 *            Id of the tower.
	 */
	public void towerDestroyed(int entid) {
		boolean destroyed = false;

		for (Lane l : radiantLanes.values()) {
			destroyed = destroyed || l.towerWithIdDestroyed(entid);
		}

		for (Lane l : direLanes.values()) {
			destroyed = destroyed || l.towerWithIdDestroyed(entid);
		}

		if (!destroyed) {
			logger.fatal("Tower was destroyed, but wasn found in interests. (and couldn't be deleted there)");
		}
	}

	/**
	 * Returns a lane, that is closest to given location.
	 * 
	 * @param l
	 *            Location.
	 * @return Returns a lane, that is closest to given location.
	 */
	public Lane getClosestLane(Location l) {
		Lane l1 = getClosestLane(Team.DIRE, l);
		Lane l2 = getClosestLane(Team.RADIANT, l);

		if (l1.getDistanceFromLane(l) > l2.getDistanceFromLane(l))
			return l1;
		else
			return l2;
	}

	/**
	 * 
	 * @param team
	 *            Team number.
	 * @param l
	 *            The location.
	 * @return Returns a lane of given team, that is close to specified location.
	 */
	public Lane getClosestLane(int team, Location l) {
		if (team == Team.DIRE)
			return direLanes.values().stream()
					.min((l1, l2) -> Double.compare(l1.getDistanceFromLane(l), l2.getDistanceFromLane(l))).get();

		// Else return dire.
		return radiantLanes.values().stream()
				.min((l1, l2) -> Double.compare(l1.getDistanceFromLane(l), l2.getDistanceFromLane(l))).get();
	}

	/**
	 * Adds a lane creep to the lane, that is closest to him.
	 * 
	 * @param team
	 *            Team number.
	 * @param creep
	 *            Creep to be added.
	 */
	public void addCreep(int team, Creep creep) {
		Lane l = getClosestLane(team, creep);
		l.addCreep(creep);
	}

	/**
	 * Removes a creep from one of the lanes.
	 * 
	 * @param team
	 *            Team number.
	 * @param creep
	 *            Creep to be removed.
	 */
	public void removeCreep(int team, Creep creep) {
		boolean removed = false;

		for (Lane l : getLanes(team)) {
			removed = removed || l.removeCreep(creep.getEntid());
		}

		if (!removed) {
			logger.fatal(
					"Unable to remove creep that died from lane creeps! It probably wasn't inserted into one of the lanes, or died multiple times.");
		}
	}

}
