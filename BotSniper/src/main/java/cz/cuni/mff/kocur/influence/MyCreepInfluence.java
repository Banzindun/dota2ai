package cz.cuni.mff.kocur.influence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.bot.AgentContext;
import cz.cuni.mff.kocur.world.GridBase;

public class MyCreepInfluence extends EnemyCreepInfluence{
	/**
	 * Basic logger registered for this class.
	 */
	private static final Logger logger = LogManager.getLogger(MyCreepInfluence.class);

	public MyCreepInfluence(AgentContext context) {
		super(context);
		
		sign = 1;
		base = context.getMyBase();		
	}

	@Override
	protected void calculateInfluence(InfluenceLayer l, PropagationPoint point) {
		double distanceTileToBase = GridBase.distanceTileToTile(point.getX(), point.getY(), baseCoords);
		double normalizedDistanceToBase = (distanceTileToBase-minDistanceToBase)/(maxDistanceToBase-minDistanceToBase);
		//double ratio = (1-normalizedDistanceToBase + distanceTileToOrigin/maxDistance)/2;
		double ratio = (1-normalizedDistanceToBase + point.getNormalizedDistance(maxDistance))/2;
		
		// Check creeps health
		if (creep.getHealth() < 0.4) {
			ratio *= creep.getHealth()/0.4;
		}
		
		
		double influence = Math.pow(maxInfluence * ratio, power);
		l.addInfluence(point.getX(), point.getY(), sign*(influence));
	}
	
	
}
