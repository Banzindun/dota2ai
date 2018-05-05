package cz.cuni.mff.kocur.utility;

/**
 * Class that represents logit utility function.
 * 
 * 0.5 * Math.log(z/(1-z))/Math.log(Math.pow(100, m)) + b + 0.5;
 * 
 * @author kocur
 *
 */
public class LogitFunction extends UtilityFunction {

	public LogitFunction(double m, double k, double c, double b) {
		super(m, k, c, b);
		type = UtilityFunction.TYPE.LOGIT_FUNCTION;
	}

	@Override
	public double calculate(double x) {
		double z = x / k - c;
		return 0.5 * Math.log(z / (1 - z)) / Math.log(Math.pow(100, m)) + b + 0.5;
	}
}
