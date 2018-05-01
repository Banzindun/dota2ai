package cz.cuni.mff.kocur.brain;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.Constants;
import cz.cuni.mff.kocur.considerations.ConsiderCreepHealth;
import cz.cuni.mff.kocur.considerations.ConsiderGoldYield;
import cz.cuni.mff.kocur.considerations.ConsiderThreat;
import cz.cuni.mff.kocur.considerations.Consideration;
import cz.cuni.mff.kocur.decisions.AttackCreepDecision;
import cz.cuni.mff.kocur.decisions.AttackHeroDecision;
import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.decisions.DecisionBuilder;
import cz.cuni.mff.kocur.decisions.DecisionMaker;
import cz.cuni.mff.kocur.decisions.DecisionScoreEvaluator;
import cz.cuni.mff.kocur.decisions.Target;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.utility.BinaryFunction;
import cz.cuni.mff.kocur.utility.LinearFunction;
import cz.cuni.mff.kocur.utility.LogisticFunction;
import cz.cuni.mff.kocur.utility.PolynomialFunction;
import cz.cuni.mff.kocur.utility.SinusoidFunction;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.Creep;
import cz.cuni.mff.kocur.world.Hero;

public class AttackingDS extends DecisionSet{
	/**
	 * Logger registered for CreepTargetingDS class.
	 */
	private static final Logger logger = LogManager.getLogger(AttackingDS.class);
	
	/**
	 * We will cap the maximum number of targets we can consider at once. 
	 * The 6 creeps will be chosen by distance.
	 */
	protected int maxTargets = 6; 

	/**
	 * Bonus factor for our decisions.
	 */
	protected double bonusFactor = 4.0;
	
	public AttackingDS() {}

	@Override
	public void updateContext(ExtendedBotContext botContext) {
		// Get list of entities around me, sorted by distance.
		super.updateContext(botContext);
	}
	
	@Override
	public void scoreAll(DecisionMaker dm) {
		dm.scoreAllDecisions(decisions);
		bestDecision = dm.getBestDecision(decisions);
	}
	
	
	public int getMaxTargets() {
		return maxTargets;
	}

	public void setMaxTargets(int maxTargets) {
		this.maxTargets = maxTargets;
	}

	public double getBonusFactor() {
		return bonusFactor;
	}

	public void setBonusFactor(double bonusFactor) {
		this.bonusFactor = bonusFactor;
	}
}
