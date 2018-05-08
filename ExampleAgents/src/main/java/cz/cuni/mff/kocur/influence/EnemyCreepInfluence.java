package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.agent.AgentContext;
import cz.cuni.mff.kocur.interests.Fort;
import cz.cuni.mff.kocur.interests.Team;
import cz.cuni.mff.kocur.world.Creep;
import cz.cuni.mff.kocur.world.GridBase;

/**
 * Class that represents a enemy creep's influence (from our agent's
 * perspective).
 * 
 * Creeps influence is given by their speed and attack distance. If they are
 * attacking the hero, they spread more influence and to bigger distance.
 * 
 * @author kocur
 *
 */
public class EnemyCreepInfluence extends WavePropagationWithContext<Creep> {
	/**
	 * We want to spread influence to a greater distance if we are attacking the
	 * hero.
	 */
	public static int ATTACKING_DISTANCE_BONUS = 20;

	/**
	 * Influence for when we attack the hero.
	 */
	public static int AGGRESIVE_INFLUENCE = 4;

	/**
	 * Influence we spread if we are not attacking the hero.
	 */
	public static int PASSIVE_INFLUENCE = 1;

	/**
	 * Distance of the entity towards it's base.
	 */
	protected double distanceEntityToBase = 0;

	/**
	 * Maximum distance to base.
	 */
	protected double maxDistanceToBase = 0;

	/**
	 * Minimum distance to base.
	 */
	protected double minDistanceToBase = 0;

	/**
	 * Reference to fort. This fort is of the opposing team. (of passed the hero)
	 */
	protected Fort base = null;

	protected Creep creep = null;

	/**
	 * Coordinates of the base of this creep.
	 */
	protected double[] baseCoords;

	public EnemyCreepInfluence(AgentContext context) {
		super(context);

		sign = -1; // Enemy influence
		base = context.getEnemyBase();
	}

	@Override
	public void propagate(InfluenceLayer l, Creep e) {
		super.updateHero();

		creep = e;

		// Get the base coordinates in this layer
		if (base != null)
			baseCoords = l.getEntityCoordinates(base);

		maxDistance = l.reverseResolution((int) (creep.getAttackRange()) + creep.getSpeed());
		maxInfluence = PASSIVE_INFLUENCE;

		double[] xyz = l.getEntityCoordinates(creep);

		if (base != null) {
			distanceEntityToBase = GridBase.distanceTileToTile(baseCoords, xyz);
			maxDistanceToBase = distanceEntityToBase + maxDistance;
			minDistanceToBase = distanceEntityToBase - maxDistance;
		}

		// Check if the creep is attacking the hero, in that case the creep should
		// spread more influence
		// That is in the case he is not a neutral creep, we do not want to run away
		// from those
		if (e.getAttackTarget() == hero.getEntid() && creep.getTeam() != Team.NEUTRAL) {
			maxInfluence = AGGRESIVE_INFLUENCE;
			maxDistance = maxDistance + ATTACKING_DISTANCE_BONUS;
			super.propagate(l, creep);
		} else {
			super.propagate(l, creep);
		}
	}

	@Override
	protected void calculateInfluence(InfluenceLayer l, PropagationPoint point) {
		double ratio = 0;
		if (base != null) {
			double distanceTileToBase = GridBase.distanceTileToTile(point.getX(), point.getY(), baseCoords);
			double normalizedDistanceToBase = (distanceTileToBase - minDistanceToBase)
					/ (maxDistanceToBase - minDistanceToBase);
			ratio = 1-( normalizedDistanceToBase + point.getNormalizedDistance(maxDistance)) / 2;
		} else {
			ratio = point.getNormalizedDistance(maxDistance);
		}

		double influence = Math.pow(maxInfluence * ratio, power);
		// Add influence
		l.addInfluence(point.getX(), point.getY(), sign * influence);
	}
}
