package cz.cuni.mff.kocur.decisions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.considerations.Consideration;

public class DecisionScoreEvaluator {
	/**
	 * Logger registered for DecisionScoreEvaluator.
	 */
	private static final Logger logger = LogManager.getLogger(DecisionScoreEvaluator.class.getName());
	
	
	public double score(Decision c, double bonus, double min) {
		DecisionContext context = c.getContext();
		
		double finalScore = bonus;
		//int usedConsiderations = 0;
				
		for (Consideration consideration : c.getConsiderations()) {
			if (0 > finalScore || finalScore < min)
				break;
			
			double score = consideration.computeResponseCurve(
					consideration.score(context)); 

			finalScore *= clamp(score);
			
			//usedConsiderations++;
		}
		
		//return compensate(finalScore, usedConsiderations);
		return finalScore;
	}	
	
	
	public double clamp(double x) {
		if (x > 1) return 1;
		else if (x < 0) return 0;
		
		return x;
	}
	
	/**
	 * Compensates for using more considerations. Because by multiplying the decisions with more considerations 
	 * have a slight disadvantage. 
	 * @param score
	 * @param usedConsiderations
	 * @return
	 */
	public double compensate(double score, int usedConsiderations) {
		if (usedConsiderations == 0)
			return score;
		
		double modificationFactor = 1-(1/usedConsiderations);
		double makeUpValue = (1-score)*modificationFactor;
		return score + (makeUpValue*score);	
	}


}
