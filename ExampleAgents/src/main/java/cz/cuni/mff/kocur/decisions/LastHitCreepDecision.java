package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.influence.InfluenceLayer;
import cz.cuni.mff.kocur.world.Creep;
import kocur.lina.agent.LayeredAgentContext;

/**
 * Decision to last hit a creep. This is a AttackCreepDecision, but that sets
 * some more data to creep.
 * 
 * @author kocur
 *
 */
public class LastHitCreepDecision extends AttackCreepDecision {

	@Override
	public void updateContext(ExtendedAgentContext bc, Creep c) {

		InfluenceLayer l = bc.getLayer(LayeredAgentContext.FRIENDLY_THREATS);

		double[] coords = l.getEntityCoordinates(c);
		double influence = l.get((int) coords[0], (int) coords[1]);

		Target target = new Target(c);
		target.setValue(influence);

		this.context.setTarget(target);
	}
}