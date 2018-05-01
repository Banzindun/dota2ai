package cz.cuni.mff.kocur.influence;

import java.util.LinkedList;

public class FrontLine {

	public LinkedList<Integer[]> locations = new LinkedList<>();
	
	public void addLocation(int x, int y) {
		locations.add(new Integer[] {x, y});
	}
	
	public LinkedList<Integer[]> getLocations(){
		return locations;
	}
}
