package cz.cuni.mff.kocur.influence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.bot.AgentContext;
import cz.cuni.mff.kocur.world.GridBase;

public class MyTowerInfluencePropagation extends EnemyTowerInfluencePropagation{
	/*
	 * Basic logger registered for this class.
	 */
	private static final Logger logger = LogManager.getLogger(MyTowerInfluencePropagation.class);
	
	public MyTowerInfluencePropagation(AgentContext context) {
		super(context); // I do not need bot context here
	
		this.sign = 1;
		base = context.getMyBase();
	}
	

	@Override
	protected void calculateInfluence(InfluenceLayer l, PropagationPoint point) {
		double ratio = 0;

		double distanceTileToBase = GridBase.distanceTileToTile(point.getX(), point.getY(), baseCoords);
		double normalizedDistanceToBase = (distanceTileToBase - minDistanceToBase)
				/ (maxDistanceToBase - minDistanceToBase);
		ratio = ((1-normalizedDistanceToBase) + 3*point.getNormalizedDistance(maxDistance)) / 4;

		double influence = Math.pow(maxInfluence * ratio, power);
		l.addInfluence(point.getX(), point.getY(), sign*(influence));
	}

}
