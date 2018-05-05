package kocur.sniper.agent;

import cz.cuni.mff.kocur.brain.BaseBrain;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;

/**
 * Sniper's brain. It creates it's own decision sets, that are added to the
 * BaseBrain's decision sets.
 * 
 * @author kocur
 *
 */
public class SniperBrain extends BaseBrain {

	public SniperBrain(ExtendedAgentContext botContext) {
		super(botContext);
	}

	@Override
	public void createDecisions() {
		// This will create all the base decision sets that handle movement etc.
		super.createDecisions();

		SniperAbilitiesDS abilitiesDS = new SniperAbilitiesDS();
		abilitiesDS.createDecisions();
		this.addDecisionSet(abilitiesDS);

		SniperGoalDS goalDS = new SniperGoalDS();
		goalDS.createDecisions();

		this.addVoidDecisionSet(goalDS);

	}

}
