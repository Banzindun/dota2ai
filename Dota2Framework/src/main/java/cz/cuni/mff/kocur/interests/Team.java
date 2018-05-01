package cz.cuni.mff.kocur.interests;

/**
 * Class for representing a team identificators.
 * 
 * @author kocur
 *
 */
public class Team {
	public static int NONE = -1;
	public static int RADIANT = 2; // Goodguys
	public static int DIRE = 3; // Badguys
	public static int NEUTRAL = 4; // Badguys

	/**
	 * 
	 * @param team
	 *            Team number.
	 * @return Returns type of team, that is opposite to this one.
	 */
	public static int getOpositeTeam(int team) {
		if (team == RADIANT)
			return DIRE;

		return RADIANT;
	}

	/**
	 * 
	 * @param configValue
	 *            String to be parsed containing one of DIRE, RADIANT, NEUTRAL.
	 * @return Returns integer corresponding to team's number.
	 */
	public static int parseTeam(String configValue) {
		if (configValue.equals("DIRE"))
			return DIRE;

		if (configValue.equals("RADIANT"))
			return RADIANT;

		if (configValue.equals("NEUTRAL"))
			return NEUTRAL;

		return NONE;

	}
}
