package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.server.AgentCommands;
import cz.cuni.mff.kocur.world.BaseEntity;

/**
 * Decision that attacks a target, if executed.
 * 
 * @author kocur
 *
 */
public class AttackTargetDecision extends Decision {

	@Override
	public AgentCommand execute() {
		// Get the target
		BaseEntity e = context.target.getEntity();

		super.execute();

		// Attack.
		return new AgentCommands.Attack(e.getEntid());
	}

	@Override
	public void updateContext(ExtendedAgentContext bc) {

	}

	@Override
	public void presetContext(ExtendedAgentContext bc) {
		context.agentContext = bc;
		context.setSource(bc.getHero());

	}

}
