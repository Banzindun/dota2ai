package cz.cuni.mff.kocur.streaming;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.dota2AIFramework.Setup;
import cz.cuni.mff.kocur.graphics.StreamOptionsWrapper;
import cz.cuni.mff.kocur.graphics.ZoomAndPanJPanel;
import cz.cuni.mff.kocur.world.GridBase;
import cz.cuni.mff.kocur.world.GridSystem;
import cz.cuni.mff.kocur.world.Tile;

public abstract class BaseDropViewer extends ZoomAndPanJPanel implements MouseWheelListener, MouseMotionListener, MouseListener{

	/**
	 * Generated serial version ID.
	 */
	private static final long serialVersionUID = 6848559487682461401L;
	public static BufferedImage gridI;

	public static void initImage() {
		GridBase grid = GridBase.getInstance();
		
		gridI = new BufferedImage(grid.getWidth(), grid.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) gridI.getGraphics();
		
		for (int y = 0; y < grid.getHeight(); y++) {
			for(int x = 0; x < grid.getWidth(); x++) {
				byte t = grid.getTile(x, y).type;
	
				switch(t) {
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
				
				
				g.fillRect(x, y, 1, 1);				
			}
		}
		
		// Get heights map and draw it over the above map
		BufferedImage heightsMap = createHeightsMap();
		
		float alpha = 0.7f;
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		g.setComposite(ac);
		g.drawImage(heightsMap, 0, 0, null);

	}

	
	private static BufferedImage createHeightsMap() {
		GridBase grid = GridBase.getInstance();
		BufferedImage heightsMap = new BufferedImage(grid.getWidth(), grid.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) heightsMap.getGraphics();
		
		Color[] shadesOfGrey = new Color[101];
		for (int i = 0; i < 101; i++) {
			shadesOfGrey[i] = Color.getHSBColor(0, 0, i/100f);
		}
		
		for (int y = 0; y < grid.getHeight(); y++) {
			for(int x = 0; x < grid.getWidth(); x++) {
				short height = grid.getTile(x, y).height;
				
				g.setColor(shadesOfGrey[(int) ((double) Math.abs(height)/1000*100)]);
				g.fillRect(x, y, 1, 1);				
			}
		}
		
		return heightsMap;
	}


	protected StreamOptionsWrapper wrapper = null;


	public BaseDropViewer(GridSystem g) {
		super(g);
	}	
		
	/**
	 * Receives the information drop.
	 * @param d
	 */
	public void receive(InformationDrop d) {
		if (this.isShowing()) {
			redrawBuffer();
			repaint();
		}		
	};
		
    public abstract JPanel getOptionsPanel();
	
	public StreamOptionsWrapper getWrapper() {
		return wrapper;
	}

	public void setWrapper(StreamOptionsWrapper wrapper) {
		this.wrapper = wrapper;
	}
}
