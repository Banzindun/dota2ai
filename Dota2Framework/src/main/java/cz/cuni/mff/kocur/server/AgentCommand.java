package cz.cuni.mff.kocur.server;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class represents all the commands any agent can send to the addon. 
 * @author kocur
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentCommand implements Command{
	/**
	 * Who issues this command? This is server/agent. 
	 */
	protected Issuer issuer = Command.Issuer.AGENT;
	
	/**
	 * Codes of the command, that any agent can issue. (MOVE, ATTACK, CAST)
	 * @author kocur
	 *
	 */
	public enum COMMAND_CODE {
		NOOP,	// Do nothing
        MOVE,	// Move to target/location
        ATTACK,	// Attack a target
        CAST,	// Cast a spell
        BUY,	// Buy an item
        SELL,	// Sell an item
        USE_ITEM,	// Use an item
        SELECT, 	// Select a hero (sent during selection)
        PICKUP_RUNE,	// Pickup a rune
        GRAB_ALL,	// Grab all items from stash
        LEVELUP	// Levelup a ability
    }
	
	/**
	 * The code of this command. (UPDATE, MOVE)
	 */
	protected COMMAND_CODE command = COMMAND_CODE.NOOP;
    
    /**
     * Id of the bot that issued this command.
     */
    protected int id;
	
	/**
	 * 
	 * @return Returns the code of this command.
	 */
    public COMMAND_CODE getCommand() {
    	return command;
    };
    
    /**
     * 
     * @param code New code of this command.
     */
    protected void setCommand(COMMAND_CODE code) {
    	this.command = code;
    }
        
    /**
     * 
     * @param id New id of this command.
     */
    public void setId(int id) {
    	this.id = id;
    }
    
    /**
     * 
     * @return Returns id of the agent that issued this command.
     */
    public int getId() {
    	return id;
    }

	@Override
	public Issuer getIssuer() {
		return issuer;
	}

	@Override
	public void setIssuer(Issuer i) {
		issuer = i;
	};

}
