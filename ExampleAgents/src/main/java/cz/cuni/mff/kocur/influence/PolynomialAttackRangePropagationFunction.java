package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.GridBase;

/**
 * Influence spreading function, that is given by polynomial. It goes through
 * all tiles and calculates influence on the tiles.
 * 
 * @author kocur
 *
 * @param <T>
 *            Type of the entity we are working with.
 */
public class PolynomialAttackRangePropagationFunction<T extends BaseEntity> implements PropagationFunction<T> {

	protected int sign = 1;

	protected double power = 1;

	protected double maxInfluence = 1.0;

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
				double distance = GridBase.distanceTileToTile(x, y, xyz);
				if (distance > r)
					continue;

				double inf = maxInfluence - Math.pow(distance / r, power);
				l.addInfluence(x, y, sign * inf);
			}
		}

	}

	public int getSign() {
		return sign;
	}

	public void setSign(int sign) {
		this.sign = sign;
	}

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}

	public double getMaxInfleunce() {
		return maxInfluence;
	}

	public void setMaxInfluence(double maxInfluence) {
		this.maxInfluence = maxInfluence;
	}
}
