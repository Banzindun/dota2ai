package cz.cuni.mff.kocur.brain;

import cz.cuni.mff.kocur.considerations.ConsiderActive;
import cz.cuni.mff.kocur.considerations.ConsiderDistanceToTarget;
import cz.cuni.mff.kocur.considerations.ConsiderItemPresent;
import cz.cuni.mff.kocur.considerations.ConsiderSourceHealth;
import cz.cuni.mff.kocur.considerations.ConsiderSourceMana;
import cz.cuni.mff.kocur.considerations.ConsiderTimePassed;
import cz.cuni.mff.kocur.considerations.Consideration;
import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.decisions.DecisionBuilder;
import cz.cuni.mff.kocur.decisions.DecisionScoreEvaluator;
import cz.cuni.mff.kocur.decisions.DecisionUsePotion;
import cz.cuni.mff.kocur.decisions.PickupRuneDecision;
import cz.cuni.mff.kocur.utility.BinaryFunction;
import cz.cuni.mff.kocur.utility.EmptyFunction;
import cz.cuni.mff.kocur.utility.LinearFunction;
import cz.cuni.mff.kocur.utility.PolynomialFunction;
import cz.cuni.mff.kocur.world.Inventory;

/**
 * Decision set, that contains predefined decisions, that handle utility
 * decisions. By that we mean decisions, that use potions, pick runes etc.
 * 
 * @author kocur
 *
 */
public class UtilityDS extends DecisionSet {
	
	/**
	 * Constructor. Creates decisions.
	 */
	public UtilityDS() {

		createDecisions();
	}
	
	/**
	 * Creates the decisions.
	 */
	private void createDecisions() {
		Decision useHealingPotion = DecisionBuilder.build().setDecision(new DecisionUsePotion("item_flask"))
				.setName("UseHealingPotion").setBonusFactor(15)
				// Consider passed time - we do not want to use all the potions at once
				.addConsideration(new ConsiderTimePassed(), new LinearFunction(2, 1, 0.6, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 20) // After 10 seconds 1.0
				// Consider our health, we should be low to use the potion
				.addConsideration(new ConsiderSourceHealth(), new LinearFunction(-4, 1, 0.1, 1.5))
				// Consider that we have the item in our inventory
				.addConsideration(new ConsiderItemPresent(),
						new BinaryFunction(0.5, -1, (double) Inventory.inventorySlotMax / (Inventory.stashSlotMax + 1),
								0.5))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, Inventory.stashSlotMax + 1)
				.setEvaluator(new DecisionScoreEvaluator()).get();

		this.add(useHealingPotion);

		Decision useManaPotion = DecisionBuilder.build().setDecision(new DecisionUsePotion("item_clarity"))
				.setName("UseManaPotion").setBonusFactor(15)
				// Consider passed time - we do not want to use all the potions at once
				.addConsideration(new ConsiderTimePassed(), new LinearFunction(2, 1, 0.6, 0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 20) // After 10 seconds 1.0
				// Consider our mana, we should have it low to use the potion
				.addConsideration(new ConsiderSourceMana(), new PolynomialFunction(2, -10, -1, -0.02))
				// Consider that we have the item in our inventory
				.addConsideration(new ConsiderItemPresent(),
						new BinaryFunction(0.5, -1, (double) Inventory.inventorySlotMax / (Inventory.stashSlotMax + 1),
								0.5))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, Inventory.stashSlotMax + 1)
				.setEvaluator(new DecisionScoreEvaluator()).get();

		this.add(useManaPotion);

		Decision pickupRune = DecisionBuilder.build().setDecision(new PickupRuneDecision()).setName("PickupRune")
				.setBonusFactor(5)
				// Consider, that the rune should be active
				.addConsideration(new ConsiderActive(), new EmptyFunction())
				// Consider our distance to the rune
				.addConsideration(new ConsiderDistanceToTarget(), new LinearFunction(-3.8, 1, 0, 1.5))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 400).setEvaluator(new DecisionScoreEvaluator())
				.get();

		this.add(pickupRune);
	}

}
