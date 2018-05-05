package cz.cuni.mff.kocur.utility;

/**
 * Class that represents an empty function. This might be useful if we do not
 * want to apply function to consideration.
 * 
 * @author kocur
 *
 */
public class EmptyFunction extends UtilityFunction {

	@Override
	public double calculate(double x) {
		return x;
	}

}
