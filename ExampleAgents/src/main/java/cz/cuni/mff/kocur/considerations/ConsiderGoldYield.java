package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.world.Creep;

/**
 * Consideration that normalizes the maximum gold yield of BaseEntity to range
 * given by PARAM_RANGE_MIN and PARAM_RANGE_MAX.
 * 
 * @author kocur
 *
 */
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
