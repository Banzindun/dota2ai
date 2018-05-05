package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.agent.BotFunction;

/**
 * A interface that represents a function that can be used for propagating the
 * influence.
 * 
 * @author kocur
 *
 * @param <T> Type of the parameter the function takes.
 */
public interface PropagationFunction<T> extends BotFunction<Integer, T> {
	public void propagate(InfluenceLayer l, T e);
}
