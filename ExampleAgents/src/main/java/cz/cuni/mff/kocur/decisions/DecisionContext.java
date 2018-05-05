package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.agent.AgentController;
import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;

/**
 * This class represents a context, that our decision will have. The context
 * represents some informations, that are crucial to make the decision. That can
 * be our target, source etc.
 * 
 * @author kocur
 *
 */
public class DecisionContext {
	/**
	 * Agent's context.
	 */
	ExtendedAgentContext agentContext = null;

	/**
	 * Source of this consideration 90% this will be hero.
	 */
	protected Location source = null;

	/**
	 * Target, that we are considering against. This can be bot, creep, healer etc.
	 */
	protected Target target = null;

	/**
	 * Time of the last execute() called on decision with this context.
	 */
	protected float lastExecution = -1;

	/**
	 * By what factor you want to multiply this decision. This can be used to give
	 * adgantage to some decisions.
	 */
	protected double bonusFactor = 1;

	/**
	 * 
	 * @return Returns agent's context.
	 */
	public ExtendedAgentContext getBotContext() {
		return agentContext;
	}

	public void setBotContext(ExtendedAgentContext botContext) {
		this.agentContext = botContext;
	}

	/**
	 * 
	 * @return Return's agent's controller.
	 */
	public AgentController getController() {
		return agentContext.getController();
	}

	/**
	 * 
	 * @return Returns source of the decision.
	 */
	public Location getSource() {
		return source;
	}

	public void setSource(Location source) {
		this.source = source;
	}

	/**
	 * 
	 * @return Returns target of a decision.
	 */
	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public double getBonusFactor() {
		return bonusFactor;
	}

	/**
	 * 
	 * @param bonusFactor
	 *            Bonus factor of the decision.
	 */
	public void setBonusFactor(double bonusFactor) {
		this.bonusFactor = bonusFactor;
	}

}
