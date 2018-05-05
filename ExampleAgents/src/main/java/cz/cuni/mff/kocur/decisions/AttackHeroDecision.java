package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.influence.InfluenceLayer;
import cz.cuni.mff.kocur.world.Hero;
import kocur.lina.agent.LayeredAgentContext;

/**
 * A decision to attack a hero.
 * 
 * @author kocur
 *
 */
public class AttackHeroDecision extends AttackTargetDecision {

	/**
	 * Updates a context using agent's context and a hero.
	 * 
	 * @param bc
	 *            Agent's context.
	 * @param h
	 *            Hero.
	 */
	public void updateContext(ExtendedAgentContext bc, Hero h) {
		InfluenceLayer l = bc.getLayer(LayeredAgentContext.FRIENDLY_THREATS);

		double[] coords = l.getEntityCoordinates(h);
		double influence = l.get((int) coords[0], (int) coords[1]);

		Target target = new Target(h);
		target.setValue(influence);

		this.context.setTarget(target);
	}

}
