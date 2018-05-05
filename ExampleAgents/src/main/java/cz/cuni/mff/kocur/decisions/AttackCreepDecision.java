package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.influence.InfluenceLayer;
import cz.cuni.mff.kocur.world.Creep;
import kocur.lina.agent.LayeredAgentContext;

public class AttackCreepDecision extends AttackTargetDecision {
	
	/**
	 * Updates the context using agent's context and a creep.
	 * @param bc Agent's context.
	 * @param c Creep.
	 */
	public void updateContext(ExtendedAgentContext bc, Creep c) {

		InfluenceLayer l = bc.getLayer(LayeredAgentContext.ENEMY_THREATS);

		double[] coords = l.getEntityCoordinates(c);
		double influence = l.get((int) coords[0], (int) coords[1]);

		Target target = new Target(c);
		target.setValue(influence);

		this.context.setTarget(target);
	}
}
