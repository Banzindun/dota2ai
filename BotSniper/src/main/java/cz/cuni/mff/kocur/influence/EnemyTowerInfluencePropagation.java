package cz.cuni.mff.kocur.influence;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.bot.AgentContext;
import cz.cuni.mff.kocur.interests.Fort;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.GridBase;
import cz.cuni.mff.kocur.world.Tower;

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

	protected double distanceEntityToBase = 0;

	protected double maxDistanceToBase = 0;
	
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

		setMaxMinInfluence();

		// This will create origin for our object, and will start the spread from the
		// origin.
		super.propagate(l, e);
	}

	private void setMaxMinInfluence() {
		// Health of the creeps closer than the hero
		double remainingHealth = 0;
		double maxHealth = 0;

		// How close to tower am I?
		int order = 0;

		// Calculate distance between tower and hero
		double heroToTowerDistance = GridBase.distance(tower, hero);
		
		// Find entities that are closer than hero and in tower's range
		List<BaseEntity> entities = context.getController().getTeamContext()
				.findEntitiesInRadius(tower, tower.getAttackRange()).stream()
				.filter((e1) -> GridBase.distance(tower, e1) < heroToTowerDistance)
				.collect(Collectors.toList());
				
		// We set influence to PASSIVE_INFLUENCE and that we will change it to better
		// correspond to the tower's state
		maxInfluence = PASSIVE_INFLUENCE;
		minInfluence = 0.1;
		
		if (entities == null) {
			minInfluence = 1;
			return;
		}
		
		// If no entities are closer
		if (entities.size() == 0) {
			minInfluence = 1;
			return;
		}
		// If there is enough of them, we "turn the tower off"
		else if (entities.size() > CREEP_THRESHOLD+1) {
			maxInfluence = 0;
			minInfluence = 0;
			return;
		} else {
			// Go through entities, stop if there is at least 4 entities closer to tower
			// than hero. Calculate order and hitpoints.
			for (BaseEntity be : entities) {
				if (be.getTeam() == hero.getTeam()) {
					// Is the creep/hero too low??
					if (be.getHealth() < 0.1 * be.getMaxHealth()) {
						continue;
					}

					// Else we add his health to max and remaining health
					maxHealth += be.getMaxHealth();
					remainingHealth += be.getHealth();

					// And we increase the order
					order++;
				}
			}

			// If they have some health we multiply the maxInfluence by some threshold
			if (remainingHealth != 0 && maxHealth != 0) {
				calculateMaxInfluence((double) order / CREEP_THRESHOLD, remainingHealth / maxHealth);
			} else {
				maxInfluence = PASSIVE_INFLUENCE * 2 * (1 - (double) order / CREEP_THRESHOLD);
			}
		}
	}

	private void calculateMaxInfluence(double order, double healthOverMaxHealh) {
		// Hero is the target
		if (tower.getAttackTarget() == hero.getEntid() || order == 1) {
			maxInfluence = PASSIVE_INFLUENCE*2;
			minInfluence = 2.0;
		} else {
			double coeff = 1 - (order + healthOverMaxHealh) / 2;
			maxInfluence = coeff * 2 * PASSIVE_INFLUENCE;
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
	}
}
