package cz.cuni.mff.kocur.utility;

/**
 * Class that represents logistic utility function.
 * 
 * k*(1/(1+Math.exp(-z)))+b
 * 
 * @author kocur
 *
 */
public class LogisticFunction extends UtilityFunction {

	public LogisticFunction(double m, double k, double c, double b) {
		super(m, k, c, b);
		type = UtilityFunction.TYPE.LOGISTIC_FUNCTION;
	}

	@Override
	public double calculate(double x) {
		double z = 10 * m * (x - c - 0.5);
		return k * (1 / (1 + Math.exp(-z))) + b;
	}

}
