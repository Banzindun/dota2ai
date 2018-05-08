package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.world.GridBase;

/**
 * Considers distance to target.
 * @author kocur
 *
 */
public class ConsiderDistanceToTarget extends Consideration{
	
	public ConsiderDistanceToTarget() {
		super();
		readableName = "ConsiderDistanceToTarget";
	}
	
	@Override
	public double score(DecisionContext context) {
		Location s = context.getSource();
		Location target = context.getTarget().getLocation();
			
		if (target == null) {
			target = context.getTarget().getEntity();
			if (target == null) {
				//logger.warn("Target is not location nor entity.");
				return 0;
			}
		}
		
				
		double minRange = getDoubleParameter(PARAM_RANGE_MIN);
		double maxRange = getDoubleParameter(PARAM_RANGE_MAX);
		
		double distance = GridBase.distance(s, target);
		double normalized = normalize(distance, minRange, maxRange);
		
		// logger.info("Normalized distance to target: " + normalized);
		
		return normalized;
	}

	
	
}
