package cz.cuni.mff.kocur.world;

import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import cz.cuni.mff.kocur.interests.BaseInterest;

/**
 * Class that extends the World. It contains interests and time, the is the only
 * difference. It is used for updates. Those are not done by World class,
 * because there might be some data, that the world should not contain
 * (interests, time).
 * 
 * @author kocur
 *
 */
public class WorldUpdate extends World {

	/**
	 * Map to which the information needed to update interests will be deserialized.
	 */
	@JsonProperty("interests")
	protected ConcurrentHashMap<Integer, BaseInterest> interests;

	/**
	 * Game time representation. Should be passed during big updates.
	 */
	@JsonProperty("time")
	protected float time = -1;

	/**
	 * Constructor.
	 */
	public WorldUpdate() {
		super();
	}

	/**
	 * Sets the interests. Used by Jackson.
	 * 
	 * @param interests
	 *            New interests.
	 */
	public void setInterests(ConcurrentHashMap<Integer, BaseInterest> interests) {
		this.interests = interests;
	}

	/**
	 * 
	 * @return Returns the interests.
	 */
	public ConcurrentHashMap<Integer, BaseInterest> getInterests() {
		return interests;
	}

	/**
	 * Sets the time.
	 * 
	 * @param time
	 *            Time.
	 */
	public void setTime(float time) {
		this.time = time;
	}

	/**
	 * 
	 * @return Returns the time, that is stored in this update.
	 */
	public float getTime() {
		return time;
	}

}
