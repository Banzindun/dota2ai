package cz.cuni.mff.kocur.decisions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.considerations.Consideration;
import cz.cuni.mff.kocur.utility.UtilityFunction;

public class DecisionBuilder {
	private static final Logger logger = LogManager.getLogger(DecisionBuilder.class.getName());
	
	public static DecisionBuilder build() {

		
		return new DecisionBuilder();
	}
	
	/**
	 * Decision reference.
	 */
	protected Decision decision = null;
	
	
	private DecisionBuilder() {
		
	}
	
	public DecisionBuilder setDecision(Decision d) {
		decision = d;
		return this;
	}
	
	public DecisionBuilder setName(String name) {
		if (decision == null) {
			logger.error("Decision not supplied.");
		} else {
			decision.setReadableName(name);
		}
		
		return this;
	}
	
	public DecisionBuilder setContext(DecisionContext context) {
		if (decision == null) {
			logger.error("Decision not supplied.");
		} else {
			decision.setContext(context);
		}
		
		return this;
	}
	
	public DecisionBuilder setEvaluator(DecisionScoreEvaluator eval) {
		if (decision == null) {
			logger.error("Decision not supplied.");
		} else {
			decision.setDSE(eval);
		}
		
		return this;
	}
	
	public DecisionBuilder addConsideration(Consideration c) {
		if (decision == null) {
			logger.error("Decision not supplied.");
		} else {
			decision.addConsideration(c);
			addParentToConsideration();
		}
		return this;
	}
	
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
	 * Adds parent to last consideration. The parent will be decision saved 
	 * in this builder.
	 */
	private void addParentToConsideration() {
		Consideration c = decision.getLastConsideration();
		
		if (c == null) {
			logger.error("No consideration specified yet.");
		} else {
			c.setParentDecision(decision);
		}
	}
	
	
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
	
	public DecisionBuilder setBonusFactor(double factor) {
		if (decision == null) {
			logger.error("Decision not yet supplied.");
			return this;
		}
		
		decision.getContext().setBonusFactor(factor);
		
		return this;
	}
	
	
	public Decision get() {
		return decision;
	}

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
