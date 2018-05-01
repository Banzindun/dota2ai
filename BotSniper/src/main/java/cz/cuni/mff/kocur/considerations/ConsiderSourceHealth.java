package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.world.BaseEntity;

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
