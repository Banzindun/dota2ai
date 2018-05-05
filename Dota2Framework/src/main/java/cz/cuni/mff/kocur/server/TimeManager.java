package cz.cuni.mff.kocur.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.interests.InterestsBase;

/**
 * Class that is supposed to handle time. It should be synchronized with the in-game time. The time should by update by team updates, that will contain the time information. That means the tick will be around on second. 
 * The goal of this class is to manage time depended events as spawning camps or runes.
 * @author kocur
 *
 */
public class TimeManager {
	/**
	 * Logger registers for TimeManager
	 */
	private static final Logger logger = LogManager.getLogger(TimeManager.class);

	/**
	 * We need to have a small time constant. We will delay the respawns by this
	 * constant, because it seems there is a small delay in game between the spawn
	 * time and the actual spawning.
	 * 
	 */
	public static float TIME_CONSTANT = 2;

	/**
	 * Last time we respawned camps.
	 */
	protected float lastCampRespawn = 0;
	
	/**
	 * Period of camps respawns.
	 */
	protected float campRespawnPeriod = 60; // 60 seconds

	/**
	 * Last time we spawned a rune.
	 */
	protected float lastRuneRespawn = 0;
	
	/**
	 * Period of rune respawns.
	 */
	protected float runeRespawnPeriod = 120; // 120 seconds

	/**
	 * When should the next respawn event occur.
	 */
	protected float nextEvent = 0;

	/**
	 * 90s is beginning of the game. The creeps spawn after that period and so do the events.
	 * 
	 */
	protected final static int preGameLength = 90;

	/**
	 * Game time.
	 */
	private static float gameTime = -1;

	/**
	 * Local time at which the game started.
	 */
	private static long localGameStartTime = 0;

	/**
	 * This is offset that will be created by pauses etc.
	 */
	private static float offset = 0;

	/**
	 * Time of the framework pause.
	 */
	private static long pauseStart = 0;

	/**
	 * 
	 * @return Returns a time that occured from the first time that the creeps were spawned.
	 */
	public static float getElapsedTime() {
		return gameTime - preGameLength;
	}

	/**
	 * 
	 * @return Returns game time.
	 */
	public static float getGameTime() {
		return gameTime;
	}

	/**
	 * 
	 * @return Returns local time, that corresponds to the moment when the game started.
	 */
	public static long getLocalGameStartTime() {
		return localGameStartTime;
	}

	/**
	 * Sets time when the game started in local time.
	 * @param time New local game start time.
	 */
	public static void setLocalGameStartTime(long time) {
		logger.info("Setting local game start time to : " + time);
		localGameStartTime = time;
	}

	/**
	 * 
	 * @return Returns current local time in relation to start of the game.
	 */
	public static float getLocalTime() {
		return (System.currentTimeMillis() - localGameStartTime) / 1000.0f + offset;
	}

	/**
	 * Constructor.
	 */
	public TimeManager() {

	}

	/**
	 * Ticks the clock. (Updates the game time)
	 * @param time New time.
	 */
	public void tick(float time) {
		// Check for first time set.
		if (gameTime == -1) {
			// First update, set time
			setLocalGameStartTime(System.currentTimeMillis());
		}

		gameTime = time;

		if (getElapsedTime() < 0)
			return;

		// Check for events
		if (getElapsedTime() > nextEvent + TIME_CONSTANT) {
			doTimeUpdates(getElapsedTime());
		}
	}

	/**
	 * Updates all the camps, runes etc. if the time is right.
	 * @param time Time (elapsed)
	 */
	public void doTimeUpdates(float time) {
		logger.debug("Time updates at time: " + time);

		if (updateCamps(time)) {
			logger.info(time + ": camps respawned.");
		}

		if (updateRunes(time)) {
			logger.info(time + ": runes respawned.");
		}

		if (updateHealers(time)) {
			logger.info(time + " healers respawned.");
		}

		// nextEvent will be camp respawn, it has smaller period than camp respawn
		nextEvent = lastCampRespawn + campRespawnPeriod;
	}

	/**
	 * Updates the camps if the time is right.
	 * @param time Time.
	 * @return Returns true if the camps were respawned.
	 */
	private boolean updateCamps(float time) {
		// Spawning should start after 60 seconds
		if (time < 60)
			return false;

		if (time - lastCampRespawn > campRespawnPeriod) {
			// I want lastCampRespawn to be divisible by runeRespawnPeriod
			lastCampRespawn = Math.floorDiv((int) time, (int) campRespawnPeriod) * campRespawnPeriod;

			InterestsBase.getInstance().respawnCamps();
			return true;
		}

		return false;
	}

	/**
	 * Respawns the runes, if the time is right.
	 * @param time Time. 
	 * @return Returns true if the runes were respawned.
	 */
	private boolean updateRunes(float time) {
		if (Math.floor(time) == 0 || time - lastRuneRespawn > runeRespawnPeriod) {
			// I want this to be divisible by runeRespawnPeriod
			lastRuneRespawn = Math.floorDiv((int) time, (int) campRespawnPeriod) * runeRespawnPeriod;

			logger.debug("Last rune respawn:" + lastRuneRespawn);
			InterestsBase.getInstance().respawnRunes(lastRuneRespawn);

			return true;
		}
		return false;
	}

	/**
	 * Respawns the shrines if the time is right.
	 * @param time Time.
	 * @return Returns true if the shrines were respawned.
	 */
	private boolean updateHealers(float time) {
		return InterestsBase.getInstance().respawnHealers(time);
	}

	/**
	 * 
	 * @return Returns the offset. That is a duration of the game pauses.
	 */
	public static float getOffset() {
		return offset;
	}

	/**
	 * Sets a new offset.
	 * @param offset New offset.
	 */
	public static void setOffset(float offset) {
		TimeManager.offset = offset;

	}

	/**
	 * Should be called after the game paused.
	 * Stores time of the pause. 
	 */
	public static void frameworkPaused() {
		pauseStart = System.currentTimeMillis();
		logger.info("Pausing the game at:" + (System.currentTimeMillis()) / 1000.0f + "ms");
	}

	/**
	 * Should be called after the framework was unpaused.
	 * Changes the offset.
	 */
	public static void frameworkUnpaused() {
		offset = offset + (System.currentTimeMillis() - pauseStart) / 1000.0f;
		logger.info("The pause took " + (System.currentTimeMillis() - pauseStart) / 1000.0f + "ms");
		logger.info("Currently all the pauses took in total: " + offset);
	}

}
