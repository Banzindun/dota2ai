package cz.cuni.mff.kocur.influence;

import java.util.HashMap;

import cz.cuni.mff.kocur.bot.AgentContext;
import cz.cuni.mff.kocur.bot.BaseAgentController;
import cz.cuni.mff.kocur.brain.Brain;
import cz.cuni.mff.kocur.events.FrameworkEventListener;
import cz.cuni.mff.kocur.events.ListenersManager;
import cz.cuni.mff.kocur.world.WorldUpdate;

public abstract class ExtendedBotContext extends AgentContext implements FrameworkEventListener{
	protected HashMap<Integer, InfluenceLayer> influenceLayers = new HashMap<>();
	
	protected Brain brain = null;
	
	public ExtendedBotContext(BaseAgentController bc) {
		super(bc);		
		ListenersManager.addFrameworkListener("bigupdate", this);
	}

	public void addLayer(Integer id, InfluenceLayer l) {
		influenceLayers.put(id, l);
	}
	
	public InfluenceLayer getLayer(Integer id) {
		return influenceLayers.get(id);
	}
	
	public void removeLayer(Integer id) {
		influenceLayers.remove(id);
	}
	
	@Override
	public void update(WorldUpdate u) {
		// This will update entities etc.
		super.update(u);		
	}
	
	/**
	 * Should update all the stored layers, defined by extender (LinaContext etc.)
	 */
	public abstract void updateLayers();
	
	/**
	 * Function that initializes the layers.  
	 */
	public abstract void initializeLayers();
	
	public String[] getLayerNames() {
		return influenceLayers.keySet().toArray(new String[influenceLayers.size()]);	
	}
	
	@Override
	public void triggered() {
				
	}

	@Override
	public void triggered(Object... os) {
		triggered();
	}

	public Brain getBrain() {
		return brain;
	}

	public void setBrain(Brain brain) {
		this.brain = brain;
	}
	
}
