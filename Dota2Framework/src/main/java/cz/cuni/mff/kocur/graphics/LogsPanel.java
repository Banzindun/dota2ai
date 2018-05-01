package cz.cuni.mff.kocur.graphics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.exceptions.ComponentNotBuiltException;
import cz.cuni.mff.kocur.logging.LoggingDistributor;

/**
 * Class that represents logs panel. That is a panel that stores the logs.
 * 
 * @author kocur
 *
 */
public class LogsPanel extends JPanel {
	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = -8544599447963250547L;

	/**
	 * LogsPane logger.
	 */
	private static Logger logger = LogManager.getLogger(LogsPanel.class.getName());

	/**
	 * Global configuration instance.
	 */
	// private static FrameworkConfiguration cfg =
	// FrameworkConfiguration.getInstance();

	/**
	 * TabPane that holds all the loggers that user registered.
	 */
	private JTabbedPane tabPane;

	/**
	 * List logging tabs that user registered. They can be deleted or a new one can
	 * be inserted, which is done in this list.
	 */
	private LinkedList<LoggingTab> tabs = new LinkedList<LoggingTab>();

	/**
	 * Panel for logCreation (add new or remove one)
	 */
	private JPanel logCreation = new JPanel();

	/**
	 * ComboBox for selecting which logger to add.
	 */
	private JComboBox<String> logCombo;

	/**
	 * LogsPane constructor - constructs LogsPane.
	 */
	public LogsPanel() {
		super();
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = setupGBC();

		tabPane = new DraggableTabbedPanel();

		// Initialize log creation and add it to this pane
		initLogCreation();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(logCreation, gbc);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 1;
		gbc.weighty = 1;

		// Add tabs to this pane
		this.add(tabPane, gbc);
	}

	/**
	 * Setups the constraints.
	 * 
	 * @return New constraints.
	 */
	private GridBagConstraints setupGBC() {
		return ConstraintsBuilder.build().gridxy(0).weightxy(1, 0).fill(GridBagConstraints.BOTH)
				.anchor(GridBagConstraints.WEST).insets(0, 10, 10, 10).get();
	}

	/**
	 * Initialized logCreation pane. This pane allows user to add LoggingTabs
	 */
	private void initLogCreation() {
		// Add titled border for creation block
		logCreation.setLayout(new GridBagLayout());

		// Create a simple holder that will hold comboBox and button together
		JPanel holder = new JPanel();

		// Get names of registered LoggingDistributors
		String[] names = LoggingDistributor.distributorNames();

		// Create combo box for choosing one of registered distributors
		logCombo = new JComboBox<String>(names);
		logCombo.setEditable(false);
		logCombo.setSelectedItem(names[0]);

		// Add to holder.
		holder.add(logCombo);

		// Create addButton for adding log to tabs
		JButton addButton = new JButton("Log");
		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Value of ComboBox
				String name = logCombo.getSelectedItem().toString();

				// First of all create a new log tab for the supplied name
				LoggingTab tab = new LoggingTab(name);
				tab.build();

				// And then register it in LogginDistributor
				LoggingDistributor.addDisplay(name, tab);

				// Add it to tabs
				try {
					tabs.add((LoggingTab) tab.get());
				} catch (ComponentNotBuiltException e1) {
					logger.warn("I have tried to add LoggingTab that haven't been built.");
				}

				updateTabPane();
			}
		});

		holder.add(addButton);
		holder.add(new HelpLabel("Select logger and click log to open logging tab."));

		GridBagConstraints gbc = setupGBC();
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		logCreation.add(holder, gbc);

		gbc.gridx++;
		gbc.anchor = GridBagConstraints.WEST;

	}

	/**
	 * Updates tab pane after change in tabs list. (after tab insertion or removal)
	 */
	private void updateTabPane() {
		tabPane.removeAll();

		for (LoggingTab t : tabs)
			tabPane.add(t, t.getName());

		tabPane.revalidate();
		tabPane.repaint();
	}
}
