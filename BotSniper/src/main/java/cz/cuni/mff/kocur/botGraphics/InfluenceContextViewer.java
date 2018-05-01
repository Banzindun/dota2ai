package cz.cuni.mff.kocur.botGraphics;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.graphics.ConstraintsBuilder;
import cz.cuni.mff.kocur.graphics.ZoomAndPanJPanel;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.influence.InfluenceLayer;
import cz.cuni.mff.kocur.streaming.BaseDropViewer;
import cz.cuni.mff.kocur.world.BaseEntity;
import kocur.lina.bot.LayeredBotContext;

public class InfluenceContextViewer extends ZoomAndPanJPanel implements ActionListener {
	private Logger logger = LogManager.getLogger(InfluenceContextViewer.class.getName());

	private JPanel options = null;

	private JComboBox<String> layerSelector = null;

	private String displayedLayer = "";

	private ExtendedBotContext context = null;

	private double[][] map = null;

	private double max = 0;

	private double min = 0;
	
	private int[] maxOrigin = null;
	
	private int[] minOrigin = null;

	private InfluenceLayer layer = null;

	private JCheckBox showEntities = new JCheckBox("entities");
	private JCheckBox showGrid = new JCheckBox("grid");

	private boolean entitiesShowing = false;
	private boolean gridShowing = false;

	public InfluenceContextViewer(ExtendedBotContext c) {
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

	private void paintInfimum(Graphics2D g, int[] origin, Color c) {
		g.setPaint(c);
		g.fillRect(origin[0] * resolution, origin[1] * resolution, resolution, resolution);		
	}

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

	public boolean isEverythingUpdated() {
		if (context.getLayerNames().length != layerSelector.getItemCount())
			return false;

		return true;
	}

	public void layersUpdated() {
		if (this.isShowing()) {
			layer = context.getLayer(LayeredBotContext.getLayerNumber(displayedLayer));
			if (layer == null) {
				return;
			}

			// If height and width are not equal change width and height of parent.
			if (layer.getHeight() != _height || layer.getWidth() != _width) {
				setup(resolution*layer.getWidth(), resolution*layer.getHeight());
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

}
