package cz.cuni.mff.kocur.console;

import java.util.LinkedList;

/**
 * Class that stores issued commands to some extends. This is then used by
 * console to allow the user to choose from the command's history.
 * 
 * @author kocur
 *
 */
public class CommandHistory {

	/**
	 * Maximum number of stored commands.
	 */
	private static final int MAXCOMMANDS = 10;

	/**
	 * Index of our current command.
	 */
	private int index = -1;

	/**
	 * List of commands.
	 */
	LinkedList<ConsoleCommand> commands = new LinkedList<>();

	/**
	 * Constructor. 
	 */
	public CommandHistory() {

	}

	/**
	 * Adds a command to history.
	 * @param c Command.
	 */
	public void addCommand(ConsoleCommand c) {
		index = -1;

		if (commands.size() < MAXCOMMANDS)
			commands.addFirst(c);
		else {
			// Equal
			commands.removeLast();
			commands.addFirst(c);
		}
	}

	/**
	 * Gets a command from history.
	 * @return Returns a command from history.
	 */
	public ConsoleCommand getCommand() {
		if (index == -1)
			return new ConsoleCommand("");
		return commands.get(index);

	}

	/**
	 * Moves the command index up.
	 */
	public void up() {
		if (index < commands.size() - 1)
			index++;
	}

	/**
	 * Moves the command index down.
	 */
	public void down() {
		if (index >= 0)
			index--;
	}

}
