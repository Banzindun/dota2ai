package cz.cuni.mff.kocur.brain;

import cz.cuni.mff.kocur.bot.AgentController;
import cz.cuni.mff.kocur.considerations.ConsiderDistanceToTarget;
import cz.cuni.mff.kocur.considerations.ConsiderGameTime;
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
import cz.cuni.mff.kocur.decisions.LastTowerAsAGoalDecision;
import cz.cuni.mff.kocur.decisions.RuneGoalDecision;
import cz.cuni.mff.kocur.decisions.StayNearFountainDecision;
import cz.cuni.mff.kocur.decisions.Target;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.utility.LinearFunction;
import cz.cuni.mff.kocur.utility.PolynomialFunction;
import cz.cuni.mff.kocur.utility.SinusoidFunction;

public class GoalDS extends DecisionSet {

	public GoalDS() {
		createDecisions();
	}

	private void createDecisions() {
		Decision setLaneCreepAsAGoal = DecisionBuilder.build()
				.setDecision(new CreepAsAGoalDecision())
				.setBonusFactor(3.0)
				.addConsideration(new ConsiderTimePassed(),
						new LinearFunction(2,1,0.6,0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 2)
				.addConsideration(new ConsiderDistanceToTarget(), new LinearFunction(1, 1, 0.4, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 1000).setName("SetLaneCreepAsAGoal")
				.setEvaluator(new DecisionScoreEvaluator()).get();

		this.add(setLaneCreepAsAGoal);
		
		Decision lastTowerAsAGoal = DecisionBuilder.build()
				.setDecision(new LastTowerAsAGoalDecision())
				.setBonusFactor(1.0)
				.addConsideration(new ConsiderTimePassed(),
						new LinearFunction(2,1,0.6,0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 2)
				.addConsideration(new ConsiderDistanceToTarget(), new LinearFunction(1, 1, 0.4, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 1000).setName("SetLastTowerAsAGoal")
				.setEvaluator(new DecisionScoreEvaluator()).get();

		this.add(lastTowerAsAGoal);
		
		Decision setRuneAsAGoal = DecisionBuilder.build()
				.setDecision(new RuneGoalDecision())
				.setBonusFactor(3.0)
				.setName("SetRuneGoal")
				.addConsideration(new ConsiderGameTime(),
						new LinearFunction(2.2,1,1.1,1.5))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 90)
				.addConsideration(new ConsiderDistanceToTarget(), new LinearFunction(1, 1, 0.4, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 1000)
				.setEvaluator(new DecisionScoreEvaluator()).get();

		this.add(setRuneAsAGoal);

		Decision escapeToBase = DecisionBuilder.build()
				.setDecision(new EscapeToBase())
				.setName("EscapeToBase")
				.setBonusFactor(5.0)
				.addConsideration(
						new ConsiderSourceHealth(),
						//new PolynomialFunction(-11, 2, 0, 1))
						new PolynomialFunction(7.4, -10, -1, -0.5))
				.setEvaluator(new DecisionScoreEvaluator())
				.get();
		
		this.add(escapeToBase);	
	
		Decision stayNearFountain = DecisionBuilder.build()
				.setDecision(new StayNearFountainDecision())
				.setName("StayNearFountainOnLowHealth")
				.setBonusFactor(7.0)
				.addConsideration(new ConsiderSourceHealth(), 
						new PolynomialFunction(1,2,1,-0.02))
				.addConsideration(new ConsiderDistanceToTarget(),
						new LinearFunction(-10, 3, 0.6, 0.05)) // Around 1200 range
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 2000)
				.setEvaluator(new DecisionScoreEvaluator())
				.get();
		
		this.add(stayNearFountain);
		
		Decision escapeFromThreat = DecisionBuilder.build()
				.setDecision(new DecisionEscapeFromThreat())
				.setName("EscapeFromThreat")
				.setBonusFactor(6.0)
				.addConsideration(new ConsiderThreat(), 
						new SinusoidFunction(3.6,1.8,1.55,0.2))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, -1)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 0)
				.setEvaluator(new DecisionScoreEvaluator())
				.get();
		
		this.add(escapeFromThreat);
		
	}

}
