package cz.cuni.mff.kocur.brain;

import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.decisions.DecisionMaker;
import cz.cuni.mff.kocur.decisions.EmptyDecision;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;

/**
 * Abstract class, that represents a agent's brain. The brain contains decision
 * and void decision sets. Void decision sets do not create create agent's
 * commands. The goal of the brain is to select best decisions given a
 * botContext, preset the decisions and update them, if deemed necessary.
 * 
 * @author kocur
 *
 */
public abstract class Brain {
	/**
	 * Logger registered for Intelligence class.
	 */
	protected Logger logger = LogManager.getLogger(Brain.class.getName());

	/**
	 * List of all decisions that might be done.
	 */
	protected LinkedList<DecisionSet> decisionSets = new LinkedList<>();

	/**
	 * List of all void decision that might be done. Void decision is a decision
	 * that does something with controller/hero, but doesn't return any commands.
	 * Basically serves to evaluate something on the hero's side, withou doing
	 * anything. For example sets a new goal for hero to follow, the goal is then
	 * used by non-void decisions to do something.
	 */
	protected LinkedList<DecisionSet> voidDecisionSets = new LinkedList<>();

	/**
	 * Reference to bot's context, that this brain is working with.
	 */
	protected ExtendedAgentContext botContext = null;

	/**
	 * Decision maker, that makes the decisions.
	 */
	protected DecisionMaker dm = new DecisionMaker();

	/**
	 * Constructor of brain, that takes reference to bot's context as argument.
	 * 
	 * @param botContext
	 *            Reference to bot's context, that will be used throughout the
	 *            thinking.
	 */
	public Brain(ExtendedAgentContext botContext) {
		this.botContext = botContext;
	}

	/**
	 * Does the thinking for non-void decision sets.
	 * 
	 * @return Returns the best decision.
	 */
	public Decision think() {
		return doThinking(decisionSets);
	}

	/**
	 * Does the thinking for void decision sets.
	 * 
	 * @return Returns the best void decision.
	 */
	public Decision voidThink() {
		return doThinking(voidDecisionSets);
	}

	/**
	 * Does the actual thinking for input decision sets list.
	 * 
	 * @param decisionSets
	 *            List of decisions that should be considered.
	 * @return Returns the best decision from the specified set.
	 */
	protected Decision doThinking(LinkedList<DecisionSet> decisionSets) {
		Decision bestDecision = null;

		for (DecisionSet d : decisionSets) {
			// Update context of decisions, will set new goals etc. and score all decision
			d.updateContext(botContext);
			d.scoreAll(dm);

			// Get the best decision from the set.
			Decision bestSetDecision = d.getBestDecision();

			if (bestSetDecision == null)
				continue;

			// Set the new best decision if possible
			if (bestDecision == null || bestDecision.getScore() < bestSetDecision.getScore()) {
				bestDecision = bestSetDecision;
			}
		}

		// If the best decision scored 0 we return empty decision, that does nothing.
		if (bestDecision != null && bestDecision.getScore() == 0)
			return new EmptyDecision();

		// Else we return the best decision.
		return bestDecision;
	}

	/**
	 * Presets all the decision that are stored inside this object.
	 * 
	 */
	public void presetDecisions() {
		for (DecisionSet d : decisionSets) {
			d.presetContext(botContext);
		}

		for (DecisionSet d : voidDecisionSets) {
			d.presetContext(botContext);
		}

	}

	/**
	 * Creates the decisions for this brain.
	 */
	public abstract void createDecisions();

	/**
	 * Adds a decision set to sets contained inside this brain.
	 * 
	 * @param s
	 *            Set to add to this brain.
	 */
	public void addDecisionSet(DecisionSet s) {
		decisionSets.add(s);
	}

	/**
	 * Adds a void decision set to void decision sets contained inside this brain.
	 * 
	 * @param s
	 *            Void decision set, that should be added to this brain.
	 */
	public void addVoidDecisionSet(DecisionSet s) {
		voidDecisionSets.add(s);
	}

	/**
	 * 
	 * @return Returns all the decisions of all sets.
	 */
	public LinkedList<Decision> getDecisions() {
		LinkedList<Decision> allDecisions = new LinkedList<>();
		for (DecisionSet ds : decisionSets) {
			allDecisions.addAll(ds.getDecisions());
		}

		return allDecisions;
	}

	/**
	 * 
	 * @return Returns all decisons from all the void sets.
	 */
	public LinkedList<Decision> getVoidDecisions() {
		LinkedList<Decision> allDecisions = new LinkedList<>();
		for (DecisionSet ds : voidDecisionSets) {
			allDecisions.addAll(ds.getDecisions());
		}

		return allDecisions;
	}

	/**
	 * 
	 * @return Returns list of all decisions - void and non-void.
	 */
	public LinkedList<Decision> getAllDecisions() {
		LinkedList<Decision> allDecisions = new LinkedList<>();

		// Add non-void decisions
		for (DecisionSet ds : decisionSets) {
			allDecisions.addAll(ds.getDecisions());
		}

		// Add void decisions
		for (DecisionSet ds : voidDecisionSets) {
			allDecisions.addAll(ds.getDecisions());
		}

		return allDecisions;
	}

	/**
	 * Copies all the decision sets from the other brain.
	 * 
	 * @param other
	 *            Other brain, from which we copy.
	 */
	public void copyDecisionsFromBrain(Brain other) {
		this.decisionSets = other.getDecisionSets();

	}

	/**
	 * Reload the decision and void decision sets.
	 */
	public void reload() {
		decisionSets.clear();
		voidDecisionSets.clear();

		// Create the sets again and initialize them
		createDecisions();
		presetDecisions();
	}

	/**
	 * 
	 * @return Returns list of void decisions.
	 */
	public LinkedList<DecisionSet> getVoidDecisionSets() {
		return voidDecisionSets;
	}

	/**
	 * 
	 * @param voidDecisionSets
	 *            New list of void decisions.
	 */
	public void setVoidDecisionSets(LinkedList<DecisionSet> voidDecisionSets) {
		this.voidDecisionSets = voidDecisionSets;
	}

	/**
	 * 
	 * @return Returns a list of decision sets that are used by this brain.
	 */
	public LinkedList<DecisionSet> getDecisionSets() {
		return decisionSets;
	}

	/**
	 * 
	 * @param decisionSets
	 *            Sets a new decision set.
	 */
	public void setDecisionSets(LinkedList<DecisionSet> decisionSets) {
		this.decisionSets = decisionSets;
	}

}
