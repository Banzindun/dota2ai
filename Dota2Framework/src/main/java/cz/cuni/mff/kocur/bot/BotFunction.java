package cz.cuni.mff.kocur.bot;

/**
 * Interface that represents a parametrized function with calculate method().
 * @author kocur
 *
 * @param <O> Return type of the calculate method. (Integer, Double .. )
 * @param <T> Type of the argument.
 */
public interface BotFunction<O, T> {
		
	/**
	 * Method that calculates the result.
	 * @param e Input object.
	 * @return Returns the result.
	 */
	public default O calculate(T e) {
		return null;
	};
}
