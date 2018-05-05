package cz.cuni.mff.kocur.utility;

/**
 * Class that can be used to create utility functions by parameters and their
 * type.
 * 
 * @author kocur
 *
 */
public class FunctionFactory {

	/**
	 * Creates a utility function given its type and parameters.
	 * 
	 * @param type
	 *            Type of the function.
	 * @param m
	 *            Multiplier.
	 * @param k
	 *            Power.
	 * @param c
	 *            Shift along y line.
	 * @param b
	 *            Shift along x line.
	 * @return Returns new function of given type.
	 */
	public static UtilityFunction createFunction(UtilityFunction.TYPE type, double m, double k, double c, double b) {
		switch (type) {
		case POLYNOMIAL_FUNCTION:
			return createPolynomial(m, k, c, b);
		case LOGISTIC_FUNCTION:
			return createLogistic(m, k, c, b);
		case LOGIT_FUNCTION:
			return createLogit(m, k, c, b);
		case LINEAR_FUNCTION:
			return createLinear(m, k, c, b);
		case BINARY_FUNCTION:
			return createBinary(m, k, c, b);
		case SINUSOID_FUNCTION:
			return createSinusoid(m, k, c, b);
		default:
			return null;
		}
	}

	/**
	 * Creates new binary function given the parameters.
	 * 
	 * @param m
	 *            Multiplier.
	 * @param k
	 *            Power.
	 * @param c
	 *            Shift along y line.
	 * @param b
	 *            Shift along x line.
	 * @return Returns the new binary function.
	 */
	private static UtilityFunction createBinary(double m, double k, double c, double b) {
		return new BinaryFunction(m, k, c, b);
	}

	/**
	 * Creates new linear function given the parameters.
	 * 
	 * @param m
	 *            Multiplier.
	 * @param k
	 *            Power.
	 * @param c
	 *            Shift along y line.
	 * @param b
	 *            Shift along x line.
	 * @return Returns the new linear function.
	 */
	private static UtilityFunction createLinear(double m, double k, double c, double b) {
		return new LinearFunction(m, k, c, b);
	}

	/**
	 * Creates new logit function given the parameters.
	 * 
	 * @param m
	 *            Multiplier.
	 * @param k
	 *            Power.
	 * @param c
	 *            Shift along y line.
	 * @param b
	 *            Shift along x line.
	 * @return Returns the new logit function.
	 */
	private static UtilityFunction createLogit(double m, double k, double c, double b) {
		return new LogitFunction(m, k, c, b);
	}

	/**
	 * Creates new logistic function given the parameters.
	 * 
	 * @param m
	 *            Multiplier.
	 * @param k
	 *            Power.
	 * @param c
	 *            Shift along y line.
	 * @param b
	 *            Shift along x line.
	 * @return Returns the new logistic function.
	 */
	private static UtilityFunction createLogistic(double m, double k, double c, double b) {
		return new LogisticFunction(m, k, c, b);
	}

	/**
	 * Creates new polynomial function given the parameters.
	 * 
	 * @param m
	 *            Multiplier.
	 * @param k
	 *            Power.
	 * @param c
	 *            Shift along y line.
	 * @param b
	 *            Shift along x line.
	 * @return Returns the new polynomial function.
	 */
	private static UtilityFunction createPolynomial(double m, double k, double c, double b) {
		return new PolynomialFunction(m, k, c, b);
	}

	/**
	 * Creates new sinusoid function given the parameters.
	 * 
	 * @param m
	 *            Multiplier.
	 * @param k
	 *            Power.
	 * @param c
	 *            Shift along y line.
	 * @param b
	 *            Shift along x line.
	 * @return Returns the new sinusoid function.
	 */
	private static UtilityFunction createSinusoid(double m, double k, double c, double b) {
		return new SinusoidFunction(m, k, c, b);
	}

}
