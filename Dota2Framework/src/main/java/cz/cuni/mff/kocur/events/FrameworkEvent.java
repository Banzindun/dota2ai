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
	 * 
	 * @return Returns the type of this event.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets new type of this event.
	 * @param type New type.
	 */
	public void setType(int type) {
		this.type = type;
	}

}
