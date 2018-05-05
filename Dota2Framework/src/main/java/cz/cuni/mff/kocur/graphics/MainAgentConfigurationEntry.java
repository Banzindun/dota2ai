package cz.cuni.mff.kocur.graphics;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.base.GraphicResources;
import cz.cuni.mff.kocur.base.Pair;
import cz.cuni.mff.kocur.configuration.ConfigurationChangeListener;
import cz.cuni.mff.kocur.configuration.ConfigurationLoader;
import cz.cuni.mff.kocur.configuration.FrameworkConfiguration;
import cz.cuni.mff.kocur.configuration.HeroConfiguration;
import cz.cuni.mff.kocur.exceptions.ConfigurationTestFailureException;
import cz.cuni.mff.kocur.exceptions.LoadingError;
import cz.cuni.mff.kocur.interests.Team;

/**
 * This is a JPanel that containts Swing components, that let the user create
 * and manage agent configuration.
 * 
 * @author Banzindun
 *
 */
public class MainAgentConfigurationEntry extends WindowedJPanel implements ConfigurationChangeListener {
	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = -7215256965977626755L;

	/**
	 * Logger for this class.
	 */
	private static final Logger logger = LogManager.getLogger(MainAgentConfigurationEntry.class);

	/**
	 * Configuration of bot assigned to this loader.
	 */
	private HeroConfiguration botCfg = null;

	/**
	 * Reference to parent, so we can tell him when we were destroyed.
	 */
	private MainAgentConfiguration parent = null;

	/**
	 * Path to the file that was loaded.
	 */
	private String filePath = null;

	/**
	 * If loading the configuration failed. Then this might be removed by parent.
	 */
	private boolean failed = false;

	/**
	 * Hero identification - his miniicon and name.
	 */
	private JPanel botIdPanel = new JPanel(new GridBagLayout());

	/**
	 * JPanel that holds textual information about imporant fields from
	 * configuration.
	 */
	private JPanel importantFields = new JPanel(new GridBagLayout());

	/**
	 * Agent hero's miniicon.
	 */
	private JLabel miniicon = new JLabel();

	/**
	 * Label with agent's id.
	 */
	private JLabel agentsIdLabel = new JLabel();

	/**
	 * Map of important labels, so that they can be rewritten.
	 */
	private HashMap<String, JLabel> importantLabels = new HashMap<>();

	/**
	 * Number of team of the supplied agent.
	 */
	private int teamNumber = 0;

	/**
	 * Constructor that reads the supplied file and loads the configuration.
	 * 
	 * @param file
	 *            File from which we create the entry.
	 */
	public MainAgentConfigurationEntry(File file) {
		super();
		this.filePath = file.getAbsolutePath();
	}

	/**
	 * Builds body, then calls build on super.
	 */
	public void build() {
		buildBody();
		super.build();
	}

	/**
	 * Builds the body.
	 */
	private void buildBody() {
		// Pass
	}

	/**
	 * 
	 * @return constraints for informationPanel.
	 */
	private GridBagConstraints getInformationConstraints() {
		// Initialize constraints
		return ConstraintsBuilder.build().gridxy(0).insets(5).weightxy(1, 0).fill(GridBagConstraints.NONE)
				.anchor(GridBagConstraints.WEST).get();
	}

	/**
	 * Displays information to panel for failed loading.
	 * 
	 * @param msg
	 *            message obtained from test of the configuration
	 */
	private void loadFailed(String msg) {
		failed = true;

		logger.warn("Loading of bot configuration at: " + filePath + " has failed with message.", msg);

		// Get constraints
		GridBagConstraints _gbc = getInformationConstraints();

		JTextPane failMsg = new JTextPane();
		failMsg.setText(msg);
		failMsg.setBackground(this.getBackground());
		failMsg.setBorder(BorderFactory.createEmptyBorder());
		failMsg.setForeground(Colors.RED);

		// Add it to body
		body.add(failMsg, _gbc);
	}

	/**
	 * Called if the loading of configuration passed.
	 */
	private void loadPassed() {
		logger.info("Successfully loaded bot cofiguration from: " + filePath);

		// Set the team number.
		teamNumber = Team.parseTeam(botCfg.getConfigValue("team"));

		displayPassedLoad();
	}

	/**
	 * Displays information, if the load passed.
	 */
	private void displayPassedLoad() {
		// Load important fields
		LinkedList<Pair<String, String>> important;
		important = botCfg.getImportantValues();

		// Setup constraints for importantFields JPanel
		GridBagConstraints _gbc = getInformationConstraints();

		buildHeroIconPanel();

		body.add(botIdPanel, gbc);

		_gbc.insets = new Insets(0, 0, 0, 0);

		// Add the important fields
		for (Pair<String, String> s : important) {
			JLabel panel = new JLabel(s.getValue());
			importantLabels.put(s.getKey(), panel);

			JPanel wrap = new JPanel();
			wrap.add(createColoredLabel(s.getKey() + ": ", Colors.ORANGE));
			wrap.add(new JLabel(s.getValue()));
			importantFields.add(wrap, _gbc);

			_gbc.gridx++;

			if (_gbc.gridx == 3) {
				_gbc.gridy++;
				_gbc.gridx = 0;
			}
		}

		gbc.gridx++;
		gbc.weightx = 1;
		body.add(importantFields, gbc);

		// Reset the gbc back
		gbc.gridx = 0;
		gbc.weightx = 0;

		changeTitle(botCfg.getType().name() + ": " + botCfg.getName());
	}

	/**
	 * Builds panel with agent's hero icon.
	 */
	private void buildHeroIconPanel() {
		GridBagConstraints _gbc = ConstraintsBuilder.build().gridxy(0).weightxy(1, 0).fill(GridBagConstraints.BOTH)
				.insets(0, 5, 5, 5).get();

		// Load and add the bot icon
		String botName = botCfg.getConfigValue("champion");
		miniicon.setIcon(GraphicResources.getBotIcon(botName));
		botIdPanel.add(miniicon, _gbc);

		_gbc.insets = new Insets(0, 5, 5, 5);
		_gbc.gridy++;
		agentsIdLabel.setText(botName.toUpperCase());
		botIdPanel.add(agentsIdLabel, _gbc);
	}

	/**
	 * Creates colored label.
	 * 
	 * @param text
	 *            The label text.
	 * @param c
	 *            Color of the label.
	 * @return Returns colored JLabel.
	 */
	private JLabel createColoredLabel(String text, Color c) {
		JLabel l = new JLabel(text);
		l.setForeground(c);
		return l;
	}

	/**
	 * Updates information from important fields.
	 * 
	 * @param important
	 *            Important fields.
	 */
	private void updateInformation(LinkedList<Pair<String, String>> important) {
		// Load and add the bot icon
		String botName = botCfg.getConfigValue("champion");
		miniicon.setIcon(GraphicResources.getBotIcon(botName));

		agentsIdLabel.setText(botName.toUpperCase());

		// Add the important fields
		for (Pair<String, String> s : important) {

			if (importantLabels.containsKey(s.getKey())) {
				JLabel panel = importantLabels.get(s.getKey());
				panel.setText(s.getValue());

			} else {
				//
			}
		}

		changeTitle(botCfg.getName());
	}

	/**
	 * Parses the configuration using the configuration loader.
	 */
	private void parseConfiguration() {
		try {
			ConfigurationLoader loader = new ConfigurationLoader(filePath);
			botCfg = loader.loadBotConfiguration();
		} catch (LoadingError err) {
			logger.error("Could not load bot configuration from file " + filePath, err);
		}

		botCfg.addChangeListener(this);
	}

	/**
	 * Loads the hero's configuration from filePath.
	 * 
	 * @return Returns the loaded hero configuration or null.
	 */
	public HeroConfiguration loadConfiguration() {
		logger.info("Loading configuration from: " + filePath);

		parseConfiguration();

		if (botCfg == null) {
			logger.error("Configuration could not be loaded.");
			return null;
		}

		try {
			botCfg.test();

			// If test passed ..
			FrameworkConfiguration cfg = FrameworkConfiguration.getInstance();

			if (cfg.containsBotCfg(botCfg.getName())) {
				logger.error("Bot with same id already exists.");
			} else {
				loadPassed();
				return botCfg;
			}

		} catch (ConfigurationTestFailureException ex) {
			loadFailed(ex.getMessage());
		}

		return null;
	}

	@Override
	protected void close() {
		parent.removeEntry(this);
		if (!failed) {
			if (!FrameworkConfiguration.getInstance().removeBotCfg(botCfg)) {
				logger.warn("Unable to remove bot configuration with name:" + botCfg.getName());
			}

			botCfg.removeChangeListener(this);
		}
	}

	/**
	 * 
	 * @return Returns true, if this configuration entry failed during loading.
	 */
	public boolean isFailed() {
		return failed;
	}

	@Override
	public void configurationChanged() {
		logger.info("CONFIGURATION CHANGED!!");

		int t = Team.parseTeam(botCfg.getConfigValue("team"));
		if (this.teamNumber != t) {
			updateInformation(botCfg.getImportantValues());

			parent.removeEntry(this);
			parent.getOtherTeamConfiguration().addEntry(this);
		} else {
			updateInformation(botCfg.getImportantValues());
		}
	}

	/**
	 * 
	 * @return Returns the team number of this entry.
	 */
	public int getTeam() {
		return teamNumber;
	}

	/**
	 * Returns parent of this entry.
	 */
	public MainAgentConfiguration getParent() {
		return parent;
	}

	/**
	 * Sets a new parent.
	 * 
	 * @param parent
	 *            New parent.
	 */
	public void setParent(MainAgentConfiguration parent) {
		this.parent = parent;
	}

}
