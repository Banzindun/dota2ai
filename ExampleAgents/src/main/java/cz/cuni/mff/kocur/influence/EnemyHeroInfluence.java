package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.agent.AgentContext;
import cz.cuni.mff.kocur.interests.Fort;
import cz.cuni.mff.kocur.world.GridBase;
import cz.cuni.mff.kocur.world.Hero;

/**
 * This class represents a propagation function that spreads influence of enemy
 * heroes on the influence map (from agent's perspective).
 * 
 * @author kocur
 *
 */
public class EnemyHeroInfluence extends WavePropagationWithContext<Hero> {
	/**
	 * We want to spread influence to a greater distance if we are attacking the
	 * hero.
	 */
	public static int AGGRESIVE_DISTANCE_BONUS = 20;

	/**
	 * Influence that the hero spreads if he is in aggressive state.
	 */
	public static double HERO_AGGRESIVE_INFLUENCE = 5.0;

	/**
	 * Influence the hero spreads if he is in neutral state.
	 */
	public static double HERO_NEUTRAL_INFLUENCE = 2.0;

	/**
	 * Distance of this entity towards the base.
	 */
	protected double distanceEntityToBase = 0;

	/**
	 * Maximum possible distance to base. Serves for defining the radius.
	 */
	protected double maxDistanceToBase = 0;

	/**
	 * Minimum possible distance to base. Serves for defining the radius.
	 */
	protected double minDistanceToBase = 0;

	/**
	 * Reference to fort. This fort is of the opposing team. (of passed the hero)
	 */
	protected Fort base = null;

	/**
	 * Coordinates of the base of this creep.
	 */
	protected double[] baseCoords;

	public EnemyHeroInfluence(AgentContext context) {
		super(context);

		// Set the sign, base and maxInfluence
		sign = -1;
		base = context.getEnemyBase();
		maxInfluence = HERO_NEUTRAL_INFLUENCE;
	}

	@Override
	public void propagate(InfluenceLayer l, Hero e) {
		super.updateHero();

		// Get the base coordinates in this layer
		if (base != null)
			baseCoords = l.getEntityCoordinates(base);

		// Take the hero.
		hero = context.getHero();

		// Calculate maximum distance from enemy hero's attack range and speed
		maxDistance = l.reverseResolution((int) (e.getAttackRange()) + e.getSpeed());

		// Get the enemy hero's coordinates.
		double[] xyz = l.getEntityCoordinates(e);

		// Base can be null if we are working with neutral creeps, else set max and min
		// distance
		if (base != null) {
			distanceEntityToBase = GridBase.distanceTileToTile(baseCoords, xyz);
			maxDistanceToBase = distanceEntityToBase + maxDistance;
			minDistanceToBase = distanceEntityToBase - maxDistance;
		}

		// Set the influence.
		maxInfluence = HERO_NEUTRAL_INFLUENCE;

		// Check if the creep is attacking the hero, in that case the creep should
		// spread more influence
		// That is in the case he is not a neutral creep, we do not want to run away
		// from those
		if (e.getAttackTarget() == hero.getEntid()) {
			maxInfluence = HERO_AGGRESIVE_INFLUENCE;
			maxDistance += AGGRESIVE_DISTANCE_BONUS;

			super.propagate(l, e);
		} else {
			super.propagate(l, e);
		}
	}

	@Override
	protected void calculateInfluence(InfluenceLayer l, PropagationPoint point) {
		double ratio = 0;
		if (base != null) {
			double distanceTileToBase = GridBase.distanceTileToTile(point.getX(), point.getY(), baseCoords);
			double normalizedDistanceToBase = (distanceTileToBase - minDistanceToBase)
					/ (maxDistanceToBase - minDistanceToBase);
			ratio = 1-(normalizedDistanceToBase + point.getNormalizedDistance(maxDistance)) / 2;
		} else {
			ratio = point.getNormalizedDistance(maxDistance);
		}

		double influence = Math.pow(maxInfluence * ratio, power);
		l.addInfluence(point.getX(), point.getY(), sign * (influence));
	}

}
