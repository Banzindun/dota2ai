package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.considerations.Consideration;

/**
 * Decision score evaluator evaluates decisions. It calculates their scores and
 * normalizes/clamps it.
 * 
 * @author kocur
 *
 */
public class DecisionScoreEvaluator {

	/**
	 * Takes decision, bonus value and minimum value of it's score. The decision is
	 * then evaluated. Final score is set to bonus. Evaluation then goes through all
	 * the considerations, uses their response curves to score them and clamps the
	 * result. Result then multiplies the final score.
	 * 
	 * 
	 * @param decision
	 *            Decision
	 * @param bonus
	 *            Bonus value. (multiplies the final score)
	 * @param min
	 *            Minimum value - if we are under the value, don't even bother.
	 * @return Returns score of the passed decision.
	 */
	public double score(Decision decision, double bonus, double min) {
		DecisionContext context = decision.getContext();

		double finalScore = bonus;

		for (Consideration consideration : decision.getConsiderations()) {
			if (0 > finalScore || finalScore < min)
				break;

			double score = consideration.computeResponseCurve(consideration.score(context));

			finalScore *= clamp(score);

			// usedConsiderations++;
		}

		// return compensate(finalScore, usedConsiderations);
		return finalScore;
	}

	/**
	 * Clamps whatever value is above 1 or below 0 to 1-0 range.
	 * 
	 * @param x
	 *            Value.
	 * @return Returns the clamped value.
	 */
	public double clamp(double x) {
		if (x > 1)
			return 1;
		else if (x < 0)
			return 0;

		return x;
	}

	/**
	 * Compensates for using more considerations. Because by multiplying the
	 * decisions with more considerations have a slight disadvantage.
	 * 
	 * @param score
	 *            Score to be compensated.
	 * @param usedConsiderations
	 *            Number of used considerations.
	 * @return Returns compensated score.
	 */
	public double compensate(double score, int usedConsiderations) {
		if (usedConsiderations == 0)
			return score;

		double modificationFactor = 1 - (1 / usedConsiderations);
		double makeUpValue = (1 - score) * modificationFactor;
		return score + (makeUpValue * score);
	}

}
