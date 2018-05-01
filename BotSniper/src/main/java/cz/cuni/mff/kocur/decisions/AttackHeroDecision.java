package cz.cuni.mff.kocur.decisions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.influence.InfluenceLayer;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.server.AgentCommands;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.Hero;
import kocur.lina.bot.LayeredBotContext;

public class AttackHeroDecision extends Decision{
	/**
	 * Logger registered for AttackCreepDecision.
	 */
	private static Logger logger = LogManager.getLogger(AttackHeroDecision.class);
	
	@Override
	public AgentCommand execute() {
		// Get the hero
		BaseEntity e = context.target.getEntity();
		
		super.execute();
		
		// Attack.
		return new AgentCommands.Attack(e.getEntid()); 			
	}
	
	@Override
	public void presetContext(ExtendedBotContext bc) {
		context.botContext = bc;
		context.setSource(bc.getHero());
	}

	@Override
	public void updateContext(ExtendedBotContext bc) {
		
	}
	
	public void updateContext(ExtendedBotContext bc, Hero h) {
		InfluenceLayer l = bc.getLayer(LayeredBotContext.FRIENDLY_THREATS);
		
		double[] coords = l.getEntityCoordinates(h);
		double influence = l.get((int) coords[0], (int) coords[1]);
		
		Target target = new Target(h);
		target.setValue(influence);
		
		this.context.setTarget(target);
	}

}
