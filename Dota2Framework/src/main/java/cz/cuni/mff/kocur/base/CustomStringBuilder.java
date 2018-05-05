package cz.cuni.mff.kocur.base;

/**
 * This class is a simple custom wrapper around StringBuilder.
 * It supplies methods for appending, appending with new line at the end and appending multiple lines.
 * 
 * @author Banzindun
 *
 */
public class CustomStringBuilder{
	/**
	 * Line separator for this platform. 
	 */
	protected static String ls = System.getProperty("line.separator");
		
	/**
	 * Builder that builds the message of this test result.
	 */
	protected StringBuilder builder = new StringBuilder(); 
	
	/**
	 * Constructor.
	 */
	public CustomStringBuilder() {
		
	};

	/**
	 * Appends line to the builder.
	 * @param msg String that should be appended to the builder.
	 */
	public void append(String msg) {
		builder.append(msg);
	}
	
	/**
	 * Appends line to the builder and adds new line to the end. 
	 * @param msg String that should be appended with added new line at the end.
	 */
	public void appendLine(String msg) {
		builder.append(msg + ls);
	}
	
	/**
	 * Appends multiple lines, that are passed as array.
	 * @param lines Array of lines that should be inserted into the builder.
	 */
	public void appendLines(String... lines) {
		for (String l : lines) {
			appendLine(l);
		}
	}
	
	/**
	 * Takes a string, splits it by line separator and inserts the lines to builder.
	 * @param lines String to split. 
	 */
	public void appendLines(String lines) {
		appendLines(lines.split(ls));		
	}
	
	/**
	 * Appends passed string and comma behind it to builder.
	 * @param msg The string.
	 */
	public void appendComma(String msg) {
		append(msg + ", ");		
	}
		
	/**
	 * Clears the builder.
	 */
	public void clear() {
		builder = new StringBuilder();
	}
	
	/**
	 * Clears the builder and appends passed String on it.
	 * @param msg String that should be appended to builder after it is cleared.
	 */
	public void clear(String msg) {
		builder = new StringBuilder(msg);
	}
	
	/**
	 * Returns the message that was built.
	 */
	@Override
	public String toString() {
		return builder.toString();
	}
}
