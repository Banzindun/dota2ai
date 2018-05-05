package cz.cuni.mff.kocur.decisions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.considerations.Consideration;
import cz.cuni.mff.kocur.utility.UtilityFunction;

/**
 * Builder that builds decisions. This will make our code much more readable.
 * 
 * @author kocur
 *
 */
public class DecisionBuilder {
	/**
	 * Logger registered for DecisionBuilder.
	 */
	private static final Logger logger = LogManager.getLogger(DecisionBuilder.class.getName());

	/**
	 * 
	 * @return Returns a new builder.
	 */
	public static DecisionBuilder build() {

		return new DecisionBuilder();
	}

	/**
	 * Decision reference.
	 */
	protected Decision decision = null;

	private DecisionBuilder() {

	}

	/**
	 * Sets a new decision to this builder.
	 * 
	 * @param d
	 *            The decision.
	 * @return Returns this.
	 */
	public DecisionBuilder setDecision(Decision d) {
		decision = d;
		return this;
	}

	/**
	 * Sets a new name for our built decision.
	 * 
	 * @param name
	 *            Name.
	 * @return Returns this.
	 */
	public DecisionBuilder setName(String name) {
		if (decision == null) {
			logger.error("Decision not supplied.");
		} else {
			decision.setReadableName(name);
		}

		return this;
	}

	/**
	 * Sets a context for the decision.
	 * 
	 * @param context
	 *            New context.
	 * @return Returns this.
	 */
	public DecisionBuilder setContext(DecisionContext context) {
		if (decision == null) {
			logger.error("Decision not supplied.");
		} else {
			decision.setContext(context);
		}

		return this;
	}

	/**
	 * Sets an evaluator (DSE) to the decision.
	 * 
	 * @param eval
	 *            DSE
	 * @return Returns this.
	 */
	public DecisionBuilder setEvaluator(DecisionScoreEvaluator eval) {
		if (decision == null) {
			logger.error("Decision not supplied.");
		} else {
			decision.setDSE(eval);
		}

		return this;
	}

	/**
	 * Adds a consideration to the decision.
	 * 
	 * @param c
	 *            Consideration.
	 * @return Returns this.
	 */
	public DecisionBuilder addConsideration(Consideration c) {
		if (decision == null) {
			logger.error("Decision not supplied.");
		} else {
			decision.addConsideration(c);
			addParentToConsideration();
		}
		return this;
	}

	/**
	 * Adds consideration to the decision.
	 * 
	 * @param c
	 *            Consideration.
	 * @param f
	 *            Utility function.
	 * @return Returns this.
	 */
	public DecisionBuilder addConsideration(Consideration c, UtilityFunction f) {
		if (decision == null) {
			logger.error("Decision not supplied.");
		} else {
			c.setUtilityFunction(f);
			decision.addConsideration(c);
			addParentToConsideration();
		}
		return this;
	}

	/**
	 * Adds parent to last consideration. The parent will be decision saved in this
	 * builder.
	 */
	private void addParentToConsideration() {
		Consideration c = decision.getLastConsideration();

		if (c == null) {
			logger.error("No consideration specified yet.");
		} else {
			c.setParentDecision(decision);
		}
	}

	/**
	 * Adds double parameter to lastly inserted consideration.
	 * 
	 * @param i
	 *            Type of the double parameter.
	 * @param value
	 *            Value of the parameter.
	 * @return Returns this.
	 */
	public DecisionBuilder addDoubleParameter(int i, double value) {
		if (decision == null) {
			logger.error("Decision not supplied.");
			return this;
		}

		Consideration c = decision.getLastConsideration();

		if (c == null) {
			logger.error("No consideration specified yet.");
			return this;
		}

		// Add the parameter
		c.addDoubleParameter(i, value);

		return this;
	}

	/**
	 * Adds string parameter to lastly inserted consideration.
	 * 
	 * @param i
	 *            Type of the double parameter.
	 * @param value
	 *            Value of the parameter.
	 * @return Returns this.
	 */
	public DecisionBuilder addStringParameter(int i, String value) {
		if (decision == null) {
			logger.error("Decision not supplied.");
			return this;
		}

		Consideration c = decision.getLastConsideration();

		if (c == null) {
			logger.error("Adding string parameter to empty considerations.");
			return this;
		}

		// Add the parameter
		c.addStringParameter(i, value);

		return this;
	}

	/**
	 * Sets a new bonus factor.
	 * 
	 * @param factor
	 *            The factor.
	 * @return Returns this.
	 */
	public DecisionBuilder setBonusFactor(double factor) {
		if (decision == null) {
			logger.error("Decision not yet supplied.");
			return this;
		}

		decision.getContext().setBonusFactor(factor);

		return this;
	}

	/**
	 * 
	 * @return Returns the built decision.
	 */
	public Decision get() {
		return decision;
	}

	/**
	 * Adds int parameter to lastly inserted consideration.
	 * 
	 * @param paramType
	 *            Type of the double parameter.
	 * @param value
	 *            Value of the parameter.
	 * @return Returns this.
	 */
	public DecisionBuilder addIntParameter(int paramType, int value) {
		if (decision == null) {
			logger.error("Decision not supplied.");
			return this;
		}

		Consideration c = decision.getLastConsideration();

		if (c == null) {
			logger.error("Adding string parameter to empty considerations.");
			return this;
		}

		// Add the parameter
		c.addIntParameter(paramType, value);

		return this;
	}

}
