package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.world.Hero;

public class ConsiderTargetValue extends Consideration {

	public ConsiderTargetValue() {
		super();
		
		readableName = "ConsiderInfluenceValue";
	}
	
	@Override
	public double score(DecisionContext context) {
		// Get the hero
		Hero h = (Hero) context.getSource();
		
		// Get the value we are considering
		double value = context.getTarget().getValue();

		// Get parameters specifying range
		double max = this.getDoubleParameter(PARAM_RANGE_MAX);
		double min = this.getDoubleParameter(PARAM_RANGE_MIN);
				
		return normalize(value, min, max);
	}
	
	

}
