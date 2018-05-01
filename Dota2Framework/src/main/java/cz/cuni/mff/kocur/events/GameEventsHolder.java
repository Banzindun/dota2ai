package cz.cuni.mff.kocur.events;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.configuration.FrameworkConfiguration;
import cz.cuni.mff.kocur.world.GridBase;

/**
 * This class serves as a holder of game events. It holds the events, manages
 * them and cleans them up. We usually want to have a few of them in store,
 * because the agent might want to know when was a last time a specific event
 * occured etc.
 * 
 * @author kocur
 *
 */
public class GameEventsHolder extends EventsHolder<GameEvent> {
	/**
	 * Logger for game events holder.
	 */
	private Logger logger = LogManager.getLogger(GameEventsHolder.class.getName());

	/**
	 * Constructor.
	 */
	public GameEventsHolder() {
		initializeCleanupPeriod();
	}

	/**
	 * Contructor, that takes GameEvent and stores it inside the holder.
	 * @param e
	 */
	public GameEventsHolder(GameEvent e) {
		initializeCleanupPeriod();
		events.add(e);
	}

	/**
	 * Gets the cleanup period from framework configuration parses it.
	 * The period is then stored.
	 */
	private void initializeCleanupPeriod() {
		String period = FrameworkConfiguration.getInstance().getConfigValue("game_event_cleanup_period");

		if (period != null) {
			try {
				int val = Integer.parseInt(period);
				cleanupPeriod = val;
			} catch (NumberFormatException ex) {
				logger.warn("Unable to get cleanupPeriod from the framework configuration.");
			}
		}
	}

	/**
	 * Returns events, that occured around a specific location.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param z Maximum distance.
	 * @param maxDistance Maximum distance.
	 * @return Returns events that occured nearby.
	 */
	public List<GameEvent> getNearbyEvents(double x, double y, int maxDistance) {
		// We need a grid to do the calculations
		return events.stream().filter(e -> GridBase.distance(x, y, e.getX(), e.getY()) < maxDistance)
				.collect(Collectors.toList());
	}
}
