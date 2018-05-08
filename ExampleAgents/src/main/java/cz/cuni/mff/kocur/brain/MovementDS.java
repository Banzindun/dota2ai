package cz.cuni.mff.kocur.brain;

import cz.cuni.mff.kocur.considerations.ConsiderThreat;
import cz.cuni.mff.kocur.considerations.ConsiderTimePassed;
import cz.cuni.mff.kocur.considerations.Consideration;
import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.decisions.DecisionBuilder;
import cz.cuni.mff.kocur.decisions.DecisionScoreEvaluator;
import cz.cuni.mff.kocur.decisions.EscapeDecision;
import cz.cuni.mff.kocur.decisions.MoveToGoalDecision;
import cz.cuni.mff.kocur.utility.LinearFunction;
import cz.cuni.mff.kocur.utility.PolynomialFunction;

/**
 * Decision set, that handles movement. Moves to goal, stays close to farm or escapes if threatened.
 * @author kocur
 *
 */
public class MovementDS extends DecisionSet {

	public MovementDS() {
		createDecisions();
	}

	/**
	 * Creates movement decisions.
	 */
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
				.setBonusFactor(8.0)
				// We consider time passed, we don't want to do this all the time. We wait a while.
				//.addConsideration(new ConsiderTimePassed(), new LinearFunction(2, 1, 0.6, 0))
				//.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 0.75) // After 2 seconds 1.0
				// We want to consider threat in the direction of target. So we don't walk into
				// some kind of trap. Or through tower or something.
				.addConsideration(new ConsiderThreat(), new PolynomialFunction(-5, 3, 1.3, -0.15))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, -1)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 0).setEvaluator(new DecisionScoreEvaluator()).get();

		this.add(escape);

		/*
		Decision stayOutOfThreat = DecisionBuilder.build().setDecision(new StayOutOfThread())
				.setName("StayOutOfthread").setBonusFactor(4.0)
				// We do not want to adjust all the time.
				.addConsideration(new ConsiderTimePassed(), new LinearFunction(2, 1, 0.5, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 2)
				// If the farming position has low influence, we do not go there
				.addConsideration(new ConsiderThreat(), new PolynomialFunction(2.5, 3, 0, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 1)
				// If it is close to us, we do not go there - again because we do not want to adjust all the time
				.addConsideration(new ConsiderDistanceToTarget(), new PolynomialFunction(-2.2, 2, 0.2, 1.2))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 400).setEvaluator(new DecisionScoreEvaluator())
				.get();

		this.add(stayOutOfThreat);*/


	}

}
