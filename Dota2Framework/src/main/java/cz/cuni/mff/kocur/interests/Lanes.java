package cz.cuni.mff.kocur.interests;

import java.awt.Graphics2D;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.world.Creep;

public class Lanes {

	private static final Logger logger = LogManager.getLogger(Lanes.class);

	protected HashMap<Lane.TYPE, Lane> direLanes = new HashMap<>();

	protected HashMap<Lane.TYPE, Lane> radiantLanes = new HashMap<>();

	public Lanes() {
		direLanes.put(Lane.TYPE.BOT, new Lane(Lane.TYPE.BOT, Team.DIRE));
		direLanes.put(Lane.TYPE.MID, new Lane(Lane.TYPE.MID, Team.DIRE));
		direLanes.put(Lane.TYPE.TOP, new Lane(Lane.TYPE.TOP, Team.DIRE));

		radiantLanes.put(Lane.TYPE.BOT, new Lane(Lane.TYPE.BOT, Team.RADIANT));
		radiantLanes.put(Lane.TYPE.MID, new Lane(Lane.TYPE.MID, Team.RADIANT));
		radiantLanes.put(Lane.TYPE.TOP, new Lane(Lane.TYPE.TOP, Team.RADIANT));
	}

	public void addLane(int team, Lane.TYPE type, Lane lane) {
		if (team == Team.DIRE)
			direLanes.put(type, lane);
		else if (team == Team.RADIANT)
			radiantLanes.put(type, lane);
		else {
			logger.warn("Unknown lane type." + type);
		}
	}

	public Lane getLane(int team, Lane.TYPE type) {
		if (team == Team.DIRE)
			return direLanes.get(type);
		else if (team == Team.RADIANT)
			return radiantLanes.get(type);

		logger.warn("No lane found for team and type: " + team + " " + type);
		return null;
	}
	
	public Lane[] getLanes(int team) { 
		if (team == Team.DIRE)
			return direLanes.values().toArray(new Lane[direLanes.size()]);
		else
			return radiantLanes.values().toArray(new Lane[radiantLanes.size()]);
	}

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

	public Lane getClosestLane(Location l) {
		Lane l1 = getClosestLane(Team.DIRE, l);
		Lane l2 = getClosestLane(Team.RADIANT, l);
		
		if (l1.getDistanceFromLane(l) > l2.getDistanceFromLane(l))
			return l1;
		else 
			return l2;
	}

	public Lane getClosestLane(int team, Location l) {
		if (team == Team.DIRE)
			return direLanes.values().stream()
					.min((l1, l2) -> Double.compare(l1.getDistanceFromLane(l), l2.getDistanceFromLane(l))).get();

		// Else return dire.
		return radiantLanes.values().stream()
				.min((l1, l2) -> Double.compare(l1.getDistanceFromLane(l), l2.getDistanceFromLane(l))).get();
	}

	public void addCreep(int team, Creep creep) {
		Lane l = getClosestLane(team, creep);
		l.addCreep(creep);
	}
	
	public void removeCreep(int team, Creep creep) {
		boolean removed = false;
		
		for (Lane l : getLanes(team)) {
			removed = removed || l.removeCreep(creep.getEntid());
		}
		
		if (!removed) {
			logger.fatal("Unable to remove creep that died from lane creeps! It probably wasn't inserted into one of the lanes, or died multiple times.");
		}
	}

}
