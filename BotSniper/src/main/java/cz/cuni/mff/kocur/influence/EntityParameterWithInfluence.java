package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.bot.BotFunction;
import cz.cuni.mff.kocur.bot.EntityParameter;
import cz.cuni.mff.kocur.interests.Team;

public class EntityParameterWithInfluence extends EntityParameter {

	private BotFunction<?, ?> direFunction = null;
	
	private BotFunction<?, ?> radiantFunction = null;
	
	private BotFunction<?, ?> neutralFunction = null;
	
	public void setFunction(BotFunction<?, ?> f) {
		radiantFunction = f;
	}
	
	public void setFunction(BotFunction<?, ?> f, int team) {
		if (team == Team.DIRE)
			direFunction = f;
		else if (team == Team.RADIANT){
			radiantFunction = f;
		} else { 
			neutralFunction = f;
		}
	}
	
	public BotFunction<?, ?> getFunction(int team){
		if (team == Team.DIRE)
			return direFunction;
		else if (team == Team.RADIANT){
			return radiantFunction;
		}
		
		return neutralFunction;
	}
	
	public BotFunction<?, ?> getFunction(){
		return radiantFunction;
	}
	
	
}
