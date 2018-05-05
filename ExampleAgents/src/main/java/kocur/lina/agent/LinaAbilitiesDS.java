package kocur.lina.agent;

import cz.cuni.mff.kocur.brain.DecisionSet;
import cz.cuni.mff.kocur.considerations.ConsiderCooldown;
import cz.cuni.mff.kocur.considerations.ConsiderSourceMana;
import cz.cuni.mff.kocur.considerations.ConsiderTargetHealth;
import cz.cuni.mff.kocur.considerations.ConsiderTargetValue;
import cz.cuni.mff.kocur.considerations.Consideration;
import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.decisions.DecisionBuilder;
import cz.cuni.mff.kocur.decisions.DecisionScoreEvaluator;
import cz.cuni.mff.kocur.utility.BinaryFunction;
import cz.cuni.mff.kocur.utility.PolynomialFunction;

/**
 * Decision set, that adds Lina's abilities considerations.
 * 
 * @author kocur
 *
 */
public class LinaAbilitiesDS extends DecisionSet {

	public LinaAbilitiesDS() {
	}

	/**
	 * Creates the decisions.
	 */
	public void createDecisions() {
		// Decision to cast dragon salve
		Decision dragonSlave = DecisionBuilder.build().setDecision(new CastDragonSlave()).setName("CastDragonSlave")
				.setBonusFactor(9.0).addConsideration(new ConsiderSourceMana(), new PolynomialFunction(-1.8, 2, 1, 0.9))
				.addConsideration(new ConsiderCooldown(), new BinaryFunction(0.5, -1, 0.01, 0.5))
				// Value will be count of targets I am able to hit:
				// 4 is good, 2 is not good enough -> we want to cast at 4/5 targets if possible
				.addConsideration(new ConsiderTargetValue(), new PolynomialFunction(-2.7, 2, 1, 1))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 6).setEvaluator(new DecisionScoreEvaluator()).get();

		this.add(dragonSlave);

		// Decision to cast dragon salve
		/*
		 * Decision dragonSalveOnHeroNearby = DecisionBuilder.build() .setDecision(new
		 * CastDragonSalve()) .setName("CastDragonSalveOnNearbyHero")
		 * .setBonusFactor(7.0) // Smaller factor .. for now .addConsideration( new
		 * ConsiderSourceMana(), new PolynomialFunction(-1.8, 2, 1, 0.9))
		 * .addConsideration( new ConsiderCooldown(), new BinaryFunction(0.5, -1, 0.01,
		 * 0.5)) // Value will be count of targets I am able to hit: // 4 is good, 2 is
		 * not good enough -> we want to cast at 4/5 targets if possible
		 * .addConsideration(new ConsiderTargetValue(), new PolynomialFunction(-2.7, 2,
		 * 1, 1)) .addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
		 * .addDoubleParameter(Consideration.PARAM_RANGE_MAX, 6) .setEvaluator(new
		 * DecisionScoreEvaluator()) .get();
		 */

		// this.add(dragonSalve);

		// Decision to cast light strike array
		// Decision to cast dragon salve
		Decision lightArray = DecisionBuilder.build().setDecision(new CastLightArray()).setName("CastLightArray")
				.setBonusFactor(10.0)
				.addConsideration(new ConsiderSourceMana(), new PolynomialFunction(-1.8, 2, 1, 0.9))
				.addConsideration(new ConsiderCooldown(), new BinaryFunction(0.5, -1, 0.01, 0.5))
				// The best use might be to stun a hero, that has low health
				// and then finish him off..
				.addConsideration(new ConsiderTargetHealth(), new PolynomialFunction(-2.5, 2, -0.1, 1.6))
				.setEvaluator(new DecisionScoreEvaluator()).get();

		this.add(lightArray);

		// Decision to cast laguna blade
		Decision lagunaBlade = DecisionBuilder.build().setDecision(new CastLagunaBlade()).setName("CastLagunaBlade")
				.setBonusFactor(11.0)
				.addConsideration(new ConsiderSourceMana(), new PolynomialFunction(-1.8, 2, 1, 0.9))
				.addConsideration(new ConsiderCooldown(), new BinaryFunction(0.5, -1, 0.01, 0.5))
				// We want to cast the ability to finish the enemy that is low
				.addConsideration(new ConsiderTargetHealth(), new PolynomialFunction(-2.7, 2, -0.3, 1.4))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 6).setEvaluator(new DecisionScoreEvaluator()).get();

		this.add(lagunaBlade);
	}
}
