package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.agent.BotFunction;
import cz.cuni.mff.kocur.agent.EntityParameter;
import cz.cuni.mff.kocur.interests.Team;

/**
 * Our extension of entity parameter. We add propagation functions to them,
 * that are then used during influence spreading.
 * 
 * We define propagation functions for teams and entity classes.
 * 
 * @author kocur
 *
 */
public class EntityParameterWithInfluence extends EntityParameter {

	/**
	 * Dire propagation function.
	 */
	private BotFunction<?, ?> direFunction = null;
	
	/**
	 * Radiant propagation function.
	 */
	private BotFunction<?, ?> radiantFunction = null;

	/**
	 * Neutral propagation function.
	 */
	private BotFunction<?, ?> neutralFunction = null;

	/**
	 * Sets a function.
	 * @param f Function.
	 */
	public void setFunction(BotFunction<?, ?> f) {
		radiantFunction = f;
	}

	/**
	 * Sets a function for team.
	 * @param f Function.
	 * @param team Team number.
	 */
	public void setFunction(BotFunction<?, ?> f, int team) {
		if (team == Team.DIRE)
			direFunction = f;
		else if (team == Team.RADIANT) {
			radiantFunction = f;
		} else {
			neutralFunction = f;
		}
	}

	/**
	 * Gets function for a team.
	 * @param team Team number.
	 * @return Returns function for a given team.
	 */
	public BotFunction<?, ?> getFunction(int team) {
		if (team == Team.DIRE)
			return direFunction;
		else if (team == Team.RADIANT) {
			return radiantFunction;
		}

		return neutralFunction;
	}

	public BotFunction<?, ?> getFunction() {
		return radiantFunction;
	}

}
