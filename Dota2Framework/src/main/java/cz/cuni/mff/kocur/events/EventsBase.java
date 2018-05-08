package cz.cuni.mff.kocur.events;

import java.util.HashMap;
import java.util.List;

/**
 * Events base stores all the events and lets user interact with them.
 * 
 * @author kocur
 *
 */
public class EventsBase {

	/**
	 * Map that stores framework event names as keys and event data as values.
	 */
	private static HashMap<String, FrameworkEventsHolder> frameworkEvents = new HashMap<>();

	/**
	 * Map that stores game event names as keys and event data as values.
	 */
	private static HashMap<String, GameEventsHolder> gameEvents = new HashMap<>();

	/**
	 * Stores data about triggered framework event.
	 * 
	 * @param name
	 *            Name of the event.
	 * @param e
	 *            FrameworkEvent object that stores data about the event.
	 */
	public static void frameworkEventTriggered(String name, FrameworkEvent e) {
		if (!frameworkEvents.containsKey(name)) {
			frameworkEvents.put(name, new FrameworkEventsHolder(e));
		} else {
			frameworkEvents.get(name).add(e);
		}
	}

	/**
	 * Stores data about triggered game event.
	 * 
	 * @param name
	 *            Name of the event.
	 * @param e
	 *            GameEvent object that stores data about the event (location, time
	 *            etc.).
	 */
	public static void gameEventTriggered(String name, GameEvent e) {
		if (!gameEvents.containsKey(name)) {
			gameEvents.put(name, new GameEventsHolder(e));
		} else {
			gameEvents.get(name).add(e);
		}
	}

	/**
	 * Clears the game events.
	 */
	public static void cleanupGameEvents() {
		gameEvents.clear();
	}

	/**
	 * Clears the framework events.
	 */
	public static void cleanupFrameworkEvents() {
		frameworkEvents.clear();
	}

	/**
	 * 
	 * @param eventName
	 *            Name of the event.
	 * @return Returns list of GameEvents of given type.
	 */
	public static List<GameEvent> getGameEvents(String eventName) {
		GameEventsHolder h = gameEvents.get(eventName);
		if (h == null)
			return null;

		return h.toList();
	}

	/**
	 * 
	 * @param eventName
	 *            Event's name.
	 * @param maxAge
	 *            Maximum age.
	 * @return Returns events that are of given name and maximum age.
	 */
	public static List<GameEvent> getGameEvents(String eventName, int maxAge) {
		GameEventsHolder h = gameEvents.get(eventName);
		if (h == null)
			return null;

		return h.getYounger(maxAge);
	}

	/**
	 * 
	 * @param eventName
	 *            Event's name.
	 * @param x
	 *            X coordinate.
	 * @param y
	 *            Y coordinate.
	 * @param maxDistance
	 *            Maximum distance.
	 * @return Returns events close to some location and closer than max distance.
	 */
	public static List<GameEvent> getGameEvents(String eventName, double x, double y, int maxDistance) {
		GameEventsHolder h = gameEvents.get(eventName);
		if (h == null)
			return null;

		return h.getNearbyEvents(x, y, maxDistance);
	}

	/**
	 * 
	 * @param eventName
	 *            Name of the event.
	 * @return Returns framework events with given name.
	 */
	public static List<FrameworkEvent> getFrameworkEvents(String eventName) {
		FrameworkEventsHolder h = frameworkEvents.get(eventName);
		if (h == null)
			return null;

		return h.toList();
	}

	/**
	 * 
	 * @param eventName
	 *            Name of the event.
	 * @param maxAge
	 *            Maximum age.
	 * @return Returns framework events with given name and younger than maximum
	 *         age.
	 */
	public static List<FrameworkEvent> getFrameworkEvents(String eventName, int maxAge) {
		FrameworkEventsHolder h = frameworkEvents.get(eventName);
		if (h == null)
			return null;

		return h.getYounger(maxAge);
	}
	
}
