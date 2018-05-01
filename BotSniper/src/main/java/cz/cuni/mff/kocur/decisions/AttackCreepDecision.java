package cz.cuni.mff.kocur.decisions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.influence.InfluenceLayer;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.server.AgentCommands;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.Creep;
import kocur.lina.bot.LayeredBotContext;

public class AttackCreepDecision extends Decision{
	/**
	 * Logger registered for AttackCreepDecision.
	 */
	private static Logger logger = LogManager.getLogger(AttackCreepDecision.class);
	
	@Override
	public AgentCommand execute() {
		// Get the creep
		BaseEntity e = context.target.getEntity();
		
		super.execute();
		
		// Attack.
		return new AgentCommands.Attack(e.getEntid()); 			
	}
	
	public void updateContext(ExtendedBotContext bc, Creep c) {
		
		InfluenceLayer l = bc.getLayer(LayeredBotContext.ENEMY_THREATS);
		
		double[] coords = l.getEntityCoordinates(c);
		double influence = l.get((int) coords[0], (int) coords[1]);
		
		Target target = new Target(c);
		target.setValue(influence);
		
		this.context.setTarget(target);
	}

	@Override
	public void presetContext(ExtendedBotContext bc) {
		context.botContext = bc;
		context.source = bc.getHero();
	}

	@Override
	public void updateContext(ExtendedBotContext bc) {
	
	}

}
