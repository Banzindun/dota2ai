package cz.cuni.mff.kocur.exampleAgent;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.agent.BaseAgentController;
import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.console.CommandResponse;
import cz.cuni.mff.kocur.console.ConsoleCommand;
import cz.cuni.mff.kocur.interests.InterestsBase;
import cz.cuni.mff.kocur.interests.Jungle;
import cz.cuni.mff.kocur.interests.Rune;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.world.ChatEvent;
import cz.cuni.mff.kocur.world.Item;
import cz.cuni.mff.kocur.world.ItemsBase;

/**
 * This is a example agent implementation, that is explained inside the
 * Programmer's documentation. This class illustrates the framework's basic
 * functionality.
 * 
 * The goal is to use every package, that is explained in the programmer's
 * documentation and showcase their functions (for example events, logging).
 * 
 * 
 * @author kocur
 *
 */
public class ExampleAgentController extends BaseAgentController {

	/**
	 * Logger, that is registered to this class.
	 */
	private static final Logger logger = LogManager.getLogger(ExampleAgentController.class);

	/**
	 * Item we want to buy, if we have the money
	 */
	private String itemToBuy = null;
	
	/**
	 * Location, where our agent will be headed (rune or tower).
	 */
	private Location destination = null;
	
	/**
	 * Is the destination, that we are headed to, a tower? 
	 */
	private boolean isTower = false;

	@Override
	public void initialize() {
		super.initialize();
		// Lets load name of the item, that we want to buy from the configuration
		itemToBuy = configuration.getConfigValue("buy");
	}

	@Override
	public AgentCommand update() {
		// Check if we are buying an item, if so buy it if we have enough money
		if (itemToBuy != null) {
			Item toBuy = ItemsBase.getItem(itemToBuy);
			if (toBuy.affordable(this.hero.getGold())) {
				itemToBuy = null;
				return buy(toBuy.getName());
			}
		}
		
		// We have not bought anything .. let's move towards the goal.
		// If it is a tower we move towards it.
		// If it is a rune, we move towards it and if we are close and the
		// rune is active, then we pick it up.
		if (destination != null) {
			if (isTower)
				return moveTo(destination);
			else 
				return pickupRune((Rune) destination);
		}
		
		return null;
	}

	@Override
	public CommandResponse command(ConsoleCommand cmd) {
		// This method is called, after a command is issued in the console,
		// that is meant for this agent.

		boolean ok = false;
		String msg = "Unknown command.";
		
		isTower = false; 

		// Let's say we want to allow user to command our agent to go to top or bot
		// rune.
		String field = cmd.getField();
		if (cmd.size() == 2 && field.equals("go")) {
			String location = cmd.getField();
			
			// Check where to move
			switch (location) {
			case "toprune":
				destination = this.agentContext.getMyJungle()
					.getTopBounty();
				msg = "Moving to top rune.";
				ok = true;
				break;
			case "botrune":
				destination = this.agentContext.getMyJungle()
					.getBotBounty();
				msg = "Moving to bot rune.";
				ok = true;
				break;
			case "lane":
				destination = this.agentContext.getMyLane()
					.getLastStandingTower();
				isTower = true; 
				msg = "Moving to lane.";
				ok = true;
				break;
			default:
				break;
			}
		}

		// Log if we were successful.
		if (ok)
			logger.info(msg);

		// Create the response.
		return new CommandResponse(ok, msg);
	}

	@Override
	public String getHelp() {
		// This might be called from console using: help [agent_name].
		// We should display commands that we have implemented inside command(..)
		return "go [toprune|botrune|lane] -> will tell the agent to go to the specified rune or his lane";
	}

	@Override
	public int onLevelup() {
		// This method is called on hero levelup. It sould return an index of ability,
		// that should be leveled up.

		// Let's just select random ability from 0 to 4, that is not fully leveled
		Random rnd = new Random();
		int index = rnd.nextInt(5);

		// We return the index if the ability can be leveled up
		if (this.hero.getAbility(index).getLevel() < 4) {
			logger.info("Leveling up ability on index: " + index);
			return index;
		}

		// Postpone the selection
		return -1;
	}

	@Override
	public void onChat(ChatEvent e) {
		// This is called after someone typed something in chat.
		// Lets use chat as a console, and handle the event as a command
		CommandResponse res = command(new ConsoleCommand(e.getText()));

		// And lets log the result
		if (res.passed()) {
			logger.info("Passed chat command: " + res.toString());
		} else {
			logger.info("Failed chat command: " + res.toString());
		}
	}

	@Override
	public void configurationChanged() {
		// Called after the configuration changed.
		super.configurationChanged();

		// Super will update everything that is required, but fields that we created
		// We work only with "buy" field, so we must update our itemToBuy
		itemToBuy = configuration.getConfigValue("buy");
	}

}
