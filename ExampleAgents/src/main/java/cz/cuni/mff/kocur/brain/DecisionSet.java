package cz.cuni.mff.kocur.brain;

import java.util.LinkedList;

import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.decisions.DecisionMaker;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;

/**
 * Class that represents decision set. Decision set groups decisions, that are
 * then scored, updated and preset in bulk.
 * 
 * @author kocur
 *
 */
public class DecisionSet {
	/**
	 * List of decisions.
	 */
	protected LinkedList<Decision> decisions = new LinkedList<>();

	/**
	 * Current best decision.
	 */
	protected Decision bestDecision = null;

	/**
	 * Adds a decision to decision set.
	 * 
	 * @param d
	 *            Decision.
	 */
	public void add(Decision d) {
		decisions.add(d);
	}

	/**
	 * Removes decision d from stored decisions.
	 * 
	 * @param d
	 *            Decision.
	 */
	public void remove(Decision d) {
		decisions.remove(d);
	}

	/**
	 * Removes decision on given index.
	 * 
	 * @param i
	 *            Index.
	 */
	public void remove(int i) {
		decisions.remove(i);
	}

	/**
	 * 
	 * @return Returns all the decisions.
	 */
	public LinkedList<Decision> getScoredDecisions() {
		return decisions; // Should be scored by scoreAll beforehand
	}

	/**
	 * Scores all decisions.
	 * 
	 * @param dm
	 *            Decision maker.
	 */
	public void scoreAll(DecisionMaker dm) {
		dm.scoreAllDecisions(decisions);
		bestDecision = dm.getBestDecision(decisions);
	}

	/**
	 * Updates decisions from agents's context.
	 * 
	 * @param botContext
	 *            Agent's context.
	 */
	public void updateContext(ExtendedAgentContext botContext) {
		for (Decision d : decisions) {
			d.updateContext(botContext);
		}

	}

	/**
	 * 
	 * @return Returns the best decision from the last scoreAll call.
	 */
	public Decision getBestDecision() {
		return bestDecision;
	}

	/**
	 * Presets the context to all decisions.
	 * 
	 * @param botContext
	 *            Agent's context.
	 */
	public void presetContext(ExtendedAgentContext botContext) {
		for (Decision d : decisions) {
			d.presetContext(botContext);
		}
	}

	/**
	 * 
	 * @return Returns list of decisions.
	 */
	public LinkedList<Decision> getDecisions() {
		return decisions;
	}

}
