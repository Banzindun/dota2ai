package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.influence.GoalLayer;
import cz.cuni.mff.kocur.influence.InfluenceLayer;
import cz.cuni.mff.kocur.server.AgentCommand;
import kocur.lina.agent.LayeredAgentContext;

/**
 * Decision that moves us to best farming position (using the farm layer).
 * 
 * @author kocur
 *
 */
public class MoveToFarmingPosition extends Decision {

	@Override
	public AgentCommand execute() {

		// Get goal layer
		GoalLayer goalLayer = ((GoalLayer) context.getBotContext().getLayer(LayeredAgentContext.GOAL));

		// Set the goal location
		goalLayer.setGoal(context.getTarget().getLocation());
		return null;
	}
	
	@Override
	public void updateContext(ExtendedAgentContext bc) {
		InfluenceLayer l = bc.getLayer(LayeredAgentContext.FARM);

		// Get maximum location in farming layer. This will return null, if position is
		// not set.
		Location maxLocation = l.getMaxLocation();
		
		// If maxLocation is null set empty target
		if (maxLocation == null) {
			context.setTarget(new Target());
			return;
		}

		// Set the target to be the maximum location
		Target target = new Target(maxLocation);
		
		// Max value will be initially set to MIN_DOUBLE, so if we use
		// ConsiderTargetValue, then unset max value will be normalized to 0
		target.setValue(l.getMax());

		context.setTarget(target);
	}

	@Override
	public void presetContext(ExtendedAgentContext bc) {
		context.setBotContext(bc);
		context.setSource(bc.getController().getHero());
	}

}
