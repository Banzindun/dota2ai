package cz.cuni.mff.kocur.bot;

/**
 * Class with all the roles a player can play.
 * @author kocur
 *
 */
public class Roles {
	/**
	 * Carries the team, should have big farm etc.
	 * Should be protected at all costs.
	 */
	public static final int CARRY = 0; 
	
	/**
	 * Should disable the enemies carry.
	 */
	public static final int DISABLER = 1;
	
	/**
	 * Initiates the fights. 
	 */
	public static final int INITIATOR = 2;
	
	/**
	 * Farms a lot in jungle.
	 */
	public static final int JUNGLER = 3;

	/**
	 * Support helps carry. Collects bounties etc.
	 */
	public static final int SUPPORT = 4;
	
	/**
	 * Can take a lot of damage.
	 */
	public static final int DURABLE = 5;
	
	/**
	 * Tons of damage in short time.
	 */
	public static final int NUKER = 6;
	
	/**
	 * Pushes lanes fast.
	 */
	public static final int PUSHER = 7;
	
	/**
	 * Is able to escape easily.
	 */
	public static final int ESCAPE = 8;
	
}
