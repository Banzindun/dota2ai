package cz.cuni.mff.kocur.brain;

import java.util.Collection;
import java.util.LinkedList;

import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.decisions.DecisionMaker;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;

public class DecisionSet{
	protected LinkedList<Decision> decisions = new LinkedList<>();
	
	protected Decision bestDecision = null;
	
	public void add(Decision d) {
		decisions.add(d);
	}
	
	/**
	 * Removes decision d from stored decisions.
	 * @param d 
	 */
	public void remove(Decision d) {
		decisions.remove(d);
	}
	
	
	public void remove(int i) {
		decisions.remove(i);
	}
	
	public LinkedList<Decision> getScoredDecisions(){
		return decisions; // Should be scored by scoreAll beforehand
	}
	
	public void scoreAll(DecisionMaker dm) {
		dm.scoreAllDecisions(decisions);
		bestDecision = dm.getBestDecision(decisions);
	}

	public void updateContext(ExtendedBotContext botContext) {
		for (Decision d : decisions) {
			d.updateContext(botContext);
		}
		
	}

	public Decision getBestDecision() {
		return bestDecision;
	}

	public void presetContext(ExtendedBotContext botContext) {
		for (Decision d : decisions) {
			d.presetContext(botContext);
		}		
	}

	public LinkedList<Decision> getDecisions() {
		return decisions;
	}
	
	
}
