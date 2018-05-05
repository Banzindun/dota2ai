package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.GridBase;

/**
 * Propagation function that is based on linear faloff.
 * 
 * @author kocur
 *
 * @param <T> Type of the entity we are dealing with.
 */
public class LinearRadiusPropagationFunction<T extends BaseEntity> implements PropagationFunction<T> {

	/**
	 * Sign of the influence.
	 */
	private int sign = 1;

	/**
	 * Maximum influence.
	 */
	protected double maxInfluence = 1;

	public LinearRadiusPropagationFunction() {

	}

	public LinearRadiusPropagationFunction(int sign) {
		this.sign = sign;
	}

	@Override
	public void propagate(InfluenceLayer l, T e) {
		// logger.debug( "Linear radius propagation propagation." + e.getName());
		double[] xyz = l.getEntityCoordinates(e);

		double ex = xyz[0];
		double ey = xyz[1];

		// R will be in layer coordinates
		double r = l.reverseResolution((int) (e.getAttackRange() + e.getSpeed()));

		double yMin = (ey - r) > 0 ? ey - r : 0;
		double yMax = (ey + r) < l.getHeight() ? ey + r : l.getHeight() - 1;

		double xMin = (ex - r) > 0 ? ex - r : 0;
		double xMax = (ex + r) < l.getWidth() ? ex + r : l.getWidth() - 1;

		for (int y = (int) yMin; y < yMax; y++) {
			for (int x = (int) xMin; x < xMax; x++) {
				double distance = GridBase.distanceTileToTile(x, y, (int) ex, (int) ey);
				if (distance > r)
					continue;

				double inf = maxInfluence - (distance / r);
				l.addInfluence(x, y, sign * inf);
			}
		}

	}

}
