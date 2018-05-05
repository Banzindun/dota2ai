package cz.cuni.mff.kocur.influence;

import java.util.HashMap;

import cz.cuni.mff.kocur.agent.AgentContext;
import cz.cuni.mff.kocur.agent.BaseAgentController;
import cz.cuni.mff.kocur.brain.Brain;
import cz.cuni.mff.kocur.events.FrameworkEventListener;
import cz.cuni.mff.kocur.events.ListenersManager;
import cz.cuni.mff.kocur.world.WorldUpdate;

/**
 * Abstract class that extends AgentContext. We have some more functions our
 * agent's might need defined here (For purpose of creating and updating
 * influence layers.
 * 
 * @author kocur
 *
 */
public abstract class ExtendedAgentContext extends AgentContext implements FrameworkEventListener {
	/**
	 * Influence layers.
	 */
	protected HashMap<Integer, InfluenceLayer> influenceLayers = new HashMap<>();

	/**
	 * Agent's brain.
	 */
	protected Brain brain = null;

	public ExtendedAgentContext(BaseAgentController bc) {
		super(bc);
		ListenersManager.addFrameworkListener("bigupdate", this);
	}

	/**
	 * Adds a new influence layer.
	 * @param id Id of the layer.
	 * @param l Influence layer.
	 */
	public void addLayer(Integer id, InfluenceLayer l) {
		influenceLayers.put(id, l);
	}

	/**
	 * 
	 * @param id Id of the influence layer.
	 * @return Returns influence layer with given id.
	 */
	public InfluenceLayer getLayer(Integer id) {
		return influenceLayers.get(id);
	}

	/**
	 * Removes layer with given id.
	 * @param id Id of the layer.
	 */
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
