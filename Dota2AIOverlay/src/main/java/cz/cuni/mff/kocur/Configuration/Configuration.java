package cz.cuni.mff.kocur.Configuration;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.kocur.Exceptions.ConversionError;
import cz.cuni.mff.kocur.Exceptions.KeyNotFound;
import cz.cuni.mff.kocur.Exceptions.LoadingError;

public class Configuration {
	/**
	 * 
	 */
	protected Map<String, CItem> configuration = new HashMap<String, CItem>();

	public Configuration() {}
	
	
	/**
	 * 
	 * @param items
	 */
	public Configuration(Map<String, CItem> items) {
		this.configuration = items;
	}
		
	
	/**
	 * 
	 * @param path
	 * @throws LoadingError 
	 */
	public void load(String path) throws LoadingError {
		ConfigurationLoader loader = new ConfigurationLoader(path);
		loader.load(this);
	}
	
	/**
	 * 
	 * @param path
	 * @throws LoadingError
	 */
	public void save(String path) throws LoadingError {
		ConfigurationSaver saver = new ConfigurationSaver();
		saver.save(this, path);		
	}
	
	
	/**
	 * 
	 * @param map
	 */
	public void loadItems(Map<String, CItem> map) {
		configuration = map;
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<String, CItem> getItems(){
		return configuration;		
	}

	
	/**
	 * Sets value of item in items to appropriate value.
	 * @param name Name of variable.
	 * @param value Value of variable of type String.
	 */
	public void set(String name, CItem value) {
		configuration.put(name, value);
	}
	
	/**
	 * Should find the named item and cast it to type T.
	 * @param name Name of variable.
	 * @param cl Class of output variable. 
	 * @return Returns value of variable with a given name, that is casted to type cl. 
	 * @throws ConversionError If Conversion fails.
	 * @throws KeyNotFound 
	 */
	public <T> T getItem(String name, Class<T> cl) throws ConversionError, KeyNotFound {
		if (cl == null) return null;
		CItem item = configuration.get(name);
		if (item == null) throw new KeyNotFound("Can not find [" + name + "] in items.");
		if (cl.isInstance(item)) return (T) item.getValue();
		else throw new ConversionError("Couldn't convert [" + name + " = " + configuration.get(name) + "] to " + cl.toString());
	}

	/**
	 * Should return the value of variable with {@link #name} represented as a string.
	 * 
	 * @param name Name of the variable.
	 * @return Returns the value of variable with specified name.
	 * @see {@link #getItem(String, Class)}
	 */
	public CItem getItem(String name) {
		return configuration.get(name);
	}
	
	
}
