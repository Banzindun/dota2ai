package cz.cuni.mff.kocur.influence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.bot.AgentContext;
import cz.cuni.mff.kocur.world.Creep;
import cz.cuni.mff.kocur.world.GridSystem;
import cz.cuni.mff.kocur.world.Hero;
import cz.cuni.mff.kocur.world.Tower;

public class FriendlyThreatsLayer extends BotContextualLayer{
	/**
	 * Logger for LinaThreatLayer.
	 */
	private final static Logger logger = LogManager.getLogger(FriendlyThreatsLayer.class.getName());
	
	public FriendlyThreatsLayer(GridSystem parentGS, AgentContext ctx) {
		super(parentGS, ctx);
		
		// Create the parameters
		createParams();		
	}
	
	/**
	 * Copy constructor.
	 * @param l Layer from which I should copy.
	 */
	public FriendlyThreatsLayer(InfluenceLayer c) {
		super((BotContextualLayer) c);
	}

	@Override
	public void createParams() {
		MyTowerInfluencePropagation towerInfluence = new MyTowerInfluencePropagation(this.context);
		towerInfluence.setMaxInfluence(2); // Starting influence
		
		MyCreepInfluence creepInfluence =  new MyCreepInfluence(context);
		creepInfluence.setMaxInfluence(1); // Starting influence
		
		FriendlyHeroInfluence friendlyHeroInfluence =  new FriendlyHeroInfluence(context);
						
		// I should define function for every creep, tower, etc.
		params = ParamsBuilder.build()
				.createEntityParameter(Creep.class, creepInfluence, context.getMyTeam())
				.createEntityParameter(Tower.class, towerInfluence, context.getMyTeam())
				.createEntityParameter(Hero.class, friendlyHeroInfluence, context.getMyTeam())
				.get();
	}
}
