package cz.cuni.mff.kocur.base;

import java.util.LinkedList;

/**
 * Some utility functions used on more places throughout the project.
 * @author kocur
 *
 */
public class Utils {
	/**
	 * Parses the string that looks like: [12.0001, -123.45] to array of doubles.
	 * @param str String to be parsed.
	 * @return Returns array of doubles, that were parsed from the input string or null.
	 */
	public static Double[] parseDoubleCoordiantes(String str) {
		if (str.length() < 2)
			return null;
		
		// Split fields around ","
		String[] fields = str.substring(1, str.length()-1).replaceAll("\\s", "").split(",");
		LinkedList<Double> nums = new LinkedList<>();
		
		// Go through field, parse the numbers
		for (String f : fields) {
			try {
				nums.add(Double.parseDouble(f));
			} catch(NumberFormatException e) {
				return null;
			}
		}
		
		// Return the numbers in array
		return nums.toArray(new Double[fields.length]);
	}

	/**
	 * Parses the string that looks like: [12, -123] to array of ints.
	 * @param str String to be parsed.
	 * @return Returns array of ints, that were parsed from the input string or null.
	 */
	public static Integer[] parseIntCoordiantes(String str) {
		if (str.length() < 2)
			return null;

		String chopped = "";
		if (str.contains("["))
			chopped = str.substring(1, str.length()-1);
		else 
			chopped = str;
		
		// Split fields around ","
		String[] fields = chopped.replaceAll("\\s", "").split(",");
		LinkedList<Integer> nums = new LinkedList<>();
		
		// Go through field, parse the numbers
		for (String f : fields) {
			try {
				nums.add(Integer.parseInt(f));
			} catch(NumberFormatException e) {
				return null;
			}
		}
		
		// Return the numbers in array
		return nums.toArray(new Integer[fields.length]);
	}
	
	/**
	 * Takes a string in form "sdfsdfdf, sdfsdf, sdfds". Removes spaces and than splits around commas.
	 * @param str String that should be parsed.
	 * @return Returns array of strings. 
	 */
	public static String[] parseArrayOfStrings(String str) {
		return str.replaceAll("\\s", "").replace(",$", "").split(",");		
	}
}
