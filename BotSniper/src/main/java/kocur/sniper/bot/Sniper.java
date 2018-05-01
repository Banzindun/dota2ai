package kocur.sniper.bot;

import java.io.IOException;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.base.Utils;
import cz.cuni.mff.kocur.bot.BaseAgentController;
import cz.cuni.mff.kocur.bot.BuySequence;
import cz.cuni.mff.kocur.bot.LevelUpSequence;
import cz.cuni.mff.kocur.console.CommandResponse;
import cz.cuni.mff.kocur.console.ConsoleCommand;
import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.influence.InfluenceLayer;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.server.AgentCommands;
import cz.cuni.mff.kocur.server.TimeManager;
import cz.cuni.mff.kocur.world.ChatEvent;
import cz.cuni.mff.kocur.world.Hero;
import kocur.lina.bot.LayeredBotContext;



public class Sniper extends BaseAgentController {
	/**
	 * Custom logger.
	 */
    private Logger logger = LogManager.getLogger(Sniper.class.getName());
	
	private LevelUpSequence levelUpQueue = new LevelUpSequence();
	
	SniperBrain brain;
	
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
		botContext.setBuySequence(buySequence);
	}

	@Override
	public void initialize() {
		// Override botContext with our own
		botContext = new LayeredBotContext(this);
		
		// Initialize the context and the bot
		super.initialize();
		
		// Load some needed stuff from bot's configuration
		loadFromConfiguration();
		
		// Initialize the bot layers.
		((ExtendedBotContext) botContext).initializeLayers();
		
		// Create new intelligence and decisions for this bot
		brain = new SniperBrain((ExtendedBotContext) botContext);
		brain.createDecisions();
		((ExtendedBotContext) botContext).setBrain(brain);
	}
	    
    @Override
    public int getLevelUpIndex() {
    	if (hero == null)
			return -1;

		int level = hero.getLevel();

		int index = levelUpQueue.getAbilityIndex(level);
		
		if (index == -1) {
			logger.warn("I have spent all preset levels.");
			logger.warn("Choosing at random.");
			Random rnd = new Random();
			index = rnd.nextInt(4);
		}
	
	
		logger.info("Levelling up ability on index: " + index);

		return index;
    }

    @Override
    public void onChat( ChatEvent e ) {
    	logger.info("onChat");
    }

    @Override
    public void reset() {
    	logger.info( "Resetting" );
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
    	
    	// Update the layers
    	((ExtendedBotContext) botContext).updateLayers();
    	    	
		return think();
    }
    
	private AgentCommand think() {
		// Catch block is for debugging and should be removed eventually.
		try {
			if (TimeManager.getLocalGameStartTime() > 0) {
				Decision decision = brain.think();
				Decision voidDecision = brain.voidThink();
				
				((LayeredBotContext) botContext).getGraphics().updateConsiderations();
				
				if (voidDecision != null) {
					voidDecision.execute();
				}
				
				if (decision != null) {
					AgentCommand cmd = decision.execute();
					return cmd;
				}
			}
		} catch(NullPointerException ex) {
			ex.printStackTrace();
		} catch(ArrayIndexOutOfBoundsException ex) {
			ex.printStackTrace();
		}
		
		return null;		
	}

	@Override
	public void configurationChanged() {
				
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
				InfluenceLayer l = ((ExtendedBotContext) botContext).getLayer(LayeredBotContext.getLayerNumber(layerName));
				if (l != null) {
					try {
						l.saveToFile(fileName);
					} catch (IOException e) {
						logger.error("Unable to save layer to file.");
						e.printStackTrace();
					}
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
		builder.appendLines("save_layer [layer_name] [file_name] -> saves layer with given name to file with given name located in working dir");
		return builder.toString();
	}

	@Override
	public String getControllableName() {
		return null;
	}	
	
	@Override
	public void setHero(Hero h) {
		super.setHero(h); // Will set the hero
		
		brain.presetDecisions();

		/*
		 * if (graphics == null) graphics = new BotTab((LinaContext) botContext,
		 * "Lina");
		 */
	}

}
