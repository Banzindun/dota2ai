package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.agent.AgentContext;
import cz.cuni.mff.kocur.world.GridBase;
import cz.cuni.mff.kocur.world.Hero;

/**
 * Class that spreads influence of friendly heroes (from the agent's
 * perspective).
 * 
 * @author kocur
 *
 */
public class FriendlyHeroInfluence extends EnemyHeroInfluence {

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
		double normalizedDistanceToBase = (distanceTileToBase - minDistanceToBase)
				/ (maxDistanceToBase - minDistanceToBase);
		double ratio = 1-(normalizedDistanceToBase + point.getNormalizedDistance(maxDistance)) / 2;

		double influence = Math.pow(maxInfluence * ratio, power);
		l.addInfluence(point.getX(), point.getY(), sign * (influence));
	}
}
