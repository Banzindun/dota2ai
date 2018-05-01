package cz.cuni.mff.kocur.decisions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.influence.GoalLayer;
import cz.cuni.mff.kocur.influence.InfluenceLayer;
import cz.cuni.mff.kocur.interests.Fountain;
import cz.cuni.mff.kocur.interests.Tower;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.world.GridBase;
import cz.cuni.mff.kocur.world.Hero;
import kocur.lina.bot.LayeredBotContext;

public class DecisionEscapeFromThreat extends Decision {
	
	private static final Logger logger = LogManager.getLogger(DecisionEscapeFromThreat.class);

	@Override
	public AgentCommand execute() {
		// I am threatened and I have concluded, that I should not fight.
		// I must find either place in combined_threat where I hide (in my creeps
		// influence)
		// Or I must escape under my tower.
		ExtendedBotContext bc = (ExtendedBotContext) this.context.getBotContext();
		InfluenceLayer l = bc.getLayer(LayeredBotContext.COMBINED_THREATS);

		Hero hero = (Hero) context.getSource();
		
		// Get goal layer
		GoalLayer goalLayer = ((GoalLayer) context.getBotContext().getLayer(LayeredBotContext.GOAL));	
		
		// Let's say a position with influence at least 0.30 is a good one.
		// If there will be hero attacking me his influence will turn all the influence
		// around me towards negative values.
		if (l.getHighestValue() > 0.30) {
			Location highPoint = l.getMaxLocation();
			
			// Check if the location is in the direction of our base.
			Fountain f = (Fountain) bc.getMyFountain();
			if (GridBase.distance(f,  hero) > GridBase.distance(f, highPoint)) {
				goalLayer.setGoal(highPoint);
				return null;
			}
		}

		// Else we need to go towards our tower or a fountain if no tower exists
		Tower t = bc.getMyLane().getLastStandingTower();
		if (t == null) {
			goalLayer.setGoal(bc.getMyFountain());
			return null;
		}
		
		// Else go towards the tower
		goalLayer.setGoal(t);
		return null;
	}

	@Override
	public void updateContext(ExtendedBotContext bc) {
		InfluenceLayer l = bc.getLayer(LayeredBotContext.COMBINED_THREATS);
		
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
	public void presetContext(ExtendedBotContext bc) {
		context.setBotContext(bc);
		context.setSource(bc.getController().getHero());

	}

}
