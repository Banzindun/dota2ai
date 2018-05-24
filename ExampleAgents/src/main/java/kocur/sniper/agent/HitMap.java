package kocur.sniper.agent;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.world.GridBase;

/**
 * Class that stores locations, that should correspond to places, where our hero
 * took damage. We also supply some methods to save them to image.
 * 
 * @author kocur
 *
 */
public class HitMap {
	/**
	 * Logger.
	 */
	private static final Logger logger = LogManager.getLogger(HitMap.class);

	/**
	 * The locations.
	 */
	protected LinkedList<Location> locations = new LinkedList<>();

	/**
	 * Adds a new location.
	 * 
	 * @param location
	 *            Location.
	 */
	public void addLocation(Location location) {
		locations.add(location);
	}

	/**
	 * Creates and adds a new location from double array.
	 * 
	 * @param coords
	 *            Array of x and y.
	 */
	public void addLocation(double[] coords) {
		locations.add(new Location(coords[0], coords[1]));

	}

	/**
	 * Saves the image of hits to file.
	 * 
	 * @param string
	 *            Name of the file.
	 */
	public void save(String string) {
		/*File outputfile = new File(Setup.getOutputDir() + string);
		BufferedImage image = craftImage();

		try {
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			logger.warn("Unable to save hitpoints map.");
		}*/
	}

	/**
	 * 
	 * @return Returns image containing the hit locations.
	 */
	private BufferedImage craftImage() {
		GridBase grid = GridBase.getInstance();

		// Create the image and get the graphics.
		BufferedImage image = new BufferedImage(grid.getWidth(), grid.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) image.getGraphics();

		for (Location l : locations) {
			g.setColor(Colors.RED);
			g.fillRect((int) l.getX() - 3, (int) l.getY() - 3, 6, 6);
		}

		return image;
	}

}
