package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.world.Ability;

/**
 * Considers cooldown of the ability that is obtained from target using
 * getAbility().
 * 
 * return ability.getCooldownTimeRemaining() / ability.getCooldownTime();
 * 
 * @author kocur
 *
 */
public class ConsiderCooldown extends Consideration {

	public ConsiderCooldown() {
		super();
		readableName = "ConsiderCooldown";
	}

	@Override
	public double score(DecisionContext context) {
		Ability ability = (Ability) context.getTarget().getAbility();
		return ability.getCooldownTimeRemaining() / ability.getCooldownTime();
	}

}
