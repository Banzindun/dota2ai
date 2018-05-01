package cz.cuni.mff.kocur.considerations;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.utility.UtilityFunction;

public abstract class Consideration {
	/**
	 * Logger for Consideration class.
	 */
	private static final Logger logger = LogManager.getLogger(Consideration.class.getName());

	public static final int PARAM_RANGE_MIN = 0;
	public static final int PARAM_RANGE_MAX = 1;
	public static final int PARAM_ITEM_NAME = 2;
	public static final int PARAM_INVENTORY_SLOT = 3;
	public static final int PARAM_TYPE = 4;
	
	/**
	 * Double parameters, that user might need. For example PARAM_RANGE_MAX, that
	 * can be used by scoring functions to get the maximum range a value can have.
	 * And to normalize it.
	 */
	protected HashMap<Integer, Double> doubleParameters = new HashMap<>();
	
	/**
	 * Integer parameter, that might be useful to considerations. 
	 * For example type of inventory we are considering (stash, backpack ..)
	 */
	protected HashMap<Integer, Integer> integerParameters = new HashMap<>();

	/**
	 * String parameters, that the considerations might need. For example name of
	 * item that should be bought/sold. Inventory slot etc.
	 */
	protected HashMap<Integer, String> stringParameters = new HashMap<>();

	/**
	 * Readable name of this consideration.
	 */
	protected String readableName = null;

	/**
	 * Utility function of this consideration. Parameters and its type are stored
	 * inside.
	 */
	protected UtilityFunction utilityFunction = null;

	/**
	 * Parent decision of this consideration. We might want to retrieve for example
	 * time of last execution from it.
	 */
	protected Decision parentDecision;
	
	/**
	 * Score of last computation.
	 */
	private double score;

	/**
	 * Normalized input of last computation.
	 */
	private double normalizedInput;


	public Consideration() {

	}

	/**
	 * Takes decision context and scores this consideration given the context.
	 * 
	 * @param context
	 * @return Returns the score of this consideration.
	 */
	public abstract double score(DecisionContext context);

	public double computeResponseCurve(double score) {
		normalizedInput = score;
		
		double _score = utilityFunction.calculate(score);
		this.score = clamp(_score);
		return _score;
	}

	/**
	 * Normalizes the input to 0-1 range using the passed min and max ranges.
	 * 
	 * @param input
	 *            Input value.
	 * @param minRange
	 *            Minimum value of input.
	 * @param maxRange
	 *            Maximum value of input.
	 * @return Returns normalized value.
	 */
	protected double normalize(double input, double minRange, double maxRange) {
		return clamp((input - minRange) / (maxRange - minRange));
	}

	/**
	 * Clamps the input paramater to 0-1 range.
	 * 
	 * @param d
	 *            Input value.
	 * @return Returns clamped value.
	 */
	protected double clamp(double d) {
		if (d > 1.0)
			return 1.0f;
		else if (d < 0)
			return 0;

		return d;
	};
	
	/**
	 * Clamps the input paramater to min-max range.
	 * @param x
	 * @param min
	 * @param max
	 * @return Returns x clamped to range [min, max].
	 */
	protected double clamp(double x, double min, double max) {
		if (x > max)
			return max;
		else if (x < min)
			return min;

		return x;
		
	}

	public void setName(String name) {
		readableName = name;
	}

	public String getName() {
		return readableName;
	}

	public UtilityFunction getUtilityFunction() {
		return utilityFunction;
	}

	public void setUtilityFunction(UtilityFunction utilityFunction) {
		this.utilityFunction = utilityFunction;
	}

	public String getStringParameter(int param) {
		return stringParameters.get(param);
	}

	public void addStringParameter(int id, String value) {
		stringParameters.put(id, value);
	}

	public double getDoubleParameter(int param) {
		return doubleParameters.get(param);
	}

	public void addDoubleParameter(int code, double param) {
		doubleParameters.put(code, param);
	}
	
	public int getIntParameter(int param) {
		return integerParameters.get(param);
	}

	public void addIntParameter(int code, int param) {
		integerParameters.put(code, param);
	}

	public Decision getParentDecision() {
		return parentDecision;
	}

	public void setParentDecision(Decision parentDecision) {
		this.parentDecision = parentDecision;
	}
	
	public double getScore() {
		double r = score; 
		
		score = 0;
		
		return r;
	}


	public double getNormalizedInput() {
		return normalizedInput;
	}

}
