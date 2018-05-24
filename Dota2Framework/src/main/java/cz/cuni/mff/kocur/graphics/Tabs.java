package cz.cuni.mff.kocur.graphics;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cz.cuni.mff.kocur.events.Event;
import cz.cuni.mff.kocur.events.FrameworkEventListener;
import cz.cuni.mff.kocur.events.ListenersManager;
import cz.cuni.mff.kocur.framework.App;
import cz.cuni.mff.kocur.framework.App.State;

/**
 * Class that stores all the tabs, that are displayed in our application. Lets
 * user add his own tabs.
 * 
 * @author kocur
 *
 */
public class Tabs extends JTabbedPane implements FrameworkEventListener, ChangeListener {

	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = -14720610140045033L;

	/**
	 * Map of all user tabs. (user can add his own tabs)
	 */
	private HashMap<String, FocusableJPanel> userTabs = new HashMap<>();

	/**
	 * Adds a tab to the tabs.
	 * 
	 * @param name
	 *            Name of the tab.
	 * @param panel
	 *            Focusable panel, that represents the tab.
	 */
	public void addTab(String name, FocusableJPanel panel) {
		userTabs.put(name, panel);
		updateTabs();
	}

	/**
	 * Removes tab from tabs.
	 * 
	 * @param name
	 *            Name of the tab that should be removed.
	 */
	public void removeTab(String name) {
		FocusableJPanel tab = userTabs.get(name);

		this.remove(tab);

		userTabs.remove(name);
	}

	/**
	 * Lets user choose bot configuration files and start the bots.
	 */
	private MainPanel main;

	/**
	 * Lets user control the bots and UI.
	 */
	private ConsolePanel console = new ConsolePanel();

	/**
	 * Here are displayed the logs.
	 */
	private LogsPanel logs = null;

	/**
	 * Streams panel.
	 */
	private WorldPanel worldViewer = new WorldPanel();

	/**
	 * Configuration of bots.
	 */
	private ConfigurationPanel configuration = new ConfigurationPanel();

	/**
	 * Simple constructor.
	 */
	public Tabs() {
		// Listen to state_changed events. (INIT -> RUNNING etc.)
		ListenersManager.addFrameworkListener("state_changed", this);

	}

	/**
	 * Builds the tabs. Adds main, console and configurations tabs and initializes
	 * tabs that will be added in later stages of application.
	 */
	public void build() {
		main = new MainPanel();
		
		main.setup();
		console.build();
		worldViewer.build();

		this.add("Main", main);
		this.add("Console", console);
		this.add("Configuration", configuration);
		this.add(worldViewer, "World");

		this.addChangeListener(this);
	}

	/**
	 * Initializes logs tab if it is not initialized yet and adds it to tabs.
	 */
	private void addLogTab() {
		if (logs == null) {
			logs = new LogsPanel();
		}
		this.add("Logging", logs);
	}

	/**
	 * Updates tabs.
	 */
	private void updateTabs() {
		if (App.state == State.INIT || App.state == State.CONFIGURATION) {
			// Remove logs and streams if they are present.
			if (logs != null) {
				this.remove(logs);
				logs = null;
			}

			for (Entry<String, FocusableJPanel> e : userTabs.entrySet()) {
				this.remove(e.getValue());
			}

		} else if (App.state == State.RUNNING && App.lastState == State.INIT) {
			// Add logging tab and streams tab
			addLogTab();

			for (Entry<String, FocusableJPanel> e : userTabs.entrySet()) {
				this.add(e.getKey(), e.getValue());
			}
		}
	}

	/**
	 * Called after tabs are switched. We check which tab is active and alert the
	 * tab.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		JTabbedPane tabSource = (JTabbedPane) e.getSource();
		String tab = tabSource.getTitleAt(tabSource.getSelectedIndex());

		if (tab.equals("Configuration")) {
			configuration.update();
		} else if (tab.equals("Console")) {
			console.focused();
		} else if (tab.equals("World")) {
			worldViewer.focused();
		} else {
			FocusableJPanel p = userTabs.get(tab);

			if (p == null)
				return;
			p.focused();
		}

	}

	@Override
	public void triggered(Event e) {
		updateTabs();
	}

	@Override
	public void triggered(Event e, Object... os) {
		triggered(e);

	}

}
