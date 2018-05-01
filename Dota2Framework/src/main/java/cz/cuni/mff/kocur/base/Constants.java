package cz.cuni.mff.kocur.base;

/**
 * A few constants that are needed across the framework. 
 *  
 * @author kocur
 *
 */
public class Constants {
	/**
	 * Y in-game coordinate of the river's start. 
	 */
	public static final int leftRiverStartY = 2996;
	
	/**
	 * Y in-game coordinate of the river's end.
	 */
	public static final int rightRiverEndY = -2877; 
	
	
	/**
	 * Maximum height of tiles exported from the game. 
	 */
	public static final short maxHeight = 1000;

	/**
	 * Help that will be displayed during init state on the main panel.
	 */
	private static final String initHelp = "Waiting for the POST request, that will initiate the hero selection. ";

	/**
	 * Maximum number of heroes in one team.
	 */
	public static final int MAX_HEROES = 5;	

	/**
	 * 
	 * @return Returns the help that should be displayed during INIT state of the application.
	 */
	public static String getInitHelp() {
		return initHelp;
	}
}
