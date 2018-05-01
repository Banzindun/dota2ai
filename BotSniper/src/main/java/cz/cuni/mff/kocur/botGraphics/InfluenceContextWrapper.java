package cz.cuni.mff.kocur.botGraphics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;

import javax.swing.JPanel;
import cz.cuni.mff.kocur.graphics.ConstraintsBuilder;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;

public class InfluenceContextWrapper extends JPanel{

	protected InfluenceContextViewer contextViewer = null;
	
	protected JPanel optionsPanel = new JPanel();
	
	private GridBagConstraints gbc;

	public InfluenceContextWrapper(ExtendedBotContext context) {
		this.contextViewer = new InfluenceContextViewer(context);
		
		this.setLayout(new GridBagLayout());
	}

	public void build() {
		gbc = ConstraintsBuilder.build()
				.gridxy(0)
				.weightxy(1, 0)
				.fill(GridBagConstraints.BOTH)
				.get();
		
		this.add(contextViewer.getOptionsPanel(), gbc);
		
		gbc.gridy++;
		gbc.weighty = 1;

		this.add(contextViewer, gbc);
	}
	
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

	public void layersUpdated() {
		contextViewer.layersUpdated();
		
	}
	
	public void focused() {
		if (!contextViewer.isEverythingUpdated())
			update();
		else 
			contextViewer.focused();
	}

}
