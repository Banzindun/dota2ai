package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.server.TimeManager;

public class ConsiderGameTime extends Consideration{

	public ConsiderGameTime() {
		this.readableName = "ConsiderGameTime";
	}
	
	@Override
	public double score(DecisionContext context) {
		double max = getDoubleParameter(PARAM_RANGE_MAX);
		double min = getDoubleParameter(PARAM_RANGE_MIN);
				
		float current = TimeManager.getGameTime();		
		
		if (current >= max)
			return 0;
		
		return normalize(current, min, max);
	}
	
	

}
