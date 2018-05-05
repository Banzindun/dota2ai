package cz.cuni.mff.kocur.influence;

import java.awt.Rectangle;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.agent.AgentParameters;
import cz.cuni.mff.kocur.base.CustomStringBuilder;
import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.framework.Setup;
import cz.cuni.mff.kocur.interests.BaseInterest;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.GridSystem;

public abstract class InfluenceLayer extends GridSystem {
	/**
	 * Sample logger for InfluenceLayer. Should be private, so it can log
	 * InfluenceLayer specific issues etc.
	 */
	private static final Logger logger = LogManager.getLogger(InfluenceLayer.class.getName());

	/**
	 * Map where all the influence is stored.
	 */
	protected double[][] map;

	/**
	 * Minimum influence value stored inside this layer.
	 */
	protected double min = Double.POSITIVE_INFINITY;

	/**
	 * Position of the minimum influence value of this layer.
	 */
	protected int[] minOrigin = new int[] { -1, -1 };

	/**
	 * Maxmimum influence value of this layer.
	 */
	protected double max = Double.NEGATIVE_INFINITY;

	/**
	 * Location of the maximum influence value of this layer.
	 */
	protected int[] maxOrigin = new int[] { -1, -1 };

	/**
	 * Parameters of this influence layer.
	 */
	protected AgentParameters params = null;

	/**
	 * Constructor of InfluenceLayer.
	 * 
	 * @param width
	 *            Width of this layer.
	 * @param height
	 *            Height of this layer.
	 * @param resolution
	 *            Resolution = how many units inside parent GridSystem is one grid
	 *            in this layer.
	 */
	public InfluenceLayer(int width, int height, double resolution) {
		super();
		super.setResolution(resolution);
		super.setSize(width, height);

		map = new double[height + 1][width + 1];
	}

	/**
	 * Copy constructor.
	 * 
	 * @param l
	 *            Layer from which I should copy.
	 */
	public InfluenceLayer(InfluenceLayer l) {
		super(l);

		map = l.getMapCopy();

		this.minOrigin = new int[] { l.getLowestLocation()[0], l.getLowestLocation()[1] };
		this.maxOrigin = new int[] { l.getHighestLocation()[0], l.getHighestLocation()[1] };

		this.min = l.getLowestValue();
		this.max = l.getHighestValue();
	}

	/**
	 * Propagates influences inside this layer. Will be implemented by extenders.
	 */
	public abstract void propagate();

	/**
	 * Creates parameters for this layer. Will be implemented by extenders.
	 */
	public abstract void createParams();

	public double[][] getMap() {
		return map;
	}

	/**
	 * Returns influence at the specified location of the map. Doesn't perform any
	 * checks as to bounds safety.
	 * 
	 * @param x x
	 * @param y y
	 * @return Returns influence stored at position [y][x] inside this influence
	 *         layer.
	 */
	public double get(int x, int y) {
		return map[y][x];
	}

	/**
	 * 
	 * @param xy xy array [x, y]
	 *            Coordinates of influence value you want to get. [x, y]
	 * @return Returns influence value at specified location.
	 */
	public double get(int[] xy) {
		return get(xy[0], xy[1]);
	}

	/**
	 * Returns the normalized influence stored at [x, y] location. Performs no
	 * checks for boundaries.
	 * 
	 * @param x x
	 * @param y y
	 * @return Returns normalized (in range [-1, 1]) influence value of passed
	 *         location inside this layer.
	 */
	public double getNormalized(int x, int y) {
		return normalizeValue(map[y][x]);
	}

	/**
	 * Checks for peaks. Whenever it finds new max or minimum it stores them and
	 * their locations inside the appropriate variables.
	 * 
	 * @param x
	 *            X coordinate of influence that should be checked.
	 * @param y
	 *            Y coordinate of influence that should be checked.
	 * @param v
	 *            Variable that we are checking againts.
	 */
	protected void checkForPeaks(int x, int y, double v) {
		if (v > max) {
			max = v;
			maxOrigin = new int[] { x, y };
		}

		if (v < min) {
			min = v;
			minOrigin = new int[] { x, y };
		}
	}

	/**
	 * Stores the passed value on the specified position by overriding the previous
	 * value.
	 * 
	 * @param x x
	 * @param y y
	 * @param d
	 *            New influence value.
	 */
	public void setInfluence(int x, int y, double d) {
		checkForPeaks(x, y, d);
		map[y][x] = d;
	}

	/**
	 * Multiplies value that is stored on passed location with passed value.
	 * 
	 * @param x x
	 * @param y y
	 * @param d
	 *            Multiplier by which we multiply the previous value.
	 */
	public void multiplyInfluence(int x, int y, double d) {
		setInfluence(x, y, map[y][x] * d);
	}

	/**
	 * Adds value to value previously stored at map[y][x] and stores it.
	 * 
	 * @param x x
	 * @param y y
	 * @param d
	 *            Value to add to map[y][x].
	 */
	public void addInfluence(int x, int y, double d) {
		setInfluence(x, y, map[y][x] + d);
	}

	/**
	 * Checks if two layers can be combined.
	 * 
	 * @param l
	 *            Layer to check againts.
	 * @throws InfluenceCombinationException
	 *             If they cannot be combined.
	 */
	protected void checkLayerProperties(InfluenceLayer l) throws InfluenceCombinationException {
		if (parent != l.getParent())
			throw new InfluenceCombinationException("Parents are not the same.");
	}

	/**
	 * Multiplies this layer with supplied one.
	 * 
	 * @param l
	 *            Layer to combine with this layer.
	 * @return Returns this layer that has values multiplied by values of supplied
	 *         layer.
	 * @throws InfluenceCombinationException
	 *             If layers cannot be combined.
	 */
	public InfluenceLayer multiplyLayer(InfluenceLayer l) throws InfluenceCombinationException {
		return multiplyLayer(l, 1);
	}

	/**
	 * Multiplies this layer by the layer passed in arguments, if possible. During
	 * each multiplication, the value is multiplied by passed multiplier.
	 * 
	 * @param l
	 *            Layer that should be combined with this one.
	 * @param w
	 *            Weight that multiplies each value.
	 * @return Returns this layer which values were multiplied by values of passed
	 *         layer.
	 * @throws InfluenceCombinationException
	 *             If layers cannot be combined.
	 */
	public InfluenceLayer multiplyLayer(InfluenceLayer l, double w) throws InfluenceCombinationException {
		checkLayerProperties(l);

		// I translate origin of xy to coordinates of this layer
		double[] lOrigin = resolveXY(l.resolveXYBack(l.getOrigin()));
		double lWidth = resolveX(l.resolveXBack(l.getWidth()));
		double lHeight = resolveY(l.resolveYBack(l.getHeight()));

		Rectangle mineRectangle = new Rectangle((int) xOrigin, (int) yOrigin, width, height);
		Rectangle lRectangle = new Rectangle((int) lOrigin[0], (int) lOrigin[1], (int) lWidth, (int) lHeight);
		Rectangle intersect = mineRectangle.intersection(lRectangle);

		if (!mineRectangle.intersects(lRectangle)) {
			throw new InfluenceCombinationException("The layers do not intersect.");
		}

		// Reset min and max and their origins
		resetPeaks();

		int yIntersect = (int) (intersect.y - yOrigin);
		int xIntersect = (int) (intersect.x - xOrigin);

		for (int y = yIntersect; y < intersect.height; y++) {
			// To base and to layer coordinates
			int _y = (int) l.resolveY(resolveYBack(y));

			for (int x = xIntersect; x < intersect.width; x++) {
				// To base and to layer coordinates
				int _x = (int) l.resolveX(resolveXBack(x));
				multiplyInfluence(x, y, l.get(_x, _y) * w);
			}
		}
		return this;
	}

	/**
	 * Adds this and supplied layer together.
	 * 
	 * @param l
	 *            Layer that should be combined with this one.
	 * @return Returns this layer whose values were combined with values of the
	 *         other layer.
	 * @throws InfluenceCombinationException
	 *             If this and passed layer cannot be combined.
	 */
	public InfluenceLayer addToLayer(InfluenceLayer l) throws InfluenceCombinationException {
		return addToLayer(l, 1, 1);
	}

	/**
	 * Adds this and the supplied layer together by adding the values of this layer
	 * and multiplying them by given weight to values of passed layer, that are
	 * multiplied by the other passed weight.
	 * 
	 * @param l
	 *            Influence layer that should be added to this one.
	 * @param w1
	 *            Weight by which every value of this layer should be multiplied.
	 * @param w2
	 *            Weight by which every value of the other layer should be
	 *            multiplied.
	 * @return Returns this layer that is this and the passed layer added together
	 *         with respect to given weights.
	 * @throws InfluenceCombinationException
	 *             If this and the other layer couldnt be combined.
	 */
	public InfluenceLayer addToLayer(InfluenceLayer l, double w1, double w2) throws InfluenceCombinationException {
		checkLayerProperties(l);

		// I translate origin of xy to coordinates of this layer
		double[] lOrigin = resolveXY(l.resolveXYBack(l.getOrigin()));
		double lWidth = resolveX(l.resolveXBack(l.getWidth()));
		double lHeight = resolveY(l.resolveYBack(l.getHeight()));

		Rectangle mineRectangle = new Rectangle((int) xOrigin, (int) yOrigin, width, height);
		Rectangle lRectangle = new Rectangle((int) lOrigin[0], (int) lOrigin[1], (int) lWidth, (int) lHeight);
		Rectangle intersect = mineRectangle.intersection(lRectangle);

		if (!mineRectangle.intersects(lRectangle)) {
			throw new InfluenceCombinationException("The layers do not intersect.");
		}

		// Reset min and max and their origins
		resetPeaks();

		int yIntersect = (int) (intersect.y - yOrigin);
		int xIntersect = (int) (intersect.x - xOrigin);

		for (int y = yIntersect; y < intersect.height; y++) {
			// To base and to layer coordinates
			int _y = (int) l.resolveY(resolveYBack(y));

			for (int x = xIntersect; x < intersect.width; x++) {
				// To base and to layer coordinates
				int _x = (int) l.resolveX(resolveXBack(x));
				setInfluence(x, y, w1 * map[y][x] + w2 * l.get(_x, _y));
			}
		}
		return this;
	}

	/**
	 * This just reverses the sign of every influence value in this map.
	 */
	public void inverse() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				map[y][x] = -map[y][x];
			}
		}

		double _max = max;
		max = -min;
		min = -_max;

		int[] or = maxOrigin;
		maxOrigin = minOrigin;
		minOrigin = or;
	}

	/**
	 * Takes influence value and normalizes it to interval [-1, 1].
	 * 
	 * @param inf
	 *            Value to be normalized.
	 * @return Normalized value of the passed argument.
	 */
	public double normalizeValue(double inf) {
		return 2 * (inf - min) / (max - min) - 1;
	}

	/**
	 * Normalizes the whole layer, so that every influence value, including min and
	 * max, is in [-1,1] range.
	 */
	public void normalize() {
		// Interval will be min to max .. to normalize subtract min and divide by
		// (min-max)
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				setInfluence(x, y, normalizeValue(map[y][x]));
			}
		}
		max = normalizeValue(max);
		min = normalizeValue(min);
	}

	/**
	 * 
	 * @return Returns the location of the lowest influence value.
	 */
	public int[] getLowestLocation() {
		return minOrigin;
	}

	/**
	 * 
	 * @return Returns the location of the highest influence value.
	 */
	public int[] getHighestLocation() {
		return maxOrigin;
	}

	/**
	 * 
	 * @return Returns the lowest influence value in this layer.
	 */
	public double getLowestValue() {
		return min;
	}

	/**
	 * 
	 * @return Returns the highest influence value in this layer.
	 */
	public double getHighestValue() {
		return max;
	}

	public AgentParameters getParams() {
		return params;
	}

	public void setParams(AgentParameters params) {
		this.params = params;
	}

	/**
	 * Clears the influence in this layer == nulls all the values and min and max.
	 */
	public void clearInfluence() {
		fillWithInfluence(0);
	}

	/**
	 * Fills the entire map with given influence.
	 * 
	 * @param inf
	 *            Influence.
	 */
	public void fillWithInfluence(double inf) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				map[y][x] = inf;
			}
		}
		resetPeaks();
	}

	/**
	 * Resets the peaks (min, max and their locations).
	 */
	public void resetPeaks() {
		// Clear min and max
		min = Double.POSITIVE_INFINITY;
		minOrigin = new int[] { -1, -1 };

		max = Double.NEGATIVE_INFINITY;
		maxOrigin = new int[] { -1, -1 };
	}

	/**
	 * Returns the string representation of this layer.
	 */
	@Override
	public String toString() {
		CustomStringBuilder builder = new CustomStringBuilder();

		builder.appendLine("[" + width + ", " + height + "]");
		builder.appendLine("Min: " + min + " [" + minOrigin[0] + ", " + minOrigin[1] + "]");
		builder.appendLine("Min: " + max + " [" + maxOrigin[0] + ", " + maxOrigin[1] + "]");

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				builder.append(map[y][x] + " ");
			}
			builder.appendLine("");
		}

		if (parent != null)
			builder.appendLine(parent.toString());

		return builder.toString();
	}

	/**
	 * Saves this influence layer to file. The file should be saved to working
	 * directory with specified fileName. The location will be specified inside
	 * logs, so consult them if you don't know where your working directory is.
	 * 
	 * @param fileName
	 *            What is the name of the file we are saving to.
	 * @throws IOException
	 *             If I was unable to open the file etc.
	 */
	public void saveToFile(String fileName) throws IOException {
		logger.info("Saving influence layer to file: " + Setup.getOutputDir() + fileName);

		PrintWriter printWriter = new PrintWriter(new FileWriter(Setup.getOutputDir() + fileName));

		printWriter.write(toCSVString());
		printWriter.close();
	}

	/**
	 * Outputs the layer data to csv-like string. That means there will be layer
	 * values separated by commas and newlines.
	 * 
	 * @return Returns string with values separated by commas and new lines.
	 */
	private String toCSVString() {
		CustomStringBuilder builder = new CustomStringBuilder();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				builder.append(map[y][x] + ",");
			}
			builder.appendLine("");
		}

		return builder.toString();
	}

	/**
	 * 
	 * @return Returns copy of the stored map.
	 */
	public double[][] getMapCopy() {
		double[][] _m = new double[height][width];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				_m[y][x] = map[y][x];
			}
		}
		return _m;
	}

	/**
	 * Takes function corresponding to this entity from parameters. And applies it
	 * to this layer.
	 * 
	 * @param params
	 *            BotParameters that store the functions and other stuff.
	 * @param e
	 *            BaseEntity reference.
	 */
	public void propagateEntity(AgentParameters params, BaseEntity e) {
		EntityParameterWithInfluence p = (EntityParameterWithInfluence) e.getParameter(params);

		if (p == null)
			return;

		PropagationFunction<BaseEntity> f = ((PropagationFunction<BaseEntity>) p.getFunction(e.getTeam()));

		if (f != null)
			f.propagate(this, e);
	}

	/**
	 * Retrieves function from parameters that corresponds to this interest and
	 * applies it.
	 * 
	 * @param params
	 *            BotParameters that store all the relevant data
	 * @param e
	 *            Reference to BaseInterest
	 */
	public void propagateInterest(AgentParameters params, BaseInterest e) {
		InterestParameterWithInfluence p = (InterestParameterWithInfluence) e.getParameter(params);

		if (p == null)
			return;

		PropagationFunction<BaseInterest> f = ((PropagationFunction<BaseInterest>) p.getFunction());

		if (f != null)
			f.propagate(this, e);
	}

	/**
	 * Walks through a rectangle specified by startx, starty and size bounds. Finds
	 * its minimum, maximum and locations of such highpoints.
	 * 
	 * @param startx
	 *            Starting x coordinate.
	 * @param starty
	 *            Starting y coordintate.
	 * @param xSize
	 *            Width.
	 * @param ySize
	 *            Height.
	 * @return Returns MinMaxObject containing found min and max and its indices.
	 */
	public MinMaxObject findMinMax(int startx, int starty, int xSize, int ySize) {
		double _min = Double.POSITIVE_INFINITY;
		double _max = Double.NEGATIVE_INFINITY;

		int[] minLocation = new int[2];
		int[] maxLocation = new int[2];

		for (int y = starty; y < ySize && y < height; y++) {
			if (y < 0)
				continue;

			for (int x = startx; x < xSize && x < width; x++) {
				if (x < 0)
					continue;

				if (map[y][x] < _min) {
					_min = map[y][x];
					minLocation[0] = x;
					minLocation[1] = y;
				}

				if (map[y][x] > _max) {
					_max = map[y][x];
					maxLocation[0] = x;
					maxLocation[1] = y;
				}
			}
		}

		// If the grid is constant, min and max will be equal. We cannot find max and
		// min then and we shouldn't return any position.
		if (_min == _max) {
			minLocation = new int[] { -1, -1 };
			maxLocation = new int[] { -1, -1 };
		}

		MinMaxObject res = new MinMaxObject(_min, _max);

		res.setMaxLocation(maxLocation);
		res.setMinLocation(minLocation);

		return res;
	}

	/**
	 * Returns minimum on specified rectangle.
	 * 
	 * @param startx
	 *            X top left corner of the rectangle.
	 * @param starty
	 *            Y top left corner of the rectangle.
	 * @param xSize
	 *            Width of the rectangle.
	 * @param ySize
	 *            Height of the rectangle.
	 * @return Returns double representing the minimum.
	 */
	public double findMinOnGrid(int startx, int starty, int xSize, int ySize) {
		return findMinMax(startx, starty, xSize, ySize).getMin();
	}

	/**
	 * Returns location of minimum value from the specified rectangle.
	 * 
	 * @param startx
	 *            X top left corner of the rectangle.
	 * @param starty
	 *            Y top left corner of the rectangle.
	 * @param xSize
	 *            Width of the rectangle.
	 * @param ySize
	 *            Height of the rectangle.
	 * @return Returns location of minimum value from the specified rectangle.
	 */
	public int[] findMinLocationOnGrid(int startx, int starty, int xSize, int ySize) {
		return findMinMax(startx, starty, xSize, ySize).getMinLocation();
	}

	/**
	 * Returns maximum on specified rectangle.
	 * 
	 * @param startx
	 *            X top left corner of the rectangle.
	 * @param starty
	 *            Y top left corner of the rectangle.
	 * @param xSize
	 *            Width of the rectangle.
	 * @param ySize
	 *            Height of the rectangle.
	 * @return Returns double representing the maximum of the specified rectangle.
	 */
	public double findMaxOnGrid(int startx, int starty, int xSize, int ySize) {
		return findMinMax(startx, starty, xSize, ySize).getMax();
	}

	/**
	 * Returns location of a maximum value from the specified rectangle.
	 * 
	 * @param startx
	 *            X top left corner of the rectangle.
	 * @param starty
	 *            Y top left corner of the rectangle.
	 * @param xSize
	 *            Width of the rectangle.
	 * @param ySize
	 *            Height of the rectangle.
	 * @return Returns location of a maximum value from the specified rectangle.
	 */
	public int[] findMaxLocationOnGrid(int startx, int starty, int xSize, int ySize) {
		return findMinMax(startx, starty, xSize, ySize).getMaxLocation();
	}

	/**
	 * 
	 * @return Returns ArrayList of FrontLines in left to right order. Front lines
	 *         are objects that store locations on which there there is a swap in
	 *         influence signs. (meaning influence going from positive to negative
	 *         values and vice versa)
	 */
	public ArrayList<FrontLine> getFrontLine() {
		ArrayList<FrontLine> fronts = new ArrayList<>();

		for (int y = 0; y < height; y++) {
			FrontLine fl = new FrontLine();
			int lastSign = map[y][0] < 0 ? -1 : 1;

			for (int x = 0; x < width; x++) {
				int sign = map[y][x] < 0 ? -1 : 1;

				if (sign != lastSign)
					fl.addLocation(x, y);

				lastSign = sign;
			}

			fronts.add(fl);
		}

		return fronts;
	}

	/**
	 * The value can be Double.MAX_VALUE if no propagation occurred just yet or if
	 * it was just cleared.
	 * 
	 * @return Returns minimum value from this layer.
	 */
	public double getMin() {
		return min;
	}

	/**
	 * 
	 * @param min
	 *            New minimum to be set.
	 */
	public void setMin(double min) {
		this.min = min;
	}

	/**
	 * 
	 * @return Returns origin(location) of minimum peak. {-1,-1} if no such peak
	 *         exist.
	 */
	public int[] getMinOrigin() {
		return minOrigin;
	}

	/**
	 * 
	 * @param minOrigin
	 *            New origin of minimum value.
	 */
	public void setMinOrigin(int[] minOrigin) {
		this.minOrigin = minOrigin;
	}

	/**
	 * The value can be Double.MIN_VALUE if no propagation occurred just yet or if
	 * it was just cleared.
	 * 
	 * @return Returns maximum value from this layer.
	 */
	public double getMax() {
		return max;
	}

	/**
	 * 
	 * @param max
	 *            New maximum.
	 */
	public void setMax(double max) {
		this.max = max;
	}

	/**
	 * 
	 * @return Returns origin(location) of maximum peak. {-1,-1} if no such peak
	 *         exist.
	 */
	public int[] getMaxOrigin() {
		return maxOrigin;
	}

	/**
	 * 
	 * @param maxOrigin
	 *            New maximum origin.
	 */
	public void setMaxOrigin(int[] maxOrigin) {
		this.maxOrigin = maxOrigin;
	}

	/**
	 * 
	 * @return Returns maximum location.
	 */
	public Location getMaxLocation() {
		if (maxOrigin[0] == -1)
			return null;

		return new Location(this.toBase(maxOrigin[0], maxOrigin[1]));

	}

	/**
	 * 
	 * @return Returns minimum location.
	 */
	public Location getMinLocation() {
		if (minOrigin[0] == -1)
			return null;

		return new Location(this.toBase(minOrigin[0], minOrigin[1]));
	}

	/**
	 * 
	 * @param coords
	 *            Coordiantes.
	 * @return Returns influence value at given coordinates.
	 */
	public double get(double[] coords) {
		return this.get((int) coords[0], (int) coords[1]);
	}
}
