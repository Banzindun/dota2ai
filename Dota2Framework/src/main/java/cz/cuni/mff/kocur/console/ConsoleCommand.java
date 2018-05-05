package cz.cuni.mff.kocur.console;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Class that represents console command. The command is given as a string of
 * arguments separated by spaces. The command can be written to file by using
 * "fileName".
 * 
 * @author kocur
 *
 */
public class ConsoleCommand {
	/**
	 * List of command fields. (string separated by space)
	 */
	LinkedList<String> fields;

	/**
	 * String representation of the command.
	 */
	private String cmd;

	/**
	 * If we should write the result to file, then the appropriate fileName.
	 */
	private String fileName = null;

	/**
	 * Constructs the command from supplied string. Splits the input string around
	 * spaces.
	 * 
	 * @param cmd
	 *            string command
	 */
	public ConsoleCommand(String cmd) {
		this.cmd = cmd;

		fields = new LinkedList<String>(Arrays.asList(cmd.trim().toLowerCase().split("\\s+")));

		int i = 0;
		for (String f : fields) {
			if (f.equals(">") && i == fields.size() - 2) {
				// We likely want to write to file
				// Take file name and remove two last fields
				fileName = fields.removeLast();
				fields.removeLast();
				break;
			}
			i++;
		}
	}

	/**
	 * 
	 * @return Returns name of the file to which this command should be written.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets fields of this command.
	 * 
	 * @param fields
	 *            Array of new fields.
	 */
	public void setFields(String[] fields) {
		this.fields = new LinkedList<String>(Arrays.asList(fields));
	}

	/**
	 *
	 * @return Returns a first field and removes it from command's fields.
	 */
	public String getField() {
		if (fields != null && fields.size() > 0)
			return fields.removeFirst();
		return "";
	}

	/**
	 * 
	 * @return Peeks the first field. (doesn't remove it)
	 */
	public String peekField() {
		return fields.peek();
	}

	/**
	 * 
	 * @return Returns the fields stored inside this console command.
	 */
	public String[] getFields() {
		return fields.toArray(new String[fields.size()]);
	}

	/**
	 * 
	 * @return Size of this command. (number of fields)
	 */
	public int size() {
		return fields.size();
	}

	public String toString() {
		return cmd;
	}
}
