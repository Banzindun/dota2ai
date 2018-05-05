package cz.cuni.mff.kocur.decisions;

import java.util.LinkedList;

/**
 * DecisionMaker makes all the decisions. It ranks them by calculating their
 * score using DecisionScoreEvaluator. Then it sorts them and allows user to get
 * the best decision.
 * 
 * @author kocur
 *
 */
public class DecisionMaker {

	/**
	 * Scores all the passed decisions.
	 * 
	 * @param decisions
	 *            Decisions to be scored.
	 */
	public void scoreAllDecisions(LinkedList<Decision> decisions) {
		double cutoff = 0;

		// Go through the decisions
		for (Decision decision : decisions) {
			// Get their bonus factor. If we already have better decision, then continue.
			double bonus = decision.getContext().getBonusFactor();
			if (bonus < cutoff) {
				continue;
			}

			// Get dse and calculate the score.
			DecisionScoreEvaluator dse = decision.getDSE();
			double score = dse.score(decision, bonus, cutoff);

			decision.setScore(score);

			// If the score is good, set a new cutoff.
			if (score > cutoff)
				cutoff = score;
		}
	}

	/**
	 * 
	 * @param decisions
	 *            Decisions with scores.
	 * @return Returns best decision from the passed list of decisions.
	 */
	public Decision getBestDecision(LinkedList<Decision> decisions) {
		Decision best = decisions.stream().min((d1, d2) -> Double.compare(d2.getScore(), d1.getScore())).get();

		return best;
	}
}
