package cz.cuni.mff.kocur.graphics;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import cz.cuni.mff.kocur.agent.ControllerWrapper;
import cz.cuni.mff.kocur.agent.ControllersManager;
import cz.cuni.mff.kocur.events.Event;
import cz.cuni.mff.kocur.events.FrameworkEvent;
import cz.cuni.mff.kocur.events.FrameworkEventListener;
import cz.cuni.mff.kocur.events.ListenersManager;
import cz.cuni.mff.kocur.interests.Team;
import cz.cuni.mff.kocur.world.World;
import cz.cuni.mff.kocur.world.WorldManager;
import cz.cuni.mff.kocur.world.WorldViewer;

/**
 * Class that wraps around the streams inside the streams panel.
 * @author kocur
 *
 */
public class WorldViewersWrapper extends JPanel implements FrameworkEventListener, ActionListener{

	/**
	 * Generated serial version id. 
	 */
	private static final long serialVersionUID = 7349323091451151147L;
	
	/**
	 * Panel with options.
	 */
	protected JPanel optionsPanel = new JPanel();
	
	protected JComboBox<String> viewerSelector = new JComboBox<String>();
	
	String displayedViewer = null;
	
	protected LinkedHashMap<String, WorldViewer> worldViewers = new LinkedHashMap<>();
		
	protected LinkedHashMap<String, JPanel> worldViewerPanels = new LinkedHashMap<>();
	
	/**
	 * Constructor, that wraps around the viewer.
	 * @param viewer Streams viewer.
	 */
	public WorldViewersWrapper() {
		super();
		
		createWorldViewers();
		
		ListenersManager.addFrameworkListener("team_update", this);
		ListenersManager.addFrameworkListener("agent_update", this);
		ListenersManager.addFrameworkListener("agents_initialized", this);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		viewerSelector.addActionListener(this);
		
		optionsPanel.add(viewerSelector);
		this.add(optionsPanel);		
	}
	
	private JPanel createWorldViewer(WorldViewer viewer) {
		JPanel wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
		
        Rectangle rect = viewer.getBounds();
        Rectangle visibleRect = viewer.getVisibleRect();
        double tx = (rect.getWidth() - visibleRect.getWidth())/2;
        double ty = (rect.getHeight() - visibleRect.getHeight())/2;
        visibleRect.setBounds((int)tx, (int)ty, visibleRect.width, visibleRect.height);
        viewer.scrollRectToVisible(visibleRect);
		
		JPanel optionsPanel = viewer.getOptionsPanel();
		
		if (optionsPanel != null) 
			wrapper.add(optionsPanel);
		
		wrapper.add(viewer);	
		
		return wrapper;		
	}
	
	public void createWorldViewers() {
		worldViewers.clear();
		worldViewerPanels.clear();
		viewerSelector.removeAllItems();
		
		// Global, Dire, Radiant and for all the bot names
		List<String> names = ControllersManager.getInstance().getAllControllerNames();
		names.add("Global");
		names.add("RADIANT");
		names.add("DIRE");
		
		for (String name : names) {
			WorldViewer newViewer = new WorldViewer();
			worldViewers.put(name, newViewer);
			worldViewerPanels.put(name, createWorldViewer(newViewer));
			
			viewerSelector.addItem(name);
		}
		
		displayedViewer = "Global";
		viewerSelector.setSelectedItem("Global");
	}

	@Override
	public void triggered(Event e) {
		if (e.getName().equals("agent_update")) {
			String agentName = ((FrameworkEvent) e).getSourceName();
			updateAgentWorldViewer(agentName);			
		} else if (e.getName().equals("team_update")){
			int team = ((FrameworkEvent) e).getType();
			updateTeamWorldViewer(team);
			
			// And update global map here
			updateGlobalWorldViewer();
		} else if (e.getName().equals("agents_initialized")) {
			update();
		}
		
	}

	private void updateGlobalWorldViewer() {
		if (!displayedViewer.equals("Global")) {
			return;
		}
		
		// Get the global world viewer
		WorldViewer wv = worldViewers.get("Global");
		if (wv == null)
			return;
		
		// Update the world
		wv.switchWorld(WorldManager.getWorld());
		wv.update();
	}

	private void updateTeamWorldViewer(int team) {
		String type = Team.teamToString(team);
		
		if (displayedViewer == null || !displayedViewer.equals(type)) {
			return;
		}
				
		// Get the world
		World world = ControllersManager.getInstance()
				.getTeamContext(team).getWorld();
		
		// Get the world viewer
		WorldViewer wv = worldViewers.get(type);
		if (wv == null)
			return;
		
		// Update the world viewer
		wv.switchWorld(world);
		wv.update();		
	}

	private void updateAgentWorldViewer(String agentName) {
		if (displayedViewer == null || !displayedViewer.equals(agentName)) {
			return;
		}
		
		// Get the world .. 
		ControllerWrapper wrapper = ControllersManager.getInstance()
				.getControllerWrapperWithName(agentName);
		World world = wrapper.getController().getContext().getWorld();
				
		// Get the world viewer
		WorldViewer wv = worldViewers.get(agentName); 
		if (wv == null)
			return;
		
		// Else we switch world, to be sure, that we have the right one
		wv.switchWorld(world);
		// And we update.
		wv.update();	
	}

	@Override
	public void triggered(Event e, Object... os) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == viewerSelector) {
			displayedViewer = (String) viewerSelector.getSelectedItem();
			changeDisplayedPanel();
		}
		
	}

	private void changeDisplayedPanel() {
		this.removeAll();
		
		this.add(optionsPanel);
		
		if (displayedViewer != null)	
			this.add(worldViewerPanels.get(displayedViewer));
		
		this.revalidate();
		this.repaint();
	}
	
	public void update() {
		createWorldViewers();
		changeDisplayedPanel();		
	}

	public void focused() {
		worldViewers.get(displayedViewer).focused();
		
	}
}
