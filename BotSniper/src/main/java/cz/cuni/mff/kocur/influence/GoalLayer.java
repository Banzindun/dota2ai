package cz.cuni.mff.kocur.influence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.Location;

public class GoalLayer extends InfluenceLayer {
	/**
	 * Logger for FarmingLayer.
	 */
	private final static Logger logger = LogManager.getLogger(GoalLayer.class.getName());

	/**
	 * Wave propagation function we use to spread the goal's influence.
	 */
	private WavePropagationFunction<Location> wp;

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
		super((BotContextualLayer) l);
	}

	@Override
	public void createParams() {
		// We create a wave propagation function, that will spread influene from our
		// goal
		wp = new WavePropagationFunction<Location>();
		wp.setMaxDistance(width);
		wp.setMaxInfluence(maxInfluence);
	}

	/**
	 * Sets the goal.
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
