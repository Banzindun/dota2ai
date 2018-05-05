package cz.cuni.mff.kocur.server;

/**
 * Class that represents a server command. The server can tell the addon to pause, unpause etc.
 * @author kocur
 *
 */
public class ServerCommand implements Command{
	/**
	 * Issuer of this command. SERVER.
	 */
	protected Issuer issuer = Command.Issuer.SERVER;
	
	/**
	 * Codes the server commands can have. (PAUSE, UNPAUSE, CHAT)
	 * @author kocur
	 *
	 */
	enum COMMAND_CODE{
		NOOP,	// DO nothing
		PAUSE,	// Pause the game.
		UNPAUSE,	// Unpause the game.
		CHAT,	// Chat.
	    GAME_COMMANDS, // Game commands.
	    RESET // RESET THE GAME	
	}

	/**
	 * Code of the command.
	 */
	protected COMMAND_CODE command = COMMAND_CODE.NOOP;
	
	/**
	 * 
	 * @return Returns the command code.
	 */
	public COMMAND_CODE getCommand() {
		return command;
	}
	
	/**
	 * Sets the command code.
	 * @param c New command code.
	 */
	public void setCommand(COMMAND_CODE c) {
		command = c;
	}
	
	@Override
	public Issuer getIssuer() {
		return issuer;
	}

	@Override
	public void setIssuer(Issuer i) {
		issuer = i;		
	}




}
