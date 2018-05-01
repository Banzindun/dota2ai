package cz.cuni.mff.kocur.events;

/**
 * Build the game event. Sets its coordinates etc.
 * 
 * @author kocur
 *
 */
public class GameEventBuilder {
	
	/**
	 * 
	 * @return Returns new instance of GameEventsBuilder.
	 */
	public static GameEventBuilder build() {
		return new GameEventBuilder();
	}

	/**
	 * Game event we are creating.
	 */
	private GameEvent e = new GameEvent();

	/**
	 * Sets a location to given event.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param z Z coordinate.
	 * @return Returns reference to this object (for chaining purposes).
	 */
	public GameEventBuilder location(double x, double y, double z) {
		e.setX(x);
		e.setY(y);
		e.setZ(z);

		return this;
	}

	/**
	 * Sets id to event.
	 * @param id Id of the event.
	 * @return Returns reference to this object (for chaining purposes).
	 */
	public GameEventBuilder source(int id) {
		e.setId(id);
		return this;
	}

	/**
	 * 
	 * @return Returns the built GameEvent.
	 */
	public GameEvent get() {
		return e;
	}

	/**
	 * 
	 * @param team Number of team that caused this event.
	 * @return Returns reference to this object (for chaining purposes).
	 */
	public GameEventBuilder team(int team) {
		e.setTeam(team);
		return this;
	}

}
