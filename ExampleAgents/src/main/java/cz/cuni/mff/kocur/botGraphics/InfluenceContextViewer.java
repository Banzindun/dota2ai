package cz.cuni.mff.kocur.botGraphics;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.framework.Setup;
import cz.cuni.mff.kocur.graphics.ConstraintsBuilder;
import cz.cuni.mff.kocur.graphics.ZoomAndPanJPanel;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.influence.InfluenceLayer;
import cz.cuni.mff.kocur.streaming.BaseDropViewer;
import cz.cuni.mff.kocur.world.BaseEntity;
import kocur.lina.agent.LayeredAgentContext;

/**
 * This class displays the influence layers etc.
 * 
 * @author kocur
 *
 */
public class InfluenceContextViewer extends ZoomAndPanJPanel implements ActionListener {
	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = -7597363410674890352L;

	/**
	 * Options panel.
	 */
	private JPanel options = null;

	/**
	 * Combo box for selecting the layers.
	 */
	private JComboBox<String> layerSelector = null;

	/**
	 * Layer we are displaying.
	 */
	private String displayedLayer = "";

	/**
	 * Agent's context.
	 */
	private ExtendedAgentContext context = null;

	/**
	 * The map we have displayed.
	 */
	private double[][] map = null;

	/**
	 * Maximum value in the map.
	 */
	private double max = 0;

	/**
	 * Minimum value in the map.
	 */
	private double min = 0;

	/**
	 * Origin of the maximum value.
	 */
	private int[] maxOrigin = null;

	/**
	 * Origin of the minimum value.
	 */
	private int[] minOrigin = null;

	/**
	 * The layer we are displaying.
	 */
	private InfluenceLayer layer = null;

	/**
	 * Checkbox for showing the entitites.
	 */
	private JCheckBox showEntities = new JCheckBox("entities");

	/**
	 * Checkbox for showing the grid.
	 */
	private JCheckBox showGrid = new JCheckBox("grid");

	/**
	 * True if the entities are showing.
	 */
	private boolean entitiesShowing = false;

	/**
	 * True if the grid is showing.
	 */
	private boolean gridShowing = false;

	/**
	 * Constructor, that takes extended bot's context.
	 * 
	 * @param c
	 *            Agent's context.
	 */
	public InfluenceContextViewer(ExtendedAgentContext c) {
		super();

		this.context = c;
		resolution = 2;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	// Synchronized?
	@Override
	protected void redrawBuffer() {
		if (buffer == null)
			return;

		Graphics2D g = (Graphics2D) buffer.getGraphics();

		// We don't want to draw if the layer is black.
		if (max == Double.NEGATIVE_INFINITY && min == Double.POSITIVE_INFINITY)
			return;
		// g.clearRect(0, 0, _width, _height);

		// Rectangle r = g.getClipBounds();
		// if (r == null)
		// return;

		if (map == null)
			return;

		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[0].length; x++) {

				// logger.debug(xy[0] + " " + xy[1]);
				double inf = map[y][x];

				double norm = 0; // (map[y][x]-min)/(max-min);
				if (inf < 0)
					norm = -inf / min;
				else {
					norm = inf / max;
				}

				if (norm == 0)
					g.setPaint(Color.BLACK);
				else
					g.setPaint(Colors.getColor(norm));

				g.fillRect(x * resolution, y * resolution, resolution, resolution);
			}
		}

		// Paint the maximum and minimum
		paintInfimum(g, maxOrigin, Colors.PURPLE);
		paintInfimum(g, minOrigin, Colors.PURPLE);

		if (entitiesShowing) {
			g.scale(resolution, resolution);

			for (BaseEntity e : context.getEntites().values()) {
				// I tell the entity to paint itself and I tell it where to paint itself.
				Integer[] cords = InfluenceLayer.toTile(layer.getEntityCoordinates(e));
				e.paint(cords, g);
			}

			g.scale(0.5, 0.5);
		}

		if (gridShowing) {
			float alpha = 0.3f;
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
			g.setComposite(ac);

			g.scale(resolution, resolution);
			g.drawImage(BaseDropViewer.gridI, (int) -layer.resolveXBack(0), (int) -layer.resolveYBack(0), null);
			g.scale(0.5, 0.5);
		}

	}

	/**
	 * Paints the minimum/maximum with given color.
	 * 
	 * @param g
	 *            Graphics.
	 * @param origin
	 *            [x, y]
	 * @param c
	 *            Color that we use to paint.
	 */
	private void paintInfimum(Graphics2D g, int[] origin, Color c) {
		g.setPaint(c);
		g.fillRect(origin[0] * resolution, origin[1] * resolution, resolution, resolution);
	}

	/**
	 * 
	 * @return Returns the panel, that contains options panel of this viewer.
	 */
	public JPanel getOptionsPanel() {
		options = new JPanel(new GridBagLayout());

		GridBagConstraints gbc = ConstraintsBuilder.build().gridxy(0).fill(GridBagConstraints.HORIZONTAL).weightxy(1)
				.get();

		layerSelector = new JComboBox<String>(context.getLayerNames());

		displayedLayer = layerSelector.getItemAt(layerSelector.getItemCount() - 1);
		layerSelector.setSelectedIndex(layerSelector.getItemCount() - 1);

		layerSelector.addActionListener(this);

		showGrid.addActionListener(this);
		showEntities.addActionListener(this);

		JPanel wrap = new JPanel();
		wrap.add(layerSelector);
		wrap.add(showGrid);
		wrap.add(showEntities);

		options.add(wrap, gbc);

		return options;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == layerSelector) {
			displayedLayer = (String) layerSelector.getSelectedItem();
			layersUpdated();
			redrawBuffer();
			repaint();
		} else if (e.getSource() == showGrid) {
			gridShowing = showGrid.isSelected();
			redrawBuffer();
			repaint();
		} else if (e.getSource() == showEntities) {
			entitiesShowing = showEntities.isSelected();
			redrawBuffer();
			repaint();
		}

	}

	/**
	 * 
	 * @return Returns true, if everything is up to date.
	 */
	public boolean isEverythingUpdated() {
		if (context.getLayerNames().length != layerSelector.getItemCount())
			return false;

		return true;
	}

	/**
	 * Called after IM were updated.
	 */
	public void layersUpdated() {
		if (this.isShowing()) {
			layer = context.getLayer(LayeredAgentContext.getLayerNumber(displayedLayer));
			if (layer == null) {
				return;
			}

			// If height and width are not equal change width and height of parent.
			if (layer.getHeight() != _height || layer.getWidth() != _width) {
				setup(resolution * layer.getWidth(), resolution * layer.getHeight());
			}

			map = layer.getMapCopy();
			max = layer.getHighestValue();
			min = layer.getLowestValue();

			minOrigin = layer.getLowestLocation();
			maxOrigin = layer.getHighestLocation();

			redrawBuffer();
			repaint();
		}

	}

	/**
	 * Saves current buffer to file.
	 * 
	 * @param fileName
	 *            Name of the file.
	 */
	public void saveDisplayedLayer(String fileName) {
		if (buffer == null)
			return;

		File outputfile = new File(Setup.getOutputDir() + fileName);

		try {
			ImageIO.write(buffer, "png", outputfile);
		} catch (IOException e) {
			// nothing
		}
	}
}
