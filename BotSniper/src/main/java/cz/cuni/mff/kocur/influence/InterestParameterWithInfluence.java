package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.bot.BotFunction;
import cz.cuni.mff.kocur.bot.InterestParameter;

public class InterestParameterWithInfluence extends InterestParameter{

	private BotFunction<?, ?> bf = null;
	
	public void setFunction(BotFunction<?, ?> f) {
		bf = f;
	}
	
	public BotFunction<?, ?> getFunction(){
		return bf;
	}	
	
}
