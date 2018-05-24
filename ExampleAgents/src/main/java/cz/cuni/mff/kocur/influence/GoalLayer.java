package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.base.Location;

/**
 * Layers that stores goals inside. Goals set by agents and they spread their
 * influence around the map. So this layer can be used to navigate the agent
 * towards a goal.
 * 
 * @author kocur
 *
 */
public class GoalLayer extends InfluenceLayer {
	/**
	 * Wave propagation function we use to spread the goal's influence.
	 */
	private MaxSpreadWavePropagation wp;

	/**
	 * The goal.
	 */
	private Location goal = null;

	/**
	 * Maximum influence we can have.
	 */
	protected double maxInfluence = 1;

	public GoalLayer(int width, int height, double resolution) {
		super(width, height, resolution);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param l
	 *            Influence layer to copy inside this one.
	 */
	public GoalLayer(InfluenceLayer l) {
		super((ContextualLayer) l);
	}

	@Override
	public void createParams() {
		// We create a wave propagation function, that will spread influene from our
		// goal
		wp = new MaxSpreadWavePropagation();
		wp.setMaxDistance(width);
		wp.setMaxInfluence(maxInfluence);
	}

	/**
	 * Sets the goal in in-game's coordinates.
	 * 
	 * @param l
	 *            Location that represents the goal where we want to get.
	 */
	public void setGoal(Location l) {
		goal = l;
		propagate();
	}

	@Override
	public void propagate() {
		this.clearInfluence();

		if (goal == null) {
			return;
		}

		wp.propagate(this, goal);
	}

	public void setMaxInfluence(double maxInfluence) {
		this.maxInfluence = maxInfluence;
	}

	public double getMaxInfluence() {
		return maxInfluence;
	}
}
