package cz.cuni.mff.kocur.utility;

/**
 * Abstract class that represents a UtilityFunction. This class defines some
 * methods used by every function as well as their parameters.
 * 
 * We have got function type: BINARY_FUNCTION, LINEAR_FUNCTION,
 * LOGISTIC_FUNCTION, LOGIT_FUNCTION, POLYNOMIAL_FUNCTION, SINUSOID_FUNCTION.
 * 
 * We have got parameters m, k, c, b that shape the curves.
 * 
 * m == multiplier, k == power, c == shift up and along the y line, b == shift
 * left and right along the x line
 * 
 * 
 * @author kocur
 *
 */
public abstract class UtilityFunction {

	/**
	 * Utility function types.
	 * 
	 * @author kocur
	 *
	 */
	public static enum TYPE {
		BINARY_FUNCTION, LINEAR_FUNCTION, LOGISTIC_FUNCTION, LOGIT_FUNCTION, POLYNOMIAL_FUNCTION, SINUSOID_FUNCTION
	};

	/**
	 * Type of this function.
	 */
	protected TYPE type;

	/**
	 * M parameter. Multiplier.
	 */
	protected double m;

	/**
	 * K parameter. "Power."
	 */
	protected double k;

	/**
	 * C parameter. Shift along y line.
	 */
	protected double c;

	/**
	 * B parameter. Shift along x line.
	 */
	protected double b;

	/**
	 * 
	 * @param x
	 *            Input value.
	 * @return Value calculated by the utility function.
	 */
	public abstract double calculate(double x);

	public UtilityFunction() {

	}

	/**
	 * Creates utility function given the parameters.
	 * 
	 * @param m
	 *            m
	 * @param k
	 *            k
	 * @param c
	 *            c
	 * @param b
	 *            b
	 */
	public UtilityFunction(double m, double k, double c, double b) {
		setMKCB(m, k, c, b);
	}

	/**
	 * Sets the paramters for this function.
	 * 
	 * @param m
	 *            m multiplier
	 * @param k
	 *            k power
	 * @param c
	 *            c shift along y axis
	 * @param b
	 *            b shift along x axis
	 */
	public void setMKCB(double m, double k, double c, double b) {
		this.m = m;
		this.k = k;
		this.c = c;
		this.b = b;
	}

	public double[] getSignature() {
		return new double[] { m, k, c, b };
	}

	public double getM() {
		return m;
	}

	public void setM(double m) {
		this.m = m;
	}

	public double getK() {
		return k;
	}

	public void setK(double k) {
		this.k = k;
	}

	public double getC() {
		return c;
	}

	public void setC(double c) {
		this.c = c;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}

	/**
	 * 
	 * @return Returns the type of this function.
	 */
	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}
}
