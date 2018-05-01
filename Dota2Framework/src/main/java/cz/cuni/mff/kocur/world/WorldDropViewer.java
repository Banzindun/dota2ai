package cz.cuni.mff.kocur.world;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Map;

import javax.swing.JPanel;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.interests.InterestsBase;
import cz.cuni.mff.kocur.streaming.BaseDropViewer;
import cz.cuni.mff.kocur.streaming.InformationDrop;

public class WorldDropViewer extends BaseDropViewer {

	/**
	 * Generated serial version ID.
	 */
	private static final long serialVersionUID = -7045989090280984462L;

	private World world = null;

	public WorldDropViewer() {
		// Initialize BaseDropViewer using grid from WorldManager
		super(GridBase.getInstance());
	}

	@Override
	public void focused() {
		super.focused();
	}

	@Override
	public void receive(InformationDrop d) {
		if (this.isShowing()) {
			world = (World) d.getData();
		}

		// Will redraw the component
		super.receive(d);
	}

	private boolean pointInside(Rectangle r, int x, int y) {
		if (x < r.getX() || x > r.getX() + r.getWidth())
			return false;

		if (y < r.getY() || y > r.getY() + getHeight())
			return false;

		return true;
	}

	@Override
	protected void redrawBuffer() {
		if (buffer == null)
			return;

		// Can happen on the first iteration
		if (world == null)
			return;
		
		Graphics2D g = (Graphics2D) buffer.getGraphics();

		GridBase grid = GridBase.getInstance();

		Map<Integer, BaseEntity> entities = world.getEntities();

		g.drawImage(gridI, 0, 0, null);

		g.setPaint(Colors.ORANGE);

		for (BaseEntity e : entities.values()) {
			// I tell the entity to paint itself and I tell it where to paint itself.
			e.paint(grid.toTile(grid.resolveXY(e.getX(), e.getY())), g);
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
