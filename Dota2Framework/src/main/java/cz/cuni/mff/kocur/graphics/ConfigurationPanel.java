package cz.cuni.mff.kocur.graphics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.configuration.HeroConfiguration;
import cz.cuni.mff.kocur.configuration.FrameworkConfiguration;
import cz.cuni.mff.kocur.exceptions.ComponentNotBuiltException;

/**
 * Class that represents panel, that contains all the configuration entries and
 * sections containing them.
 * 
 * @author kocur
 *
 */
public class ConfigurationPanel extends BuildableJPanel {
	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = -856650013725726858L;

	/**
	 * Logger registered for ConfigurationPanel class.
	 */
	private static final Logger logger = LogManager.getLogger(ConfigurationPanel.class);

	/**
	 * GlobalConfiguration reference
	 */
	private FrameworkConfiguration cfg = FrameworkConfiguration.getInstance();

	/**
	 * List containing tabs.
	 */
	private LinkedList<ConfigurationEntry> tabs = new LinkedList<ConfigurationEntry>();

	/**
	 * TabbedPane with bot configuration tabs.
	 */
	private JTabbedPane tabPane = new JTabbedPane();

	/**
	 * Configuration entry that stores the framework configuration.
	 */
	private ConfigurationEntry frameworkEntry = null;

	/**
	 * Constraints for this JPanel.
	 */
	private GridBagConstraints gbc;

	/**
	 * ConfigurationPane onstructor. Sets up constraints and layout.
	 */
	public ConfigurationPanel() {
		this.setLayout(new GridBagLayout());

		tabPane.setBorder(BorderFactory.createEmptyBorder());
	}

	/**
	 * Sets up constraints for this class.
	 * 
	 * @return Returns new grid bag constraints.
	 */
	public GridBagConstraints setupGbc() {
		return ConstraintsBuilder.build().weightxy(1, 1).gridxy(0).insets(10).fill(GridBagConstraints.BOTH)
				.anchor(GridBagConstraints.NORTH).get();
	}

	/**
	 * Loads configurations of bots that are saved in botConfigurations pane.
	 * 
	 */
	@Override
	public void build() {
		gbc = setupGbc();

		buildAgentsSection();
		buildFrameworkSection();

		// Set this panel as built
		super.build();
	}

	/**
	 * Updates this section and all its entries.
	 */
	public void update() {
		// Get bot configurations
		ArrayList<HeroConfiguration> botConfigurations = cfg.getHeroConfigurations();

		// Check if there was a change in bot configurations - we do not want to reload
		// when it is not necessary
		if (!cfgChanged(botConfigurations) && !frameworkCfgChanged())
			return;

		this.removeAll();

		tabs.clear();
		tabPane.removeAll();

		build();

		// Validate and repaint
		this.revalidate();
		this.repaint();

	}

	/**
	 * Builds section, that contains agent configurations.
	 */
	private void buildAgentsSection() {
		// Get bot configurations
		ArrayList<HeroConfiguration> botConfigurations = cfg.getHeroConfigurations();

		// Create entry and new tab for every bot configuration
		for (HeroConfiguration c : botConfigurations) {
			ConfigurationEntry newTab = new ConfigurationEntry(c);
			newTab.build();

			try {
				tabPane.add(newTab.getComponent(), c.getName());
			} catch (ComponentNotBuiltException e) {
				logger.warn("I have tried to add configuration entry, that somehow wasn't built.");
				continue;
			}

			tabs.add(newTab);
		}

	}

	/**
	 * Builds a section, that contains the framework configuration.
	 */
	private void buildFrameworkSection() {

		// Create new entry for framework configuration
		frameworkEntry = new ConfigurationEntry(cfg);
		frameworkEntry.build();
		// frameworkEntry.setBorder(BorderFactory.createLoweredSoftBevelBorder());

		gbc.insets = new Insets(10, 10, 10, 0);
		this.add(tabPane, gbc);

		// Add frameworkEntry to this panel
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(10, 0, 10, 10);

		try {
			this.add(frameworkEntry.getComponent(), gbc);
		} catch (ComponentNotBuiltException e) {
			logger.warn("I have tried to add framework configuration entry, that somehow wasn't built.");
		}

	}

	/**
	 * Checks if the configurations are different from the ones that we are
	 * representing.
	 * 
	 * @param botConfigurations
	 *            List of bot configurations.
	 * @return Returns true if configurations changed.
	 */
	private boolean cfgChanged(ArrayList<HeroConfiguration> botConfigurations) {
		if (botConfigurations.size() != tabs.size())
			return true;

		// Check if I can find all configurations from tabs inside the configurations in
		// global cfg
		for (HeroConfiguration c : botConfigurations) {
			boolean foundEqual = false;
			for (int i = 0; i < tabs.size(); i++) {
				if (tabs.get(i).cfgEqual(c)) {
					foundEqual = true;
					break;
				}
			}

			if (foundEqual == false)
				return true; // I have not found a configuration equal to this one.
		}
		return false;
	}

	/**
	 * Checks if the framework configuration is different from the one we are
	 * displaying.
	 * 
	 * @return Returns true if the framework configuration changed.
	 */
	private boolean frameworkCfgChanged() {
		FrameworkConfiguration fcfg = FrameworkConfiguration.getInstance();
		boolean foundEqual = false;

		for (int i = 0; i < tabs.size(); i++) {
			if (frameworkEntry.cfgEqual(fcfg)) {
				foundEqual = true;
				break;
			}
		}

		if (foundEqual == false)
			return true; // I have not found a configuration equal to this one.

		return false;
	}

}
