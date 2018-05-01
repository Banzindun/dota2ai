package cz.cuni.mff.kocur.events;

/**
 * Abstract class that represents event. Event can be a framework or game event.
 * This class represents methods that are inherent to both classes.
 * 
 * @author kocur
 *
 */
public abstract class Event {
	/**
	 * Name of this event.
	 */
	protected String name = "";

	/**
	 * When was this event triggered.
	 */
	protected long time = 0;

	/**
	 * Time of this event to live in milliseconds. We want to keep events for the
	 * time for which they might be relevant.
	 */
	protected long toLive = 10000;

	/**
	 * Constructor.
	 */
	public Event() {
		time = System.currentTimeMillis();
	}

	/**
	 * 
	 * @return Returns the time, when was this event created.
	 */
	public long getTime() {
		return time;
	}

	/**
	 * 
	 * @return Returns true if this event has became irrelevant, then it might be
	 *         removed.
	 */
	public boolean isIrrelevant() {
		return (System.currentTimeMillis() - time) > toLive;
	}

	/**
	 * 
	 * @return Returns how long this event will be stored inside event base.
	 */
	public long getToLive() {
		return toLive;
	}

	/**
	 * 
	 * @param t
	 *            New "time to live" of this event.
	 */
	public void setTimeToLive(long t) {
		this.toLive = t;
	}

	/**
	 * 
	 * @return Returns the name of this event.
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 *            New name of this event.
	 */
	public void setName(String name) {
		this.name = name;
	}

}
