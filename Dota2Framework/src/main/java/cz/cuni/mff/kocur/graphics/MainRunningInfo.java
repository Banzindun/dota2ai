package cz.cuni.mff.kocur.graphics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.Constants;
import cz.cuni.mff.kocur.bot.ControllerWrapper;
import cz.cuni.mff.kocur.bot.ControllersManager;
import cz.cuni.mff.kocur.dota2AIFramework.App;
import cz.cuni.mff.kocur.dota2AIFramework.App.State;
import cz.cuni.mff.kocur.events.FrameworkEventListener;
import cz.cuni.mff.kocur.events.ListenersManager;
import cz.cuni.mff.kocur.exceptions.ComponentNotBuiltException;

/**
 * Class that represents panel with information, that is displayed while the game is running. 
 * @author kocur
 *
 */
public class MainRunningInfo extends BuildableJPanel implements FrameworkEventListener{
	/**
	 * Generated serial version id. 
	 */
	private static final long serialVersionUID = 5218522510656997039L;

	/**
	 * Logger registered for this class.
	 */
	private static final Logger logger = LogManager.getLogger(MainRunningInfo.class.getName());
		
	private LinkedList<Buildable> entries = new LinkedList<>();
			
	/**
	 * Constructor.
	 */
	public MainRunningInfo() {		
		// Lets listen to statechanged event. This one is called each time application changes state (INIT -> RUNNING etc.)
		ListenersManager.addFrameworkListener("statechanged", this);
	}
	
	@Override
	public void build() {
		this.setLayout(new GridBagLayout());
		
		buildInitInformation();
		//updateInfo();
		
		// Call build on super. Will set this component to built.
		super.build();
	}

	
	/**
	 * Builds the agent running information entries.
	 */
	private void buildAgentInfo() {
		ArrayList<ControllerWrapper> controllers = ControllersManager.getInstance().getAllControllerWrappers();
			
		for (ControllerWrapper cw : controllers) {
			MainBotInfoEntry e = new MainBotInfoEntry(this, cw);
			e.build();
			
			try {
				entries.add(e.get());
			} catch (ComponentNotBuiltException e1) {
				logger.warn("Trying to get running information entry, that haven't been built.");
			}			
		}				
	}
	
	/**
	 * Updates the informations.
	 */
	private void updateInfo() {
		this.removeAll();
		
		// Setup constraints
		GridBagConstraints gbc = setupGbc();
		
		if (entries.size() != 0) {
			for (Buildable i : entries) {
				try {
					this.add(i.getComponent(), gbc);
				} catch (ComponentNotBuiltException e) {
					logger.warn("I have tried to add entry that haven't been built yet.");
				}
				gbc.gridy++;
			}
		}
		
		// Add filler - will push everything up.
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(new JPanel(), gbc);
		
		this.revalidate();
		this.repaint();		
	}
	
	/**
	 * Setups the grid bag constraints.
	 * @return Returns new grid bag constraints.
	 */
	private GridBagConstraints setupGbc() {
		return ConstraintsBuilder.build()
				.gridxy(0)
				.weightxy(0.5,0)
				.fill(GridBagConstraints.HORIZONTAL)
				.insets(5,20,5,20)
				.get();		
	}
	
	/**
	 * Removes given entry.
	 * @param e Entry to be removed.
	 */
	public void removeEntry(Buildable e) {
		entries.remove(e);
	}

	@Override
	public void triggered() {
		State s = App.state;
		State ls = App.lastState;
		
		// If Paused, don't change anything.
		if (s == State.PAUSED) return;
		
		// Unregister listeners if necessary
		if (ls == State.RUNNING) {
			entries = new LinkedList<>();
		} else if (ls == State.INIT && s != State.RUNNING) {
			// Bot informations not loaded yet
		}
		
		// Build the listeners
		if (s == State.RUNNING && ls != State.PAUSED) {
			entries.clear();
			
			buildAgentInfo();
			updateInfo();
		}		
	}

	/**
	 * 
	 */
	private void buildInitInformation() {
		// Setup constraints
		GridBagConstraints gbc = setupGbc();
		gbc.weightx = 1;
		
		JTextArea info = new JTextArea();
		info.setText(Constants.getInitHelp());
		info.setColumns(30);
		info.setBorder(BorderFactory.createEmptyBorder());
		info.setLineWrap(true);
		info.setWrapStyleWord(true);
		info.setBackground(this.getBackground());
		
		this.add(info, gbc);
		
		// Filler to push the information up
		JPanel filler = new JPanel();
		gbc.gridy++;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(filler);		
	}

	@Override
	public void triggered(Object... os) {
		triggered();		
	}
	
	/**
	 * Called after we have removed this from the main panel.
	 */
	public void removed() {
		ListenersManager.removeFromListeners("statechanged", this);
	}

}
