package cz.cuni.mff.kocur.world;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.CustomStringBuilder;
import cz.cuni.mff.kocur.base.IndentationStringBuilder;

public class Inventory {
	/**
	 * Logger registered for inventory class.
	 */
	private static final Logger logger = LogManager.getLogger(Inventory.class.getName());

	public static final int INVENTORY = 0 ;
	public static final int BACKPACK = 1;
	public static final int STASH = 3;
	
	
	/**
	 * Represents player's inventory. Slots 0-5
	 */
	protected ArrayList<Item> inventory = new ArrayList<>(6);

	/**
	 * Represents player's backpack. Slots 6-8
	 */
	protected ArrayList<Item> backpack = new ArrayList<>(3);

	/**
	 * Represents a player's stash. Slots 9-14
	 */
	protected ArrayList<Item> stash = new ArrayList<>(6);
	
	public static final int inventorySlotMax = 5;
	public static final int inventorySlotMin = 0;
	public static final int backpackSlotMin = 6;
	public static final int backpackSlotMax = 8;
	public static final int stashSlotMin = 9;
	public static final int stashSlotMax = 14;

	public static boolean isInventorySlot(int slot) {
		return slot >= inventorySlotMin && slot <= inventorySlotMax;
	}

	public static boolean isStashSlot(int slot) {
		return slot >= stashSlotMin && slot <= stashSlotMax;
	}

	public static boolean isBackpackSlot(int slot) {
		return slot >= backpackSlotMin && slot <= backpackSlotMax;
	}

	/**
	 * Adds item to inventory on given slot.
	 * 
	 * @param slot
	 *            Index of slot where you want to add the item.
	 * @param item
	 *            Item that should be added to that slot.
	 */
	public void addToSlot(int slot, Item item) {
		// Check for correct inventory type
		if (isInventorySlot(slot))
			inventory.add(slot - inventorySlotMin, item);
		else if (isStashSlot(slot))
			stash.add(slot - stashSlotMin, item);
		else if (isBackpackSlot(slot))
			backpack.add(slot - backpackSlotMin, item);
		else
			logger.warn("You have tried to insert item to non existent slot. [" + slot + ", " + item + "]");

	}

	/**
	 * 
	 * @param slot
	 *            Index of inventory slot that you want to retrieve.
	 * @return Returns item stored in passed slot or "" if no item is stored there.
	 */
	public Item getItem(int slot) {
		Item item = null;

		if (isInventorySlot(slot))
			item = inventory.get(slot - inventorySlotMin);
		else if (isStashSlot(slot))
			item = stash.get(slot - stashSlotMin);
		else if (isBackpackSlot(slot))
			item = backpack.get(slot - backpackSlotMin);
		else
			logger.warn("You have tried to take item from non existent slot. [" + slot + "]");

		return item;
	}

	public ArrayList<Item> getBackpack() {
		return backpack;
	}

	public ArrayList<Item> getInventory() {
		return inventory;
	}

	public ArrayList<Item> getStash() {
		return stash;
	}
	
	public ArrayList<Item> getAllItems(){
		ArrayList<Item> items = new ArrayList<>();
		
		items.addAll(inventory);
		items.addAll(backpack);
		items.addAll(stash);
		
		return items;
	}

	/**
	 * 
	 * @param name Name of the item.
	 * @return Returns index of item with given name in inventory(inv, backpack, stash). Returns -1 if it
	 *         wasn't found.
	 */
	public int findInInventory(String name) {
		Item item = ItemsBase.getItem(name);
		
		int index = findInArray(stash, item);
		if (index != -1) {
			return index + stashSlotMin;
		}

		index = findInArray(inventory, item);
		if (index != -1) {
			return index + inventorySlotMin;
		}

		index = findInArray(backpack, item);
		if (index != -1)
			 return index + backpackSlotMin;
		
		return -1;
	}

	private int findInArray(ArrayList<Item> arr, Item item) {
		return arr.indexOf(item);
	}

	public String toString() {
		IndentationStringBuilder builder = new IndentationStringBuilder();
		builder.appendLine("Inventory: ");
		builder.indent();
		
		builder.append("Main: [");
		for (Item i : inventory) {
			if (i == null) {
				builder.append("_");
			} else
			builder.append(i.toString() + ",");
		}
		builder.appendLine("]");

		builder.append("Backpack: [");
		for (Item i : backpack) {
			if (i == null) {
				builder.append("_");
			} else
			builder.append(i + ",");
		}
		builder.appendLine("]");
		
		builder.append("Stash: [");
		
		for (Item i : stash) {
			if (i == null) {
				builder.append("_");
			} else
			builder.append(i + ",");
		}
		
		return builder.toString();
	}

	public int findInInventory(Item c) {
		if (c == null) {
			logger.warn("Item I received is null.");
			return -1;
		}
		
		//logger.debug("Looking for item with name: " + c.getName());
		
		return findInInventory(c.getName());
	}
	
	public ArrayList<Item> getInventory(int type) {
		if (type == INVENTORY) return inventory;
		else if (type == STASH) return stash;
		
		return backpack;		
	}
	
	public int countEmptySlots(int type) {
		int count = 0;
		
		for (Item i : getInventory(type))
			if (i == null) count++;
		
		return count;
	}
	
	public int countFullSlots(int type) {
		return getInventory(type).size() - countEmptySlots(type);
	}

	/**
	 * Counts items with same name as passed item inside the whole inventory (stash, backpack ..).
	 * @param nextItem Item we are counting.
	 * @return Returns the count of items.
	 */
	public int countItems(Item nextItem) {
		int counter = 0;
		
		for (Item i : getAllItems()) {
			// Item not found? (empty slot)
			if (i == null)
				continue;
			
			// We look for item with the same name
			if (i.equals(nextItem)) {
				counter += i.getCount();
			}
			// If there is none and it can be composed, we will look into components.
			else if (i.hasComponents()) {
				counter += countInComponents(nextItem, i);
			}
		}
		
		return counter;
	}

	/**
	 * Recursively counts all the components with the given name.
	 * @param nextItem Item we are looking for.
	 * @param item Out current item, where we start looking.
	 * @return Returns count of items in the items "tree". 
	 */
	private int countInComponents(Item nextItem, Item item) {
		if (item.getName().equals(nextItem.getName()))
			return item.getCount();
		
		if (item.hasComponents() == false)
			return 0;
		
		// Else the item has components
		int counter = 0;
		for (Item i : item.getComponents()) {
			counter += countInComponents(nextItem, i); 
		}
		
		return counter;
	}
	
	
}
