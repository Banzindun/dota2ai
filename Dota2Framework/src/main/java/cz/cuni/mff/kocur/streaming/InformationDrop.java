package cz.cuni.mff.kocur.streaming;

/**
 * This class represents a information drop. The drop contains some data and
 * it's type. The drop flows to sink, where it is redistributed to catchers.
 * Catchers store the drops and further work with them.
 * 
 * @author kocur
 *
 */
public class InformationDrop {
	/**
	 * Empty type of information drop.
	 */
	public static int EMPTY = 0;

	/**
	 * 1 means this drop contains grid data.
	 */
	public static int WORLD = 1;

	/**
	 * This drop contains context information.
	 */
	public static int CONTEXT = 3;

	/**
	 * Data = information that is dissolved in this drop.
	 */
	Object data = null;

	private int type = 0;

	public InformationDrop(int type, Object o) {
		this.type = type;
		this.data = o;
	}

	/**
	 * Sets type of transferred data. The type might preferably be one of the static
	 * types in InformationDrop class. User can give its own meaning to new
	 * integers.
	 * 
	 * @param type
	 *            Type of the drop we want to be catching.
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * 
	 * @return Returns type of this drop.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Assigns passed data object to internal data object reference.
	 * 
	 * @param data
	 *            New data object.
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * 
	 * @return Returns the object that represents stored data.
	 */
	public Object getData() {
		return data;
	}

}
