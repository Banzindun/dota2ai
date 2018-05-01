package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;

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
