package cz.cuni.mff.kocur.streaming;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.graphics.StreamOptionsWrapper;
import cz.cuni.mff.kocur.graphics.ZoomAndPanJPanel;
import cz.cuni.mff.kocur.world.GridBase;
import cz.cuni.mff.kocur.world.GridSystem;
import cz.cuni.mff.kocur.world.Tile;

/**
 * A base viewer we use to display world. This class has static methods, that
 * create images for the map.
 * 
 * @author kocur
 *
 */
public abstract class BaseDropViewer extends ZoomAndPanJPanel
		implements MouseWheelListener, MouseMotionListener, MouseListener {

	/**
	 * Generated serial version ID.
	 */
	private static final long serialVersionUID = 6848559487682461401L;

	/**
	 * Image, that represents the grid.
	 */
	public static BufferedImage gridI;

	/**
	 * Static method, that creates and stores image from GridBase tiles. The image
	 * contains passable and impassable tiles. It also contains heights, that
	 * highlight the map.
	 */
	public static void initImage() {
		GridBase grid = GridBase.getInstance();

		// Create the image and get the graphics.
		gridI = new BufferedImage(grid.getWidth(), grid.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) gridI.getGraphics();

		// Go through the tiles
		for (int y = 0; y < grid.getHeight(); y++) {
			for (int x = 0; x < grid.getWidth(); x++) {
				// Set color and fill the rectangle
				byte t = grid.getTile(x, y).type;
				setColor(g, t);
				g.fillRect(x, y, 1, 1);
			}
		}

		// Get heights map and draw it over the above map
		BufferedImage heightsMap = createHeightsMap();

		// Draw heights with some alpha over the map
		float alpha = 0.7f;
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		g.setComposite(ac);
		g.drawImage(heightsMap, 0, 0, null);
	}

	/**
	 * Takes a graphics and a type. Sets color according to the type.
	 * 
	 * @param g
	 *            Graphics.
	 * @param type
	 *            Type of the tile.
	 */
	private static void setColor(Graphics2D g, byte type) {
		switch (type) {
		case Tile.TRAVERSABLE:
			g.setColor(Colors.TREEGREEN);
			break;
		case Tile.BLOCKED:
			g.setColor(Colors.WHITE);
			break;
		case Tile.NOTTRAVERSABLE:
			g.setColor(Colors.DARKGRAY);
			break;
		default:
			g.setColor(Colors.BASE);
			break;
		}
	}

	/**
	 * Creates heights map.
	 * 
	 * @return Returns a image, that represents a heights map in shades of gray.
	 */
	private static BufferedImage createHeightsMap() {
		GridBase grid = GridBase.getInstance();
		BufferedImage heightsMap = new BufferedImage(grid.getWidth(), grid.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) heightsMap.getGraphics();

		Color[] shadesOfGrey = new Color[101];
		for (int i = 0; i < 101; i++) {
			shadesOfGrey[i] = Color.getHSBColor(0, 0, i / 100f);
		}

		for (int y = 0; y < grid.getHeight(); y++) {
			for (int x = 0; x < grid.getWidth(); x++) {
				short height = grid.getTile(x, y).height;

				g.setColor(shadesOfGrey[(int) ((double) Math.abs(height) / 1000 * 100)]);
				g.fillRect(x, y, 1, 1);
			}
		}

		return heightsMap;
	}

	/**
	 * Wrapper, that wraps around the options.
	 */
	protected StreamOptionsWrapper wrapper = null;

	public BaseDropViewer(GridSystem g) {
		super(g);
	}

	/**
	 * Receives the information drop.
	 * 
	 * @param d The drop.
	 */
	public void receive(InformationDrop d) {
		if (this.isShowing()) {
			redrawBuffer();
			repaint();
		}
	};

	/**
	 * 
	 * @return Returns a options panel.
	 */
	public abstract JPanel getOptionsPanel();

	/**
	 * 
	 * @return Returns the options wrapper.
	 */
	public StreamOptionsWrapper getWrapper() {
		return wrapper;
	}

	/**
	 * Sets the options wrapper.
	 * 
	 * @param wrapper
	 *            The wrapper.
	 */
	public void setWrapper(StreamOptionsWrapper wrapper) {
		this.wrapper = wrapper;
	}
}
