package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.bot.BaseAgentController;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.interests.InterestsBase;
import cz.cuni.mff.kocur.interests.Rune;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.world.Hero;

public class PickupRuneDecision extends Decision{
	
	@Override
	public AgentCommand execute() {
		Rune rune = (Rune) context.getTarget().getLocation();
		BaseAgentController bc = context.getBotContext().getController();
		
		if (rune == null)
			return null;
		
		super.execute();
		
		return bc.pickupRune(rune);
	}
		
	@Override
	public void updateContext(ExtendedBotContext bc) {
		Hero hero = bc.getHero();
		Rune rune = InterestsBase.getInstance().getNearestRune(hero);
		
		context.setTarget(new Target(rune));		
	}

	
	@Override
	public void presetContext(ExtendedBotContext bc) {
		context.setBotContext(bc);
		context.setSource(bc.getHero());		
	}
	
	
	

}
