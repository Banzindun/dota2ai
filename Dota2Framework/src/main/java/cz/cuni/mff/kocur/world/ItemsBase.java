package cz.cuni.mff.kocur.world;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.CustomStringBuilder;

public class ItemsBase {
	/**
	 * Basic logger for ItemsBase.
	 */
	private static Logger logger = LogManager.getLogger(ItemsBase.class.getName());
	
	/**
	 * Map that will contain all the item objects.
	 */
	protected static HashMap<String, Item> items = new HashMap<>();
	
	/**
	 * This function stores the item, without adding its components.
	 * @param name Name of the item.
	 * @param price Price of the item.
	 * @param type Type of the shop, that buys this item. {@link cz.cuni.mff.kocur.interests.Shop}
	 */
	public static void preloadItem(String name, int price, int type) {
		items.put(name, new Item(name, price, type));
	}
	
	/**
	 * Loads item's components. This function will not throw if item or its component weren't found. 
	 * The item base is quite big and one item without component is not a problem that would break 
	 * the framework (it just needs to be logged and solved later). 
	 * @param name Name of the item. 
	 * @param components It's components. 
	 */
	public static void loadItem(String name, String[] components) {
		Item i = items.get(name);
		
		if (i == null) {
			logger.warn("Item: " + name + " wasn't found. No components set.");
			return;
		}
		
		for (String c : components) {
			if (c.length() == 0) 
				continue;
			
			Item comp = items.get(c);
			if (comp == null) {
				logger.warn("Component " + c + " for item: " + name + " wasn't found and set.");
				continue;
			}
			
			i.addComponent(comp);
		}
	}
	
	/**
	 * Clears the stored items.
	 */
	public static void clear() {
		items.clear();
	}
	
	/**
	 * 
	 * @param name Name of the item.
	 * @return Returns item with given name or null if the item doesn't exist inside the base.
	 */
	public static Item getItem(String name) {
		return items.get(name);			
	}
	
	public static String  getToString() {
		CustomStringBuilder b = new CustomStringBuilder();
		
		for (Item i : items.values()) {
			b.appendLine(i.toString());
		}
		
		return b.toString();
	}
	
}
