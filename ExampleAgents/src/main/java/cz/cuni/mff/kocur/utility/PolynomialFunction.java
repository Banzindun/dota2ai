package cz.cuni.mff.kocur.utility;

/**
 * Class that represents polynomial utility function.
 * 
 * m * Math.pow(x - c, k) + b;
 * 
 * @author kocur
 *
 */
public class PolynomialFunction extends UtilityFunction {

	public PolynomialFunction(double m, double k, double d, double b) {
		super(m, k, d, b);
		type = UtilityFunction.TYPE.POLYNOMIAL_FUNCTION;
	}

	@Override
	public double calculate(double x) {
		return m * Math.pow(x - c, k) + b;
	}

}
