package cz.cuni.mff.kocur.influence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.agent.AgentContext;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.GridSystem;
import cz.cuni.mff.kocur.world.Hero;

/**
 * If we are working with agent, most of it's influence layers will be moving
 * with him and will represents his surroundings. For that reason we have
 * created this class. It creates layer, that is dependent on hero's context.
 * 
 * @author kocur
 *
 */
public abstract class ContextualLayer extends InfluenceLayer {
	/**
	 * Logger for LinaThreatLayer.
	 */
	private final static Logger logger = LogManager.getLogger(ContextualLayer.class.getName());

	/**
	 * Bot's context.
	 */
	protected AgentContext context = null;

	/**
	 * Current vision range of the associated hero.
	 */
	protected double visionRange = 0;

	public ContextualLayer(GridSystem parentGS, AgentContext ctx) {
		super(1, 1, 1);
		// Set parent and resolution, we want to work directly with grid -> 1
		setParent(parentGS);

		// Setup reference to agent's context
		context = ctx;

		// Create the parameters
		createParams();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param c
	 *            Layer from which I should copy.
	 */
	public ContextualLayer(ContextualLayer c) {
		super(c);
		this.context = c.getContext();
		this.visionRange = c.getVisionRange();
	}

	@Override
	public void propagate() {
		Hero h = context.getController().getHero();
		if (h == null) {
			logger.warn("Hero is null during propagation.");
			return;
		}

		// Take vision and speed and resolve using resolutions
		double _vision = reverseResolution(h.getVisionRange() + h.getSpeed());

		if (visionRange != _vision) {
			// Vision range is different from the one used before
			visionRange = _vision;

			// logger.debug("Setting size to: " + (int) (2*visionRange) + " " + (int)
			// (2*visionRange));

			// Set the changed width and height
			setSize((int) (2 * visionRange), (int) (2 * visionRange));

			// We will override map
			map = new double[(int) (2 * visionRange + 1)][(int) (2 * visionRange + 1)];
		}

		// Move the hero and the grid with him
		// We need to find origin's in parent's base
		double[] resolved = parent.getEntityCoordinates(h);

		if (resolved != null) {
			xOrigin = resolved[0] - parent.reverseResolution(h.getVisionRange() + h.getSpeed());
			yOrigin = resolved[1] - parent.reverseResolution(h.getVisionRange() + h.getSpeed());
		} else {
			logger.warn("No parent supplied, resolution from base returned null.");
			return;
		}

		// logger.debug("I have set origin to: " + xOrigin + " " + yOrigin);

		this.clearInfluence();

		// The entities should already be in radius, use the dire ones
		for (BaseEntity e : context.getEntites().values()) {
			propagateEntity(params, e);
		}

	}

	public AgentContext getContext() {
		return context;
	}

	public void setContext(AgentContext context) {
		this.context = context;
	}

	public double getVisionRange() {
		return visionRange;
	}

	public void setVisionRange(double visionRange) {
		this.visionRange = visionRange;
	}

}
