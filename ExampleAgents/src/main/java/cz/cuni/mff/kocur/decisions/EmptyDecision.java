package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.server.AgentCommands;

/**
 * This is an empty decision. This serves just as a way to return something,
 * when doing nothing. For example when the best decision you obtained has 0
 * score.
 * 
 * @author kocur
 *
 */
public class EmptyDecision extends Decision {
	@Override
	public AgentCommand execute() {
		return new AgentCommands.Noop();
	}

	@Override
	public void updateContext(ExtendedAgentContext bc) {
	}

	@Override
	public void presetContext(ExtendedAgentContext bc) {
	}
}
