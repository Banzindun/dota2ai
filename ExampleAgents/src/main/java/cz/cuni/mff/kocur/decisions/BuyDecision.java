package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.agent.BaseAgentController;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.world.Item;

/**
 * Decision that buys a target item.
 * 
 * @author kocur
 *
 */
public class BuyDecision extends SetShopGoalDecision {
	@Override
	public AgentCommand execute() {
		BaseAgentController bc = context.getBotContext().getController();
		Item item = context.getTarget().getItem();

		// logger.info("Buying: " + item.getName());

		// "Remove" the item from buy sequence
		bc.getContext().getBuySequence().getNextBuy(bc.getHero().getInventory());

		super.execute();
		return bc.buy(item.getName());
	}
}
