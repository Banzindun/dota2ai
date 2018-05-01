package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.world.Ability;
import cz.cuni.mff.kocur.world.BaseEntity;

public class ConsiderCooldown extends Consideration{

	public ConsiderCooldown() {
		super();
		readableName = "ConsiderCooldown";
	}
	
	@Override
	public double score(DecisionContext context) {
		Ability ability = (Ability) context.getTarget().getAbility();
		return ability.getCooldownTimeRemaining()/ability.getCooldownTime();
	}

}
