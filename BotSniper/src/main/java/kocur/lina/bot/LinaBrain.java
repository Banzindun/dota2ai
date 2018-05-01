package kocur.lina.bot;

import org.apache.logging.log4j.LogManager;

import cz.cuni.mff.kocur.brain.BaseBrain;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import kocur.sniper.bot.SniperAbilitiesDS;

public class LinaBrain extends BaseBrain{
		
	public LinaBrain(ExtendedBotContext botContext) {
		super(botContext);
		logger = LogManager.getLogger(LinaBrain.class.getName()); 
		
	}
	
	@Override
	public void createDecisions() {
		// This will create all the base decision sets that handle movement etc. 
		super.createDecisions();
		
		LinaAbilitiesDS abilitiesDS = new LinaAbilitiesDS();
		abilitiesDS.createDecisions();
		
		this.addDecisionSet(abilitiesDS);
		
		
		// Here we must define our skill/ability sets
	}
	
}
