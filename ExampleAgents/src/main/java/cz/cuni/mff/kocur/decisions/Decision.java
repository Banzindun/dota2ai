package cz.cuni.mff.kocur.decisions;

import java.util.LinkedList;

import cz.cuni.mff.kocur.considerations.Consideration;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.server.TimeManager;

/**
 * Class that represents a decision.
 * 
 * @author kocur
 *
 */
public abstract class Decision {
	/**
	 * DSE.
	 */
	protected DecisionScoreEvaluator DSE = null;

	/**
	 * List of considerations for this decision.
	 */
	protected LinkedList<Consideration> considerations = new LinkedList<>();

	/**
	 * Decision context.
	 */
	protected DecisionContext context = new DecisionContext();

	/**
	 * Human readable string name.
	 */
	protected String readableName = "";

	/**
	 * Score of the decision.
	 */
	protected double score = 0;

	/**
	 * Last time the decision was executed.
	 */
	protected float lastTimeExecuted = -1;

	/**
	 * Executes this decision. This function should specify what to do, after it was
	 * triggered.
	 * 
	 * @return Returns agent's command that should be executed.
	 */
	public AgentCommand execute() {
		lastTimeExecuted = TimeManager.getLocalTime();
		return null;
	};

	/**
	 * Should set decision context from bot context.
	 * 
	 * @param bc
	 *            BotContext.
	 */
	public abstract void updateContext(ExtendedAgentContext bc);

	/**
	 * For example if you have some references, that do not change, you can set them
	 * here.
	 * 
	 * @param bc
	 *            Bot context with influence.
	 */
	public abstract void presetContext(ExtendedAgentContext bc);

	/**
	 * 
	 * @return Returns last time, that this decision was executed.
	 */
	public float getLastTimeExecuted() {
		return lastTimeExecuted;
	}

	public void setLastTimeExecuted(float lastTimeExecuted) {
		this.lastTimeExecuted = lastTimeExecuted;
	}

	/**
	 * 
	 * @return Returns score of the decision.
	 */
	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * 
	 * @return Returns context of this decision.
	 */
	public DecisionContext getContext() {
		return context;
	}

	public void setContext(DecisionContext context) {
		this.context = context;
	}

	/**
	 * 
	 * @return Returns a decision score evaluator of this decision.
	 */
	public DecisionScoreEvaluator getDSE() {
		return DSE;
	}

	public void setDSE(DecisionScoreEvaluator dse) {
		this.DSE = dse;
	}

	public LinkedList<Consideration> getConsiderations() {
		return considerations;
	}

	public void setConsiderations(LinkedList<Consideration> considerations) {
		this.considerations = considerations;
	}

	public String getReadableName() {
		return readableName;
	}

	public void setReadableName(String readableName) {
		this.readableName = readableName;
	}

	public void addConsideration(Consideration cons) {
		considerations.add(cons);
	}

	/**
	 * 
	 * @return Returns a last consideration.
	 */
	public Consideration getLastConsideration() {
		return considerations.getLast();
	}
}
