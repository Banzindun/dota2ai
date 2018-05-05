package cz.cuni.mff.kocur.influence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.agent.AgentContext;
import cz.cuni.mff.kocur.interests.Fort;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.GridBase;

/**
 * Class that represents farming influence propagation function. This influence
 * goes only towards our agent's base. And it goes up to agent's attack range.
 * It has maximum almost at the end of the attack range.
 * 
 * @author kocur
 *
 * @param <T>
 *            Type of the entity we deal with (creep, hero).
 */
public class FarmingInfluence<T extends BaseEntity> extends WavePropagationWithContext<T> {
	/**
	 * Logger.
	 */
	private Logger logger = LogManager.getLogger(FarmingInfluence.class);

	/**
	 * Distance of this entity to base.
	 */
	protected double distanceEntityToBase = 0;

	/**
	 * Maximum distance of entity to base. (defines the radius)
	 */
	protected double maxDistanceToBase = 0;

	/**
	 * Minimum distance of entity to base. (defines the radius)
	 */
	protected double minDistanceToBase = 0;

	/**
	 * Reference to team's base.
	 */
	protected Fort base = null;

	/**
	 * We don't want to be necessarily at maximum range, but we might want to be a
	 * bit closer.
	 */
	protected final double POSITION_OFFSET = 10;

	/**
	 * Coordinates of the base of this creep.
	 */
	protected double[] baseCoords;

	/**
	 * Creep for which we are currently calculating the influence.
	 */
	protected T entity;

	/**
	 * We will multiply the result by some coefficient calculated from creep's
	 * health.
	 */
	protected double coeff = 1;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            Agent's context.
	 */
	public FarmingInfluence(AgentContext context) {
		super(context);
	}

	@Override
	public void propagate(InfluenceLayer l, T e) {
		super.updateHero();
		entity = e;

		// Get my base or enemy base from context, according to entity team
		base = context.getMyBase();
		sign = 1;

		// Check that base is not null
		if (base == null) {
			logger.fatal("Base is null!");
			return;
		}

		// Get the base coordinates in this layer
		baseCoords = l.getEntityCoordinates(base);

		// I will take hero's attack range as a max distance
		maxDistance = l.reverseResolution((int) (context.getHero().getAttackRange()));

		double[] xyz = l.getEntityCoordinates(e);

		double ex = xyz[0];
		double ey = xyz[1];

		// I want hero to be positioned towards my base. So every entity will emmit the
		// influence only to cells that are closer to tha base than the entity.
		distanceEntityToBase = GridBase.distanceTileToTile((int) baseCoords[0], (int) baseCoords[1], (int) ex,
				(int) ey);
		maxDistanceToBase = distanceEntityToBase;
		minDistanceToBase = distanceEntityToBase - maxDistance + POSITION_OFFSET;

		// We calculate a missing percentage of creeps health and use it as coefficient.
		coeff = 1 - Math.pow((double) entity.getHealth() / entity.getMaxHealth(), 2);

		// This will create origin for our object, and will start the spread from the
		// origin.
		super.propagate(l, e);
	}

	@Override
	protected void calculateInfluence(InfluenceLayer l, PropagationPoint point) {
		double inf = 0;

		// Check if the team is different. (we are taking neutral and enemy's creeps)
		if (context.getMyTeam() != entity.getTeam()) {
			inf = calculateCreepsInfluence(point);
		}

		// Add influence
		l.addInfluence(point.getX(), point.getY(), inf);
	}

	/**
	 * Calculates the influence the creep spreads.
	 * 
	 * @param point
	 *            Point for which the influence should be calculated.
	 * @return Returns a influence value.
	 */
	private double calculateCreepsInfluence(PropagationPoint point) {
		double result = 0;

		// Distance between tile and base
		double distanceTileToBase = GridBase.distanceTileToTile(point.getX(), point.getY(), baseCoords);

		if (distanceTileToBase > maxDistanceToBase || distanceTileToBase < minDistanceToBase) // This tile is tilted
																								// towards enemy's base.
			return 0;

		double normalizedDistanceToBase = (distanceTileToBase - minDistanceToBase)
				/ (maxDistanceToBase - minDistanceToBase);

		result = coeff * (maxInfluence - Math.pow(normalizedDistanceToBase, power));
		return result;
	}
}
