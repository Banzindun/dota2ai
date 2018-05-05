package cz.cuni.mff.kocur.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.configuration.HeroConfiguration.TYPE;
import cz.cuni.mff.kocur.exceptions.ConfigurationTestFailureException;
import cz.cuni.mff.kocur.exceptions.KeyNotFound;
import cz.cuni.mff.kocur.interests.Team;

/**
 * This class holds configuration that should store values that are important on
 * the top level of the application. (Not dependent on bot etc.)
 * 
 * It should be loaded from config.cfg file.
 * 
 * This class implements singleton pattern.
 * 
 * @author Banzindun
 *
 */
public class FrameworkConfiguration extends Configuration {
	/**
	 * Instance holder.
	 */
	private static FrameworkConfiguration instance = null;

	/**
	 * Logger for this class.
	 */
	private static Logger logger = LogManager.getLogger(FrameworkConfiguration.class.getName());

	/**
	 * Map that will contain configuration items. These are items that store
	 * informations about how to display their configuration (as label, checkbox
	 * etc.).
	 */
	private Map<String, CItem> configuration = new HashMap<String, CItem>();

	/**
	 * @return Returns instance of framework configuration.
	 */
	@JsonCreator
	public static FrameworkConfiguration getInstance() {
		if (instance == null)
			instance = new FrameworkConfiguration();
		return instance;
	}

	/**
	 * Private constructor. Private for singleton pattern. Must be inaccessible.
	 */
	private FrameworkConfiguration() {
		super();
	}

	/**
	 * @return Returns true if {@link #instance} exists.
	 */
	public static boolean exists() {
		return (instance != null);
	}

	/**
	 * True if the bots were loaded.
	 */
	public static boolean botsLoaded = false;

	/**
	 * Set after app starts to run. It is equal to number of loaded and running
	 * bots.
	 */
	public static int numberOfBots = 0;

	/**
	 * Stores dire hero configurations. They are stored here because they should be
	 * accessible in more than one place during CONFIGURATION phase.
	 */
	@JsonIgnore
	private LinkedHashMap<String, HeroConfiguration> direConfigurations = new LinkedHashMap<String, HeroConfiguration>();

	/**
	 * Stores radiant hero configurations. They are stored here because they should
	 * be accessible in more than one place during CONFIGURATION phase.
	 */
	@JsonIgnore
	private LinkedHashMap<String, HeroConfiguration> radiantConfigurations = new LinkedHashMap<String, HeroConfiguration>();

	/**
	 * Puts agent's configuration into map.
	 * 
	 * @param cfg
	 *            Configuration of bot that should be setup.
	 * @param teamNumber
	 *            Team number of the agent.
	 */
	public void addBotCfg(HeroConfiguration cfg, int teamNumber) {
		if (cfg == null) {
			logger.warn("I cannot save null to bot configurations.");
		}
		;

		logger.info("Adding new bot configuration: " + cfg.getName());

		if (teamNumber == Team.DIRE)
			direConfigurations.put(cfg.getName(), cfg);
		else if (teamNumber == Team.RADIANT) {
			radiantConfigurations.put(cfg.getName(), cfg);
		} else {
			logger.error("Unknown team number.");
		}
	}

	/**
	 * 
	 * @param name
	 *            Name of the configuration
	 * @return True if botConfigurations contain this configuration.
	 */
	public boolean containsBotCfg(String name) {
		if (radiantConfigurations.containsKey(name) || direConfigurations.containsKey(name))
			return true;

		return false;
	}

	/**
	 * Returns bot configuration if loaded.
	 * 
	 * @param name
	 *            Name of the bot.
	 * @return Returns configuration of bot with given name.
	 * @throws KeyNotFound
	 *             when configuration with given name is not found.
	 */
	public Configuration getBotCfg(String name) throws KeyNotFound {
		Configuration c = radiantConfigurations.get(name);

		if (c == null)
			c = direConfigurations.get(name);

		if (c == null)
			throw new KeyNotFound("Bot configuration with " + name + " was not found.");

		return c;
	}

	@Override
	public String toString() {
		IndentationStringBuilder builder = new IndentationStringBuilder();
		builder.appendMap(items);
		builder.appendMap(configuration);
		return builder.toString();
	}

	/**
	 * 
	 * @return Returns array of all dire agent configurations.
	 */
	public HeroConfiguration[] getDireConfigurationsInArray() {
		int size = direConfigurations.size();
		return direConfigurations.values().stream().collect(Collectors.toList()).toArray(new HeroConfiguration[size]);
	}

	/**
	 * 
	 * @return Returns array of all radiant agent configurations.
	 */
	public HeroConfiguration[] getRadiantConfigurationsInArray() {
		int size = radiantConfigurations.size();
		return radiantConfigurations.values().stream().collect(Collectors.toList())
				.toArray(new HeroConfiguration[size]);
	}

	/**
	 * Returns configuration value.
	 * 
	 * @param name
	 *            of the field you want to retrieve
	 * @return return value of the specified field
	 */
	public String getConfigValue(String name) {
		CItem item = configuration.get(name);
		if (item == null) {
			logger.error("Can not find [" + name + "] in items.");
			return null;
		}
		return item.getValue();
	}

	/**
	 * Returns CItem that is stored inside configuration on field with supplied
	 * name.
	 * 
	 * @param name
	 *            Name of the variable we want to get.
	 * @return Returns the CItem associated with supplied name or null if it wasnt
	 *         found.
	 */
	public CItem getConfigItem(String name) {
		return configuration.get(name);
	}

	/**
	 * Sets CItem to configuration field with supplied name.
	 * 
	 * @param name
	 *            Name of the field that should be filled up.
	 * @param value
	 *            CItem that should be stored on the field with supplied name.
	 */
	public void setConfigItem(String name, CItem value) {
		configuration.put(name, value);
	}

	/**
	 * Sets the value of item with name inside the configuration map (in JSON under
	 * the "configuration")
	 * 
	 * @param name
	 *            Name of the variable inside configuration.
	 * @param value
	 *            New value.
	 */
	public void setConfigValue(String name, String value) {
		configuration.get(name).setValue(value);
		;
	}

	/**
	 * Returns map that stores the configuration of this bot,
	 * 
	 * @return Configuration map. (keys and values as in JSON specified in
	 *         "configuration": [..])
	 */
	public Map<String, CItem> getConfiguration() {
		return configuration;
	}

	/**
	 * Sets configuration to the map supplied below.
	 * 
	 * @param configuration
	 *            New configuration of this bot.
	 */
	public void setConfiguration(Map<String, CItem> configuration) {
		this.configuration = configuration;
	}

	/**
	 * Removes bot configuration from botConfigurations.
	 * 
	 * @param botCfg
	 *            Reference to bot configuration that should be removed.
	 * @return True if the configuration existed inside GlobalConfiguration, false
	 *         otherwise.
	 */
	public boolean removeBotCfg(HeroConfiguration botCfg) {
		boolean result = false;

		result = radiantConfigurations.remove(botCfg.getName()) != null;

		if (result == true)
			return result;

		result = direConfigurations.remove(botCfg.getName()) != null;

		return result;
	}

	/**
	 * Test the configuration for incorrect fields etc.
	 * 
	 * @throws ConfigurationTestFailureException
	 *             if the test failed.
	 * 
	 */
	public void test() throws ConfigurationTestFailureException {
		if (items == null) {
			throw new ConfigurationTestFailureException("Items that were set to this configuration are null.");
		}

		if (items.size() == 0) {
			throw new ConfigurationTestFailureException("Items that were set to this configuration are empty.");
		}

		// Test the configuration fields:
		String msg = ConfigurationTesting.containsKeys(configuration,
				new String[] { "dota2_path", "port", "hero_time_to_live", "basenpc_time_to_live", "creep_time_to_live",
						"tower_time_to_live", "building_time_to_live", "game_event_cleanup_period",
						"framework_event_cleanup_period", "save_incoming", "save_outcoming" });

		if (msg.length() != 0) {
			throw new ConfigurationTestFailureException(msg);
		}
	}

	/**
	 * 
	 * @return Returns array of all hero configuration.
	 */
	public ArrayList<HeroConfiguration> getHeroConfigurations() {
		ArrayList<HeroConfiguration> result = new ArrayList<>();

		for (HeroConfiguration c : getRadiantConfigurationsInArray())
			result.add(c);

		for (HeroConfiguration c : getDireConfigurationsInArray())
			result.add(c);

		return result;
	}

	@JsonIgnore
	public HeroConfiguration[] getHeroConfigurations(int team) {
		if (team == Team.RADIANT)
			return getRadiantConfigurationsInArray();
		else if (team == Team.DIRE)
			return getDireConfigurationsInArray();

		// Else return null
		return null;
	}

	/**
	 * 
	 * @param t
	 *            Type of the configuration (AI, BOT, PLAYER).
	 * @param teamNumber
	 *            Number of team.
	 * @return Returns count of configuration with given type and team number.
	 */
	public int countHeroConfigurations(TYPE t, int teamNumber) {
		int count = 0;
		for (HeroConfiguration c : this.getHeroConfigurations(teamNumber)) {
			if (c.getType() == t)
				count++;
		}

		return count;
	}

	@JsonIgnore
	public LinkedHashMap<String, HashMap<String, String>> getHeroConfigurationsSignature() {
		LinkedHashMap<String, HashMap<String, String>> bs = new LinkedHashMap<>();

		for (HeroConfiguration c : this.getHeroConfigurations()) {
			bs.put(c.getName(), c.getSignature());
		}

		return bs;
	}
}
