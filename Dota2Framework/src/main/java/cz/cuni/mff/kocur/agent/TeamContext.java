package cz.cuni.mff.kocur.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.configuration.FrameworkConfiguration;
import cz.cuni.mff.kocur.configuration.HeroConfiguration;
import cz.cuni.mff.kocur.configuration.HeroConfiguration.TYPE;
import cz.cuni.mff.kocur.exceptions.LoadingError;
import cz.cuni.mff.kocur.interests.InterestsBase;
import cz.cuni.mff.kocur.interests.Lane;
import cz.cuni.mff.kocur.interests.Team;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.Building;
import cz.cuni.mff.kocur.world.ChatEvent;
import cz.cuni.mff.kocur.world.Courier;
import cz.cuni.mff.kocur.world.Creep;
import cz.cuni.mff.kocur.world.Tower;
import cz.cuni.mff.kocur.world.World;
import cz.cuni.mff.kocur.world.WorldUpdate;

/**
 * Class that represents team's context.
 * Team context is a representation of a world that is inherent to every bot (that has a team). 
 * It's goal is to supply function that will help agents to navigate around the world.
 * @author kocur
 *
 */
public class TeamContext {
	/**
	 * TeamContext registered logger.
	 */
	private static final Logger logger = LogManager.getLogger(TeamContext.class);

	/**
	 * Our team's world.
	 */
	protected World world = new World();

	/**
	 * HashMap of controllers of given team.
	 */
	private HashMap<String, ControllerWrapper> controllers = new HashMap<>();

	/**
	 * Reference to framework configuration.
	 */
	private static FrameworkConfiguration cfg = FrameworkConfiguration.getInstance();

	/**
	 * HashMap that will contain all the towers in this context.
	 */
	protected ConcurrentHashMap<Integer, Tower> towers = new ConcurrentHashMap<>();

	/**
	 * HashMap that will contain all the creeps in this context.
	 */
	protected ConcurrentHashMap<Integer, Creep> creeps = new ConcurrentHashMap<>();

	/**
	 * HashMap that will represent all the building in this context.
	 */
	protected ConcurrentHashMap<Integer, Building> buildings = new ConcurrentHashMap<>();

	/**
	 * Number of my team.
	 */
	private int teamNumber = Team.NEUTRAL;

	/**
	 * Courier of this team.
	 */
	private Courier teamCourier = null;

	/**
	 * Constructor.
	 * 
	 * @param teamNumber
	 *            Number of my team.
	 */
	public TeamContext(int teamNumber) {
		this.teamNumber = teamNumber;
	}

	/**
	 * Loads the team context from BotLoader. Checks the bots and adds bots to Controllers.
	 * Basically loads the whole team.
	 * @return Returns true if the loading was successful.
	 */
	public boolean load() {
		ArrayList<AgentController> bs;
		try {
			bs = AgentLoader.load(teamNumber);
		} catch (LoadingError e) {
			logger.fatal("Could not load the bots.", e);
			return false;
		}

		boolean passed = checkLoadedAgents(bs);

		if (!passed)
			return false;

		// Create BotWrapper around every one of them and add them to bots array.
		for (AgentController bc : bs) {
			String configurationName = bc.getConfiguration().getName();
			ControllerWrapper wrapper = new ControllerWrapper(configurationName, bc);
			controllers.put(bc.getConfiguration().getName(), wrapper);
		}

		logger.info("BotManager loaded " + controllers.size() + " bots.");
		return true;
	}

	/**
	 * Checks that the agents, that are being loaded are correctly set. 
	 * @param bs ArrayList of agent controllers.
	 * @return Returns true, if the bots were loaded correctly.
	 */
	private boolean checkLoadedAgents(ArrayList<AgentController> bs) {
		if (bs == null) {
			logger.fatal("Nothing was loaded.");
			return false;
		}

		// Count the configurations of bots we will be loading
		int count = cfg.countHeroConfigurations(TYPE.PLAYER, teamNumber)
				+ cfg.countHeroConfigurations(TYPE.AI, teamNumber);

		if (bs.size() != count) {
			logger.fatal(
					"Not all bots were loaded. " + controllers.size() + " vs. " + cfg.getHeroConfigurations().size());
			return false;
		}

		return true;
	}

	/**
	 * Registers the controller wrapper in this team context.
	 * 
	 * @param wp
	 *            Wrapped controller, that controls a hero corresponding to mine
	 *            team number.
	 */
	public void registerController(ControllerWrapper wp) {
		wp.getId();
	}

	/**
	 * 
	 * @param name Name of the agent, that we want controller wrapper for.
	 * @return Returns controller wrapper, that corresponds to agent with given name.
	 */
	public ControllerWrapper getControllerWrapper(String name) {
		return controllers.get(name);
	}

	/**
	 * Initializes the agents stored inside this team's context.
	 */
	public void initialize() {
		for (ControllerWrapper c : controllers.values()) {
			c.getController().initialize();
		}

	}

	/**
	 * 
	 * @param type
	 *            Type of the lane. (Lane.Type == MID, TOP, BOT)
	 * @return Returns array of BotControllerWrapper(s) that are assigned to Lane of
	 *         given type.
	 */
	public ControllerWrapper[] getAgentsOnLane(Lane.TYPE type) {
		LinkedList<ControllerWrapper> bts = new LinkedList<>();
		for (ControllerWrapper bw : controllers.values()) {
			// Get Lane type of wrapper
			Lane.TYPE bwType = bw.getController().getContext().getMyLane().getType();

			// Check if it is the searched type
			if (bwType == type) {
				bts.add(bw);
			}
		}

		return bts.toArray(new ControllerWrapper[bts.size()]);
	}

	/**
	 * 
	 * @param role Role, that we want to get.
	 * @return Returns all the ControllerWrappers with given role.
	 */
	public ControllerWrapper[] getAgentsWithRole(int role) {
		LinkedList<ControllerWrapper> bts = new LinkedList<>();
		for (ControllerWrapper bw : controllers.values()) {
			// Get Lane type of wrapper
			int[] roles = bw.getController().getContext().getMyRoles();

			for (int i : roles) {
				if (i == role)
					bts.add(bw);
			}
		}

		return bts.toArray(new ControllerWrapper[bts.size()]);

	}

	/**
	 * Function, that reacts to chat event.
	 * @param e ChatEvent object.
	 */
	public void chatEvent(ChatEvent e) {
		for (ControllerWrapper b : controllers.values()) {
			b.onChat(e);
		}

	}

	/**
	 * Tries to set an agent with given name and id.
	 * @param name Name of the agent.
	 * @param id Id of the agent.
	 * @return Returns true if the agent was set. False otherwise.
	 */
	public boolean tryToSetAgent(String name, int id) {
		ControllerWrapper bw = controllers.get(name);
		if (bw == null)
			return false;

		bw.initializeBotsId(id);

		return true;
	}

	/**
	 * 
	 * @return Returns true if all agents are selected.
	 */
	public boolean areAllSelected() {
		boolean r = true;

		for (ControllerWrapper bw : controllers.values()) {
			if (bw.getController().getConfiguration().getType() != HeroConfiguration.TYPE.BOT) {
				r = r && bw.isSet();
			}
		}

		return r;
	}

	/**
	 * 
	 * @return Returns all the agent controllers. 
	 */
	public ArrayList<AgentController> getAllControllers() {
		ArrayList<AgentController> result = new ArrayList<>();
		for (ControllerWrapper wp : controllers.values()) {
			result.add(wp.getController());
		}

		return result;
	}

	/**
	 * 
	 * @return Returns all the controller wrappers.
	 */
	public ArrayList<ControllerWrapper> getAllControllerWrappers() {
		ArrayList<ControllerWrapper> result = new ArrayList<>();
		for (ControllerWrapper wp : controllers.values()) {
			result.add(wp);
		}

		return result;
	}

	/**
	 * 
	 * @return Returns the number of controllers stored inside this team's context.
	 */
	public int countControllers() {
		return controllers.size();
	}

	/**
	 * Should be called after the framework was stopped. Destroys all the wrappers and clears controllers.
	 */
	public void frameworkStopped() {

		for (ControllerWrapper cw : controllers.values()) {
			cw.destroy();
		}

		controllers.clear();
	}

	/**
	 * 
	 * @return Returns this team's courier.
	 */
	public Courier getTeamCourier() {
		return teamCourier;
	}

	/**
	 * Sets this team's courier.
	 * @param teamCourier New team courier.
	 */
	public void setTeamCourier(Courier teamCourier) {
		this.teamCourier = teamCourier;
	}

	public Lane getLane(cz.cuni.mff.kocur.interests.Lane.TYPE t) {
		return InterestsBase.getInstance().getLanes().getLane(teamNumber, t);
	}

	/**
	 * 
	 * @param type
	 *            Type of the controller.
	 * @return Returns count of controllers with the given type.
	 */
	public int countControllers(TYPE type) {
		int count = 0;

		for (ControllerWrapper wp : controllers.values()) {
			if (wp.getController().getConfiguration().getType() == type)
				count++;
		}

		return count;
	}

	/**
	 * Updates the team context and performs voidCleanup, that removes all the old
	 * object without alerting them, that they are dying. That is done only by
	 * WorldManager while working with world representation.
	 * 
	 * @param u
	 *            World, that should be used to update this context's world.
	 */
	public void update(WorldUpdate u) {
		world.voidUpdate(u);
		world.voidCleanup();
	}

	/**
	 * Finds entities in radius around some entity.
	 * @param e Midpoint of our search.
	 * @param dist The radius.
	 * @return Returns list of entities in given radius.
	 */
	public List<BaseEntity> findEntitiesInRadius(BaseEntity e, double dist) {
		if (world == null)
			return new LinkedList<BaseEntity>();
		return world.findEntitiesInRadius(e, dist);
	}

	/**
	 * 
	 * @return Returns map of world's entities.
	 */
	public ConcurrentHashMap<Integer, BaseEntity> getEntites() {
		return world.getEntities();
	}

	/**
	 * 
	 * @return Returns world representation.
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * Sets a new world.
	 * @param world New world.
	 */
	public void setWorld(World world) {
		this.world = world;
	}

	
	public String toString() {
		return world.toString();
	}
	
	
	public ConcurrentHashMap<Integer, Tower> getTowers() {
		return towers;
	}

	public void setTowers(ConcurrentHashMap<Integer, Tower> towers) {
		this.towers = towers;
	}

	public ConcurrentHashMap<Integer, Creep> getCreeps() {
		return creeps;
	}

	public void setCreeps(ConcurrentHashMap<Integer, Creep> creeps) {
		this.creeps = creeps;
	}

	public ConcurrentHashMap<Integer, Building> getBuildings() {
		return buildings;
	}

	public void setBuildings(ConcurrentHashMap<Integer, Building> buildings) {
		this.buildings = buildings;
	}

	public void addBuilding(Integer id, Building b) {
		buildings.put(id, b);
	}

	public Building getBuilding(Integer id) {
		return buildings.get(id);
	}

	public void removeBuilding(Integer id) {
		buildings.remove(id);
	}

	public void addCreep(Integer id, Creep b) {
		creeps.put(id, b);
	}

	public Creep getCreep(Integer id) {
		return creeps.get(id);
	}

	public void removeCreep(Integer id) {
		creeps.remove(id);
	}

	public void addTower(Integer id, Tower b) {
		towers.put(id, b);
	}

	public Tower getTower(Integer id) {
		return towers.get(id);
	}

	public void removeTower(Integer id) {
		towers.remove(id);
	}

}
