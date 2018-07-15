package cz.cuni.mff.kocur.brain;

import cz.cuni.mff.kocur.considerations.ConsiderDistanceToTarget;
import cz.cuni.mff.kocur.considerations.ConsiderInventorySpace;
import cz.cuni.mff.kocur.considerations.ConsiderMoney;
import cz.cuni.mff.kocur.considerations.ConsiderTimePassed;
import cz.cuni.mff.kocur.considerations.Consideration;
import cz.cuni.mff.kocur.decisions.BuyDecision;
import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.decisions.DecisionBuilder;
import cz.cuni.mff.kocur.decisions.DecisionScoreEvaluator;
import cz.cuni.mff.kocur.decisions.GrabAllDecision;
import cz.cuni.mff.kocur.utility.BinaryFunction;
import cz.cuni.mff.kocur.utility.LinearFunction;
import cz.cuni.mff.kocur.utility.LogisticFunction;
import cz.cuni.mff.kocur.world.Inventory;

/**
 * Decision set that handles shopping. 
 * @author kocur
 *
 */
public class ShoppingDS extends DecisionSet{

	/**
	 * Constructor, that creates all the decisions.
	 */
	public ShoppingDS(){
		
		
		createDecisions();
	}

	/**	
	 * Creates decisions.
	 */
	private void createDecisions() {
		Decision buyItemInstantly = DecisionBuilder.build()
				.setDecision(new BuyDecision())
				.setBonusFactor(1.0)
				.setName("BuyItems")
				// Consider our money, we should have more than the item's price
				.addConsideration(
						new ConsiderMoney(),
						new BinaryFunction(0.5, 1, 0.5, 0.5))
				.addConsideration(new ConsiderTimePassed(),
						new LinearFunction(2,1,0.6,0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 4) // After 2 seconds 1.0
				// Consider our distance to the shop
				.addConsideration(new ConsiderDistanceToTarget(),
						new LogisticFunction(-1.0, 1.0, 0.1, 0.0))
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 1200)
				.setEvaluator(new DecisionScoreEvaluator())
				.get();
		
		this.add(buyItemInstantly);
		
		Decision grabAll = DecisionBuilder.build()
				.setDecision(new GrabAllDecision())
				.setName("GrabAllItems")
				.setBonusFactor(15.0)
				// Consider our distance to shop
				.addConsideration(new ConsiderDistanceToTarget(),
						new BinaryFunction(0.5, -1, 0.99, 0.5)) // I will want to take item on distance of 800
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, 1500)
				// COnsider that the stash is not empty
				.addConsideration(new ConsiderInventorySpace(),
						new BinaryFunction(0.5, 1, 0.01, 0.5)) // On empty stash will be 0, else 1
				.addIntParameter(Consideration.PARAM_TYPE, Inventory.STASH)
				.addDoubleParameter(Consideration.PARAM_RANGE_MIN, 0)
				.addDoubleParameter(Consideration.PARAM_RANGE_MAX, Inventory.stashSlotMax)
				.setEvaluator(new DecisionScoreEvaluator())
				.get();
		
		this.add(grabAll);
	}
	
	
	
}
