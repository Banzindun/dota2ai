package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.influence.GoalLayer;
import cz.cuni.mff.kocur.influence.InfluenceLayer;
import cz.cuni.mff.kocur.interests.Fountain;
import cz.cuni.mff.kocur.interests.Tower;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.world.GridBase;
import cz.cuni.mff.kocur.world.Hero;
import kocur.lina.agent.LayeredAgentContext;

/**
 * Decision to escape from threat. It escapes to safe location using combined
 * threats layer. If there is no location we escape to our nearest tower or to
 * our base.
 * 
 * @author kocur
 *
 */
public class DecisionEscapeFromThreat extends Decision {

	@Override
	public AgentCommand execute() {
		// I am threatened and I have concluded, that I should not fight.
		// I must find either place in combined_threat where I hide (in my creeps
		// influence)
		// Or I must escape under my tower.
		ExtendedAgentContext bc = (ExtendedAgentContext) this.context.getBotContext();
		InfluenceLayer l = bc.getLayer(LayeredAgentContext.COMBINED_THREATS);

		Hero hero = (Hero) context.getSource();

		// Get goal layer
		GoalLayer goalLayer = ((GoalLayer) context.getBotContext().getLayer(LayeredAgentContext.GOAL));

		// Fountain
		Fountain fountain = (Fountain) bc.getMyFountain();
				
		// Let's say a position with influence at least 0.30 is a good one.
		// If there will be hero attacking me his influence will turn all the influence
		// around me towards negative values.
		if (l.getHighestValue() > 0.30) {
			Location highPoint = l.getMaxLocation();

			// Check if the location is in the direction of our base.
			
			if (GridBase.distance(fountain, hero) > GridBase.distance(fountain, highPoint)) {
				goalLayer.setGoal(highPoint);
				return null;
			}
		}

		// Else we need to go towards our tower or a fountain if no tower exists
		Tower t = bc.getMyLane().getLastStandingTower();
		if (t == null) {
			int gridx = fountain.getGridX();
			int gridy = fountain.getGridY();

			Location target = new Location(GridBase.getInstance().resolveXYBack(gridx, gridy));
			goalLayer.setGoal(target);
			return null;
		}

		// Else go towards the tower
		goalLayer.setGoal(t);
		return null;
	}

	@Override
	public void updateContext(ExtendedAgentContext bc) {
		InfluenceLayer l = bc.getLayer(LayeredAgentContext.COMBINED_THREATS);

		if (l == null) {
			this.context.setTarget(new Target());
			return;
		}

		Hero h = bc.getHero();

		// Am I dead?
		if (h.getHealth() <= 0) {
			this.context.setTarget(new Target(0));
			return;
		}

		double[] heroCoords = l.getEntityCoordinates(h);
		double influence = l.get((int) heroCoords[0], (int) heroCoords[1]);

		this.context.setTarget(new Target(influence));
	}

	@Override
	public void presetContext(ExtendedAgentContext bc) {
		context.setBotContext(bc);
		context.setSource(bc.getController().getHero());

	}

}
