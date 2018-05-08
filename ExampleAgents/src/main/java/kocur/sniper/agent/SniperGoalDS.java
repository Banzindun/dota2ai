package kocur.sniper.agent;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.brain.DecisionSet;
import cz.cuni.mff.kocur.considerations.ConsiderDistanceToTarget;
import cz.cuni.mff.kocur.considerations.ConsiderGameTime;
import cz.cuni.mff.kocur.considerations.Consideration;
import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.decisions.DecisionBuilder;
import cz.cuni.mff.kocur.decisions.DecisionScoreEvaluator;
import cz.cuni.mff.kocur.decisions.RuneGoalDecision;
import cz.cuni.mff.kocur.decisions.Target;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.utility.LinearFunction;

/**
 * Class that contains sniper's goals. For now just a rune location at the start
 * of the game.
 * 
 * @author kocur
 *
 */
public class SniperGoalDS extends DecisionSet {

	public SniperGoalDS() {

	}

	/**
	 * Creates the decisions.
	 */
	public void createDecisions() {

		/*Decision setRuneAsAGoal = DecisionBuilder.build().setDecision(new RuneGoalDecision() {
			@Override
			public void updateContext(ExtendedAgentContext bc) {
				// We will need a location of the most distant creep, as he defines the front of
				// our lane.
				Location topBounty = bc.getMyJungle().getTopBounty();
				context.setTarget(new Target(topBounty));
			}
		}).setBonusFactor(3.0).setName("SetRuneGoal")
				.addConsideration(new ConsiderGameTime(), new LinearFunction(2.2, 1, 1.1, 1.5))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 90)
				.addConsideration(new ConsiderDistanceToTarget(), new LinearFunction(1, 1, 0.4, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 1000).setEvaluator(new DecisionScoreEvaluator())
				.get();

		this.add(setRuneAsAGoal);*/
		
		Decision setLastTowerAsAGoal = DecisionBuilder.build().setDecision(new RuneGoalDecision() {
			@Override
			public void updateContext(ExtendedAgentContext bc) {
				// We will need a location of the most distant creep, as he defines the front of
				// our lane.
				Location lastTower = bc.getMyLane().getLastStandingTower();
				context.setTarget(new Target(lastTower));
			}
		}).setBonusFactor(3.0).setName("SetRuneGoal")
				.addConsideration(new ConsiderGameTime(), new LinearFunction(2.2, 1, 1.1, 1.5))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 90)
				.addConsideration(new ConsiderDistanceToTarget(), new LinearFunction(1, 1, 0.4, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 1000).setEvaluator(new DecisionScoreEvaluator())
				.get();

		this.add(setLastTowerAsAGoal);

	}
}
