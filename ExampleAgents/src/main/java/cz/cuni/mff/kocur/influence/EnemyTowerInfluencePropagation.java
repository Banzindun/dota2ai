package cz.cuni.mff.kocur.influence;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.agent.AgentContext;
import cz.cuni.mff.kocur.interests.Fort;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.GridBase;
import cz.cuni.mff.kocur.world.Tower;

/**
 * Class that represents a propagation function that spreads enemy towers
 * influence (from the agent's perspective).
 * 
 * @author kocur
 *
 */
public class EnemyTowerInfluencePropagation extends WavePropagationWithContext<Tower> {
	/*
	 * Basic logger registered for this class.
	 */
	private static final Logger logger = LogManager.getLogger(EnemyTowerInfluencePropagation.class);

	/**
	 * Because the updates come in intervals, it might happen that we enter towers
	 * range and get shot, before are able to escape it. To counter that, we have
	 * added a constant, that is added to towers range.
	 */
	public static double ADDITIONAL_SPREAD_DISTANCE = 40;

	/**
	 * Distance of the entity to it's base.
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
	 * How many creeps should be before tower before I start caring.
	 */
	protected final int CREEP_THRESHOLD = 3;

	/**
	 * Base influence the tower has.
	 */
	public final int PASSIVE_INFLUENCE = 2;

	protected Fort base = null;

	protected Tower tower = null;

	/**
	 * Coordinates of the base of this creep.
	 */
	protected double[] baseCoords;

	public EnemyTowerInfluencePropagation(AgentContext context) {
		super(context);

		this.sign = -1;
		base = context.getEnemyBase();
		maxInfluence = PASSIVE_INFLUENCE;
	}

	@Override
	public void propagate(InfluenceLayer l, Tower e) {
		super.updateHero();

		// Store the tower reference
		this.tower = e;

		// Check that base is not null
		if (base == null) {
			logger.fatal("Base is null!");
			return;
		}

		// Get the base coordinates of this layer
		baseCoords = l.getEntityCoordinates(base);
		maxDistance = l.reverseResolution((int) (e.getAttackRange())) + ADDITIONAL_SPREAD_DISTANCE;

		double[] xyz = l.getEntityCoordinates(e);

		distanceEntityToBase = GridBase.distanceTileToTile(baseCoords, xyz);
		maxDistanceToBase = distanceEntityToBase + maxDistance;
		minDistanceToBase = distanceEntityToBase - maxDistance;

		if (sign == -1) setMaxMinInfluence();

		// This will create origin for our object, and will start the spread from the
		// origin.
		super.propagate(l, e);
	}

	/**
	 * Sets maximum and minimum influences.
	 */
	private void setMaxMinInfluence() {
		// Calculate distance between tower and hero
		double heroToTowerDistance = GridBase.distance(tower, hero);

		// Find entities that are closer than hero and in tower's range
		List<BaseEntity> entities = context.findEntitiesInRadius(tower, tower.getAttackRange()).stream()
				.filter(e1 -> (GridBase.distance(tower, e1) < heroToTowerDistance) && (e1.getTeam() == hero.getTeam())
						&& (e1.isCreep() || e1.isHero()))
				.collect(Collectors.toList());
		
		// We set influence to PASSIVE_INFLUENCE and then we will change it to better
		// correspond to the tower's state
		maxInfluence = PASSIVE_INFLUENCE;
		minInfluence = 0;

		// Health of the creeps closer than the hero
		double remainingHealth = 0;
		double maxHealth = 0;

		// How close to tower am I?
		int order = 0;

		// If no entities are closer
		if (entities == null || entities.size() == 0) {
			minInfluence = PASSIVE_INFLUENCE;
			return;
		}
		// If there is enough of them, we "turn the tower off"
		else if (entities.size() >= CREEP_THRESHOLD) {
			maxInfluence = 0;
			minInfluence = 0;
			return;
		} else {
			// Go through entities. Calculate order and hitpoints.
			for (BaseEntity be : entities) {
				// Else we add his health to max and remaining health
				maxHealth += be.getMaxHealth();
				remainingHealth += be.getHealth();

				// And we increase the order
				order++;			
			}

			// If they have some health we multiply the maxInfluence by some threshold
			if (remainingHealth != 0 && maxHealth != 0) {
				// (double) order / CREEP_THRESHOLD
				calculateMaxInfluence(CREEP_THRESHOLD - order, remainingHealth / maxHealth);
				
			} else {
				maxInfluence = PASSIVE_INFLUENCE * 2; // As if the tower is attacking us
				minInfluence = 2.0; 
			}
		}
	}

	/**
	 * Calculates maximum influences from order and health.
	 * 
	 * @param orderOverCreep
	 *            order / CREEP_THRESHOLD
	 * @param healthOverMaxHealth
	 *            remainingHealth / maxHealth
	 */
	private void calculateMaxInfluence(double orderOverCreep, double healthOverMaxHealth) {
		// Hero is the target
		if (tower.getAttackTarget() == hero.getEntid() || hero.getHealth() < hero.getMaxHealth()*0.4) {
			maxInfluence = PASSIVE_INFLUENCE * 2;
			minInfluence = 2.0;
		} else {
			double health = 0;
			if (healthOverMaxHealth < 0.5) {
				health = -2*healthOverMaxHealth + 1;
			}
			
			maxInfluence = health * CREEP_THRESHOLD;	
			minInfluence = orderOverCreep*health;
		}
	}

	@Override
	protected void calculateInfluence(InfluenceLayer l, PropagationPoint point) {
		double distanceTileToBase = GridBase.distanceTileToTile(point.getX(), point.getY(), baseCoords);
		double normalizedDistanceToBase = (distanceTileToBase - minDistanceToBase)
				/ (maxDistanceToBase - minDistanceToBase);

		double ratio = (normalizedDistanceToBase + 3 * point.getNormalizedDistance(maxDistance)) / 4;
		double influence = Math.pow(maxInfluence * ratio, power);

		// Add influence
		if (maxInfluence != 0)
			l.addInfluence(point.getX(), point.getY(), sign * (minInfluence + maxInfluence - influence));
		else {
			l.setInfluence(point.getX(), point.getY(), 0);
		}
	}
}
