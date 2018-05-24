package cz.cuni.mff.kocur.graphics;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import cz.cuni.mff.kocur.events.Event;
import cz.cuni.mff.kocur.events.FrameworkEventListener;
import cz.cuni.mff.kocur.events.ListenersManager;
import cz.cuni.mff.kocur.framework.App;
import cz.cuni.mff.kocur.interests.Team;

/**
 * Class that represents body of the main panel.
 * 
 * @author kocur
 *
 */
public class MainBody extends BuildableJPanel implements FrameworkEventListener {
	/**
	 * Sample logger for MainBody class.
	 */
	// private Logger logger = LogManager.getLogger(MainBody.class.getName());

	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = 7215648508735234535L;

	/**
	 * GraphicsComponent containing radiant bot configurations.
	 */
	private MainAgentConfiguration radiantBotConfiguration = new MainAgentConfiguration(Team.RADIANT);

	/**
	 * GraphicsComponent containing dire Bot configurations configurations.
	 */
	private MainAgentConfiguration direBotConfiguration = new MainAgentConfiguration(Team.DIRE);

	/**
	 * GraphicsComponent containing running bot statistics.
	 */
	private MainRunningInfo botStatistics = null;

	/**
	 * Right section. This will contain loading bot information and then some
	 * running information about bots.
	 */
	private JPanel rightSection;

	/**
	 * This will contain some information about the framework and when running will
	 * display statistics or scenario selection.
	 */
	private Buildable leftSection = null;

	/**
	 * Constructor, that creates main body. The body contains dire and radiant configuration sections.
	 */
	public MainBody() {

		direBotConfiguration.setOtherTeamConfiguration(radiantBotConfiguration);
		direBotConfiguration.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "DIRE"));

		radiantBotConfiguration.setOtherTeamConfiguration(direBotConfiguration);
		radiantBotConfiguration.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "RADIANT"));

		// Add this to listeners, we will be listening for state_changed event. That one
		// is called each time the app changes the state. (CONFIGURATION, INIT,
		// RUNNING..)
		ListenersManager.addFrameworkListener("state_changed", this);
	}

	@Override
	public void build() {
		// Build configuration sections
		rightSection = new JPanel(new GridLayout(2, 1));

		addTeamSections();

		update();
	}

	/**
	 * Adds team sections.
	 */
	public void addTeamSections() {
		radiantBotConfiguration.build();
		direBotConfiguration.build();

		rightSection.add(radiantBotConfiguration);
		rightSection.add(direBotConfiguration);
	}

	/**
	 * Updates the main body.
	 */
	private void update() {
		this.setLayout(new GridBagLayout());

		GridBagConstraints gbc = ConstraintsBuilder.build().gridxy(0).weightxy(0, 1).fill(GridBagConstraints.BOTH)
				.anchor(GridBagConstraints.WEST).get();

		if (leftSection != null)
			this.add((Component) leftSection, gbc);

		if (rightSection != null) {
			JScrollPane scroller = new JScrollPane((Component) rightSection);
			scroller.setBorder(BorderFactory.createEmptyBorder());

			gbc.gridx++;
			gbc.weightx = 1;
			this.add(scroller, gbc);
		}
	}


	@Override
	public void triggered(Event e) {
		App.State state = App.state;

		this.removeAll();

		// Change botsSection
		if (state == App.State.CONFIGURATION) {
			rightSection = new JPanel(new GridLayout(2, 1));
			addTeamSections();
		} else if (state == App.State.SETUP) {
			// Whopsie
		} else if (state == App.State.INIT) {
			if (botStatistics != null) {
				botStatistics.removed();
			}
			botStatistics = new MainRunningInfo();
			botStatistics.build();

			rightSection = botStatistics;
		}

		update();

		this.revalidate();
		this.repaint();

	}

	@Override
	public void triggered(Event e, Object... os) {
		triggered(e);
	}

}
