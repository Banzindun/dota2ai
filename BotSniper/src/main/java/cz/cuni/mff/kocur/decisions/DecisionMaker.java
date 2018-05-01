package cz.cuni.mff.kocur.decisions;

import java.util.LinkedList;

public class DecisionMaker {
	
	public void scoreAllDecisions(LinkedList<Decision> decisions) {
		double cutoff = 0;
		
		for (Decision decision : decisions) {
			double bonus = decision.getContext().getBonusFactor();
			if (bonus < cutoff) {
				continue;
			}
			
			DecisionScoreEvaluator dse = decision.getDSE();
			double score = dse.score(decision, bonus, cutoff);
			
			decision.setScore(score);
			
			if (score > cutoff)
				cutoff = score;			
		}		
	}
	
	public Decision getBestDecision(LinkedList<Decision> decisions) {
		Decision best = decisions.stream().min(
				(d1, d2) -> Double.compare(d2.getScore(), d1.getScore())).get();
		
		return best;
	}
}
