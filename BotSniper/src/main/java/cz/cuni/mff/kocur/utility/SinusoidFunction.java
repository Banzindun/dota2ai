package cz.cuni.mff.kocur.utility;

public class SinusoidFunction extends UtilityFunction {

	public SinusoidFunction(double m, double k, double c, double b) {
		super(m, k, c, b);
		type = UtilityFunction.TYPE.SINUSOID_FUNCTION;
	}

	@Override
	public double calculate(double x) {
		return m*Math.sin(k*x+c) + b;
	}

}
