package cz.cuni.mff.kocur.influence;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.Point;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.GridBase;
import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Propagates hero's field of view to influence map.
 * 
 * @author kocur
 *
 * @param <T> Type of entity we are propagating.
 */
public class FOWPropagation<T extends BaseEntity> implements PropagationFunction<T> {

	private static final Logger logger = LogManager.getLogger(FOWPropagation.class.getName());

	/**
	 * Rays.
	 */
	protected int numberOfRays = 180;

	/**
	 * Our hero's origin.
	 */
	protected double[] origin = new double[2];

	public FOWPropagation() {

	}

	/**
	 * Bresenham's line algorithm. Finds intersections with tiles.
	 * 
	 * @param x1
	 *            x
	 * @param y1
	 *            y
	 * @return List of points.
	 */
	private ArrayList<Point> BresenhamLine(int x1, int y1) {
		int x0 = (int) origin[0];
		int y0 = (int) origin[1];

		// List of points.
		ArrayList<Point> result = new ArrayList<Point>();

		boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);
		if (steep) {
			int tmp = y0;
			y0 = x0;
			x0 = tmp;

			tmp = y1;
			y1 = x1;
			x1 = tmp;
		}
		if (x0 > x1) {
			int tmp = x1;
			x1 = x0;
			x0 = tmp;

			tmp = y1;
			y1 = y0;
			y0 = tmp;
		}

		int deltax = x1 - x0;
		int deltay = Math.abs(y1 - y0);
		int error = 0;
		int ystep;
		int y = y0;

		if (y0 < y1)
			ystep = 1;
		else
			ystep = -1;

		for (int x = x0; x <= x1; x++) {
			if (steep)
				result.add(new Point(y, x));
			else
				result.add(new Point(x, y));

			error += deltay;
			if (2 * error >= deltax) {
				y += ystep;
				error -= deltax;
			}
		}

		return result;
	}

	@Override
	public synchronized void propagate(InfluenceLayer l, T e) {
		// logger.debug( "Linear radius propagation propagation." + e.getName());
		origin = l.getEntityCoordinates(e);

		if (origin[0] < 0 || origin[1] < 0) {
			logger.debug("Smaller than 0");
			return;
		}

		// Get vision range
		double visionRange = l.reverseResolution((int) (e.getVisionRange())) - 1;

		double rad = 0;
		double maxRad = 2 * Math.PI;

		double step = maxRad / numberOfRays;

		ArrayList<Point> intersections = new ArrayList<Point>();
		BufferedImage image = new BufferedImage(l.getWidth(), l.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

		while (rad < maxRad) {
			int rx = (int) (origin[0] + visionRange * Math.cos(rad));
			int ry = (int) (origin[1] + visionRange * Math.sin(rad));

			Point p = rayCast(l, rx, ry);
			if (p != null)
				intersections.add(p);

			rad += step;
		}

		Polygon poly = new Polygon();
		for (Point p : intersections) {
			poly.addPoint(p.x, p.y);
		}

		Graphics g = image.getGraphics();
		g.setColor(Color.WHITE);
		g.fillPolygon(poly);

		int bytesPerRow = image.getWidth() / 8;
		if (image.getWidth() % 8 > 0)
			bytesPerRow += 1;

		byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		for (int y = 0; y < l.getHeight(); y++) {
			for (int x = 0; x < l.getWidth(); x++) {
				int bytePos = y * bytesPerRow + x / 8;
				int bitPos = x % 8;

				if (((pixels[bytePos] >> bitPos) & 1) == 0)
					l.setInfluence(x, y, 0);
				else
					l.setInfluence(x, y, 1.0);
			}
		}
	}

	/**
	 * 
	 * @param l
	 *            layer
	 * @param rx
	 *            rx
	 * @param ry
	 *            ry
	 * @return Returns point of intersection between ray and impassable tiles.
	 */
	public Point rayCast(InfluenceLayer l, int rx, int ry) {

		// Get the list of points from the Bresenham algorithm
		ArrayList<Point> rayLine = BresenhamLine(rx, ry);

		GridBase grid = GridBase.getInstance();

		if (rayLine.size() > 0) {
			if (!rayLine.get(0).sameCoordinates((int) origin[0], (int) origin[1]))
				Collections.reverse(rayLine);

			// Loop through all the points starting from "position"
			for (Point p : rayLine) {
				int _x = (int) l.resolveXBack(p.x);
				int _y = (int) l.resolveYBack(p.y);

				if (_x >= grid.getWidth() || _x < 0)
					break;
				if (_y >= grid.getHeight() || _y < 0)
					break;

				if (!grid.passable(_x, _y)) {
					return p;
				}

			}
		}

		// If we got here there was no blocking tile, return the last point
		return rayLine.get(rayLine.size() - 1);
	}

}
