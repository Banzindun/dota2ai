package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.bot.BaseAgentController;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.interests.Lane;
import cz.cuni.mff.kocur.server.AgentCommand;

public class MoveToDecision extends Decision {
	
	@Override
	public AgentCommand execute() {
		BaseAgentController bc = context.getBotContext().getController();
		Location target = context.getTarget().getLocation();
		
		if (target == null)
			return null;
		
		super.execute();
		
		// I will send target to controller, he will decide if 
		// he will send the unit to target by id or by location
		return bc.moveTo(target);								
	}

	@Override
	public void updateContext(ExtendedBotContext bc) {
		Lane lane = bc.getMyLane();
		Location mostDistantCreep = lane.getMostDistantCreep();
		context.setTarget(new Target(mostDistantCreep));
	}

	@Override
	public void presetContext(ExtendedBotContext bc) {
		context.setBotContext(bc);
		context.setSource(bc.getController().getHero());
		
	}
}
