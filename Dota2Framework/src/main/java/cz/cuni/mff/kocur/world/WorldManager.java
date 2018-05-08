package cz.cuni.mff.kocur.world;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.agent.ControllersManager;
import cz.cuni.mff.kocur.configuration.ConfigurationChangeListener;
import cz.cuni.mff.kocur.configuration.FrameworkConfiguration;
import cz.cuni.mff.kocur.console.CommandResponse;
import cz.cuni.mff.kocur.console.ConsoleCommand;
import cz.cuni.mff.kocur.console.ConsoleHelp;
import cz.cuni.mff.kocur.console.ConsoleManager;
import cz.cuni.mff.kocur.console.Controllable;
import cz.cuni.mff.kocur.interests.Team;
import cz.cuni.mff.kocur.server.MapperWrapper;
import cz.cuni.mff.kocur.server.RequestArguments;
import cz.cuni.mff.kocur.server.RequestHandler.CODE;
import cz.cuni.mff.kocur.server.TimeManager;
import cz.cuni.mff.kocur.streaming.InformationDrop;

/**
 * This class manages the world
 * 
 * World update will take these steps: First the client will use a POST message
 * to send some data/ The data will be in JSON and they will be addressed to
 * some agent by his name. Manager should then take the data and parse the
 * objects that correspond to the World objects. Then we update the world
 * information by updating/inserting the objects using their ids.
 * 
 * Manager should allow agents to query for informations that are close to them.
 * 
 * 
 * @author Banzindun
 *
 */
public class WorldManager implements Controllable, ConfigurationChangeListener {
	/**
	 * WorldManager instance.
	 */
	private static WorldManager instance = null;

	/**
	 * 
	 * @return Returns WorldManager instance. The instance should be only one
	 *         object, during the whole execution.
	 */
	public static WorldManager getInstance() {
		if (instance == null)
			instance = new WorldManager();
		return instance;
	}

	/**
	 * Static reference to current world.
	 */
	private static World world = new World();

	/**
	 * Our time manager.
	 */
	private static TimeManager timeManager = new TimeManager();

	/**
	 * 
	 * @return Returns the world stored inside this class.
	 */
	public static World getWorld() {
		return world;
	}

	/**
	 * Instance of framework configuration.
	 */
	private FrameworkConfiguration cfg = FrameworkConfiguration.getInstance();

	/**
	 * Private constructor. Registers this to talkers.
	 */
	private WorldManager() {
		logger.info("Creating the WorldManager.");

		// Register to console manager.
		ConsoleManager.register(this);

		setupEntities();

		// Turn on the world source
		worldSource = new WorldSource();
		worldSource.startFlowing();
	}

	/**
	 * Logger for this class.
	 */
	private Logger logger = LogManager.getLogger(WorldManager.class.getName());

	/**
	 * World source. This object will be sending world data to catchers.
	 */
	private WorldSource worldSource = null;

	/**
	 * Updates the world information.
	 * 
	 * @param arg
	 *            Request arguments.
	 * @return Returns true, if the world was updated.
	 */
	public boolean update(RequestArguments arg) {
		try {
			WorldUpdate u = MapperWrapper.readValue(arg.getMessage(), WorldUpdate.class);

			// Update the world
			world.update(u);

			// Update the bot's contextual information
			updateAgentContext(arg.getAgentName(), u);
		} catch (IOException e) {
			logger.error("Unable to update the world information from supplied json.", e, arg.getAgentName(),
					arg.getMessage());
			e.printStackTrace();
			return false;
		}

		// Create separate thread (so that we won't spend more time here), that will
		// update informations to graphics window
		worldSource.flow(new InformationDrop(InformationDrop.WORLD, world));
		return true;
	}

	/**
	 * Updates the agent's context.
	 * 
	 * @param name
	 *            Name of the agent.
	 * @param u
	 *            WorldUpdate, from which we should update the context.
	 */
	private void updateAgentContext(String name, WorldUpdate u) {
		ControllersManager.getInstance().updateAgentContext(name, u);
	}

	/**
	 * Does a big world update. This one comes once a second from both teams.
	 * 
	 * @param arg
	 *            Request arguments - like the body of the request, its url ..
	 * @return Returns true if the update went ok.
	 */
	public synchronized boolean updateBig(RequestArguments arg) {
		try {
			WorldUpdate u = MapperWrapper.readValue(arg.getMessage(), WorldUpdate.class);

			if (u.getTime() != -1) {
				timeManager.tick(u.getTime());
			}

			// Update the world
			world.update(u);

			// Update the team context.
			updateTeamContext(arg.getMethod(), u);

			// Do the cleanup
			world.cleanup();
		} catch (IOException e) {
			logger.error("Unable to perform big update from supplied json.", e);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		worldSource.flow(new InformationDrop(InformationDrop.WORLD, world));
		return true;
	}

	/**
	 * Updates team context.
	 * 
	 * @param method
	 *            Code of the method (UPDATEDIRE, UPDATERADIANT)
	 * @param u
	 *            WorldUpdate from which we update the world.
	 */
	private void updateTeamContext(CODE method, WorldUpdate u) {
		if (method == CODE.UPDATERADIANT)
			ControllersManager.getInstance().updateTeamContext(Team.RADIANT, u);
		else if (method == CODE.UPDATEDIRE)
			ControllersManager.getInstance().updateTeamContext(Team.DIRE, u);
		else {
			logger.warn("Unknown update on team context level.");
		}
	}

	/**
	 * Setups the StaticEntity, Hero and BaseNPC static variables to match the
	 * values from configuration.
	 */
	private void setupEntities() {
		try {
			Float l = Float.parseFloat(cfg.getConfigValue("hero_time_to_live"));
			if (l != null)
				Hero.timeToLive = l;
		} catch (NumberFormatException e) {
			logger.warn("Could not parse Hero_time_to_live from FrameworkConfiguration.");
		}
		try {
			Float l = Float.parseFloat(cfg.getConfigValue("basenpc_time_to_live"));
			if (l != null)
				BaseNPC.timeToLive = l;
		} catch (NumberFormatException e) {
			logger.warn("Could not parse BaseNPC_time_to_live from FrameworkConfiguration.");
		}
		try {
			Float l = Float.parseFloat(cfg.getConfigValue("creep_time_to_live"));
			if (l != null)
				Creep.timeToLive = l;
		} catch (NumberFormatException e) {
			logger.warn("Could not parse Creep_time_to_live from FrameworkConfiguration.");
		}
		try {
			Float l = Float.parseFloat(cfg.getConfigValue("tower_time_to_live"));
			if (l != null)
				Tower.timeToLive = l;
		} catch (NumberFormatException e) {
			logger.warn("Could not parse Tower_time_to_live from FrameworkConfiguration.");
		}
		try {
			Float l = Float.parseFloat(cfg.getConfigValue("building_time_to_live"));
			if (l != null)
				Building.timeToLive = l;
		} catch (NumberFormatException e) {
			logger.warn("Could not parse Building_time_to_live from FrameworkConfiguration.");
		}
	}

	/**
	 * Resets the world reference.
	 */
	public static void reset() {
		world = new World();
		timeManager = new TimeManager();
	}

	@Override
	public CommandResponse command(ConsoleCommand cmd) {
		CommandResponse res = new CommandResponse();

		if (cmd.size() == 0)
			return res.fail("Too few arguments.");

		String first = cmd.getField();
		switch (first) {
		case "print":
			if (world != null)
				res.append(world.toString());
			res.pass();
			break;
		case "search":
			res = searchByName(cmd);
			break;
		default:
			return res.fail("Unknown command");
		}

		return res;
	}

	/**
	 * Searches the world by name (through the console). Returns the result inside
	 * the CommandResponse.
	 * 
	 * @param cmd
	 *            The command = contains the name of the entity.
	 * @return Returns a command's response. The response can be negative or it can
	 *         fail.
	 */
	private CommandResponse searchByName(ConsoleCommand cmd) {
		CommandResponse res = new CommandResponse();

		// I should have just the one argument.
		if (cmd.size() != 1) {
			return res.fail("Too few arguments supplied for search.");
		}

		res.appendLine("id: " + world.searchIdByName(cmd.getField()));

		return res.pass();
	}

	@Override
	public String getHelp() {
		ConsoleHelp help = new ConsoleHelp();
		help.appendLines(new String[] { "World controllable:", "\tprint -> displays the current world",
				"\tinterests -> displays interests in the current world",
				"\tbuildings -> displays buildings in the current world",
				"\ttowers -> displays towers in the current world", "\tcreeps -> displays creeps in the current world",
				"\theroes -> displays heroes in the current world",
				"\tsearch [name] -> searches the objects by name, returns the id", });

		return help.toString();
	}

	@Override
	public String getControllableName() {
		return "world";
	}

	@Override
	public void configurationChanged() {

	}
}
