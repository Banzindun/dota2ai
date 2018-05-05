package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.agent.BotFunction;
import cz.cuni.mff.kocur.agent.InterestParameter;

/**
 * Class that represents a parameter, an interest object can use to spread its
 * influence etc.
 * 
 * @author kocur
 *
 */
public class InterestParameterWithInfluence extends InterestParameter {

	/**
	 * Propagation function.
	 */
	private BotFunction<?, ?> bf = null;

	public void setFunction(BotFunction<?, ?> f) {
		bf = f;
	}

	public BotFunction<?, ?> getFunction() {
		return bf;
	}

}
