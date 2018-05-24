package cz.cuni.mff.kocur.brain;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.kocur.considerations.ConsiderCreepHealth;
import cz.cuni.mff.kocur.considerations.ConsiderGoldYield;
import cz.cuni.mff.kocur.considerations.ConsiderProjectileSpeed;
import cz.cuni.mff.kocur.considerations.ConsiderThreat;
import cz.cuni.mff.kocur.considerations.ConsiderTimePassed;
import cz.cuni.mff.kocur.considerations.Consideration;
import cz.cuni.mff.kocur.decisions.AttackCreepDecision;
import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.decisions.DecisionBuilder;
import cz.cuni.mff.kocur.decisions.DecisionScoreEvaluator;
import cz.cuni.mff.kocur.decisions.LastHitCreepDecision;
import cz.cuni.mff.kocur.decisions.Target;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.utility.BinaryFunction;
import cz.cuni.mff.kocur.utility.LinearFunction;
import cz.cuni.mff.kocur.utility.PolynomialFunction;
import cz.cuni.mff.kocur.utility.SinusoidFunction;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.Creep;

/**
 * Decision set that configures, store and updates decisions, that should handle
 * attacking creeps and last hitting.
 * 
 * @author kocur
 *
 */
public class CreepAttackingDS extends AttackingDS {
	/**
	 * Array of decisions to last hit a creep.
	 */
	protected ArrayList<LastHitCreepDecision> lastHitDecisions = new ArrayList<>();

	/**
	 * Array of decisions to attack a creep.
	 */
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

	/**
	 * Creates the decisions.
	 */
	public void createDecisions() {
		for (int i = 0; i < this.maxTargets; i++) {
			addLastHitDecision();
			addAttackDecision();
		}
	}

	/**
	 * Adds last hit decisions.
	 */
	private void addLastHitDecision() {
		LastHitCreepDecision decision = (LastHitCreepDecision) DecisionBuilder.build()
				.setDecision(new LastHitCreepDecision()).setName("LastHitCreep").setBonusFactor(lastHitBonusFactor)
				// We consider creeps health.
				.addConsideration(new ConsiderCreepHealth(),
						// new LogisticFunction(1.5, -0.5, 0.1, 1))
						new BinaryFunction(0.5, -1, 0.6, 0.5))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 3) // Is this good?
				// .addConsideration(new ConsiderCreepHealth(),
				// new LinearFunction(-0.1, 1, -4, 1.4))
				// .addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				// .addDoubleParameter(Consideration.PARAM_RANGE_MAX, 4)
				// Consider thread on the creep - we prefer more threatened creeps, as they are
				// probably being attacked
				.addConsideration(new ConsiderThreat(), new LinearFunction(0.2, 1, -4.2, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 1)
				// Max gold bounty ranges from 30-80 gold pieces (melee-catapult)
				// We want to prefer creeps, that yield more.
				.addConsideration(new ConsiderGoldYield(), new LinearFunction(0.3, 1, -0.7, 0.5))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 100).setEvaluator(new DecisionScoreEvaluator())
				// We need to consider our projectile speed - and lasthit attack sooner if
				// needed
				.addConsideration(new ConsiderProjectileSpeed(), new LinearFunction(-0.2, 1, 5.1, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 3000).setEvaluator(new DecisionScoreEvaluator())
				.get();

		lastHitDecisions.add(decision);
		this.add(decision);
	}

	/**
	 * Adds attack decisions.
	 */
	private void addAttackDecision() {
		AttackCreepDecision decision = (AttackCreepDecision) DecisionBuilder.build()
				.setDecision(new AttackCreepDecision()).setName("AttackCreep").setBonusFactor(attackBonusFactor)
				// We consider time passed, we do not want to attack all the time
				// There should be a space for other things and for movement
				.addConsideration(new ConsiderTimePassed(), new LinearFunction(2, 1, 0.6, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 1.5)
				// Consider creep's health, we should be more likely to attack if its health is
				// higher than 2 times my attack damage
				.addConsideration(new ConsiderCreepHealth(), new SinusoidFunction(1.9, 2, 5, 0.6))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 4)
				// Consider threat on the target, we should attack creeps that are in place with
				// higher thread
				.addConsideration(new ConsiderThreat(), new PolynomialFunction(-0.9, 2, -0.2, 1.2))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, -1)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 0).setEvaluator(new DecisionScoreEvaluator()).get();

		attackDecisions.add(decision);
		this.add(decision);
	}

	@Override
	public void updateContext(ExtendedAgentContext agentContext) {
		// Get list of entities around me, sorted by distance.
		List<BaseEntity> entitiesInAttackRange = agentContext
				.findEntititesAroundMe(agentContext.getHero().getAttackRange());

		int team = agentContext.getEnemyTeam();

		// Take the enemy creeps
		ArrayList<Creep> enemyCreeps = new ArrayList<>();
		for (BaseEntity e : entitiesInAttackRange) {
			if (e.getTeam() == team) {
				if (e.isCreep()) {
					enemyCreeps.add((Creep) e);
				}
			}
		}

		// Update attack creep decisions
		updateAttackCreepDecisions(agentContext, enemyCreeps);

		super.updateContext(agentContext);
	}

	/**
	 * Updates decisions to attack creep.
	 * 
	 * @param agentContext
	 *            Agent's context
	 * @param enemyCreeps
	 *            Array of enemy creeps we can target.
	 */
	private void updateAttackCreepDecisions(ExtendedAgentContext agentContext, ArrayList<Creep> enemyCreeps) {

		int i = 0;

		for (Creep c : enemyCreeps) {
			if (i < maxTargets) {
				AttackCreepDecision ac = attackDecisions.get(i);
				ac.getContext().setBonusFactor(attackBonusFactor);
				ac.updateContext(agentContext, c);

				LastHitCreepDecision lhc = lastHitDecisions.get(i);
				lhc.getContext().setBonusFactor(lastHitBonusFactor);
				lhc.updateContext(agentContext, c);

				i++;
			}
		}

		for (; i < maxTargets; i++) {
			Decision d = attackDecisions.get(i);
			turnOffDecision(d);

			d = lastHitDecisions.get(i);
			turnOffDecision(d);
		}
	}

	/**
	 * Turns some decision off by setting it's bonus factor to 0.
	 * 
	 * @param d
	 *            Decision to be turned off.
	 */
	private void turnOffDecision(Decision d) {
		d.getContext().setBonusFactor(0);
		d.setScore(0);
		d.getContext().setTarget(new Target());
	}

	/**
	 * 
	 * @return Returns list of attack decisions.
	 */
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
