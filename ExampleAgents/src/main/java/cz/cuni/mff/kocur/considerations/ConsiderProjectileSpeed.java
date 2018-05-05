package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.world.BaseEntity;

/**
 * Class that represents a consideration, that aims at considering speed of the
 * hero's projectiles.
 * 
 * return normalize(e.getProjectileSpeed(), PARAM_RANGE_MIN, PARAM_RANGE_MAX);
 * 
 * @author kocur
 *
 */
public class ConsiderProjectileSpeed extends Consideration {

	public ConsiderProjectileSpeed() {
		this.readableName = "ConsiderProjectileSpeed";
	}

	@Override
	public double score(DecisionContext context) {
		BaseEntity e = (BaseEntity) context.getSource();

		double max = this.getDoubleParameter(PARAM_RANGE_MAX);
		double min = this.getDoubleParameter(PARAM_RANGE_MIN);

		return normalize(e.getProjectileSpeed(), min, max);
	}

}
