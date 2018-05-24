package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.world.Creep;
import cz.cuni.mff.kocur.world.GridSystem;

/**
 * Layer that represents farming influence. Enemy creeps emmit influence in
 * direction of our base and at max distance of our attack range.
 * 
 * @author kocur
 *
 */
public class FarmingLayer extends ContextualLayer {
	public FarmingLayer(GridSystem grid, ExtendedAgentContext linaContext) {
		super(grid, linaContext);

		// Create the parameters
		createParams();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param l
	 *            Influence layer to copy inside this one.
	 */
	public FarmingLayer(InfluenceLayer l) {
		super((ContextualLayer) l);
	}

	@Override
	public void createParams() {
		// I should define function for every creep, tower, etc.
		params = ParamsBuilder.build()
				.createEntityParameter(Creep.class, new FarmingInfluence<Creep>(context), context.getEnemyTeam())
				//.createEntityParameter(Tower.class, new FarmingInfluence<Tower>(context), context.getEnemyTeam())
				.get();

	}

}
