package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.influence.GoalLayer;
import cz.cuni.mff.kocur.interests.InterestsBase;
import cz.cuni.mff.kocur.interests.Shop;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.world.GridBase;
import cz.cuni.mff.kocur.world.Hero;
import cz.cuni.mff.kocur.world.Item;
import kocur.lina.agent.LayeredAgentContext;

/**
 * Decision, that sets a shop as a goal inside the GoalLayer. Shop type is given
 * by distance from source (we pick nearest) and by item.
 * 
 * @author kocur
 *
 */
public class SetShopGoalDecision extends Decision {
	@Override
	public AgentCommand execute() {

		Shop shop = (Shop) context.getTarget().getLocation();
				
		if (shop == null)
			return null;
		
		int gridx = shop.getGridX();
		int gridy = shop.getGridY();
		
		Location target = new Location(GridBase.getInstance().resolveXYBack(gridx, gridy));

		// Get goal layer
		GoalLayer goalLayer = ((GoalLayer) context.getBotContext().getLayer(LayeredAgentContext.GOAL));
		goalLayer.setGoal(target);

		super.execute();

		return null;
	}

	@Override
	public void updateContext(ExtendedAgentContext bc) {

		Item buyOrder = bc.getBuySequence().getNextBuy(bc.getHero().getInventory());

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
	public void presetContext(ExtendedAgentContext bc) {
		context.setBotContext(bc);

		Hero hero = bc.getController().getHero();
		context.setSource(hero);
	}
}
