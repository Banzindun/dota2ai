package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.interests.Fort;
import cz.cuni.mff.kocur.world.GridBase;

/**
 * Propagation function that propagates fort's influence across the map.
 * 
 * @author kocur
 *
 * @param <T>
 *            Type of entity we are spreading the influence for.
 */
public class FortPropagation<T extends Fort> implements PropagationFunction<T> {

	private double maxRange = 100;

	private int sign = 1;

	public FortPropagation() {

	}

	public FortPropagation(int sign, double halfDiagonal) {
		this.sign = sign;
		this.maxRange = halfDiagonal;
	}

	@Override
	public void propagate(InfluenceLayer l, T e) {
		double[] xyz = l.getEntityCoordinates(e);

		double ex = xyz[0];
		double ey = xyz[1];

		double yMin = (ey - maxRange) > 0 ? ey - maxRange : 0;
		double yMax = (ey + maxRange) < l.getHeight() ? ey + maxRange : l.getHeight() - 1;

		double xMin = (ex - maxRange) > 0 ? ex - maxRange : 0;
		double xMax = (ex + maxRange) < l.getWidth() ? ex + maxRange : l.getWidth() - 1;

		for (int y = (int) yMin; y < yMax; y++) {
			for (int x = (int) xMin; x < xMax; x++) {
				double distance = GridBase.distanceTileToTile(x, y, (int) ex, (int) ey);
				if (distance > maxRange)
					continue;

				double inf = 1.0 - (distance / maxRange);
				l.addInfluence(x, y, sign * inf);
			}
		}

	}

}
