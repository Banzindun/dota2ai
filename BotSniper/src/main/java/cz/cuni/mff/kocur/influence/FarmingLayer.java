package cz.cuni.mff.kocur.influence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.world.Creep;
import cz.cuni.mff.kocur.world.GridSystem;

public class FarmingLayer extends BotContextualLayer{
	/**
	 * Logger for FarmingLayer.
	 */
	private final static Logger logger = LogManager.getLogger(FarmingLayer.class.getName());

	public FarmingLayer(GridSystem grid, ExtendedBotContext linaContext) {
		super(grid, linaContext);
		
		// Create the parameters 
		createParams();
	}
	
	/**
	 * Copy constructor.
	 * @param l Influence layer to copy inside this one.
	 */
	public FarmingLayer(InfluenceLayer l) {
		super((BotContextualLayer) l);
	}
	

	@Override
	public void createParams() {
		// I should define function for every creep, tower, etc.
		params = ParamsBuilder.build()
				.createEntityParameter(Creep.class, new CreepFarmingInfluence(context), context.getEnemyTeam())
				.get();
		
	}

}
