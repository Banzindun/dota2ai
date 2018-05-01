package kocur.sniper.bot;

import org.apache.logging.log4j.LogManager;

import cz.cuni.mff.kocur.brain.BaseBrain;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;

public class SniperIntelligence extends BaseBrain{
	
	public SniperIntelligence(ExtendedBotContext botContext) {
		super(botContext);
		logger = LogManager.getLogger(SniperIntelligence.class.getName());
	}

	@Override
	public void createDecisions() {
		super.createDecisions();
	}
}
