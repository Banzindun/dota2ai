package cz.cuni.mff.kocur.configuration;

import cz.cuni.mff.kocur.exceptions.ConfigurationTestFailureException;

/**
 * Configuration of player. It should extend a HeroConfiguration and
 * it should have some custom fields as class, roles, lane and a champion.
 * Those parameters are explained below:
 * 
 * 	class - class where is the agent's implementation stored (== it's controller)
 * 	logger_classpath - class for which we register our logger, preferably root of our project
 * 	roles - roles, that the AI plays
 *  lane - lane that the AI plays
 *  champion - name of the champion that the AI plays 	
 * @author kocur
 *
 */
public class PlayerConfiguration extends AiConfiguration {
	
	/**
	 * Constructor that sets the type of the player configuration and its required items.
	 */
	public PlayerConfiguration() {
		super();
		type = TYPE.HUMAN;
	}
	
	@Override
	public void test() throws ConfigurationTestFailureException{
		super.test();
		
		
	}
}
