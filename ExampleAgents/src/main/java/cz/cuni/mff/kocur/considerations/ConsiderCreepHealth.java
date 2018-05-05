package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.world.Creep;
import cz.cuni.mff.kocur.world.Hero;

/**
 * Considers creep's health in relation to hero's damage. 
 * Creep is obtained from target using the getEntity().
 * 
 * return normalize((double) target.getHealth() / hero.getDamage(), min, max);
 * 
 * @author kocur
 *
 */
public class ConsiderCreepHealth extends Consideration {

	public ConsiderCreepHealth() {
		this.readableName = "ConsiderCreepHealth";
	}

	@Override
	public double score(DecisionContext context) {
		Hero hero = (Hero) context.getSource();
		Creep target = (Creep) context.getTarget().getEntity();

		double min = this.getDoubleParameter(PARAM_RANGE_MIN);
		double max = this.getDoubleParameter(PARAM_RANGE_MAX);

		if (target == null)
			return max;

		return normalize((double) target.getHealth() / hero.getDamage(), min, max);
	}
}