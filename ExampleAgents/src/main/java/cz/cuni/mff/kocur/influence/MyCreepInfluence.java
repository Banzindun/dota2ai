package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.agent.AgentContext;
import cz.cuni.mff.kocur.world.GridBase;

/**
 * Propagation function, that spreads influence of agent's friendly creeps.
 * @author kocur
 *
 */
public class MyCreepInfluence extends EnemyCreepInfluence{

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
		double ratio = 1-(normalizedDistanceToBase + point.getNormalizedDistance(maxDistance))/2;
		
		// Check creep's health
		if (creep.getHealth() < 0.4) {
			ratio *= creep.getHealth()/0.4;
		}
		
		
		double influence = Math.pow(maxInfluence * ratio, power);
		l.addInfluence(point.getX(), point.getY(), sign*(influence));
	}
}
