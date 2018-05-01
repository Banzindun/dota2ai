package kocur.sniper.bot;

import cz.cuni.mff.kocur.brain.BaseBrain;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;

public class SniperBrain extends BaseBrain{

	public SniperBrain(ExtendedBotContext botContext) {
		super(botContext);
	}

	@Override
	public void createDecisions() {
		// This will create all the base decision sets that handle movement etc. 
		super.createDecisions();
		
		SniperAbilitiesDS abilitiesDS = new SniperAbilitiesDS();
		abilitiesDS.createDecisions();		
		this.addDecisionSet(abilitiesDS);
		
	}
	
}
