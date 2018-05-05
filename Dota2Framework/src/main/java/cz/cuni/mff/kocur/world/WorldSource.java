package cz.cuni.mff.kocur.world;

import cz.cuni.mff.kocur.streaming.BasicSource;

/**
 * Class that extends BasicSource. It represents the World's source. (A place,
 * that sends information about the world).
 * 
 * @author kocur
 *
 */
public class WorldSource extends BasicSource {
	public WorldSource() {
		super();
		this.name = "WorldSource";
	}
}
