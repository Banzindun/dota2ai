package cz.cuni.mff.kocur.world;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Map;

import javax.swing.JPanel;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.interests.InterestsBase;

/**
 * A class that serves for viewing world drops.
 * 
 * @author kocur
 *
 */
public class WorldViewer extends BaseViewer {

	/**
	 * Generated serial version ID.
	 */
	private static final long serialVersionUID = -7045989090280984462L;

	/**
	 * The world object, we have received.
	 */
	private World world = null;

	public WorldViewer() {
		// Initialize BaseDropViewer using grid from WorldManager
		super(GridBase.getInstance());
	}

	public void switchWorld(World world) {
		this.world = world;
	}
	
	@Override
	public void focused() {
		super.focused();
	}

	@Override
	protected void redrawBuffer() {
		if (buffer == null)
			return;

		Graphics2D g = (Graphics2D) buffer.getGraphics();

		GridBase grid = GridBase.getInstance();

		g.drawImage(gridI, 0, 0, null);

		// Can happen on the first iteration
		if (world == null)
			return;
		
		Map<Integer, BaseEntity> entities = world.getEntities();

		g.setPaint(Colors.ORANGE);

		for (BaseEntity e : entities.values()) {
			// I tell the entity to paint itself and I tell it where to paint itself.
			e.paint(GridBase.toTile(grid.resolveXY(e.getX(), e.getY())), g);
		}

		InterestsBase.getInstance().paintInterests(g);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	@Override
	public JPanel getOptionsPanel() {
		return null;
	}

}
