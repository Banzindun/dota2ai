package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.interests.Shop;

public class ConsiderSecretShop extends Consideration {

	public ConsiderSecretShop() {
		super();
		readableName = "ConsiderSecretShopDistance";
	}
	
	@Override
	public double score(DecisionContext context) {
		Shop s = (Shop) context.getTarget().getLocation();

		if (s.getType() == Shop.SECRET)
			return 0;
		else return 1;
	}
}
