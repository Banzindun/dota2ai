package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.influence.InfluenceLayer;
import kocur.lina.agent.LayeredAgentContext;

/**
 * Decision that moves us to best farming position (using the farm layer).
 * 
 * @author kocur
 *
 */
public class MoveToFarmingPosition extends MoveToDecision {

	@Override
	public void updateContext(ExtendedAgentContext bc) {
		InfluenceLayer l = bc.getLayer(LayeredAgentContext.FARM);

		// Get maximum location in farming layer. This will return null, if position is
		// not set.!
		Location maxLocation = l.getMaxLocation();

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
