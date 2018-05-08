package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.agent.AgentContext;
import cz.cuni.mff.kocur.world.GridBase;

/**
 * Propagates influence of tower, that belongs to my team (from the agent's
 * perspective).
 * 
 * @author kocur
 *
 */
public class MyTowerInfluencePropagation extends EnemyTowerInfluencePropagation {

	public MyTowerInfluencePropagation(AgentContext context) {
		super(context); // I do not need bot context here

		this.sign = 1;
		base = context.getMyBase();
	}

	@Override
	protected void calculateInfluence(InfluenceLayer l, PropagationPoint point) {
		double ratio = 0;

		//double distanceToTower = GridBase.distanceTileToTile((int) l.resolveX(tower.getX()), (int) l.resolveY(tower.getY()), point.getX(), point.getY());
		
		double distanceTileToBase = GridBase.distanceTileToTile(point.getX(), point.getY(), baseCoords);
		double normalizedDistanceToBase = (distanceTileToBase - minDistanceToBase)
				/ (maxDistanceToBase - minDistanceToBase);
		
		ratio = 1-(normalizedDistanceToBase + point.getNormalizedDistance(maxDistance)) / 2;

		double influence = Math.pow(maxInfluence * ratio, power);
		l.addInfluence(point.getX(), point.getY(), sign * (influence));
	}
}
