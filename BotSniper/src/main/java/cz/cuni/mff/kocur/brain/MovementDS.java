package cz.cuni.mff.kocur.brain;

import cz.cuni.mff.kocur.considerations.ConsiderDistanceToTarget;
import cz.cuni.mff.kocur.considerations.ConsiderInventorySpace;
import cz.cuni.mff.kocur.considerations.ConsiderMoney;
import cz.cuni.mff.kocur.considerations.ConsiderSecretShop;
import cz.cuni.mff.kocur.considerations.ConsiderTargetValue;
import cz.cuni.mff.kocur.considerations.ConsiderThreat;
import cz.cuni.mff.kocur.considerations.ConsiderTimePassed;
import cz.cuni.mff.kocur.considerations.Consideration;
import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.decisions.DecisionBuilder;
import cz.cuni.mff.kocur.decisions.DecisionScoreEvaluator;
import cz.cuni.mff.kocur.decisions.EscapeDecision;
import cz.cuni.mff.kocur.decisions.MoveToFarmingPosition;
import cz.cuni.mff.kocur.decisions.MoveToGoalDecision;
import cz.cuni.mff.kocur.decisions.MoveToShopDecision;
import cz.cuni.mff.kocur.decisions.Target;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.utility.BinaryFunction;
import cz.cuni.mff.kocur.utility.LinearFunction;
import cz.cuni.mff.kocur.utility.PolynomialFunction;
import cz.cuni.mff.kocur.world.Inventory;

public class MovementDS extends DecisionSet {

	public MovementDS() {
		createDecisions();
	}

	private void createDecisions() {
		Decision moveToGoal = DecisionBuilder.build().setDecision(new MoveToGoalDecision()).setName("MoveToGoal")
				.setBonusFactor(3.0)
				// We consider time passed, we don't want to do this all the time. We wait at
				// least 1 second.
				.addConsideration(new ConsiderTimePassed(), new LinearFunction(2, 1, 0.6, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 2) // After 2 seconds 1.0
				// We want to consider threat in the direction of target. So we don't walk into
				// some kind of trap. Or through tower or something.
				.addConsideration(new ConsiderThreat(), new PolynomialFunction(10, 5, 0.42, -0.05))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, -1)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 0).setEvaluator(new DecisionScoreEvaluator()).get();

		this.add(moveToGoal);

		Decision escape = DecisionBuilder.build().setDecision(new EscapeDecision()).setName("Escape")
				.setBonusFactor(7.0)
				// We consider time passed, we don't want to do this all the time. We wait at
				// least 1 second.
				.addConsideration(new ConsiderTimePassed(), new LinearFunction(2, 1, 0.6, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 1) // After 2 seconds 1.0
				// We want to consider threat in the direction of target. So we don't walk into
				// some kind of trap. Or through tower or something.
				.addConsideration(new ConsiderThreat(), new PolynomialFunction(-5, 3, 1.3, -0.15))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, -1)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 0).setEvaluator(new DecisionScoreEvaluator()).get();

		this.add(escape);

		Decision stayCloseToFarm = DecisionBuilder.build().setDecision(new MoveToFarmingPosition())
				.setName("MoveToFarmingPosition").setBonusFactor(4.0)
				.addConsideration(new ConsiderTimePassed(), new LinearFunction(2, 1, 0.5, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 4)
				.addConsideration(new ConsiderTargetValue(), new PolynomialFunction(2.5, 3, 0, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 1)
				.addConsideration(new ConsiderDistanceToTarget(), new PolynomialFunction(-2.3, 2, 0.76, 1.2))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 600).setEvaluator(new DecisionScoreEvaluator())
				.get();

		this.add(stayCloseToFarm);

		Decision moveToSecretShop = DecisionBuilder.build().setDecision(new MoveToShopDecision()).setBonusFactor(7.0)
				.setName("MoveToSecretShop").addConsideration(new ConsiderMoney(), new BinaryFunction(0.5, 1, 0.5, 0.5))
				.addConsideration(new ConsiderTimePassed(), new LinearFunction(2, 1, 0.6, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 2) // After 2 seconds 1.0
				.addConsideration(new ConsiderSecretShop(), new BinaryFunction(0.5, -1, 0.5, 0.5))
				.setEvaluator(new DecisionScoreEvaluator()).get();

		this.add(moveToSecretShop);

		Decision moveToBaseToGrabAll = DecisionBuilder.build().setDecision(new MoveToShopDecision() {
			@Override
			public void updateContext(ExtendedBotContext bc) {
				context.setTarget(new Target(bc.getMyBaseShop()));
			}
		}).setName("MoveToBaseToGrab").setBonusFactor(8.0)
				.addConsideration(new ConsiderInventorySpace(), new BinaryFunction(0.5, 1, 0.01, 0.5)) // Do I have
																										// something
																										// inside the
																										// stash?
				.addIntParameter(Consideration.PARAM_TYPE, Inventory.STASH)
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, Inventory.stashSlotMax)
				.addConsideration(new ConsiderDistanceToTarget(), new PolynomialFunction(-10, 2, 0.5, 1)) // On 1500
																											// range I
																											// should be
																											// able to
																											// take the
																											// item
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 2500).setEvaluator(new DecisionScoreEvaluator())
				.get();

		this.add(moveToBaseToGrabAll);

	}

}
