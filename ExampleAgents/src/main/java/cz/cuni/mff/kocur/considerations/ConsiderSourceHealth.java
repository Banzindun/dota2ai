package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.world.BaseEntity;

/**
 * Consideration that considers the source's health.
 * 
 * return normalize(h.getHealth(), 0, h.getMaxHealth());
 * 
 * @author kocur
 *
 */
public class ConsiderSourceHealth extends Consideration {

	public ConsiderSourceHealth() {
		super();
		readableName = "ConsiderHealth";

	}

	@Override
	public double score(DecisionContext context) {
		BaseEntity h = (BaseEntity) context.getSource();

		return normalize(h.getHealth(), 0, h.getMaxHealth());
	}

}
