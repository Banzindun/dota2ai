package cz.cuni.mff.kocur.logging;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;

/**
 * Class that distributes logs from QueueAppenders to
 * 
 * @author Banzindun
 *
 */
public class LoggingDistributor {
	/**
	 * Logger registered for this class.
	 */
	private static Logger logger = LogManager.getLogger(LoggingDistributor.class);

	/**
	 * Map of all the distributors.
	 */
	private static Map<String, LoggingDistributor> distributors = new HashMap<>();

	/**
	 * Creates and registers a new distributor with given name.
	 * @param name Name of the distributor.
	 * @return Returns a new logging distributor.
	 */
	public static LoggingDistributor register(String name) {
		// Check if we already have one. If so, return it.
		if (distributors.containsKey(name))
			return distributors.get(name);

		// Else we create a new one.
		LoggingDistributor dstr = new LoggingDistributor();
		distributors.put(name, dstr);

		return dstr;
	};

	/**
	 * Adds display for distributor with supplied name.
	 * 
	 * @param name
	 *            Name of the distributor for which we want to add a new display.
	 * @param dsp
	 *            Display we want to save in named distributor.
	 */
	public static void addDisplay(String name, LoggingDisplay dsp) {
		if (distributors.containsKey(name)) {
			distributors.get(name).addDisplay(dsp);

			logger.info("Sucessfully set LoggingDisplay for logger with name: " + name);
		} else {
			logger.error("Unable to set LoggingDisplay for logger with name:  " + name);
		}

	}

	/**
	 * @return Returns names of stored distributors.
	 */
	public static String[] distributorNames() {
		return distributors.keySet().toArray(new String[distributors.size()]);
	}

	/**
	 * Display - the place where we send logs to be displayed
	 */
	private List<LoggingDisplay> displays = new LinkedList<LoggingDisplay>();

	/**
	 * 
	 * @return Return display of this distributor.
	 */
	public List<LoggingDisplay> getDisplays() {
		return displays;
	}

	/**
	 * Sets display to current distributor.
	 * 
	 * @param display
	 *            Display we want to set.
	 */
	public void addDisplay(LoggingDisplay display) {
		displays.add(display);
	}

	public LoggingDistributor() {

	};

	/**
	 * Receives and distributes a log.
	 * @param log The log.
	 */
	public void receiveLog(LogEvent log) {
		distribute(log);
	};

	/**
	 * Distributes the log to all receivers.
	 * @param e LogEvent.
	 */
	private void distribute(LogEvent e) {
		for (LoggingDisplay d : displays)
			d.receiveLog(e);
	}
}
