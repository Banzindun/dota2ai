package cz.cuni.mff.kocur.brain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.considerations.ConsiderCreepHealth;
import cz.cuni.mff.kocur.considerations.ConsiderGoldYield;
import cz.cuni.mff.kocur.considerations.ConsiderProjectileSpeed;
import cz.cuni.mff.kocur.considerations.ConsiderThreat;
import cz.cuni.mff.kocur.considerations.Consideration;
import cz.cuni.mff.kocur.decisions.AttackCreepDecision;
import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.decisions.DecisionBuilder;
import cz.cuni.mff.kocur.decisions.DecisionScoreEvaluator;
import cz.cuni.mff.kocur.decisions.LastHitCreepDecision;
import cz.cuni.mff.kocur.decisions.Target;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.utility.BinaryFunction;
import cz.cuni.mff.kocur.utility.LinearFunction;
import cz.cuni.mff.kocur.utility.PolynomialFunction;
import cz.cuni.mff.kocur.utility.SinusoidFunction;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.Creep;

/**
 * Decision set that will configure, store and update decisions, that should
 * handle attacking creeps and last hitting.
 * 
 * 
 * 
 * @author kocur
 *
 */
public class CreepAttackingDS extends AttackingDS {
	/**
	 * Logger registered for CreepTargetingDS class.
	 */
	private static final Logger logger = LogManager.getLogger(CreepAttackingDS.class);

	protected ArrayList<LastHitCreepDecision> lastHitDecisions = new ArrayList<>();

	protected ArrayList<AttackCreepDecision> attackDecisions = new ArrayList<>();

	/**
	 * Bonus factor for attacking a creep.
	 */
	protected double attackBonusFactor = 1;
	
	/**
	 * Bonus factor for denying a creep. (my own creep)
	 */
	protected double denyBonusFactor = 1;
	
	/**
	 * Bonus factor for last hitting.
	 */
	protected double lastHitBonusFactor = 1;
	
	public CreepAttackingDS() {
	
	}

	public void createDecisions() {
		for (int i = 0; i < this.maxTargets; i++) {
			addLastHitDecision();
			addAttackDecision();
		}
	}

	private void addLastHitDecision() {
		LastHitCreepDecision decision = (LastHitCreepDecision) DecisionBuilder.build()
				.setDecision(new LastHitCreepDecision()).setName("LastHitCreep").setBonusFactor(lastHitBonusFactor)
				.addConsideration(new ConsiderCreepHealth(),
						// new LogisticFunction(1.5, -0.5, 0.1, 1))
						new BinaryFunction(0.5, -1, 0.7, 0.5))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 4)
				.addConsideration(new ConsiderCreepHealth(),
						new LinearFunction(-0.1, 1, -4, 1.4))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 4)				
				.addConsideration(new ConsiderThreat(), new PolynomialFunction(-0.9, 2, 1, 1.2))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 1)
				// Max gold bounty ranges from 30-80 gold pieces (the bigger one is for
				// catapult)
				.addConsideration(new ConsiderGoldYield(), new LinearFunction(0.3, 1, -0.7, 0.5))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 100).setEvaluator(new DecisionScoreEvaluator())
				//.addConsideration(new ConsiderProjectileSpeed(), new LinearFunction(-0.1, 1, 1, 0.9))
				//.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				//.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 1)
				.setEvaluator(new DecisionScoreEvaluator())
				.get();

		lastHitDecisions.add(decision);
		this.add(decision);
	}

	private void addAttackDecision() {
		AttackCreepDecision decision = (AttackCreepDecision) DecisionBuilder.build()
				.setDecision(new AttackCreepDecision()).setName("AttackCreep").setBonusFactor(attackBonusFactor)
				// Consider creep's health, we should be more likely to attack if its health is
				// higher than 2 times my attack damage
				.addConsideration(new ConsiderCreepHealth(),
						new SinusoidFunction(1.7, 1.4, 5.8, 0.6))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 4)
				// Consider threat on the target, we should attack creeps that are in place with higher thread
				.addConsideration(new ConsiderThreat(), new PolynomialFunction(-0.9, 2, -0.2, 1.2))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, -1)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 0)
				.setEvaluator(new DecisionScoreEvaluator())
				.get();

		attackDecisions.add(decision);
		this.add(decision);
	}

	@Override
	public void updateContext(ExtendedBotContext botContext) {
		// Get list of entities around me, sorted by distance.
		List<BaseEntity> entitiesInAttackRange = botContext
				.findEntititesAroundMe(botContext.getHero().getAttackRange());

		int team = botContext.getEnemyTeam();

		ArrayList<Creep> enemyCreeps = new ArrayList<>();
		for (BaseEntity e : entitiesInAttackRange) {
			if (e.getTeam() == team) {
				if (e.isCreep()) {
					enemyCreeps.add((Creep) e);
				}
			}
		}

		updateAttackCreepDecisions(botContext, enemyCreeps);

		super.updateContext(botContext);
	}

	private void updateAttackCreepDecisions(ExtendedBotContext botContext, ArrayList<Creep> enemyCreeps) {
		
		int i = 0;
		
		for (Creep c : enemyCreeps) {
			if ( i < maxTargets ) {
				AttackCreepDecision ac = attackDecisions.get(i);
				ac.getContext().setBonusFactor(attackBonusFactor);
				ac.updateContext(botContext, c);
				
				LastHitCreepDecision lhc = lastHitDecisions.get(i);
				lhc.getContext().setBonusFactor(lastHitBonusFactor);
				lhc.updateContext(botContext, c);
				
				i++;				
			}			
		}
		
		for(; i < maxTargets; i++) {
			Decision d = attackDecisions.get(i);
			turnOffDecision(d);
			
			d = lastHitDecisions.get(i);
			turnOffDecision(d);		
		}
	}
	
	private void turnOffDecision(Decision d) {
		d.getContext().setBonusFactor(0);
		d.setScore(0);
		d.getContext().setTarget(new Target());
	}

	public ArrayList<AttackCreepDecision> getAttackDecisions() {
		return attackDecisions;
	}

	public void setAttackDecisions(ArrayList<AttackCreepDecision> attackDecisions) {
		this.attackDecisions = attackDecisions;
	}

	public double getAttackBonusFactor() {
		return attackBonusFactor;
	}

	public void setAttackBonusFactor(double attackBonusFactor) {
		this.attackBonusFactor = attackBonusFactor;
	}

	public double getDenyBonusFactor() {
		return denyBonusFactor;
	}

	public void setDenyBonusFactor(double denyBonusFactor) {
		this.denyBonusFactor = denyBonusFactor;
	}

	public double getLastHitBonusFactor() {
		return lastHitBonusFactor;
	}

	public void setLastHitBonusFactor(double lastHitBonusFactor) {
		this.lastHitBonusFactor = lastHitBonusFactor;
	}
}
