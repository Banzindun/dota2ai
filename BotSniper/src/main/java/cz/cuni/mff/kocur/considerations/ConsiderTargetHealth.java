package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.world.BaseEntity;

public class ConsiderTargetHealth extends Consideration {
	
	public ConsiderTargetHealth() {
		super();
		readableName = "ConsiderTargetHealth";
	}
	
	@Override
	public double score(DecisionContext context) {
		BaseEntity target = context.getTarget().getEntity();
		
		if (target == null)
			return 1;
		
		return normalize(target.getHealth(), 0, target.getMaxHealth());
	}

	
	
}
