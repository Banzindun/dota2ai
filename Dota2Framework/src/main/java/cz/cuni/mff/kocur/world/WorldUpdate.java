package cz.cuni.mff.kocur.world;

import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import cz.cuni.mff.kocur.interests.BaseInterest;

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
	
	public void setInterests(ConcurrentHashMap<Integer, BaseInterest> interests) {
		this.interests = interests;
	}
	
	public ConcurrentHashMap<Integer, BaseInterest> getInterests(){
		return interests;
	}
	
	
	public void setTime(float time) {
		this.time = time;
	}
	
	public float getTime() {
		return time;
	}
	
    public WorldUpdate() {
        super();
    }
}
