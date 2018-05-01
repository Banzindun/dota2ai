package cz.cuni.mff.kocur.bot;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.configuration.HeroConfiguration;
import cz.cuni.mff.kocur.configuration.HeroConfiguration.TYPE;
import cz.cuni.mff.kocur.dota2AIFramework.Setup;
import cz.cuni.mff.kocur.configuration.BotConfiguration;
import cz.cuni.mff.kocur.configuration.FrameworkConfiguration;
import cz.cuni.mff.kocur.exceptions.LoadingError;
import cz.cuni.mff.kocur.interests.Team;
import cz.cuni.mff.kocur.logging.QueueAppender;

/**
 * The purpose of this class is to load agents from framework configuration. The agent configurations are stored inside framework configuration, after they are loaded.
 * @author Banzindun
 *
 */
public class BotLoader {
	/**
	 * Logger registered for this class.
	 */
	private static Logger logger = LogManager.getLogger(BotLoader.class.getName());
	
	/**
	 * Global configuration. Serves for getting the bot configurations.
	 */
	private static FrameworkConfiguration cfg = FrameworkConfiguration.getInstance();

	/**
	 * Loads bots from configurations that are supplied using the framework (and stored in global configuration).
	 * @return Returns array of loaded bots. 
	 * @throws LoadingError If some of the bots could not be loaded.
	 */
	public static ArrayList<AgentController> load(int team) throws LoadingError {
		HeroConfiguration[] cfgs = cfg.getHeroConfigurations(team);
		
		logger.info("Loading " + cfgs.length+ " bots.");
		
		ArrayList<AgentController> nonBotHeroes = new ArrayList<>();
		ArrayList<String> aisNames = new ArrayList<>();
		
		for(HeroConfiguration c : cfgs) {
			// Load only players and ais
			if (c.getType() != HeroConfiguration.TYPE.BOT) {
				AgentController hero = createHero(c);
				if (hero != null) nonBotHeroes.add(hero);
				
				if (c.getType() == TYPE.AI)
					aisNames.add(c.getConfigValue("champion"));
			}
			
		}
		
		// Override the scripts, that ais will need
		Setup.overrideAisScripts(aisNames);
		
		return nonBotHeroes;
	}
	
	/**
	 * Tries to load one bot form supplied configuration.
	 * @param c BotConfiguration that represents the bot.
	 * @return Returns the loaded bot.
	 * @throws LoadingError If the class couldn't be loaded.
	 */
	public static AgentController createHero(HeroConfiguration c) throws LoadingError {
		String loggerClassPath = c.getConfigValue("logger_classpath");
		registerAppender(loggerClassPath);
		
		// Load the bot
		BaseAgentController b = loadAgentController(c);
		
		setupController(b, c);

		return b;
			
	}
	
	/**
	 * Setups the agent controller.
	 * @param bc Agent's controller.
	 * @param c Agent's configuration.
	 */
	private static void setupController(AgentController bc, HeroConfiguration c) {
		bc.setConfiguration(c);
		
		// Take team from configuration
		int team = Team.parseTeam(c.getConfigValue("team"));
		
		TeamContext tc = ControllersManager.getInstance().getTeamContext(team);
		bc.setTeamContext(tc);
		
		// Sets this bots configuration
		bc.getContext().setHeroName(c.getConfigValue("champion"));
	}
	
	/**
	 * Loads an agent from configuration and creates a new BaseAgentController instance out of it.
	 * @param c Agent's configuration.
	 * @return Returns new agent's controller.
	 * @throws LoadingError Thrown if something went wrong during agent loading. 
	 */
	private static BaseAgentController loadAgentController(HeroConfiguration c) throws LoadingError{
		// Get class name - it must exist
		String cl = c.getConfigValue("class");
		if (cl == null) throw new LoadingError("No class in configuration: " + c.getName());
		
		// Load the class and construct the agent by passing agent's configuration to it.
		try {
	    	@SuppressWarnings("unchecked")
			final Class<BaseAgentController> heroClass = (Class<BaseAgentController>) Class.forName( cl );
	    	
			return heroClass.newInstance();		
		} catch (Exception e) {
			logger.fatal("Could not create bot from supplied classpath: " + cl, e);
			throw new LoadingError("Could not create bot from supplied classpath: " + cl, e);
		}	
	}
	
	/**
	 * Adds QueueAppender to supplied class.
	 * @param className Class path we want to register the appender for.
	 */
	private static void registerAppender(String className) {
	   QueueAppender.registerAppender(className);
	}
	
	/**
	 * Returns true if the bot can be loaded from supplied configuration.
	 * This can be used for testing the configuration.
	 * @param c BotConfiguration of the loaded bot.
	 * @return Returns true if the bot can be loaded.
	 */
	public static boolean canBeLoaded(HeroConfiguration c) {
		// If we work with bot, there is no class to load
		if (c.getType() == TYPE.BOT)
			return true;
		
		try {
			return loadAgentController(c) != null;
		} catch (LoadingError e) {
			logger.warn("Bot could not be loaded.");
		}
		return false;
	}
	
	
	
	
}
