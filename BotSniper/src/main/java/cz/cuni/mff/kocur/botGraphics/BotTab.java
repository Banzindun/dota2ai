package cz.cuni.mff.kocur.botGraphics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.brain.Brain;
import cz.cuni.mff.kocur.dota2AIFramework.App;
import cz.cuni.mff.kocur.graphics.ConstraintsBuilder;
import cz.cuni.mff.kocur.graphics.FocusableJPanel;
import cz.cuni.mff.kocur.graphics.StreamsPanel;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import kocur.lina.bot.LinaBrain;
import kocur.lina.bot.LayeredBotContext;

public class BotTab extends FocusableJPanel {
	/**
	 * Logger registered for SreamsPanel.
	 */
	private Logger logger = LogManager.getLogger(StreamsPanel.class.getName());

	GridBagConstraints gbc;

	InfluenceContextWrapper influenceContextWrapper = null;
	
	ConsiderationsViewer considerationsViewer = null;

	protected JScrollPane scroller = null;
	
	protected String tabName;

	public BotTab(ExtendedBotContext context, String tabName) {
		super();
		
		this.tabName = tabName;
		
		influenceContextWrapper = new InfluenceContextWrapper(context);
		considerationsViewer = new ConsiderationsViewer(context.getBrain());

		// Set layout
		//this.setLayout(new GridBagLayout());
		this.setLayout(new GridLayout(1,2));
		
		// Add the tab
		App.getInstance().getWindow().getTabs().addTab(tabName, this);
		
		build();		
	}
	
	public void build() {
		/*gbc = ConstraintsBuilder.build()
				.gridxy(0)
				.weightxy(1, 1)
				.fill(GridBagConstraints.BOTH)
				.insets(0)
				.get();*/
		
		influenceContextWrapper.build();
		
		considerationsViewer.build();
		JScrollPane scroller = new JScrollPane(considerationsViewer);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		this.add(influenceContextWrapper);//, gbc);
		//gbc.gridx++;
		this.add(scroller);//, gbc);		
	}
	
	public void update() {
		considerationsViewer.update();
		influenceContextWrapper.update();
	
	}
	
	public void layersUpdated() {
		influenceContextWrapper.layersUpdated();
	}
	
	@Override
	public void focused() {
		influenceContextWrapper.focused();
		considerationsViewer.focused();
		
	}

	public void updateConsiderations() {
		considerationsViewer.update();		
	}

	public void destroyed() {
		// Remove the tab
		App.getInstance().getWindow().getTabs().removeTab(tabName);
		
	}
}
