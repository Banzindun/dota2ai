package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.world.BaseEntity;

/**
 * Consideration that considers source's mana.
 * 
 * return normalize(h.getMana(), 0, h.getMaxMana());
 * 
 * @author kocur
 *
 */
public class ConsiderSourceMana extends Consideration {

	public ConsiderSourceMana() {
		super();
		readableName = "ConsiderMana";
	}
	
	@Override
	public double score(DecisionContext context) {
		BaseEntity h = (BaseEntity) context.getSource();
		
		return normalize(h.getMana(), 0, h.getMaxMana());
	}

}
