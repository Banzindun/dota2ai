package cz.cuni.mff.kocur.decisions;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.bot.BaseAgentController;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.world.Item;

public class BuyDecision extends MoveToShopDecision{

	private static Logger logger = LogManager.getLogger(BuyDecision.class.getName());
	
	@Override
	public AgentCommand execute() {
		BaseAgentController bc = context.getBotContext().getController();
		Item item = context.getTarget().getItem();
		
		// logger.info("Buying: " + item.getName());
		
		// "Remove" the item from buy sequence
		bc.getContext().getBuySequence().getNextBuy(
				bc.getHero().getInventory());
		
		super.execute();
		return bc.buy(item.getName());			
	}
}
