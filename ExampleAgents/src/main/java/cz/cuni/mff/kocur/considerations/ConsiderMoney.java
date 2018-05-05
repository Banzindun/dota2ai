package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;

/**
 * Consideration that considers agent's money and item's price. 
 * 
 * Score returns 0 if gold is lower than our price.
 * 
 * @author kocur
 *
 */
public class ConsiderMoney extends Consideration {
	
	public ConsiderMoney() {
		super();
		readableName = "ConsiderationMoney";
	}
	
	@Override
	public double score(DecisionContext context) {
		int price = context.getTarget().getItem().getPrice();
		int gold = context.getBotContext().getController().getHero().getGold();
		
		if (price < gold)
			return 1;
		else 
			return 0;
		
	}

}
