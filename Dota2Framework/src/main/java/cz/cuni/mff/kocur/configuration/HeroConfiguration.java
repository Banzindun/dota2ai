package cz.cuni.mff.kocur.configuration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import cz.cuni.mff.kocur.agent.AgentLoader;
import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.base.Pair;
import cz.cuni.mff.kocur.exceptions.ConfigurationTestFailureException;

/**
 * Class that stores bot configuration.
 * 
 * Bot configuration file is a JSON file with structure like this: { "name" :
 * "bot_lina", "configuration" : { "class": { "type": "CText", "label": "Please
 * type in class of your bot.", "help": "While starting server, the bot object
 * will be ..", "value": "kocur.lina.bot.Bot" } ...
 * 
 * Top level variables will get deserialized into items map inherited from
 * Configuration. Variables inside "configuration" will be deserialized into
 * {@link cz.cuni.mff.kocur.configuration.CItem} and stored in configuration
 * map.
 *
 * @author Banzindun
 *
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "heroType")
@JsonSubTypes({ @Type(name = "player", value = PlayerConfiguration.class),
		@Type(name = "ai", value = AiConfiguration.class), @Type(name = "bot", value = BotConfiguration.class) })
public abstract class HeroConfiguration extends Configuration {
	public enum TYPE {
		AI, PLAYER, BOT
	}

	@JsonIgnore
	protected TYPE type = TYPE.AI;

	/**
	 * Stores CItem variables that correspond to fields in JSON file under
	 * "configuration"
	 */
	protected Map<String, CItem> configuration = new HashMap<String, CItem>();

	/**
	 * Array with keys that must be stored inside "configuration".
	 */
	protected String[] configurationRequiredItemKeys = { "team" };

	/**
	 * Array with keys that are required by this configuration.
	 */
	protected String[] configurationRequiredKeys = { "heroType" };

	/**
	 * Unique identifier of the bot. (bot_lina etc.)
	 */
	protected String name;

	/**
	 * Type of the hero.
	 */
	protected String heroType;

	/**
	 * Constructor.
	 */
	public HeroConfiguration() {
		super();
	}

	/**
	 * 
	 * @return Returns the {@link #name} of the bot.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the bot.
	 * 
	 * @param name
	 *            Name of the configuration.
	 * 
	 */
	public void setName(String name) {
		this.name = name.toLowerCase();
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
		for (Entry<String, CItem> e : configuration.entrySet()) {
			setConfigValue(e.getKey(), e.getValue());
		}
	}

	/**
	 * Returns configuration value.
	 * 
	 * @param name
	 *            of the field you want to retrieve
	 * @return return value of the specified field
	 */
	public String getConfigValue(String name) {
		CItem item = configuration.get(name.toLowerCase());
		return item.getValue();
	}

	/**
	 * Sets CItem for supplied variable name.
	 * 
	 * @param name
	 *            Name of the variable (field name in JSON) you want to
	 *            replace/create
	 * @param value
	 *            CItem representing the configuration value.
	 */
	public void setConfigValue(String name, CItem value) {
		configuration.put(name.toLowerCase(), value);
	}

	/**
	 * Returns important values of this configuration and returns them in string
	 * array.
	 * 
	 * @return Important values of this configuration (so the information about this
	 *         config can be printed)
	 */
	@JsonIgnore
	public LinkedList<Pair<String, String>> getImportantValues() {
		LinkedList<Pair<String, String>> important = new LinkedList<>();

		return important;
	}

	/**
	 * Checks that the keys in this configuration contain all the required keys.
	 * Throws an error if not.
	 * 
	 * @throws ConfigurationTestFailureException
	 *             Throw, when there is a key, that is not contained in the
	 *             configuration.
	 */
	protected void checkKeysContained() throws ConfigurationTestFailureException {

		String msg = ConfigurationTesting.containsKeys(configuration, configurationRequiredItemKeys);

		if (msg.length() != 0) {
			ConfigurationTestFailureException ex = new ConfigurationTestFailureException(msg);
			throw ex;
		}

	}

	@Override
	public void test() throws ConfigurationTestFailureException {
		if (configuration.size() == 0)
			throw new ConfigurationTestFailureException("Configuration contains 0 elements.");

		checkKeysContained();

		// Check if this configuration has name
		if ((name == null || name.length() == 0))
			throw new ConfigurationTestFailureException("No name supplied.");

		// Try to load the bot, this is the final test and if this passes the
		// configuration should be ok
		if (!AgentLoader.canBeLoaded(this))
			throw new ConfigurationTestFailureException(
					"Bot could not be loaded. Make sure the class points to correct bot implementation.");

	}

	/*
	 * private static HeroConfiguration createConfigurationFromType(String heroType)
	 * { if (heroType.equals("bot")) return new BotConfiguration(); else if
	 * (heroType.equals("ai")) return new AiConfiguration(); else return new
	 * PlayerConfiguration(); }
	 */

	/**
	 * Returns the configuration printed in JSON. available to print from console
	 */
	@Override
	public String toString() {
		IndentationStringBuilder builder = new IndentationStringBuilder();
		builder.appendMap(items);
		builder.appendMap(configuration);
		return builder.toString();
	}

	@JsonIgnore
	public TYPE getType() {
		return type;
	}

	/**
	 * Sets a type of this configuration.
	 * 
	 * @param type
	 *            The type.
	 */
	public void setType(TYPE type) {
		this.type = type;
	}

	/**
	 * Adds required items to the items required by this configuration.
	 * 
	 * @param required
	 *            Array of required items, that should be added.
	 */
	public void addRequiredItems(String... required) {
		String[] newRequired = new String[configurationRequiredItemKeys.length + required.length];

		// Add our required keys
		int i = 0;
		for (String s : configurationRequiredItemKeys) {
			newRequired[i] = s;
			i++;
		}

		// Add the passed required keys
		for (String s : required) {
			newRequired[i] = s;
			i++;
		}

		configurationRequiredItemKeys = newRequired;
	}

	/**
	 * 
	 * @return Returns the type of this configuration.
	 */
	public String getHeroType() {
		return heroType;
	}

	@JsonSetter("heroType")
	public void setHeroType(String heroType) {
		this.heroType = heroType;
	}

	@JsonIgnore
	public HashMap<String, String> getSignature() {
		HashMap<String, String> values = new HashMap<String, String>();

		TYPE type = this.getType();
		values.put("type", type.name().toLowerCase());

		return values;
	}

}
