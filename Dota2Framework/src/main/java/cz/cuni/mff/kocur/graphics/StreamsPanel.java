package cz.cuni.mff.kocur.graphics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.streaming.BaseDropViewer;
import cz.cuni.mff.kocur.streaming.DropCatcher;
import cz.cuni.mff.kocur.streaming.Sink;

/**
 * Panel that displays "streams".
 * 
 * @author kocur
 *
 */
public class StreamsPanel extends BuildableJPanel implements ChangeListener {
	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = -8713632292536555438L;

	/**
	 * Logger registered for SreamsPanel.
	 */
	private Logger logger = LogManager.getLogger(StreamsPanel.class.getName());

	/**
	 * Draggable tabs that represent the streams.
	 */
	private DraggableTabbedPanel tabs = new DraggableTabbedPanel();

	/**
	 * Active drop catcher.
	 */
	public static DropCatcher activeCatcher = null;

	/**
	 * List of catchers.
	 */
	ArrayList<DropCatcher> catchers;

	/**
	 * Constraints we use.
	 */
	GridBagConstraints gbc;

	/**
	 * Constructor, that sets the layout and builds the constraints.
	 */
	public StreamsPanel() {
		this.setLayout(new GridBagLayout());
		gbc = ConstraintsBuilder
				.build()
				.gridxy(0)
				.weightxy(1)
				.fill(GridBagConstraints.BOTH)
				.get();
	}

	/**
	 * Builds the panel.
	 */
	public void build() {
		tabs.addChangeListener(this);

		updateTabs();
		this.add(tabs, gbc);

		// call build on super.
		super.build();
	}

	/**
	 * Called when user switches to this tab.
	 */
	public void focused() {
		if (Sink.hasCatchersChanged()) {
			updateTabs();
			Sink.setCatchersChanged(false);
		}
	}

	/**
	 * Updates the tabs.
	 */
	private void updateTabs() {
		logger.debug("Updating tabs.");

		catchers = Sink.getCatchers();

		// Better safe than sorry
		if (catchers == null)
			return;

		logger.debug("I have obtained " + catchers.size() + "catchers.");

		tabs.removeAll();
		for (DropCatcher c : catchers) {
			StreamOptionsWrapper wrap = new StreamOptionsWrapper(c.getViewer());
			tabs.add(c.getCatcherName(), wrap);
		}
	}

	/**
	 * Called after tabs are switched. We check which tab is active and alert the
	 * tab.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		JTabbedPane tabSource = (JTabbedPane) e.getSource();
		// String tab = tabSource.getTitleAt(tabSource.getSelectedIndex());

		int index = tabSource.getSelectedIndex();
		if (index != -1) {
			BaseDropViewer dv = (BaseDropViewer) catchers.get(index).getViewer();
			if (dv != null)
				dv.focused();
		}
	}

}
