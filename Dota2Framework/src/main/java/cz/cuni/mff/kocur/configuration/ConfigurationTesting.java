package cz.cuni.mff.kocur.configuration;

import java.util.Map;

/**
 * Sample class that has static functions that should make testing configurations easier.
 * @author kocur
 *
 */
public class ConfigurationTesting {
	/**
	 * System dependent new line symbol.
	 */
	static String nl = System.getProperty("line.separator");
	
	/**
	 * Tests the map if it contains the supplied keys. Returns the test result as string.
	 * @param map Map which should be tested.
	 * @param keys Keys that the map should contain.
	 * @return Returns the test result. Should be empty if the result passed.
	 */
	public static String containsKeys(Map<String, ?> map,String[] keys ) {
		StringBuilder result = new StringBuilder();
		
		for (String key : keys) {
			if (!map.containsKey(key.toLowerCase())) {
				result.append("Missing key: " + key + nl);
			};
		}
		
		return result.toString();
	}
	
	
}
