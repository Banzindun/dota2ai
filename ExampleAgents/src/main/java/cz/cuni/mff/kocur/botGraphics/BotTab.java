package cz.cuni.mff.kocur.botGraphics;

import java.awt.GridLayout;

import javax.swing.JScrollPane;

import cz.cuni.mff.kocur.framework.App;
import cz.cuni.mff.kocur.graphics.FocusableJPanel;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;

/**
 * Bot tab is a example of how one might extend framework's tabs.
 * 
 * @author kocur
 *
 */
public class BotTab extends FocusableJPanel {
	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = -5692636748301689746L;

	/**
	 * Wraps around influence context.
	 */
	protected InfluenceContextWrapper influenceContextWrapper = null;

	/**
	 * Considerations viewer - displays the configurations and their score.
	 */
	protected ConsiderationsViewer considerationsViewer = null;

	/**
	 * Scroller.
	 */
	protected JScrollPane scroller = null;

	/**
	 * Tab's name.
	 */
	protected String tabName;

	/**
	 * Constructor of BotTab.
	 * 
	 * @param context
	 *            Agent's context.
	 * @param tabName
	 *            Name of the tab.
	 */
	public BotTab(ExtendedAgentContext context, String tabName) {
		super();

		this.tabName = tabName.substring(0, 1).toUpperCase() + tabName.substring(1);

		influenceContextWrapper = new InfluenceContextWrapper(context);
		considerationsViewer = new ConsiderationsViewer(context.getBrain());

		// Set layout
		this.setLayout(new GridLayout(1, 2));

		// Add the tab
		App.getInstance().getWindow().getTabs().addTab(tabName, this);

		build();
	}

	/**
	 * Builds the tab.
	 */
	public void build() {
		influenceContextWrapper.build();

		considerationsViewer.build();
		JScrollPane scroller = new JScrollPane(considerationsViewer);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		this.add(influenceContextWrapper);
		this.add(scroller);
	}

	/**
	 * Updates the tab.
	 */
	public void update() {
		considerationsViewer.update();
		influenceContextWrapper.update();

	}

	/**
	 * Called after the influence layers were updated.
	 */
	public void layersUpdated() {
		influenceContextWrapper.layersUpdated();
	}

	@Override
	public void focused() {
		influenceContextWrapper.focused();
		considerationsViewer.focused();

	}

	/**
	 * Updates the consideration viewer.
	 */
	public void updateConsiderations() {
		considerationsViewer.update();
	}

	/**
	 * Called if the agent is being destroyed.
	 */
	public void destroyed() {
		// Remove the tab
		App.getInstance().getWindow().getTabs().removeTab(tabName);

	}
}
