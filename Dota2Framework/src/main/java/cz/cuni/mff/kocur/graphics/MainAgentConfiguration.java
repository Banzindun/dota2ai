package cz.cuni.mff.kocur.graphics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.configuration.FrameworkConfiguration;
import cz.cuni.mff.kocur.configuration.HeroConfiguration;
import cz.cuni.mff.kocur.interests.Team;

/**
 * Class that represents panel with agent configurations.
 * 
 * @author kocur
 *
 */
public class MainAgentConfiguration extends BuildableJPanel {
	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = 3029141347902903882L;

	/**
	 * Logger registered for this class.
	 */
	private Logger logger = LogManager.getLogger(MainAgentConfiguration.class.getName());

	/**
	 * Constraints of this JPanel.
	 */
	private GridBagConstraints gbc;

	/**
	 * Reference to other team's configurations.
	 */
	private MainAgentConfiguration otherTeamConfiguration = null;

	/**
	 * Configuration entries.
	 */
	private ArrayList<MainAgentConfigurationEntry> entries = new ArrayList<>();

	/**
	 * Number of team this object represents.
	 */
	private int teamNumber = 0;

	/**
	 * Default constructor of MainBotConfiguration.F
	 * @param teamNumber Number of team.
	 */
	public MainAgentConfiguration(int teamNumber) {
		// Initialize layout and border
		this.setLayout(new GridBagLayout());

		this.teamNumber = teamNumber;
	}

	/**
	 * Setups the constraints for this section.
	 * 
	 * @return initialized constraints
	 */
	private GridBagConstraints setupGbc() {
		return ConstraintsBuilder.build().gridxy(0).fill(GridBagConstraints.HORIZONTAL)
				.anchor(GridBagConstraints.NORTHWEST).insets(4, 20, 4, 20).weightxy(1, 0).get();
	}

	/**
	 * Builds the MainBotConfiguration. Initializes this section.
	 */
	public void build() {
		// Initialize constraints
		gbc = setupGbc();

		// We add new listener for drop target.
		this.setDropTarget(new DropTarget() {
			public synchronized void drop(DropTargetDropEvent evt) {
				try {
					evt.acceptDrop(DnDConstants.ACTION_COPY);
					List<File> droppedFiles = (List<File>) evt.getTransferable()
							.getTransferData(DataFlavor.javaFileListFlavor);
					handleDrop(droppedFiles);
				} catch (Exception ex) {
					logger.error("Error on dropping files. Drop files there!", ex);
				}
			}
		});

		// Call update
		update();

		// Call build on super. This will set this
		super.build();
	}

	/**
	 * Removes the configuration's entry.
	 * 
	 * @param e
	 *            The entry to be removed.
	 */
	public void removeEntry(MainAgentConfigurationEntry e) {
		entries.remove(e);
		update();
	}

	/**
	 * Adds entry to this configuration section.
	 * 
	 * @param e
	 *            The entry to be added.
	 */
	public void addEntry(MainAgentConfigurationEntry e) {
		entries.add(e);
		update();
	}

	/**
	 * Updates this section.
	 */
	public void update() {
		this.removeAll();

		// Initialize gbc
		gbc = setupGbc();

		for (MainAgentConfigurationEntry e : entries) {
			this.add(e, gbc);
			gbc.gridy++;
		}

		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.anchor = GridBagConstraints.CENTER;
		this.add(new JLabel("Drag and drop bot configurations here."), gbc);
		gbc.gridy++;

		this.revalidate();
		this.repaint();
	}

	/**
	 * Handles the file drop.
	 * 
	 * @param files
	 *            List of dropped files.
	 */
	public void handleDrop(List<File> files) {
		for (int i = 0; i < entries.size(); i++) {
			if (entries.get(i).isFailed())
				entries.remove(i);
		}

		for (File f : files) {
			handleFile(f);
		}
	}

	/**
	 * Handles the dropped file.
	 * 
	 * @param f
	 *            Dropped file.
	 */
	public void handleFile(File f) {
		if (entries.size() < 5) {
			MainAgentConfigurationEntry en = new MainAgentConfigurationEntry(f);
			HeroConfiguration bc = en.loadConfiguration();
			en.build();

			if (en.isFailed())
				return;

			boolean passed = handleConfiguration(en, bc);

			if (passed == false)
				logger.warn("Unable to resolve the file you have passed.");
		}
	}

	/**
	 * Handles a configuration.
	 * 
	 * @param en
	 *            Configuration entry.
	 * @param bc
	 *            Hero's configuration.
	 * @return Returns true, if the configuration was added to framework.
	 */
	public boolean handleConfiguration(MainAgentConfigurationEntry en, HeroConfiguration bc) {
		boolean result = false;

		int entryStatus = checkEntry(en);
		if (entryStatus == 0) { // OK
			entries.add(en);
			en.setParent(this);

			// We add the configuration to framework configuration.
			addBotConfigurationToFramework(bc);

			result = true;
		} else if (entryStatus == 1) { // OTHER TEAM
			result = otherTeamConfiguration.handleConfiguration(en, bc);
		} // ELSE SOMETHING WRONG -> IGNORE

		return result;
	}

	/**
	 * Adds bot configuration to framework configuration.
	 * 
	 * @param bc
	 *            Configuration, that should be added.
	 */
	private void addBotConfigurationToFramework(HeroConfiguration bc) {
		FrameworkConfiguration.getInstance().addBotCfg(bc, teamNumber);
		update();
	}

	/**
	 * Checks the entry.
	 * 
	 * @param e
	 *            Entry to be checked.
	 * @return Returns 0 if entry's team is this team. 1 if it is the opposing team.
	 */
	private int checkEntry(MainAgentConfigurationEntry e) {
		int team = e.getTeam();

		int opposingTeam = 0;
		if (teamNumber == Team.DIRE)
			opposingTeam = Team.RADIANT;
		else
			opposingTeam = Team.DIRE;

		if (team == teamNumber) {
			return 0;
		} else if (team == opposingTeam) {
			return 1;
		} else {
			logger.warn("You have tried to load configuration with unknown team: " + team);
			return -1;
		}

	}

	/**
	 * 
	 * @return Returns other team's configuration panel.
	 */
	public MainAgentConfiguration getOtherTeamConfiguration() {
		return otherTeamConfiguration;
	}

	/**
	 * Sets other team's configuration panel.
	 * 
	 * @param otherFaction
	 *            Other team's configuration panel.
	 */
	public void setOtherTeamConfiguration(MainAgentConfiguration otherFaction) {
		this.otherTeamConfiguration = otherFaction;
	}

}
