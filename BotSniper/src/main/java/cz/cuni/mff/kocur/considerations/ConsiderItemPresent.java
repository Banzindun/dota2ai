package cz.cuni.mff.kocur.considerations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.world.Hero;
import cz.cuni.mff.kocur.world.Item;

public class ConsiderItemPresent extends Consideration{

	private static Logger logger = LogManager.getLogger(ConsiderItemPresent.class);
	
	public ConsiderItemPresent() {
		super();
		readableName = "ConsiderItemPresent";
	}
	
	@Override
	public double score(DecisionContext context) {
		Hero hero = (Hero) context.getSource();
		Item target = context.getTarget().getItem();
		
		int index = hero.getInventory().findInInventory(target);
		
		double max = this.getDoubleParameter(PARAM_RANGE_MAX);
		double min = this.getDoubleParameter(PARAM_RANGE_MIN);
		
		if (index == -1)
			return max;
		
		return normalize(index, min, max);
	}

}
