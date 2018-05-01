package cz.cuni.mff.kocur.decisions;

import java.util.LinkedList;

import cz.cuni.mff.kocur.considerations.Consideration;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.server.TimeManager;

public abstract class Decision {
	protected DecisionScoreEvaluator DSE = null;
	
	protected LinkedList<Consideration> considerations = new LinkedList<>();
	
	protected DecisionContext context = new DecisionContext();
	
	protected String readableName = "";
	
	protected double score = 0;
	
	protected float lastTimeExecuted = -1;
	
	/**
	 * Executes this decision. This function should specify what to do, after it 
	 * was triggered.
	 */
	public AgentCommand execute(){
		lastTimeExecuted = TimeManager.getLocalTime();
		return null;		
	};
	
	/**
	 * Should set decision context from bot context. 
	 * @param bc BotContext. 
	 */
	public abstract void updateContext(ExtendedBotContext bc);
	
	/**
	 * For example if you have some references, that do not change, you can set them here.
	 * @param bc Bot context with influence.
	 */
	public abstract void presetContext(ExtendedBotContext bc);
	
	public float getLastTimeExecuted() {
		return lastTimeExecuted;
	}

	public void setLastTimeExecuted(float lastTimeExecuted) {
		this.lastTimeExecuted = lastTimeExecuted;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public DecisionContext getContext() {
		return context;
	}

	public void setContext(DecisionContext context) {
		this.context = context;
	}

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
	
	public Consideration getLastConsideration() {
		return considerations.getLast();
	}
}
