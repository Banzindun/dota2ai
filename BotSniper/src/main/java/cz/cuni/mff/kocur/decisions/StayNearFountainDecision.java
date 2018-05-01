package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.influence.GoalLayer;
import cz.cuni.mff.kocur.server.AgentCommand;
import kocur.lina.bot.LayeredBotContext;

/**
 * Decision that sets a goal to fountain hero's fountain (inside the base).
 * @author kocur
 *
 */
public class StayNearFountainDecision extends Decision{

	@Override
	public AgentCommand execute() {
		Location loc = context.getTarget().getLocation();
		
		if (loc != null)
			((GoalLayer) context.getBotContext().getLayer(LayeredBotContext.GOAL)).setGoal(loc);	
		
		// We are void decision, return null
		return null;		
	}
	
	@Override
	public void updateContext(ExtendedBotContext bc) {
		
	}

	@Override
	public void presetContext(ExtendedBotContext bc) {
		context.setBotContext(bc);
		context.setSource(bc.getHero());
		context.setTarget(new Target(bc.getMyFountain()));
	}

}
