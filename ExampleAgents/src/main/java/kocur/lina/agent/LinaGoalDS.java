package kocur.lina.agent;

import cz.cuni.mff.kocur.brain.DecisionSet;
import cz.cuni.mff.kocur.considerations.ConsiderDistanceToTarget;
import cz.cuni.mff.kocur.considerations.ConsiderGameTime;
import cz.cuni.mff.kocur.considerations.Consideration;
import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.decisions.DecisionBuilder;
import cz.cuni.mff.kocur.decisions.DecisionScoreEvaluator;
import cz.cuni.mff.kocur.decisions.RuneGoalDecision;
import cz.cuni.mff.kocur.utility.LinearFunction;

/**
 * Lina's goals.
 * 
 * @author kocur
 *
 */
public class LinaGoalDS extends DecisionSet {

	public LinaGoalDS() {

	}

	public void createDecisions() {
		// Go towards a bottom rune.
		Decision setRuneAsAGoal = DecisionBuilder.build().setDecision(new RuneGoalDecision()).setBonusFactor(3.0)
				.setName("SetRuneGoal").addConsideration(new ConsiderGameTime(), new LinearFunction(2.2, 1, 1.1, 1.5))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 90)
				.addConsideration(new ConsiderDistanceToTarget(), new LinearFunction(1, 1, 0.4, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 1000).setEvaluator(new DecisionScoreEvaluator())
				.get();

		this.add(setRuneAsAGoal);

	}

}
