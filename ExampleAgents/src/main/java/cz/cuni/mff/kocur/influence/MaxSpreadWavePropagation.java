package cz.cuni.mff.kocur.influence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.Location;

/**
 * This class spreads the wave to maximum distance (each step increments the distance by one).
 * @author kocur
 *
 */
public class MaxSpreadWavePropagation extends WavePropagationFunction<Location>{
	private static final Logger logger = LogManager.getLogger(MaxSpreadWavePropagation.class);

	/**
	 * Constructor, that initializes the MaxSpreadWavePropagation.
	 */
	public MaxSpreadWavePropagation() {

	}

	/**
	 * Spreads the influence from given point to its surroundings.
	 * 
	 * @param l Influence layer.
	 * @param point Propagation point.
	 */
	@Override
	protected void spreadInfluence(InfluenceLayer l, PropagationPoint point) {
		calculateInfluence(l, point);

		// Go through neighbors and if not visited and entity
		// can walk through them, then add them to queue
		for (PropagationPoint p : neighbours) {
			int _x = point.getX() + p.getX();
			int _y = point.getY() + p.getY();

			if (_x < 0 || _x >= l.getWidth())
				continue;
			if (_y < 0 || _y >= l.getHeight())
				continue;

			// If the tile is not passable set visited to true.
			if (!passable(l, _x, _y)) {
				visited[_y][_x] = true;
			}
			
			// If not visited create new PP and add it to queue
			if (!visited[_y][_x]) {
				// Create new point with incremented distance 
				PropagationPoint newPoint = new PropagationPoint(_x, _y, distance);
				if (newPoint.getDistance() < maxDistance) {
					queue.add(newPoint);
					visited[_y][_x] = true;
				}
			}
		}
	}

	
	
}
