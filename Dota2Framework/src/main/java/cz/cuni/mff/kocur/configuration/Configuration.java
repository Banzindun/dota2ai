package cz.cuni.mff.kocur.configuration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.cuni.mff.kocur.exceptions.ConfigurationSavingError;
import cz.cuni.mff.kocur.exceptions.ConfigurationTestFailureException;
import cz.cuni.mff.kocur.exceptions.LoadingError;

/**
 * Abstract configuration.
 * Adds some basic methods for retrieving configuration values etc.
 * @author kocur
 *
 */
public abstract class Configuration {
	/**
	 * Register logger for this class.
	 */
	protected Logger logger = LogManager.getLogger(Configuration.class.getName());
	
	/**
	 * Map that stores pairs that are specified in JSON file as pairs "name" : "value".
	 */
	protected Map<String, String> items = new HashMap<String, String>();
	
	/**
	 * Path from where this configuration comes from.
	 */
	protected String path; 
	
	/**
	 * List of ConfigurationChangeListener that listen for change of the configuration.
	 * They will be alerted after the {@link #onChange()} method is called.
	 */
	@JsonIgnore
	protected LinkedList<ConfigurationChangeListener> waiting = new LinkedList<>();
	
	/**
	 * Adds the listener that will be alerted on configuration change.
	 * @param l Listener that should be added to waiting list.
	 */
	public void addChangeListener(ConfigurationChangeListener l) {
		waiting.add(l);
	}
	
	/**
	 * Removes the specified listener from waiting list of objects that are waiting for configuration change.
	 * @param l Listener that should be removed.
	 * @return Returns true if the listener was successfully removed.
	 */
	public boolean removeChangeListener(ConfigurationChangeListener l) {
		return waiting.remove(l);
	}
	
	/**
	 * Sample constructor.
	 */
	public Configuration() {
			
	}
	
	
		
	/**
	 * Constructs configuration from supplied items.
	 * @param items Map with keys and values giving fields in shape "name": "value" in JSON file.
	 */
	public Configuration(String path, Map<String, String> items) {
		this.path = path;
		this.items = items;
		
	}
	
	/**
	 * Saves the configuration to file.
	 */
	public void save(){
		logger.info("Saving the configuration");
		ConfigurationSaver saver = new ConfigurationSaver();
		
		if (path == null || path.length() == 0) {
			logger.error("Path is not supplied.");
			return;
		}
		
		try {
			saver.save(this, path);
		} catch (ConfigurationSavingError e) {
			logger.error("Unable to save configuration.", e);
		}		
	}
	
	/**
	 * Sets the path of this configuration.
	 * @param path Sets the path.
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * Returns the path of this configuration.
	 * @return Path to this configuration.
	 */
	@JsonIgnore
	public String getPath() {
		return path;
	}
	
	
	/**
	 * Sets items to be equal to the passed argument. 
	 * @param items Map of new {@link #items}
	 */
	public void setItems(Map<String, String> items) {
		this.items = items;
	}
	
	/**
	 * 
	 * @return pairs of names and values of this configuration.
	 */
	@JsonAnyGetter
	public Map<String, String> getItems(){		
		return items;		
	}

	
	/**
	 * Sets value of record with given name to appropriate value.
	 * @param name of variable.
	 * @param value of variable of type String.
	 */
	@JsonAnySetter
	public void setItem(String name, String value) {
		if (name == null) return;
	
		if (value == null)
			items.put(name.toLowerCase(), null);
		else if (value != null) 
			items.put(name.toLowerCase(), value.toLowerCase());
		
	}
	
	/**
	 * 
	 * @param name name of the field which value we want to know
	 * @return value of the field with given name, null if not found
	 */
	public String getItem(String name){
		String item = items.get(name.toLowerCase());
		
		if (item == null) {
			logger.warn("I have not found item with name: " + name.toLowerCase());
		}
		return item;
	}
	
	
	/**
	 * Method for classes that implement configuration fields. 
	 * This method should return the value associated with given field name.
	 * @param name Name of the field whose value you want to retrive.
	 * @return Returns the value associated with given field or null.
	 */
	public String getConfigValue(String name){
		return null;
	}
	
	/**
	 * Gets the whole config item associated with given name or null. 
	 * @param name Name of the field. 
	 * @return Returns the item associated with given name.
	 */
	public CItem getConfigItem(String name) {
		return null;
	}
	
	/**
	 * Sets configuration item on given field.
	 * @param name Name of the item. 
	 * @param value The item that should be stored.
	 */
	public void setConfigItem(String name, CItem value) {}
	
	/**
	 * Sets the value inside configuration field with given name.
	 * @param name Name of the configuration field.
	 * @param value Its new value.
	 */
	public void setConfigValue(String name, String value) {}
	
	
	/**
	 * Returns map that stores the configuration of this bot,
	 * @return Configuration map. (keys and values as in JSON specified in "configuration": [..])
	 */
	public Map<String, CItem> getConfiguration() {
		return null;
	}
	
	/**
	 * Should be called after there was a change in the configuration fields. </br>
	 * This method allerts all the listeners that there was a change in configuration. 
	 */
	public void onChange() {
		for (ConfigurationChangeListener l : waiting) {
			l.configurationChanged();
		}
	}
	
	/**
	 * Test the configuration for incorrect fields etc.	
	 * @return returns result that says if the test passed and supplies message
	 */
	public void test() throws ConfigurationTestFailureException{
		ConfigurationTestResult result = new ConfigurationTestResult();
		result.setPassed(true);
		
		if (items == null) {
			ConfigurationTestFailureException ex = new ConfigurationTestFailureException("Items that were set to this configuration are null.");
			throw ex; 
		}
	}	
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();

		for (Entry<String, String> i : items.entrySet()) {
			out.append("Name of this configuration is: " + i.getKey() + "\n");
			out.append(i.getValue().toString());
		}
		
		return out.toString();
	}
	
	
}
