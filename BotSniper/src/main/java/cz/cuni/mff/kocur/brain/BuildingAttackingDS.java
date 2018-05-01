package cz.cuni.mff.kocur.brain;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.considerations.ConsiderTargetHealth;
import cz.cuni.mff.kocur.considerations.ConsiderThreat;
import cz.cuni.mff.kocur.considerations.Consideration;
import cz.cuni.mff.kocur.decisions.AttackBuildingDecision;
import cz.cuni.mff.kocur.decisions.DecisionBuilder;
import cz.cuni.mff.kocur.decisions.DecisionScoreEvaluator;
import cz.cuni.mff.kocur.decisions.Target;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.utility.LinearFunction;
import cz.cuni.mff.kocur.utility.PolynomialFunction;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.Building;

public class BuildingAttackingDS extends AttackingDS{
	/**
	 * Custom logger for Hero Attacking DS.
	 */
	private static final Logger logger = LogManager.getLogger(BuildingAttackingDS.class);
	
	protected LinkedList<AttackBuildingDecision> attackDecisions = new LinkedList<>();
	
	public BuildingAttackingDS() {}
	
	/**
	 * Lets inserts maxTargets decisions, that will handle the creep selection. 
	 * The decisions will look the same, only thing that will change will be the targets (creeps).
	 * 
	 */
	public void createDecisions() {
		for (int i = 0; i < maxTargets; i++) {
			AttackBuildingDecision decision = (AttackBuildingDecision) DecisionBuilder.build()
					// Consider threat on me ...
					// What else?? 
					
					.setDecision(new AttackBuildingDecision())
					.setName("AttackBuilding")
					.setBonusFactor(bonusFactor)
					.addConsideration(new ConsiderTargetHealth(),
							//new LogisticFunction(1.5, -0.5, 0.1, 1))
							new LinearFunction(-0.2, 1, 0, 1))
					// I should be more likely to attack on building that doesn't threaten me that much
					.addConsideration(
							new ConsiderThreat(),
							new PolynomialFunction(-3, 2, 1, 1))
					.addDoubleParameter(Consideration.PARAM_RANGE_MIN, -4)
					.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 0)
					.setEvaluator(new DecisionScoreEvaluator())
					.get();
			
			attackDecisions.add(decision);
			this.add(decision);			
		}
		
	}

	@Override
	public void updateContext(ExtendedBotContext botContext) {
		// Get list of entities around me, sorted by distance.
		List<BaseEntity> entitiesInAttackRange = botContext.findEntititesAroundMe(botContext.getHero().getAttackRange());
		
		int enemyTeam = botContext.getEnemyTeam();
		
		LinkedList<Building> enemies = new LinkedList<>();
		for (BaseEntity e : entitiesInAttackRange) {
			if (e.getTeam() == enemyTeam) {
				if (e.isBuilding() && !e.getName().contains("healer")){
					enemies.add((Building) e);
				}
			}
		}
		
		updateAttackBuildingDecisions(botContext, enemies);
			
		super.updateContext(botContext);
	}
	
	private void updateAttackBuildingDecisions(ExtendedBotContext botContext, LinkedList<Building> enemyBuildings) {
		int i = 0;
		for (AttackBuildingDecision d : attackDecisions) {
			if (enemyBuildings.size() != 0 && i < maxTargets) {
				Building b = enemyBuildings.removeFirst();
				d.updateContext(botContext, b);
				d.getContext().setBonusFactor(bonusFactor);
				i++;
			} else {
				d.getContext().setBonusFactor(0);
				d.setScore(0);
				d.getContext().setTarget(new Target());
			}		
		}
		
	}
}
