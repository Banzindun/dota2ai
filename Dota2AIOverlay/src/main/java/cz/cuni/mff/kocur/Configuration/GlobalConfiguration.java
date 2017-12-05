package cz.cuni.mff.kocur.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cz.cuni.mff.kocur.Exceptions.KeyNotFound;

public class GlobalConfiguration extends Configuration {
	/**
	 * Instance holder.
	 */
	private static GlobalConfiguration instance = null;
	
	// AND I need more configs for paths and for overlay settings that are not in cfg file ?? OR DO I? 
	
		
	/**
	 * @return Returns instance of {@link #GlobalConfiguration()}.
	 */
	public static GlobalConfiguration getInstance() {
		if (instance == null) instance = new GlobalConfiguration();
		return instance;	
	}	
	
	/**
	 * @return Returns true if {@link #GlobalConfiguration()} exists.
	 */
	public static boolean exists() {
		return (instance != null);
	}

	// Add ID instead of String
	private Map<String, BotConfiguration> botConfigurations = new HashMap<String, BotConfiguration>();
	
	/**
	 * Puts bot's configuration into {@link #botConfigurations} map.
	 * @param id
	 * @param cfg
	 */
	public void setBotCfg(BotConfiguration cfg) {
		if (cfg == null) return; // throw??
		System.out.println(cfg.getId());
		
		for (Entry<String,CItem> e : cfg.getConfiguration().entrySet()) {
			System.out.println(e.getKey());
			System.out.println(e.getValue().toString());	
		}
		
		botConfigurations.put(cfg.getId(), cfg);		
	}
	
	/**
	 * Returns bot configuration if loaded.
	 * @param id ID of bot.
	 * @return Returns configuration of bot with {@link #id}
	 * @throws KeyNotFound when configuration with {@link #id} is not found.
	 */
	public Configuration getBotCfg(String id) throws KeyNotFound {
		Configuration out = botConfigurations.get(id);
		if (out == null) throw new KeyNotFound("Bot configuration with " + id + " was not found.");
		return out;
	}
	
	public Map<String, BotConfiguration> getConfigurations() {
		return botConfigurations;		
	}
		
}
