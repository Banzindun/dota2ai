package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.agent.AgentContext;
import cz.cuni.mff.kocur.world.Creep;
import cz.cuni.mff.kocur.world.GridSystem;
import cz.cuni.mff.kocur.world.Hero;
import cz.cuni.mff.kocur.world.Tower;

/**
 * Class that stores friendly (from the agent's perspective) threats to the influence layer.
 * @author kocur
 *
 */
public class FriendlyThreatsLayer extends ContextualLayer{
	
	public FriendlyThreatsLayer(GridSystem parentGS, AgentContext ctx) {
		super(parentGS, ctx);
		
		// Create the parameters
		createParams();		
	}
	
	/**
	 * Copy constructor.
	 * @param c Layer from which I should copy.
	 */
	public FriendlyThreatsLayer(InfluenceLayer c) {
		super((ContextualLayer) c);
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
