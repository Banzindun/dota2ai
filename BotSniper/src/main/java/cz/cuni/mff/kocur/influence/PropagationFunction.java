package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.bot.BotFunction;

public interface PropagationFunction<T> extends BotFunction<Integer, T>{
	public void propagate(InfluenceLayer l, T e);	
}
