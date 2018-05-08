package cz.cuni.mff.kocur.logging;

import org.apache.logging.log4j.core.LogEvent;

/**
 * Interface that represents a class, that can receive LogEvents.
 * 
 * @author kocur
 *
 */
public interface LoggingDisplay {

	/**
	 * Receives a LogEvent.
	 * 
	 * @param log
	 *            LogEvent.
	 */
	public void receiveLog(LogEvent log);

}
