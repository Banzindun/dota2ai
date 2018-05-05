package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.interests.BaseInterest;

/**
 * This decision considers if BaseInterest is active.
 * 
 * 	if (bi.isActive())
 * 		return 1;
 * 	else return 0;
 * 
 * @author kocur
 *
 */
public class ConsiderActive extends Consideration{

	@Override
	public double score(DecisionContext context) {
		BaseInterest bi = (BaseInterest) context.getTarget().getLocation();
		
		if (bi.isActive())
			return 1;
		else return 0;
	}

}
