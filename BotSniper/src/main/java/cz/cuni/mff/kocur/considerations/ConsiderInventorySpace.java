package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.world.Hero;

public class ConsiderInventorySpace extends Consideration{


	@Override
	public double score(DecisionContext context) {
		Hero hero = (Hero) context.getSource();
		
		int type =  this.getIntParameter(PARAM_TYPE);
		double max = this.getDoubleParameter(PARAM_RANGE_MAX);
		double min = this.getDoubleParameter(PARAM_RANGE_MIN);
				
		int count = hero.getInventory().countFullSlots(type);
		
	
		return normalize(count, min, max);
	}
	
	
}
