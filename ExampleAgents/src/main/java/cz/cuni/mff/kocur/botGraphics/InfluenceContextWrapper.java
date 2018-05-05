package cz.cuni.mff.kocur.botGraphics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;

import javax.swing.JPanel;

import cz.cuni.mff.kocur.graphics.ConstraintsBuilder;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;

/**
 * This class represents a wrapper that wraps around incluence context viewer
 * and it's options panel.
 * 
 * @author kocur
 *
 */
public class InfluenceContextWrapper extends JPanel {
	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = 4318361201660095406L;

	/**
	 * Our influence context viewer.
	 */
	protected InfluenceContextViewer contextViewer = null;

	/**
	 * Our options panel.
	 */
	protected JPanel optionsPanel = new JPanel();

	/**
	 * Grid bag constraints we use.
	 */
	private GridBagConstraints gbc;

	/**
	 * Creates ICW from the passed context.
	 * 
	 * @param context
	 *            Context.
	 */
	public InfluenceContextWrapper(ExtendedAgentContext context) {
		this.contextViewer = new InfluenceContextViewer(context);

		this.setLayout(new GridBagLayout());
	}

	/**
	 * Builds the context wrapper.
	 */
	public void build() {
		gbc = ConstraintsBuilder.build().gridxy(0).weightxy(1, 0).fill(GridBagConstraints.BOTH).get();

		this.add(contextViewer.getOptionsPanel(), gbc);

		gbc.gridy++;
		gbc.weighty = 1;

		this.add(contextViewer, gbc);
	}

	/**
	 * Updates the wrapper.
	 */
	public void update() {
		if (this.isShowing() == false)
			return;

		this.removeAll();

		gbc = ConstraintsBuilder.build().gridxy(0).weightxy(1, 0).fill(GridBagConstraints.BOTH).get();

		Rectangle rect = contextViewer.getBounds();
		Rectangle visibleRect = contextViewer.getVisibleRect();
		double tx = (rect.getWidth() - visibleRect.getWidth()) / 2;
		double ty = (rect.getHeight() - visibleRect.getHeight()) / 2;
		visibleRect.setBounds((int) tx, (int) ty, visibleRect.width, visibleRect.height);
		contextViewer.scrollRectToVisible(visibleRect);

		optionsPanel = contextViewer.getOptionsPanel();

		if (optionsPanel != null) {
			this.add(optionsPanel, gbc);
			gbc.gridy++;
		}

		gbc.weighty = 1;
		this.add(contextViewer, gbc);

		this.revalidate();
		this.repaint();

	}

	/**
	 * Called after IM updated.
	 */
	public void layersUpdated() {
		contextViewer.layersUpdated();

	}

	/**
	 * Called after this panel was focused.
	 */
	public void focused() {
		if (!contextViewer.isEverythingUpdated())
			update();
		else
			contextViewer.focused();
	}

}
