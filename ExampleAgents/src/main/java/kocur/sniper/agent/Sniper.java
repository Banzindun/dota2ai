package kocur.sniper.agent;

import java.io.IOException;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.agent.BaseAgentController;
import cz.cuni.mff.kocur.agent.BuySequence;
import cz.cuni.mff.kocur.agent.LevelUpSequence;
import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.base.Utils;
import cz.cuni.mff.kocur.console.CommandResponse;
import cz.cuni.mff.kocur.console.ConsoleCommand;
import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.influence.InfluenceLayer;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.server.AgentCommands;
import cz.cuni.mff.kocur.server.TimeManager;
import cz.cuni.mff.kocur.world.BaseViewer;
import cz.cuni.mff.kocur.world.ChatEvent;
import cz.cuni.mff.kocur.world.GridBase;
import cz.cuni.mff.kocur.world.Hero;
import kocur.lina.agent.LayeredAgentContext;

/**
 * Class that implements our Sniper agent. The agent is capable of playing any
 * lane. First thing it does is that it goes to pickup a rune. Then it farms the
 * lane, attacks buildings and heroes, while trying to keep away from the
 * threats and moving towards the farm.
 * 
 * @author kocur
 *
 */
public class Sniper extends BaseAgentController {
	/**
	 * Custom logger.
	 */
	private Logger logger = LogManager.getLogger(Sniper.class.getName());

	/**
	 * Queue that we use to levelup.
	 */
	private LevelUpSequence levelUpQueue = new LevelUpSequence();

	/**
	 * Our brain.
	 */
	SniperBrain brain;
	
	/**
	 * Health we had last time.
	 */
	private int lastHealth = 0;
	
	/**
	 * Map of places where we took damage.
	 */
	private HitMap hitMap = new HitMap();

	public Sniper() {
		super();
	}

	public void loadFromConfiguration() {
		// Load and set levelup sequence
		Integer[] abilityLevels = Utils.parseIntCoordiantes(configuration.getConfigValue("level_orders"));
		levelUpQueue.setSequence(abilityLevels);

		// Set up sequence in which I will be buying items
		BuySequence buySequence = new BuySequence(
				Utils.parseArrayOfStrings(configuration.getConfigValue("buy_orders")));
		agentContext.setBuySequence(buySequence);
	}

	@Override
	public void initialize() {
		// Override botContext with our own
		agentContext = new LayeredAgentContext(agentContext);

		// Initialize the context and the bot
		super.initialize();

		// Load some needed stuff from bot's configuration
		loadFromConfiguration();

		// Initialize the bot layers.
		((ExtendedAgentContext) agentContext).initializeLayers();

		// Create new intelligence and decisions for this bot
		brain = new SniperBrain((ExtendedAgentContext) agentContext);
		brain.createDecisions();
		((ExtendedAgentContext) agentContext).setBrain(brain);
	}

	@Override
	public int onLevelup() {
		if (hero == null)
			return -1;

		// Get our level
		int level = hero.getLevel();

		// Get the index from levelup queue
		int index = levelUpQueue.getAbilityIndex(level);

		// If index is negative, then there is nothing to levelup
		// so choose at random
		if (index == -1) {
			logger.warn("I have spent all the preset levels.");
			logger.warn("Choosing at random.");
			Random rnd = new Random();
			index = rnd.nextInt(4);
		}

		logger.info("Levelling up ability on index: " + index);

		return index;
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
	public synchronized AgentCommand update() {
		if (hero == null) {
			logger.warn("I am dead or nowhere to be found.");
			return null;
		}

		// Am I dead?
		if (hero.getHealth() <= 0) {
			return new AgentCommands.Noop();
		}
		
		// Check if we have took damage
		if (lastHealth != 0 && lastHealth != hero.getHealth()) {
			double[] coords = GridBase.getInstance().getEntityCoordinates(hero);
			hitMap.addLocation(coords);
		}
		
		lastHealth = hero.getHealth();

		// Update the layers
		((ExtendedAgentContext) agentContext).updateLayers();

		return think();
		//return null;
	}

	/**
	 * Function that evaluates the decision sets.
	 * @return Returns command we want to execute.
	 */
	private AgentCommand think() {
		// Catch block is for debugging (should be removed eventually).
		try {
			if (TimeManager.getLocalGameStartTime() > 0) {
				Decision decision = brain.think();
				Decision voidDecision = brain.voidThink();

				((LayeredAgentContext) agentContext).getGraphics().updateConsiderations();

				if (voidDecision != null) {
					voidDecision.execute();
				}

				if (decision != null) {
					AgentCommand cmd = decision.execute();
					return cmd;
				}
			}
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException ex) {
			ex.printStackTrace();
		}

		return null;
	}

	@Override
	public void configurationChanged() {
		// Called after the configuration changed.
		super.configurationChanged();
	}

	@Override
	public CommandResponse command(ConsoleCommand cmd) {
		CommandResponse res = new CommandResponse();

		String commandName = cmd.getField();

		switch (commandName) {
		case "save_layer":
			String layerName = cmd.getField();
			String fileName = cmd.getField();
			if (!layerName.equals("") && !fileName.equals("")) {
				InfluenceLayer l = ((ExtendedAgentContext) agentContext)
						.getLayer(LayeredAgentContext.getLayerNumber(layerName));
				if (l != null) {
					try {
						l.saveToFile(fileName);
						res.pass("Layers saved to file: " + fileName);
					} catch (IOException e) {
						logger.error("Unable to save layer to file.");
						res.fail("Unable to save layer to file:" + fileName);
						e.printStackTrace();
					}
				} else {
					res.fail("Unknown layer: " + layerName);
				}
			}
			break;
		default:
			res.fail("Unknown method.");
			break;
		}

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
	public void setHero(Hero h) {
		super.setHero(h); // Will set the hero

		brain.presetDecisions();
	}
	
	@Override
	public void destroy() {
		super.destroy();
		
		hitMap.save("hitmap.png");
		BaseViewer.saveGridMap("gridmap.png");
	}

}
