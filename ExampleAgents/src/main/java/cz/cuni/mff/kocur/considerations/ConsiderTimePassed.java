package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.server.TimeManager;

/**
 * Consideration that considers time, that has passed from the last time the
 * decision was executed.
 * 
 * return normalize(currentTime, lastTime, lastTime + PARAM_RANGE_MAX);
 * 
 * @author kocur
 *
 */
public class ConsiderTimePassed extends Consideration {

	public ConsiderTimePassed() {
		super();
		readableName = "ConsiderTimePassed";

	}

	@Override
	public double score(DecisionContext context) {
		double max = getDoubleParameter(PARAM_RANGE_MAX);

		float lastTime = this.parentDecision.getLastTimeExecuted();

		float current = TimeManager.getLocalTime();
		// float elapsed = current - lastTime;

		return normalize(current, lastTime, lastTime + max);
	}
}
