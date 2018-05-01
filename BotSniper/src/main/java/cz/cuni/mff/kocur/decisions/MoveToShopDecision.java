package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.bot.BaseAgentController;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.interests.InterestsBase;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.world.Hero;
import cz.cuni.mff.kocur.world.Item;

public class MoveToShopDecision extends Decision {
	@Override
	public AgentCommand execute() {
		
		BaseAgentController bc = context.getBotContext().getController();
		Location target = context.getTarget().getLocation();
		
		super.execute();
		return bc.moveTo(target);			
	}

	@Override
	public void updateContext(ExtendedBotContext bc) {
		
		Item buyOrder = bc.getBuySequence().getNextBuy(
				bc.getHero().getInventory());
		
		// Check if order is null
		if (buyOrder == null) {
			context.setBonusFactor(0); // Turn this decision off.
			return;
		}
		
		Location shop = InterestsBase.getInstance().getShops().findNearest(context.getSource(), buyOrder);

		Target target = new Target(buyOrder);
		target.setLocation(shop);
		
		context.setTarget(target);			
	}

	@Override
	public void presetContext(ExtendedBotContext bc) {
		context.setBotContext(bc);
		
		Hero hero = bc.getController().getHero();
		context.setSource(hero);
	}				
}
