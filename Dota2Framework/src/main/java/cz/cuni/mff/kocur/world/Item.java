package cz.cuni.mff.kocur.world;

import java.util.ArrayList;
import java.util.LinkedList;

import cz.cuni.mff.kocur.base.CustomStringBuilder;
import cz.cuni.mff.kocur.interests.Shop;

/**
 * Class that represents item. Items can be bought in on of three shops -
 * secret, side, base. Some of the items can be bought in more than one shop.
 * 
 * Each item knows its name, price and has a list of components, that make up
 * the item.
 * 
 * 
 * @author kocur
 *
 */
public class Item {
	public static final int SECRET = Shop.SECRET;
	public static final int BASE = Shop.BASE;
	public static final int BASESIDE = 10;
	public static final int SIDESECRET = 11;

	/**
	 * Type of shop where we can buy this item.
	 */
	protected int shopType = 0;

	/**
	 * Price of this item.
	 */
	protected int price = 0;

	/**
	 * In-game of this item.
	 */
	protected String name = "";

	/**
	 * Count of this item - if inside inventory. Can be used for potions and
	 * stackable items.
	 */
	protected int count = 1;

	/**
	 * Reference to other components.
	 */
	protected ArrayList<Item> components = new ArrayList<>();

	/**
	 * Constructs an item from name, price and shop type.
	 * 
	 * @param name
	 *            Name of the item.
	 * @param price
	 *            Price of the item.
	 * @param type
	 *            Type of the item.
	 */
	public Item(String name, int price, int type) {
		this.name = name;
		this.price = price;
		this.shopType = type;
	}

	/**
	 * Constructs the item from item (copy constructor).
	 * 
	 * @param other
	 *            Other item.
	 */
	public Item(Item other) {
		this.name = other.getName();
		this.price = other.getPrice();
		this.shopType = other.getShopType();
		this.count = other.getCount(); // Will be probably zero
	}

	/**
	 * 
	 * @return Returns the type of shop, that sells this item.
	 */
	public int getShopType() {
		return shopType;
	}

	/**
	 * 
	 * @param shopType
	 *            Sets shop type for this item.
	 */
	public void setShopType(int shopType) {
		this.shopType = shopType;
	}

	/**
	 * 
	 * @return Returns price of this item.
	 */
	public int getPrice() {
		return price;
	}

	/**
	 * 
	 * @param price
	 *            New price of this item.
	 */
	public void setPrice(int price) {
		this.price = price;
	}

	/**
	 * 
	 * @return Returns name of this item.
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 *            New name of this item.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return Returns ArrayList of items, that are needed to complete this one. The
	 *         ArrayList can be empty, if this item is not composed out of many
	 *         items.
	 */
	public ArrayList<Item> getComponents() {
		return components;
	}

	/**
	 * 
	 * @param components
	 *            New components to be stored inside this item.
	 */
	public void setComponents(ArrayList<Item> components) {
		this.components = components;
	}

	/**
	 * Adds item to components of this item.
	 * 
	 * @param comp
	 *            Component of this item.
	 */
	public void addComponent(Item comp) {
		components.add(comp);
	}

	/**
	 * 
	 * @return Returns true if this item hase components.
	 */
	public boolean hasComponents() {
		return components.size() > 0;
	}

	/**
	 * 
	 * @param i
	 *            Inventory.
	 * @return Returns true if item is in specified inventory.
	 */
	public boolean doIHaveItem(Inventory i) {
		return i.findInInventory(name) == -1;
	}

	/**
	 * 
	 * @param i
	 *            Inventory.
	 * @return Returns array of items needed to complete this item.
	 */
	public Item[] missingComponents(Inventory i) {
		LinkedList<Item> missing = new LinkedList<>();

		for (Item c : components) {
			if (!c.doIHaveItem(i))
				missing.add(c);
		}

		return missing.toArray(new Item[missing.size()]);
	}

	/**
	 * 
	 * @param money
	 *            Money we have.
	 * @return True if this item can be bought with given money.
	 */
	public boolean affordable(int money) {
		return price < money;
	}

	/**
	 * 
	 * @param s
	 *            Shop, where I want to buy this item.
	 * @return Returns true if passed shop is the one, where I should buy this item.
	 */
	public boolean correctShop(Shop s) {
		if (s.getType() == shopType)
			return true;

		// Else check if correct for items that can be in two shops.
		if (shopType == SIDESECRET) {
			return s.getType() == Shop.SECRET || s.getType() == Shop.SIDE;
		} else if (shopType == BASESIDE) {
			return s.getType() == Shop.BASE || s.getType() == Shop.SIDE;
		}

		return false;
	}

	public String toString() {
		CustomStringBuilder b = new CustomStringBuilder();
		b.append(name + " " + price + " " + shopType + " [");
		for (Item c : components) {
			b.append(c.getName() + ",");
		}

		return b.toString() + "]";
	}

	/**
	 * 
	 * @return Returns item count. (Can be greater than 1, if the item is stackable)
	 */
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other == this)
			return true;
		if (!(other instanceof Item))
			return false;

		Item otherItem = (Item) other;

		if (this.getName().equals(otherItem.getName()))
			return true;
		return false;
	}
}
