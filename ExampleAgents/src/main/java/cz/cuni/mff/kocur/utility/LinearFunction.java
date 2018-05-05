package cz.cuni.mff.kocur.utility;

/**
 * Defines linear function in the form of: m*(x-c)+b Where m,c,b are parameters.
 * The parameter k from parent class is defined as 1.
 * 
 * m*(x-c)+b;
 * 
 * @author kocur
 *
 */
public class LinearFunction extends UtilityFunction {

	protected double k = 1;

	public LinearFunction(double m, double k, double c, double b) {
		super(m, k, c, b);
		type = UtilityFunction.TYPE.LINEAR_FUNCTION;
	}

	@Override
	public double calculate(double x) {
		return m * (x - c) + b;
	}

}
