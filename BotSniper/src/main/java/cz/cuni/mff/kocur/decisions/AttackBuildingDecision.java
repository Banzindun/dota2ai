package cz.cuni.mff.kocur.decisions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.influence.InfluenceLayer;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.server.AgentCommands;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.Building;
import kocur.lina.bot.LayeredBotContext;

public class AttackBuildingDecision extends Decision{
	/**
	 * Logger registered for AttackCreepDecision.
	 */
	private static final Logger logger = LogManager.getLogger(AttackBuildingDecision.class);
	
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

	public void updateContext(ExtendedBotContext botContext, Building b) {
		InfluenceLayer l = botContext.getLayer(LayeredBotContext.COMBINED_THREATS);
		
		double[] coords = l.getEntityCoordinates(b);
		double influence = l.get((int) coords[0], (int) coords[1]);
		
		Target target = new Target(b);
		target.setValue(influence);
		
		this.context.setTarget(target);
	}

}
