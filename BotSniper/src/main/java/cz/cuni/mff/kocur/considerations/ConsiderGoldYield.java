package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.world.Creep;

public class ConsiderGoldYield extends Consideration {

	public ConsiderGoldYield() {
		super();
		this.readableName = "ConsiderGoldYield";
	}
	
	@Override
	public double score(DecisionContext context) {
		Creep c = (Creep) context.getTarget().getEntity();
		
		double min = this.getDoubleParameter(PARAM_RANGE_MIN);
		double max = this.getDoubleParameter(PARAM_RANGE_MAX);
		
		if (c == null)
			return min;
			
		return normalize(c.getMaxGoldBounty(), min, max);
	}

	
	
}
