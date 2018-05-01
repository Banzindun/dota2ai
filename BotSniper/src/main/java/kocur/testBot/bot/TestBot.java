package kocur.testBot.bot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.bot.BaseAgentController;
import cz.cuni.mff.kocur.console.CommandResponse;
import cz.cuni.mff.kocur.console.ConsoleCommand;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.server.AgentCommands;
import cz.cuni.mff.kocur.world.ChatEvent;
import cz.cuni.mff.kocur.world.Hero;


public class TestBot extends BaseAgentController {
	/**
	 * Custom logger for lina class.
	 */
	protected Logger logger = LogManager.getLogger(TestBot.class.getName());

	public TestBot() {
		super();
	}

	@Override
	public void initialize() {
		// Override botContext with my own
		botContext = new TestBotContext(this);

		// Initialize the context and the bot
		super.initialize();
	}

	@Override
	public int getLevelUpIndex() {
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
		IndentationStringBuilder builder = new IndentationStringBuilder();
		builder.appendLines(
				"save_layer [layer_name] [file_name] -> saves layer with given name to file with given name located in working dir");
		return builder.toString();
	}

	@Override
	public String getControllableName() {
		// Bots are controlled through BotControllerWrapper, this method does nothing
		return null;
	}

	@Override
	public void setHero(Hero h) {
		super.setHero(h); // Will set the hero
	}

	@Override
	public void configurationChanged() {

	}

}
