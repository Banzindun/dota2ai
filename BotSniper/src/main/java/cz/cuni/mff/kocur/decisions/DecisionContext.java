package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.bot.AgentController;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;

public class DecisionContext {
	ExtendedBotContext botContext = null;

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

	public ExtendedBotContext getBotContext() {
		return botContext;
	}

	public void setBotContext(ExtendedBotContext botContext) {
		this.botContext = botContext;
	}

	public AgentController getController() {
		return botContext.getController();
	}

	public Location getSource() {
		return source;
	}

	public void setSource(Location source) {
		this.source = source;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public double getBonusFactor() {
		return bonusFactor;
	}

	public void setBonusFactor(double bonusFactor) {
		this.bonusFactor = bonusFactor;
	}
	
	
}
