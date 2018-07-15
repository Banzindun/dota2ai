package kocur.emptyAgent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.agent.BaseAgentController;
import cz.cuni.mff.kocur.console.CommandResponse;
import cz.cuni.mff.kocur.console.ConsoleCommand;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.server.AgentCommands;
import cz.cuni.mff.kocur.world.ChatEvent;
import cz.cuni.mff.kocur.world.Hero;

/**
 * This class creates an empty agent, that can be used by players to control a
 * hero. (We create an agent, that do not send any commands)
 * 
 * @author kocur
 *
 */
public class EmptyAgent extends BaseAgentController {
	/**
	 * Custom logger for our class.
	 */
	protected Logger logger = LogManager.getLogger(EmptyAgent.class);

	public EmptyAgent() {
		super();
	}

	@Override
	public void initialize() {
		// Initialize the context and the bot
		super.initialize();
	}

	@Override
	public int onLevelup() {
		logger.info("levelup");
		return -1;
	}

	@Override
	public void onChat(ChatEvent e) {
		logger.info("onChat");
	}

	@Override
	public void reset() {
		logger.info("Resetting");
	}

	@Override
	public AgentCommand update() {

		return new AgentCommands.Noop();
	}

	@Override
	public CommandResponse command(ConsoleCommand cmd) {
		CommandResponse res = new CommandResponse();
		return res;
	}

	@Override
	public String getHelp() {
		return "";
	}

	@Override
	public void setHero(Hero h) {
		super.setHero(h); // Will set the hero
	}

	@Override
	public void configurationChanged() {

	}

}
