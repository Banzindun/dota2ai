package cz.cuni.mff.kocur.brain;

import cz.cuni.mff.kocur.considerations.ConsiderDistanceToTarget;
import cz.cuni.mff.kocur.considerations.ConsiderInventorySpace;
import cz.cuni.mff.kocur.considerations.ConsiderMoney;
import cz.cuni.mff.kocur.considerations.ConsiderSecretShop;
import cz.cuni.mff.kocur.considerations.ConsiderSourceHealth;
import cz.cuni.mff.kocur.considerations.ConsiderThreat;
import cz.cuni.mff.kocur.considerations.ConsiderTimePassed;
import cz.cuni.mff.kocur.considerations.Consideration;
import cz.cuni.mff.kocur.decisions.CreepAsAGoalDecision;
import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.decisions.DecisionBuilder;
import cz.cuni.mff.kocur.decisions.DecisionEscapeFromThreat;
import cz.cuni.mff.kocur.decisions.DecisionScoreEvaluator;
import cz.cuni.mff.kocur.decisions.EscapeToBase;
import cz.cuni.mff.kocur.decisions.SetShopGoalDecision;
import cz.cuni.mff.kocur.decisions.StayNearFountainDecision;
import cz.cuni.mff.kocur.decisions.Target;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.utility.BinaryFunction;
import cz.cuni.mff.kocur.utility.LinearFunction;
import cz.cuni.mff.kocur.utility.PolynomialFunction;
import cz.cuni.mff.kocur.utility.SinusoidFunction;
import cz.cuni.mff.kocur.world.Inventory;

/**
 * Decision set, that handles goals. Goals locations on the map, that are set on
 * the influence map. Agent uses the influence map to navigate towards the goals
 * using the MovementDS.
 * 
 * @author kocur
 *
 */
public class GoalDS extends DecisionSet {

	/**
	 * Constructor that creates decisions.
	 */
	public GoalDS() {
		createDecisions();
	}

	/**
	 * Creates decisions.
	 */
	private void createDecisions() {

		// Decision that keeps us close to lane front
		Decision setLaneCreepAsAGoal = DecisionBuilder.build().setDecision(new CreepAsAGoalDecision())
				.setBonusFactor(3.0)
				// We consider time passed, we do not want to update the IM all the time
				.addConsideration(new ConsiderTimePassed(), new LinearFunction(2, 1, 0.6, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 2)
				// We consider distance to target, if we are too close we decrease the score
				.addConsideration(new ConsiderDistanceToTarget(), new LinearFunction(1, 1, 0.4, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 1000).setName("SetLaneCreepAsAGoal")
				.setEvaluator(new DecisionScoreEvaluator()).get();

		this.add(setLaneCreepAsAGoal);

		/*
		 * // This is just a filler, that sometimes sends us towards our tower Decision
		 * lastTowerAsAGoal = DecisionBuilder.build().setDecision(new
		 * LastTowerAsAGoalDecision()) .setBonusFactor(1.0).addConsideration(new
		 * ConsiderTimePassed(), new LinearFunction(2, 1, 0.6, 0))
		 * .addDoubleParameter(Consideration.PARAM_RANGE_MAX, 2) .addConsideration(new
		 * ConsiderDistanceToTarget(), new LinearFunction(1, 1, 0.4, 0))
		 * .addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
		 * .addDoubleParameter(Consideration.PARAM_RANGE_MAX,
		 * 1000).setName("SetLastTowerAsAGoal") .setEvaluator(new
		 * DecisionScoreEvaluator()).get();
		 * 
		 * this.add(lastTowerAsAGoal);
		 */

		// If we are too low, we try to escape to base.
		Decision escapeToBase = DecisionBuilder.build().setDecision(new EscapeToBase()).setName("EscapeToBase")
				.setBonusFactor(5.0).addConsideration(new ConsiderSourceHealth(),
						// new PolynomialFunction(-11, 2, 0, 1))
						new PolynomialFunction(7.4, -10, -1, -0.5))
				.setEvaluator(new DecisionScoreEvaluator()).get();

		this.add(escapeToBase);

		// If we are close to fountain and we are not fully healed, we stay near it
		Decision stayNearFountain = DecisionBuilder.build().setDecision(new StayNearFountainDecision())
				.setName("StayNearFountainOnLowHealth").setBonusFactor(7.0)
				.addConsideration(new ConsiderSourceHealth(), new PolynomialFunction(1, 2, 1, -0.02))
				.addConsideration(new ConsiderDistanceToTarget(), new LinearFunction(-10, 3, 0.6, 0.05)) // Around 1200
																											// range
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 2000).setEvaluator(new DecisionScoreEvaluator())
				.get();

		this.add(stayNearFountain);

		// We escape from threat if we are threatened too much
		Decision escapeFromThreat = DecisionBuilder.build().setDecision(new DecisionEscapeFromThreat())
				.setName("EscapeFromThreat").setBonusFactor(8.0)
				.addConsideration(new ConsiderThreat(), new SinusoidFunction(3.6, 1.8, 1.55, 0.2))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, -1)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 0).setEvaluator(new DecisionScoreEvaluator()).get();

		this.add(escapeFromThreat);

		// We go to secret shop, if we have a money to buy secret shop's item, that we
		// need
		Decision secretShopGoal = DecisionBuilder.build().setDecision(new SetShopGoalDecision()).setBonusFactor(7.0)
				.setName("SetGoalToSecretShop")
				.addConsideration(new ConsiderMoney(), new BinaryFunction(0.5, 1, 0.5, 0.5))
				.addConsideration(new ConsiderTimePassed(), new LinearFunction(2, 1, 0.6, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 2) // After 2 seconds 1.0
				.addConsideration(new ConsiderSecretShop(), new BinaryFunction(0.5, -1, 0.5, 0.5))
				.setEvaluator(new DecisionScoreEvaluator()).get();

		this.add(secretShopGoal);

		// If we have something in stash and we are near our base shop, we should go to
		// pick it up.
		Decision grabAllGoal = DecisionBuilder.build().setDecision(new SetShopGoalDecision() {
			@Override
			public void updateContext(ExtendedAgentContext bc) {
				context.setTarget(new Target(bc.getMyBaseShop()));
			}
		}).setName("SetGoalToBaseShop").setBonusFactor(8.0)
				.addConsideration(new ConsiderInventorySpace(), new BinaryFunction(0.5, 1, 0.01, 0.5))
				// Do I have something inside the stash?
				.addIntParameter(Consideration.PARAM_TYPE, Inventory.STASH)
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, Inventory.stashSlotMax)
				.addConsideration(new ConsiderDistanceToTarget(), new PolynomialFunction(-10, 2, 0.5, 1))
				// On 1500 range I should be able to take the item
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 2500).setEvaluator(new DecisionScoreEvaluator())
				.get();

		this.add(grabAllGoal);

	}

}
