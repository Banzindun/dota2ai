package cz.cuni.mff.kocur.agent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.world.Inventory;
import cz.cuni.mff.kocur.world.Item;
import cz.cuni.mff.kocur.world.ItemsBase;

/**
 * This class represents a buy sequence. An agent may specify a buy sequence as
 * a list of items he wants to buy. Then the interaction with addon must be
 * careful and must not send anything twice or too fast, as that would result in
 * buying two items or skipping one. To overcome those problems, the buy
 * sequence checks that the item we wanted to buy was actually bought, before it
 * issues another item.
 * 
 * Another task this class has is to split items into components and buy those
 * components first (or make sure they are inside our inventory).
 * 
 * @author kocur
 *
 */
public class BuySequence {
	/**
	 * Logger registered for BuySequence class.
	 */
	private static final Logger logger = LogManager.getLogger(BuySequence.class.getName());

	/**
	 * Sequence of items to buy.
	 */
	LinkedList<Item> sequence = new LinkedList<>();

	/**
	 * Hero's inventory. We will be updating it to match last inventory we have encountered.
	 */
	Inventory inventory = null;

	public BuySequence(String[] itemNames) {
		loadFromItemNames(itemNames);
	}

	/**
	 * Loads the sequence from array of item names.
	 * @param itemNames Names of items.
	 */
	public void loadFromItemNames(String[] itemNames) {
		for (String i : itemNames) {
			Item item = ItemsBase.getItem(i);
			if (item == null) {
				logger.warn("Non existent item.");
			} else {
				sequence.add(item);
				logger.debug("Item: " + item.getName());
			}
		}
	}

	/**
	 * Gets next buy from the sequence.
	 * @param inv Hero's inventory.
	 * @return Returns the item to buy.
	 */
	public Item getNextBuy(Inventory inv) {
		if (sequence.size() == 0)
			return null;

		if (inventory == null) {
			inventory = inv;
			return sequence.getFirst();
		}

		// First check if the item is composed out of more items
		Item nextItem = sequence.removeFirst();

		// System.out.println("Count before: " + inv.countItems(nextItem) + "Count now:
		// " + inventory.countItems(nextItem));

		// Inventory is our last inventory state
		// Check if the item we have been buying has been added, if so we return the
		// next one
		if (inv.countItems(nextItem) <= inventory.countItems(nextItem)) {
			sequence.addFirst(nextItem);
			inventory = inv;
			return nextItem;
		}

		if (sequence.size() == 0)
			return null;

		nextItem = sequence.getFirst();

		// If the item is made out of other items, we insert them to the start of the
		// sequence and start with them
		if (nextItem.hasComponents()) {
			ArrayList<Item> components = nextItem.getComponents();

			sequence.removeFirst();

			// Check if they are already in inventory, if so then don't buy them
			Iterator<Item> i = components.iterator();
			while (i.hasNext()) {
				Item c = i.next();
				if (inv.findInInventory(c) == -1) {
					sequence.addFirst(c);
				}
			}

			nextItem = sequence.getFirst();
		}

		// logger.info("Returning item to buy: " + sequence.getFirst().getName());
		inventory = inv;

		// Return first item from sequence
		return nextItem;
	}
}
