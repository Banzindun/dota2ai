package cz.cuni.mff.kocur.influence;

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
	
	public static void craftRadiantThreats() {
		GridBase grid = GridBase.getInstance();
		radiantConstantThreats = new RadiantThreatLayer(grid.getWidth(), grid.getHeight(), 5);
		radiantConstantThreats.setParent(grid);
		radiantConstantThreats.createParams();
		
		radiantConstantThreats.propagate();
	}
	
	public static void updateRadiantThreats() {
		radiantConstantThreats.propagate();
	}
	
	public static void craftDireThreats() {
		GridBase grid = GridBase.getInstance();
		direConstantThreats = new DireThreatLayer(grid.getWidth(), grid.getHeight(), 5);
		direConstantThreats.setParent(grid);
		direConstantThreats.createParams();
		
		direConstantThreats.propagate();
	}
	
	public static void updateDireThreats() {
		direConstantThreats.propagate();
	}
	
	
	private static InfluenceLayerTemplates instance = null;
	
	public static InfluenceLayerTemplates getInstance() {
		if (instance == null)
			instance = new InfluenceLayerTemplates();
		return instance;
	}
	
	private InfluenceLayerTemplates() {
		ListenersManager.addListener("tower_destroyed", this);
	}

	@Override
	public void triggered() {
		updateRadiantThreats();
		updateDireThreats();
	}

	@Override
	public void triggered(Object... os) {
		triggered();
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
