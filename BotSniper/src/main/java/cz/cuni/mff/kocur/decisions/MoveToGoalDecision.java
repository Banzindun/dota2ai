package cz.cuni.mff.kocur.decisions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.bot.BaseAgentController;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.influence.InfluenceLayer;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.world.Hero;
import kocur.lina.bot.LayeredBotContext;

public class MoveToGoalDecision extends Decision {
	/**
	 * Logger for MoveToGoalDecision.
	 */
	private static final Logger logger = LogManager.getLogger(MoveToGoalDecision.class);
	
	/**
	 * Radius in which we search for a best position to go to.
	 */
	public static int SEARCH_RADIUS = 10;
	
	@Override
	public AgentCommand execute() {
		BaseAgentController bc = context.getBotContext().getController();
		Location target = context.getTarget().getLocation();
		
		if (target == null)
			return null;
		
		// Set the time of this decision
		super.execute();
		
		return bc.moveTo(target);								
	}

	@Override
	public void updateContext(ExtendedBotContext bc) {
		// Get the hero
		Hero hero = (Hero) context.getSource();
		
		// Get the goal influence layer
		InfluenceLayer goalLayer = context.getBotContext().getLayer(LayeredBotContext.GOAL);
		
		// Get the hero's coordinates
		double[] coords = goalLayer.getEntityCoordinates(hero);
		
		// Get the best position and translate it back to in-game coordinates
		int[] bestPosition = goalLayer.findMaxLocationOnGrid((int) coords[0]-SEARCH_RADIUS, (int) coords[1]-SEARCH_RADIUS, (int) coords[0] + SEARCH_RADIUS, (int) coords[1] + SEARCH_RADIUS);
		
		if (bestPosition[0] == -1 && bestPosition[1] == -1) {
			// The goal might not be set .. 
			context.setTarget(new Target());
			return;			
		}
		
		double[] bestPositionInBase = goalLayer.toBase(bestPosition[0], bestPosition[1]);

		// Create location from the best position
		Location bestLocation = new Location(bestPositionInBase);

		// Get the enemy influence layer and the influence on 
		InfluenceLayer combinedThreats = context.getBotContext().getLayer(LayeredBotContext.COMBINED_THREATS);
		double influenceOnBest = combinedThreats.get(combinedThreats.getEntityCoordinates(bestLocation));
		
		// Set the target
		Target target = new Target(bestLocation);
		target.setValue(influenceOnBest);		
		context.setTarget(target);
	}

	@Override
	public void presetContext(ExtendedBotContext bc) {
		context.setBotContext(bc);
		context.setSource(bc.getController().getHero());
	}
}
