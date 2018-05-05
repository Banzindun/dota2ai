package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.influence.GoalLayer;
import cz.cuni.mff.kocur.interests.Fountain;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.world.GridBase;
import kocur.lina.agent.LayeredAgentContext;

/**
 * Decision that sets a goal to fountain hero's fountain (inside the base).
 * 
 * @author kocur
 *
 */
public class StayNearFountainDecision extends Decision {

	@Override
	public AgentCommand execute() {
		Fountain fountain = (Fountain) context.getTarget().getLocation();

		if (fountain != null) {
			int gridx = fountain.getGridX();
			int gridy = fountain.getGridY();

			Location target = new Location(GridBase.getInstance().resolveXYBack(gridx, gridy));
			((GoalLayer) context.getBotContext().getLayer(LayeredAgentContext.GOAL)).setGoal(target);

		}

		// We are void decision, return null
		return null;
	}

	@Override
	public void updateContext(ExtendedAgentContext bc) {

	}

	@Override
	public void presetContext(ExtendedAgentContext bc) {
		context.setBotContext(bc);
		context.setSource(bc.getHero());
		context.setTarget(new Target(bc.getMyFountain()));
	}

}
