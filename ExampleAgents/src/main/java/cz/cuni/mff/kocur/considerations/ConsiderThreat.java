package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;

/**
 * Consideration that considers threat from target's value field.
 * 
 * return normalize(influence, min, max);
 * 
 * @author kocur
 *
 */
public class ConsiderThreat extends Consideration{
	public ConsiderThreat() {
		this.readableName = "ConsiderThreat";
	}
	
	
	@Override
	public double score(DecisionContext context) {
		double influence = context.getTarget().getValue();
		
		if (influence == Double.NEGATIVE_INFINITY) {
			return 0;
		}

		double max = this.getDoubleParameter(PARAM_RANGE_MAX);
		double min = this.getDoubleParameter(PARAM_RANGE_MIN);
		
		return normalize(influence, min, max);
	}	
}
