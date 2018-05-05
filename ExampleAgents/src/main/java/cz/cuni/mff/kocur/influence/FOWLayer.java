package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.agent.AgentContext;
import cz.cuni.mff.kocur.world.GridSystem;
import cz.cuni.mff.kocur.world.Hero;

/**
 * Layer that stores our field of view inside the influence map. It sets 1
 * influence to tiles that are visible.
 * 
 * This should have been used to combine with other layers by multiplying. That
 * would give us only a visible influence.
 * 
 * @author kocur
 *
 */
public class FOWLayer extends InfluenceLayer {

	private AgentContext context;

	private double visionRange = 0;

	public FOWLayer(GridSystem parentGS, AgentContext ctx) {
		// Set parent and resolution, we want to work directly with grid -> 1
		super(1, 1, 1);
		setParent(parentGS);

		// Setup reference to Lina's context
		context = ctx;

		// Create the parameters
		createParams();
	}

	@Override
	public void propagate() {
		Hero h = context.getController().getHero();

		// Take vision and resolve using resolutions
		double _vision = reverseResolution(h.getVisionRange());

		if (visionRange != _vision) {
			// Vision range is different from the one used before
			visionRange = _vision;

			// Set the changed width and height
			setSize((int) (2 * visionRange), (int) (2 * visionRange));

			// We will override map
			map = new double[(int) (2 * visionRange)][(int) (2 * visionRange)];
		} else {
			// The grid has the same size, we will just clear it.
			clearInfluence();
		}

		// Set min and max
		this.setMax(1.0);
		this.setMin(0.0);

		// Move the hero and the grid with him
		double[] resolved = fromBaseNoResolution(h.getX(), h.getY());
		if (resolved != null) {
			xOrigin = resolved[0] - visionRange;
			yOrigin = resolved[1] - visionRange;
		} else {
			// logger.warn("No parent supplied, resolution from base returned null.");
			return;
		}

		propagateEntity(params, h);
	}

	@Override
	public void createParams() {
		params = ParamsBuilder.build().createEntityParameter(Hero.class, new FOWPropagation<Hero>()).get();

	}

}
