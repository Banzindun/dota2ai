package cz.cuni.mff.kocur.brain;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.kocur.base.Constants;
import cz.cuni.mff.kocur.considerations.ConsiderTargetHealth;
import cz.cuni.mff.kocur.considerations.ConsiderThreat;
import cz.cuni.mff.kocur.considerations.Consideration;
import cz.cuni.mff.kocur.decisions.AttackHeroDecision;
import cz.cuni.mff.kocur.decisions.DecisionBuilder;
import cz.cuni.mff.kocur.decisions.DecisionScoreEvaluator;
import cz.cuni.mff.kocur.decisions.Target;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.utility.LinearFunction;
import cz.cuni.mff.kocur.utility.PolynomialFunction;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.Hero;

/**
 * Decision set, that handles attacking heroes.
 * @author kocur
 *
 */
public class HeroAttackingDS extends AttackingDS{
	/**
	 * List of attack decisions.
	 */
	protected LinkedList<AttackHeroDecision> attackDecisions = new LinkedList<>();
	
	public HeroAttackingDS() {}
	
	/**
	 * Inserts maxTargets decisions, that will handle the creep selection. 
	 * The decisions will look the same, only thing that will change will be the targets (heroes).
	 * 
	 */
	public void createDecisions() {
		for (int i = 0; i < Constants.MAX_HEROES; i++) {
			AttackHeroDecision decision = (AttackHeroDecision) DecisionBuilder.build()
					.setDecision(new AttackHeroDecision())
					.setName("AttackHero")
					.setBonusFactor(bonusFactor)
					.addConsideration(new ConsiderTargetHealth(),
							//new LogisticFunction(1.5, -0.5, 0.1, 1))
							new LinearFunction(-0.2, 1, 0, 1))
					// I should be more likely to attack him if he is threatened by my allies
					// I will look only to friendly_threats layer for that
					.addConsideration(
							new ConsiderThreat(),
							new PolynomialFunction(-0.25, 2, 1.2, 1))
					.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
					.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 1)
					.setEvaluator(new DecisionScoreEvaluator())
					.get();
			
			attackDecisions.add(decision);
			this.add(decision);			
		}
		
	}

	@Override
	public void updateContext(ExtendedAgentContext botContext) {
		// Get list of entities around me, sorted by distance.
		List<BaseEntity> entitiesInAttackRange = botContext.findEntititesAroundMe(botContext.getHero().getAttackRange());
		
		int team = botContext.getEnemyTeam();
		
		LinkedList<Hero> enemyHeroes = new LinkedList<>();
		for (BaseEntity e : entitiesInAttackRange) {
			if (e.getTeam() == team) {
				if (e.getName().contains("hero")) {
					enemyHeroes.add((Hero) e);
				}
			}
		}
		
		updateAttackHeroDecisions(botContext, enemyHeroes);
			
		super.updateContext(botContext);
	}
	
	/**
	 * Updates attack hero decisions according to context.
	 * @param botContext Agent's context.
	 * @param enemyHeroes List of enemy heroes.
	 */
	private void updateAttackHeroDecisions(ExtendedAgentContext botContext, LinkedList<Hero> enemyHeroes) {
		int i = 0;
		for (AttackHeroDecision d : attackDecisions) {
			if (enemyHeroes.size() != 0 && i < maxTargets) {
				Hero hero = enemyHeroes.removeFirst();
				d.updateContext(botContext, hero);
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
