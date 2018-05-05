package cz.cuni.mff.kocur.influence;

import java.util.LinkedList;

/**
 * Class that represents a front line. Front line is defined as changing sign
 * while traversing friendly and enemy influences stored in one layer (with
 * opposing signs).
 * 
 * @author kocur
 *
 */
public class FrontLine {

	/**
	 * List of the front line.
	 */
	public LinkedList<Integer[]> locations = new LinkedList<>();

	public void addLocation(int x, int y) {
		locations.add(new Integer[] { x, y });
	}

	public LinkedList<Integer[]> getLocations() {
		return locations;
	}
}
