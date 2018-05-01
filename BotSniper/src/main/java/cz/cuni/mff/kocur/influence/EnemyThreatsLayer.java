package cz.cuni.mff.kocur.influence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.bot.AgentContext;
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
public class EnemyThreatsLayer extends BotContextualLayer{
	/**
	 * Logger for LinaThreatLayer.
	 */
	private final static Logger logger = LogManager.getLogger(EnemyThreatsLayer.class.getName());
	
	public EnemyThreatsLayer(GridSystem parentGS, AgentContext ctx) {
		super(parentGS, ctx);
		
		// Create the parameters
		createParams();		
	}
	
	/**
	 * Copy constructor.
	 * @param l Layer from which I should copy.
	 */
	public EnemyThreatsLayer(InfluenceLayer c) {
		super((BotContextualLayer) c);
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
