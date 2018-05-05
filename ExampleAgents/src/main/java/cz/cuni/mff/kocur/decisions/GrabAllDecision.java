package cz.cuni.mff.kocur.decisions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.server.AgentCommands;

/**
 * Decision to grab all items.
 * 
 * @author kocur
 *
 */
public class GrabAllDecision extends Decision {
	private static final Logger logger = LogManager.getLogger(GrabAllDecision.class);

	@Override
	public AgentCommand execute() {
		super.execute();

		logger.info("Grabbing all items.");

		return new AgentCommands.GrabAll();
	};

	@Override
	public void updateContext(ExtendedAgentContext bc) {

	}

	@Override
	public void presetContext(ExtendedAgentContext bc) {
		context.setBotContext(bc);
		context.setSource(bc.getHero());
		context.setTarget(new Target(bc.getMyBaseShop()));
	}

}
