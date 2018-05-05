package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.agent.AgentContext;
import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.world.Hero;

/**
 * This class extends a WavePropagationFunction (WPF). 
 * It creates fields for hero and it's context, that might be useful for some WPF implementations.
 * @author kocur
 *
 * @param <T> Type of entity we will be using.
 */
public class WavePropagationWithContext <T extends Location> extends WavePropagationFunction<T>{
	/**
	 * Reference to context of this agent.
	 */
	protected AgentContext context = null;
	

	/**
	 * Reference to agent's hero.
	 */
	protected Hero hero = null;
	
	/**
	 * Constructs the WP. Stores the context. 
	 * @param context Agent's context.
	 */
	public WavePropagationWithContext(AgentContext context) {
		this.context = context;
	}
	
	@Override
	public void propagate(InfluenceLayer l, T e) {
		super.propagate(l, e);
	}	
	
	/**
	 * Updates the hero.
	 */
	public void updateHero() {
		
		hero = context.getHero();
	}

	/**
	 * Sets the context of this propagation function.
	 * @param context New context.
	 */
	public void setContext(AgentContext context) {
		this.context = context;
	}

	
	
}
