package cz.cuni.mff.kocur.considerations;

import java.util.HashMap;

import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.utility.UtilityFunction;

/**
 * Abstract class that represents a basic Consideration. Considerations have
 * parameteres, that can be stored inside them. Each of the considerations
 * scores input value the scores are then used by decisions to calculate their
 * scores.
 * 
 * To work with scores we introduce methods clamp(..) and normalize(..). Clap
 * clamps the values to 0-1 interval. Normalize uses min and max to normalize
 * the values to 0-1 interval.
 * 
 * @author kocur
 *
 */
public abstract class Consideration {
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
	 * Integer parameter, that might be useful to considerations. For example type
	 * of inventory we are considering (stash, backpack ..)
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
	 *            Decision context.
	 * @return Returns the score of this consideration.
	 */
	public abstract double score(DecisionContext context);

	/**
	 * Computes the utility function for passed score.
	 * 
	 * @param score
	 *            The score.
	 * @return Returns computed value.
	 */
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
	 * 
	 * @param x
	 *            x
	 * @param min
	 *            Mininum.
	 * @param max
	 *            Maximum.
	 * @return Returns x clamped to range [min, max].
	 */
	protected double clamp(double x, double min, double max) {
		if (x > max)
			return max;
		else if (x < min)
			return min;

		return x;

	}

	/**
	 * Sets a name to consideration.
	 * 
	 * @param name
	 *            New name of this consideration.
	 */
	public void setName(String name) {
		readableName = name;
	}

	/**
	 * 
	 * @return Returns the name of this consideration.
	 */
	public String getName() {
		return readableName;
	}

	/**
	 * 
	 * @return Returns utility function of this consideration.
	 */
	public UtilityFunction getUtilityFunction() {
		return utilityFunction;
	}

	/**
	 * Sets a new utility function.
	 * 
	 * @param utilityFunction
	 *            New utility function.
	 */
	public void setUtilityFunction(UtilityFunction utilityFunction) {
		this.utilityFunction = utilityFunction;
	}

	/**
	 * Get's string parameter of given type from parameters.
	 * 
	 * @param param
	 *            Returns the string that corresponds to given parameter.
	 * @return Returns the parameter.
	 */
	public String getStringParameter(int param) {
		return stringParameters.get(param);
	}

	/**
	 * Adds a string parameter.
	 * 
	 * @param id
	 *            Id of the parameter.
	 * @param value
	 *            Value of the parameter.
	 */
	public void addStringParameter(int id, String value) {
		stringParameters.put(id, value);
	}

	/**
	 * 
	 * @param param
	 *            Type of the parameter.
	 * @return Returns the double parameter of given type.
	 */
	public double getDoubleParameter(int param) {
		return doubleParameters.get(param);
	}

	/**
	 * Adds double parameter.
	 * 
	 * @param code
	 *            Code of the parameter.
	 * @param param
	 *            It's value.
	 */
	public void addDoubleParameter(int code, double param) {
		doubleParameters.put(code, param);
	}

	/**
	 * Returns integer parameter of given name.
	 * 
	 * @param param
	 *            Type of the parameter.
	 * @return Returns the integer parameter.
	 */
	public int getIntParameter(int param) {
		return integerParameters.get(param);
	}

	/**
	 * Adds a new integer parameter.
	 * 
	 * @param code
	 *            Code of the parameter.
	 * @param param
	 *            It's value.
	 */
	public void addIntParameter(int code, int param) {
		integerParameters.put(code, param);
	}

	public Decision getParentDecision() {
		return parentDecision;
	}

	public void setParentDecision(Decision parentDecision) {
		this.parentDecision = parentDecision;
	}

	/**
	 * 
	 * @return Returns score of this consideration.
	 */
	public double getScore() {
		double r = score;

		score = 0;

		return r;
	}

	/**
	 * 
	 * @return Returns the normalized input of this consideration.
	 */
	public double getNormalizedInput() {
		return normalizedInput;
	}

}
