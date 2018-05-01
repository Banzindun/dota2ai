package cz.cuni.mff.kocur.decisions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.bot.AgentController;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.influence.GoalLayer;
import cz.cuni.mff.kocur.server.AgentCommand;
import kocur.lina.bot.LayeredBotContext;

public class EscapeToBase extends Decision{

	private static final Logger logger = LogManager.getLogger(EscapeToBase.class);
	
	@Override
	public AgentCommand execute(){
		// In target, there should be a location of the base, where we are escaping
		Location target = context.getTarget().getLocation();
		if (target == null) {
			return null;
		}
				
		// Set the creep to goal layer as our goal.
		((GoalLayer) context.getBotContext().getLayer(LayeredBotContext.GOAL)).setGoal(target);	

		// Set time of this decision
		super.execute();
		
		return null;
	};
	
	@Override
	public void updateContext(ExtendedBotContext bc) {
		// Bot is set, hp should be updated.
		
	}

	@Override
	public void presetContext(ExtendedBotContext bc) {
		context.setBotContext(bc);
		AgentController controller = bc.getController();
		
		// Who is the source? Hero.
		context.setSource(bc.getController().getHero());
		
		// Where should I escape to? To my base.
		context.setTarget(new Target(controller.getContext().getMyFountain()));
	}

	
	
}
