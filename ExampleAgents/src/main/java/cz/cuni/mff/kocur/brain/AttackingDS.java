package cz.cuni.mff.kocur.brain;

import cz.cuni.mff.kocur.decisions.DecisionMaker;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;

/**
 * Class that is extended by all attacking decision sets. This class stores
 * fields like max number of targets an agent can consider and a base bonus
 * factor for attack decisions.
 * 
 * @author kocur
 *
 */
public class AttackingDS extends DecisionSet {
	/**
	 * We will cap the maximum number of targets we can consider at once. The 6
	 * creeps will be chosen by distance.
	 */
	protected int maxTargets = 6;

	/**
	 * Bonus factor for our decisions.
	 */
	protected double bonusFactor = 3.0;

	public AttackingDS() {
	}

	@Override
	public void updateContext(ExtendedAgentContext botContext) {
		// Get list of entities around me, sorted by distance.
		super.updateContext(botContext);
	}

	@Override
	public void scoreAll(DecisionMaker dm) {
		dm.scoreAllDecisions(decisions);
		bestDecision = dm.getBestDecision(decisions);
	}

	/**
	 * 
	 * @return Returns maximum number of targets.
	 */
	public int getMaxTargets() {
		return maxTargets;
	}

	public void setMaxTargets(int maxTargets) {
		this.maxTargets = maxTargets;
	}

	/**
	 * 
	 * @return Returns bonus factor of this decision set.
	 */
	public double getBonusFactor() {
		return bonusFactor;
	}

	public void setBonusFactor(double bonusFactor) {
		this.bonusFactor = bonusFactor;
	}
}
