package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.world.BaseEntity;

public class ConsiderProjectileSpeed extends Consideration{

	@Override
	public double score(DecisionContext context) {
		BaseEntity e = (BaseEntity) context.getSource();
		
		double max = this.getDoubleParameter(PARAM_RANGE_MAX);
		double min = this.getDoubleParameter(PARAM_RANGE_MIN);
		
		return normalize(e.getProjectileSpeed(), min, max);
	}

}
