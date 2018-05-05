package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.influence.InfluenceLayer;
import cz.cuni.mff.kocur.world.Building;
import kocur.lina.agent.LayeredAgentContext;

/**
 * Decision to attack a building.
 * 
 * @author kocur
 *
 */
public class AttackBuildingDecision extends AttackTargetDecision {

	/**
	 * Updates a context using agent's context and building.
	 * 
	 * @param botContext
	 *            Agent's context.
	 * @param b
	 *            Building.
	 */
	public void updateContext(ExtendedAgentContext botContext, Building b) {
		InfluenceLayer l = botContext.getLayer(LayeredAgentContext.COMBINED_THREATS);

		double[] coords = l.getEntityCoordinates(b);
		double influence = l.get((int) coords[0], (int) coords[1]);

		Target target = new Target(b);
		target.setValue(influence);

		this.context.setTarget(target);
	}

}
