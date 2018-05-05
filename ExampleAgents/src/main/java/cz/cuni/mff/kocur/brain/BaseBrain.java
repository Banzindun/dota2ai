package cz.cuni.mff.kocur.brain;

import cz.cuni.mff.kocur.influence.ExtendedAgentContext;

/**
 * The purpose of this class is to represent a brain with decision sets, that
 * might be used by every bot. This brain should use decision sets, that are
 * quite global in their nature. Meaning they define decisions, that might be
 * performed by any hero. That includes their movement, some basic creep and
 * hero selection etc.
 * 
 * The basic brain should also include some void decisions for laning etc.
 * 
 * @author kocur
 *
 */
public class BaseBrain extends Brain {
	/**
	 * Constructor for the BaseBrain.
	 * 
	 * @param botContext
	 *            Bot context, that will be used during thinking.
	 */
	public BaseBrain(ExtendedAgentContext botContext) {
		super(botContext);
	}

	@Override
	public void createDecisions() {
		// Decision set that handles the creep attacking.
		CreepAttackingDS creepAttacking = new CreepAttackingDS();
		creepAttacking.setAttackBonusFactor(3.0);
		creepAttacking.setLastHitBonusFactor(6.0);
		creepAttacking.setMaxTargets(6);
		creepAttacking.createDecisions();

		this.addDecisionSet(creepAttacking);

		// Decision set, that handles attacking the heroes.
		HeroAttackingDS heroAttacking = new HeroAttackingDS();
		heroAttacking.setBonusFactor(6.0);
		heroAttacking.createDecisions();

		this.addDecisionSet(heroAttacking);

		// Decision set that considers attacking a buildings .
		BuildingAttackingDS buildingAttacking = new BuildingAttackingDS();
		buildingAttacking.setBonusFactor(5.0);
		buildingAttacking.setMaxTargets(3);
		buildingAttacking.createDecisions();

		this.addDecisionSet(buildingAttacking);

		// Decision set that handles the utilities (using the potions).
		UtilityDS utilityDS = new UtilityDS();
		this.addDecisionSet(utilityDS);

		// Decision set that handles escaping.
		//EscapeDS escapeDS = new EscapeDS();
		//this.addDecisionSet(escapeDS);

		// Decision set that handles movement.
		MovementDS movementDS = new MovementDS();
		this.addDecisionSet(movementDS);

		// Decision set that handles shopping.
		ShoppingDS shoppingDS = new ShoppingDS();
		this.addDecisionSet(shoppingDS);

		///////////////////// Void decision sets. ////////////////////

		// Decision for chaning the goals of hero.
		GoalDS goalDecisionSet = new GoalDS();
		this.addVoidDecisionSet(goalDecisionSet);

	}

}
