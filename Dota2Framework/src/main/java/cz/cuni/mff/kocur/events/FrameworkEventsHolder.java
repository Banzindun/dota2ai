package cz.cuni.mff.kocur.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.configuration.FrameworkConfiguration;

/**
 * Holds framework events. Manages their cleaning, querying etc.
 * 
 * @author kocur
 *
 */
public class FrameworkEventsHolder extends EventsHolder<FrameworkEvent> {
	/**
	 * Logger registered for FrameworkEventsHolder class.
	 */
	private Logger logger = LogManager.getLogger(GameEventsHolder.class.getName());

	/**
	 * Constructor.
	 */
	public FrameworkEventsHolder() {
		initializeCleanupPeriod();
	}

	/**
	 * Constructor, that adds a passed event.
	 * 
	 * @param e
	 *            FrameworkEvent to be added to holder.
	 */
	public FrameworkEventsHolder(FrameworkEvent e) {
		initializeCleanupPeriod();
		events.add(e);
	}

	/**
	 * Loads and initializes the cleanup period out of framework configuration.
	 */
	private void initializeCleanupPeriod() {
		String period = FrameworkConfiguration.getInstance().getConfigValue("framework_event_cleanup_period");

		if (period != null) {
			try {
				int val = Integer.parseInt(period);
				cleanupPeriod = val;
			} catch (NumberFormatException ex) {
				logger.warn("Unable to get cleanup period for framework events from the configuration.");
			}
		}
	}

}
