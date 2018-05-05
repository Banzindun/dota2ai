package cz.cuni.mff.kocur.world;

/**
 * Just a dummy, that helps us to get items from the game just by sending their
 * names
 * 
 * @author kocur
 *
 */
public class ItemDummy {

	/**
	 * Count of the item. (if stackable)
	 */
	protected int count = 0;

	/**
	 * Name of this item.
	 */
	protected String name = "";

	/**
	 * 
	 * @return Returns this items count.
	 */
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * 
	 * @return Returns the name of this item.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets a new name for this item.
	 * 
	 * @param name
	 *            New name.
	 */
	public void setName(String name) {
		this.name = name;
	}

}
