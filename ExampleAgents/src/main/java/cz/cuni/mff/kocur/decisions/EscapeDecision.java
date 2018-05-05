package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.influence.InfluenceLayer;
import cz.cuni.mff.kocur.world.Hero;
import kocur.lina.agent.LayeredAgentContext;

/**
 * Decision to escape from threat.
 * 
 * @author kocur
 *
 */
public class EscapeDecision extends MoveToGoalDecision {

	/**
	 * Search radius for escaping.
	 */
	public static int ESCAPE_SEARCH_RADIUS = 50;

	@Override
	public void updateContext(ExtendedAgentContext bc) {
		// Get the hero
		Hero hero = (Hero) context.getSource();

		// Get the goal influence layer
		InfluenceLayer goalLayer = context.getBotContext().getLayer(LayeredAgentContext.GOAL);

		// Get the hero's coordinates
		double[] coords = goalLayer.getEntityCoordinates(hero);

		// Get the best position and translate it back to in-game coordinates
		int[] bestPosition = goalLayer.findMaxLocationOnGrid((int) coords[0] - ESCAPE_SEARCH_RADIUS,
				(int) coords[1] - ESCAPE_SEARCH_RADIUS, (int) coords[0] + ESCAPE_SEARCH_RADIUS,
				(int) coords[1] + ESCAPE_SEARCH_RADIUS);

		if (bestPosition[0] == -1 && bestPosition[1] == -1) {
			// The goal might not be set ..
			Target target = new Target();
			target.setValue(0);
			context.setTarget(target);

			return;
		}

		double[] bestPositionInBase = goalLayer.toBase(bestPosition[0], bestPosition[1]);

		// Create location from the best position
		Location bestLocation = new Location(bestPositionInBase);

		// Get the enemy influence layer and the influence on
		InfluenceLayer combinedLayer = context.getBotContext().getLayer(LayeredAgentContext.COMBINED_THREATS);
		double influenceOnHero = combinedLayer.get(combinedLayer.getEntityCoordinates(hero));

		// Set the target
		Target target = new Target(bestLocation);
		target.setValue(influenceOnHero);
		context.setTarget(target);
	}
}
