package cz.cuni.mff.kocur.console;

/**
 * Agents are controlled using the ControllerWrapper. That class holds 
 * their name, so the getControllableName is not needed for agent implementation. 
 * So we change the interface a little, so that the the base agent's class is 
 * cleaner for users. 
 * @author kocur
 *
 */
public interface ControllableAgent extends Controllable{
	
	@Override
	public default String getControllableName() {
		return null;
	}
}
