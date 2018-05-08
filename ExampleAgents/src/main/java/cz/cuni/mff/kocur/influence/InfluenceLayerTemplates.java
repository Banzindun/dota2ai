package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.events.Event;
import cz.cuni.mff.kocur.events.FrameworkEventListener;
import cz.cuni.mff.kocur.events.ListenersManager;
import cz.cuni.mff.kocur.world.GridBase;

/**
 * Class that stores influence layers, that do not change during the
 * application. For example radiant towers and base generate a field, that can
 * help ais to navigate to safer zone.
 * 
 * @author kocur
 *
 */
public class InfluenceLayerTemplates implements FrameworkEventListener{
	private static RadiantThreatLayer radiantConstantThreats;

	private static DireThreatLayer direConstantThreats;
	
	/**
	 * Creates radiant constant threats.
	 */
	public static void craftRadiantThreats() {
		GridBase grid = GridBase.getInstance();
		radiantConstantThreats = new RadiantThreatLayer(grid.getWidth(), grid.getHeight(), 5);
		radiantConstantThreats.setParent(grid);
		radiantConstantThreats.createParams();
		
		radiantConstantThreats.propagate();
	}
	
	/**
	 * Updates radiant constant threats.
	 */
	public static void updateRadiantThreats() {
		radiantConstantThreats.propagate();
	}
	
	/**
	 * Crafts dire constant threats.
	 */
	public static void craftDireThreats() {
		GridBase grid = GridBase.getInstance();
		direConstantThreats = new DireThreatLayer(grid.getWidth(), grid.getHeight(), 5);
		direConstantThreats.setParent(grid);
		direConstantThreats.createParams();
		
		direConstantThreats.propagate();
	}
	
	/**
	 * Updates dire constant threats.
	 */
	public static void updateDireThreats() {
		direConstantThreats.propagate();
	}
	
	/**
	 * The instance of this class.
	 */
	private static InfluenceLayerTemplates instance = null;
	
	/**
	 * 
	 * @return Returns a instance of InfluenceLayerTemplates.
	 */
	public static InfluenceLayerTemplates getInstance() {
		if (instance == null)
			instance = new InfluenceLayerTemplates();
		return instance;
	}
	
	private InfluenceLayerTemplates() {
		ListenersManager.addListener("tower_destroyed", this);
	}

	@Override
	public void triggered(Event e) {
		updateRadiantThreats();
		updateDireThreats();
	}

	@Override
	public void triggered(Event e, Object... os) {
		triggered(e);
	}
	
	public static RadiantThreatLayer getRadiantConstantThreats() {
		return radiantConstantThreats;
	}

	public static void setRadiantConstantThreats(RadiantThreatLayer radiantConstantThreats) {
		InfluenceLayerTemplates.radiantConstantThreats = radiantConstantThreats;
	}

	public static DireThreatLayer getDireConstantThreats() {
		return direConstantThreats;
	}

	public static void setDireConstantThreats(DireThreatLayer direConstantThreats) {
		InfluenceLayerTemplates.direConstantThreats = direConstantThreats;
	}
	
}
 