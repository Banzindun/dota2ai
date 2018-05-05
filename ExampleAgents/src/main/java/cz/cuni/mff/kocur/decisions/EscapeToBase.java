package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.agent.AgentController;
import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.influence.GoalLayer;
import cz.cuni.mff.kocur.interests.Fountain;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.world.GridBase;
import kocur.lina.agent.LayeredAgentContext;

/**
 * Decision to escape to base.
 * 
 * @author kocur
 *
 */
public class EscapeToBase extends Decision {

	@Override
	public AgentCommand execute() {
		// In target, there should be a location of the base, where we are escaping
		Fountain fountain = (Fountain) context.getTarget().getLocation();
		if (fountain == null) {
			return null;
		}
		
		int gridx = fountain.getGridX();
		int gridy = fountain.getGridY();

		Location target = new Location(GridBase.getInstance().resolveXYBack(gridx, gridy));
		
		// Set the creep to goal layer as our goal.
		((GoalLayer) context.getBotContext().getLayer(LayeredAgentContext.GOAL)).setGoal(target);

		// Set time of this decision
		super.execute();

		return null;
	};

	@Override
	public void updateContext(ExtendedAgentContext bc) {
		// Bot is set, hp should be updated.

	}

	@Override
	public void presetContext(ExtendedAgentContext bc) {
		context.setBotContext(bc);
		AgentController controller = bc.getController();

		// Who is the source? Hero.
		context.setSource(bc.getController().getHero());

		// Where should I escape to? To my base.
		context.setTarget(new Target(controller.getContext().getMyFountain()));
	}

}
