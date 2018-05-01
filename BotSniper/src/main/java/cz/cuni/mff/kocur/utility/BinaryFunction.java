package cz.cuni.mff.kocur.utility;

public class BinaryFunction extends UtilityFunction {
	
	public BinaryFunction(double m, double k, double c, double b) {
		super(m,k,c,b);
		type = UtilityFunction.TYPE.BINARY_FUNCTION;
	}
	
	@Override
	public double calculate(double x) {
		double returnValue = -1;
		if (x >= c )
			returnValue = 1;

		return m*k*returnValue + b;
	}

}

