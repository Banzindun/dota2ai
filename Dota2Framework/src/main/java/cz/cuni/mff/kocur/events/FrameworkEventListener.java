package cz.cuni.mff.kocur.events;

/**
 * Interface for framework event listeners. They can listen for game or
 * framework events.
 * 
 * @author kocur
 *
 */
public interface FrameworkEventListener {

	/**
	 * Called after the event was triggered.
	 */
	public void triggered();

	/**
	 * Called after the event was triggered. There can be some passed arguments.
	 * 
	 * @param os
	 *            Objects.
	 */
	public void triggered(Object... os);

}
