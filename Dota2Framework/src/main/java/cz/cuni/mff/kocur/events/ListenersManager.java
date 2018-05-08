package cz.cuni.mff.kocur.events;

import java.util.HashMap;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class represents a manager, that handles all the events. Events can be
 * of two types: framework and game event. This class manages listeners for both
 * of these events.
 * 
 * @author kocur
 *
 */
public class ListenersManager {
	/**
	 * Logger for ListenersManager class.
	 */
	private static Logger logger = LogManager.getLogger(ListenersManager.class.getName());

	/**
	 * Map representing listening (value) to event (key) relation.
	 */
	private static HashMap<String, LinkedList<FrameworkEventListener>> listeners = new HashMap<>();

	/**
	 * HashMap that has event names as keys and listeners that listen to events with
	 * giben name as values.
	 */
	private static HashMap<String, LinkedList<FrameworkEventListener>> frameworkListeners = new HashMap<>();

	/**
	 * Registers new listener for specified game event.
	 * 
	 * @param name
	 *            Name of the event.
	 * @param l
	 *            The listener.
	 */
	public static void addListener(String name, FrameworkEventListener l) {
		logger.debug("Adding listener " + l.getClass().getName() + " for: " + name);
		if (!listeners.containsKey(name)) {
			listeners.put(name, new LinkedList<>());
		}

		listeners.get(name).add(l);
	}

	/**
	 * Registers new listener for specified framwork event.
	 * 
	 * @param name
	 *            Name of the event.
	 * @param l
	 *            The listener.
	 */
	public static void addFrameworkListener(String name, FrameworkEventListener l) {
		logger.debug("Adding framework listener " + l.getClass().getName() + " for: " + name);
		if (!frameworkListeners.containsKey(name)) {
			frameworkListeners.put(name, new LinkedList<>());
		}

		frameworkListeners.get(name).add(l);
	}

	/**
	 * Removes listener from listeners.
	 * 
	 * @param eventName
	 *            Name of the event.
	 * @param l
	 *            The listener.
	 */
	public static void removeFromListeners(String eventName, FrameworkEventListener l) {
		LinkedList<FrameworkEventListener> ls = listeners.get(eventName);
		if (ls != null)
			ls.remove(l);

	}

	/**
	 * Removes listener from framework listeners.
	 * 
	 * @param eventName
	 *            Name of the event.
	 * @param l
	 *            The listener.
	 */
	public static void removeFromFrameworkListeners(String eventName, FrameworkEventListener l) {
		LinkedList<FrameworkEventListener> ls = frameworkListeners.get(eventName);
		if (ls != null)
			ls.remove(l);
	}

	/**
	 * Triggers a game event with given name.
	 * 
	 * @param name
	 *            Name of the event.
	 * @param e
	 *            GameEvent object, that stores information about the event
	 *            (location, target etc.)
	 */
	public static void triggerEvent(String name, GameEvent e) {
		// Save the event to events base and set its name (so the user doesn't need to)
		e.setName(name);
		EventsBase.gameEventTriggered(name, e);

		logger.debug("Triggered event: " + name);

		LinkedList<FrameworkEventListener> ls = listeners.get(name);
		if (ls == null)
			return;

		// Spawn a new thread for every listener
		for (FrameworkEventListener l : ls) {
			spawnAThread(l, e);
		}
	}

	/**
	 * Triggers the game event.
	 * 
	 * @param name
	 *            Name of the event.
	 * @param e
	 *            GameEvent that stores all the relevant information.
	 * @param os
	 *            Objects to pass to listeners.
	 */
	public static void triggerEvent(String name, GameEvent e, Object... os) {
		// Save the event to events base and set its name (so the user doesn't need to)
		e.setName(name);
		EventsBase.gameEventTriggered(name, e);

		logger.debug("Triggered event: " + name);

		LinkedList<FrameworkEventListener> ls = listeners.get(name);
		if (ls == null)
			return;

		// Spawn a new thread for every listener
		for (FrameworkEventListener l : ls) {
			spawnAThread(l, e, os);
		}
	}

	/**
	 * Triggers the framework event.
	 * 
	 * @param name
	 *            Name of the event.
	 * @param e
	 *            FrameworkEvent containing data about the event.
	 */
	public static void triggerFrameworkEvent(String name, FrameworkEvent e) {
		// Save the event to events base and set its name (so the user doesn't need to)
		e.setName(name);
		EventsBase.frameworkEventTriggered(name, e);

		// logger.debug("Triggered event: " + name);

		LinkedList<FrameworkEventListener> ls = frameworkListeners.get(name);
		if (ls == null)
			return;

		// Spawn a new thread for every listener
		for (FrameworkEventListener l : ls) {
			spawnAThread(l, e);
		}
	}

	/**
	 * Triggers the framework event.
	 * 
	 * @param name
	 *            Name of the event.
	 * @param e
	 *            FrameworkEvent containing data about the event.
	 * @param os
	 *            Objects that should be passed to listeners.
	 */
	public static void triggerFrameworkEvent(String name, FrameworkEvent e, Object... os) {
		// Save the event to events base and set its name (so the user doesn't need to)
		e.setName(name);
		EventsBase.frameworkEventTriggered(name, e);

		// logger.debug("Triggering framework event: " + name);

		LinkedList<FrameworkEventListener> ls = frameworkListeners.get(name);
		if (ls == null)
			return;

		// Spawn a new thread for every listener
		for (FrameworkEventListener l : ls) {
			spawnAThread(l, e, os);
		}
	}

	/**
	 * Clears the listeners that are listening for game events.
	 */
	public static void clearListeners() {
		logger.debug("Clearing listeners.");
		listeners.clear();
	}

	public static void clearFrameworkListeners() {
		logger.debug("Clearing framework listeners.");
		frameworkListeners.clear();
	}

	/**
	 * Spawns a thread and triggers the listener on it.
	 * 
	 * @param l
	 *            Listener that should be triggered.
	 * @param e
	 *            Triggered game event.
	 * 
	 */
	public static void spawnAThread(FrameworkEventListener l, Event e) {
		new Thread(new Runnable() {
			public void run() {
				try {
					l.triggered(e);
				} catch (Throwable e) {
					logger.error("Error during execution of triggered event, with stack trace:", e);
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * Spawns a thread and triggers the listener on it.
	 * 
	 * @param l
	 *            Listener that should be triggered.
	 * @param e
	 *            Triggered game event.
	 * @param os
	 *            Objects that should be passed through the trigger.
	 */
	public static void spawnAThread(FrameworkEventListener l, Event e, Object... os) {
		new Thread(new Runnable() {
			public void run() {
				try {
					l.triggered(e, os);
				} catch (Throwable e) {
					logger.error("Error during execution of triggered event, with stack trace:", e);
					e.printStackTrace();
				}
			}
		}).start();
	}

}
