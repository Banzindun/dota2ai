package cz.cuni.mff.kocur.influence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.bot.AgentContext;
import cz.cuni.mff.kocur.world.GridBase;
import cz.cuni.mff.kocur.world.Hero;

public class FriendlyHeroInfluence extends EnemyHeroInfluence{

	/**
	 * Basic logger registered for this class.
	 */
	private static final Logger logger = LogManager.getLogger(FriendlyHeroInfluence.class);

	public FriendlyHeroInfluence(AgentContext context) {
		super(context);
		
		sign = 1; // Enemy influence
		base = context.getMyBase();
		maxInfluence = HERO_NEUTRAL_INFLUENCE;
	}

	@Override
	public void propagate(InfluenceLayer l, Hero e) {
		if (e.getEntid() == context.getHero().getEntid())
			return;
		
		super.propagate(l, e);
	}
	
	@Override
	protected void calculateInfluence(InfluenceLayer l, PropagationPoint point) {
		double distanceTileToBase = GridBase.distanceTileToTile(point.getX(), point.getY(), baseCoords);
		double normalizedDistanceToBase = (distanceTileToBase-minDistanceToBase)/(maxDistanceToBase-minDistanceToBase);
		double ratio = ((1-normalizedDistanceToBase) + point.getNormalizedDistance(maxDistance))/2;
		
		double influence = Math.pow(maxInfluence * ratio, power);
		l.addInfluence(point.getX(), point.getY(), sign*(maxInfluence-influence));
	}
}
