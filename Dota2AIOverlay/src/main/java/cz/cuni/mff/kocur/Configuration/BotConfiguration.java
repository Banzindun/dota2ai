package cz.cuni.mff.kocur.Configuration;

import java.util.Map;


public class BotConfiguration extends Configuration {
	private Map<String, CItem> configuration;
	
	
	private String id;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, CItem> getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Map<String, CItem> configuration) {
		this.configuration = configuration;
	}
}
