package cz.cuni.mff.kocur.console;

/**
 * Controllable interface. Has methods for commanding the class that implements
 * the interface.
 * 
 * @author kocur
 *
 */
public interface Controllable {
	
	/**
	 * Takes a command, handles it and the result should be returned.
	 * @param cmd Console command.
	 * @return Returns command's response.
	 */
	public CommandResponse command(ConsoleCommand cmd);

	/**
	 * 
	 * @return Returns this controllable's help.
	 */
	public String getHelp();

	/**
	 * 
	 * @return Returns this controllable's name.
	 */
	public String getControllableName();

}
