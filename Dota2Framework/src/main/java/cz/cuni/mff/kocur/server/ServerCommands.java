package cz.cuni.mff.kocur.server;

/**
 * Class that creates a few commands a server can use.
 * 
 * @author kocur
 *
 */
public final class ServerCommands {
	/**
	 * Class that represents a Pause command. This command should be sent if the
	 * framework wants to pause the game.
	 * 
	 * @author kocur
	 *
	 */
	public static class Pause extends ServerCommand {
		/**
		 * Constructor.
		 */
		public Pause() {
			command = COMMAND_CODE.PAUSE;
		}
	}

	/**
	 * Class that represents a Unpause command. This command should be sent if the
	 * framework wants to unpause the game.
	 * 
	 * @author kocur
	 *
	 */
	public static class Unpause extends ServerCommand {
		public Unpause() {
			command = COMMAND_CODE.UNPAUSE;
		}
	}

	/**
	 * Class that represents a command, that sends a array of commands to server. 
	 * These commands are then executed inside the game's console.
	 * 
	 * @author kocur
	 *
	 */
	public static class GameCommand extends ServerCommand {
		/**
		 * Array of commands.
		 */
		private String[] commands = null;

		/**
		 * Constructor.
		 * @param cmds Array of commands.
		 */
		public GameCommand(String[] cmds) {
			command = COMMAND_CODE.GAME_COMMANDS;
			commands = cmds;
		}

		/**
		 * 
		 * @return Returns a array of game commands.
		 */
		public String[] getCommands() {
			return commands;
		}

		/**
		 * Sets the array of commands.
		 * @param commands New array of commands.
		 */
		public void setCommands(String[] commands) {
			this.commands = commands;
		}
	}
}