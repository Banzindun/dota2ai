package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.agent.AgentContext;
import cz.cuni.mff.kocur.interests.Team;
import cz.cuni.mff.kocur.world.Creep;
import cz.cuni.mff.kocur.world.GridSystem;
import cz.cuni.mff.kocur.world.Hero;
import cz.cuni.mff.kocur.world.Tower;


/**
 * Threat layer that is generated for Lina using her context. 
 * This will be just a small grid, that is as big as her vision that will be 
 * moving around with Lina.
 * @author kocur
 *
 */
public class EnemyThreatsLayer extends ContextualLayer{
	
	public EnemyThreatsLayer(GridSystem parentGS, AgentContext ctx) {
		super(parentGS, ctx);
		
		// Create the parameters
		createParams();		
	}
	
	/**
	 * Copy constructor.
	 * @param c Layer from which I should copy.
	 */
	public EnemyThreatsLayer(InfluenceLayer c) {
		super((ContextualLayer) c);
	}

	@Override
	public void createParams() {
		EnemyTowerInfluencePropagation enemyTowerIP = new EnemyTowerInfluencePropagation(this.context);

		EnemyHeroInfluence enemyHeroInfluence = new EnemyHeroInfluence(context);
				
		// I should define function for every creep, tower, etc.
		params = ParamsBuilder.build()
				.createEntityParameter(Creep.class, new EnemyCreepInfluence(context), context.getEnemyTeam())
				.createEntityParameter(Creep.class, new EnemyCreepInfluence(context), Team.NEUTRAL)
				.createEntityParameter(Tower.class, enemyTowerIP, context.getEnemyTeam())
				.createEntityParameter(Hero.class, enemyHeroInfluence, context.getEnemyTeam())
				.get();
	}
	
}
