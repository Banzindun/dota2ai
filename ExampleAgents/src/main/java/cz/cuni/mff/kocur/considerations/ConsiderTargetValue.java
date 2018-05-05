package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;

/**
 * Consideration that considers target's value. (context.getTarget.getValue())
 * 
 * return normalize(value, PARAM_RANGE_MIN, PARAM_RANGE_MAX);
 * 
 * @author kocur
 *
 */
public class ConsiderTargetValue extends Consideration {

	public ConsiderTargetValue() {
		super();
		
		readableName = "ConsiderInfluenceValue";
	}
	
	@Override
	public double score(DecisionContext context) {
		// Get the value we are considering
		double value = context.getTarget().getValue();

		// Get parameters specifying range
		double max = this.getDoubleParameter(PARAM_RANGE_MAX);
		double min = this.getDoubleParameter(PARAM_RANGE_MIN);
				
		return normalize(value, min, max);
	}
	
	

}
