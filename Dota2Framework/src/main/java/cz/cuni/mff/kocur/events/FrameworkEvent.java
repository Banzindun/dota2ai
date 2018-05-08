package cz.cuni.mff.kocur.events;

/**
 * This class represents a framework event. Typically this can be a big update
 * or chat event ..
 * 
 * @author kocur
 *
 */
public class FrameworkEvent extends Event {
	/**
	 * Type of the event.
	 */
	protected int type = 0;

	/**
	 * Name of the source of this event.
	 */
	protected String sourceName = "";

	/**
	 * 
	 * @return Returns name of the source, that caused this event.
	 */
	public String getSourceName() {
		return sourceName;
	}

	/**
	 * Sets a new source name.
	 * 
	 * @param sourceName
	 *            New source name.
	 */
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	/**
	 * 
	 * @return Returns the type of this event.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets new type of this event.
	 * 
	 * @param type
	 *            New type.
	 */
	public void setType(int type) {
		this.type = type;
	}

}
