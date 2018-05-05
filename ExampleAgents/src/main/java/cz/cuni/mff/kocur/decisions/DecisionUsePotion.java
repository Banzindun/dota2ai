package cz.cuni.mff.kocur.decisions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.server.AgentCommands;
import cz.cuni.mff.kocur.world.Hero;
import cz.cuni.mff.kocur.world.Inventory;
import cz.cuni.mff.kocur.world.Item;
import cz.cuni.mff.kocur.world.ItemsBase;

/**
 * Decision to use a potion. (healing, replenishing mana etc.)
 * 
 * @author kocur
 *
 */
public class DecisionUsePotion extends Decision {

	private static final Logger logger = LogManager.getLogger(DecisionUsePotion.class);

	private String itemName;

	public DecisionUsePotion(String itemName) {
		this.itemName = itemName;
	}

	@Override
	public AgentCommand execute() {
		// In target, there should be a location of the base, where we are escaping
		Item healingItem = context.getTarget().getItem();

		Hero source = (Hero) context.getSource();
		int index = source.getInventory().findInInventory(healingItem);

		if (!Inventory.isInventorySlot(index)) {
			logger.warn("Trying to use healing, but no healing inside inventory.");
			return null;
		}

		// Set time of this decision
		super.execute();

		logger.info("Using healing potion.");

		return new AgentCommands.UseItem(index, source.getEntid());
	};

	@Override
	public void updateContext(ExtendedAgentContext bc) {
		// Bot is set, hp should be updated.

	}

	@Override
	public void presetContext(ExtendedAgentContext bc) {
		context.setBotContext(bc);

		// Who is the source? Hero.
		context.setSource(bc.getController().getHero());

		// Item I will be using -> this is the healing potion (= item_flask), will be
		// set to target.
		Item flask = ItemsBase.getItem(itemName);
		context.setTarget(new Target(flask));
	}

}
