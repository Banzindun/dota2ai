package kocur.sniper.agent;

import cz.cuni.mff.kocur.brain.DecisionSet;
import cz.cuni.mff.kocur.considerations.ConsiderCooldown;
import cz.cuni.mff.kocur.considerations.ConsiderDistanceToTarget;
import cz.cuni.mff.kocur.considerations.ConsiderSourceMana;
import cz.cuni.mff.kocur.considerations.ConsiderTargetHealth;
import cz.cuni.mff.kocur.considerations.ConsiderTargetValue;
import cz.cuni.mff.kocur.considerations.ConsiderTimePassed;
import cz.cuni.mff.kocur.considerations.Consideration;
import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.decisions.DecisionBuilder;
import cz.cuni.mff.kocur.decisions.DecisionScoreEvaluator;
import cz.cuni.mff.kocur.utility.BinaryFunction;
import cz.cuni.mff.kocur.utility.LinearFunction;
import cz.cuni.mff.kocur.utility.PolynomialFunction;

/**
 * Sniper's abilities and decisions that consider casting them.
 * 
 * @author kocur
 *
 */
public class SniperAbilitiesDS extends DecisionSet {

	public SniperAbilitiesDS() {

	}

	/**
	 * Creates the decisions.
	 */
	public void createDecisions() {
		// First spell we can cast is a shrapnel
		Decision shrapnel = DecisionBuilder.build().setDecision(new CastShrapnel()).setName("CastShrapnel")
				.setBonusFactor(10.0)
				.addConsideration(new ConsiderSourceMana(), new PolynomialFunction(-1.8, 2, 1, 0.9))
				.addConsideration(new ConsiderCooldown(), new BinaryFunction(0.5, -1, 0.01, 0.5))
				// Shrapnel in particular can be cast a few times in a row,
				// this might be good in some scenarios, bot not for creep
				// farming, so we limit how often it can be cast
				.addConsideration(new ConsiderTimePassed(), new LinearFunction(2, 1, 0.6, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 10)
				// Value will be count of targets I am able to hit:
				// 4 is good, 2 is not good enough -> we want to cast at 4/5 targets if possible
				.addConsideration(new ConsiderTargetValue(), new PolynomialFunction(-2.7, 2, 1, 1))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 6).setEvaluator(new DecisionScoreEvaluator()).get();

		this.add(shrapnel);

		Decision asssasinate = DecisionBuilder.build().setDecision(new CastAssasinate()).setName("CastAssasinate")
				.setBonusFactor(10.0) // We really really want to do that..
				.addConsideration(new ConsiderSourceMana(), new PolynomialFunction(-1.8, 2, 1, 0.9))
				.addConsideration(new ConsiderCooldown(), new BinaryFunction(0.5, -1, 0.01, 0.5))
				// We want to cast the ability to finish the enemy that is low
				.addConsideration(new ConsiderTargetHealth(), new PolynomialFunction(-2.7, 2, -0.3, 1.4))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 6)
				// And finally we should be casting it only on targets that are far away (say
				// 800+)
				.addConsideration(new ConsiderDistanceToTarget(), new LinearFunction(9.3, 1, 0.95, 1.1))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 1000).setEvaluator(new DecisionScoreEvaluator())
				.get();

		this.add(asssasinate);

	}
}
