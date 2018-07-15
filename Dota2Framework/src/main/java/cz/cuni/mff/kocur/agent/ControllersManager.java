package cz.cuni.mff.kocur.agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.configuration.HeroConfiguration.TYPE;
import cz.cuni.mff.kocur.events.FrameworkEvent;
import cz.cuni.mff.kocur.events.ListenersManager;
import cz.cuni.mff.kocur.interests.Team;
import cz.cuni.mff.kocur.server.MapperWrapper;
import cz.cuni.mff.kocur.server.RequestArguments;
import cz.cuni.mff.kocur.server.Response;
import cz.cuni.mff.kocur.world.ChatEvent;
import cz.cuni.mff.kocur.world.WorldUpdate;

/**
 * This class manages all the controllers. Its main purpose can be to manage
 * dire and radiant team contexts, manage the context, load agents and spread
 * the incoming updates between them.
 * 
 * @author kocur
 *
 */
public class ControllersManager {

	/**
	 * Instance of ControllersManager.
	 */
	private static ControllersManager instance = new ControllersManager();

	/**
	 * 
	 * @return Returns the instance of ControllersManager.
	 */
	public static ControllersManager getInstance() {
		return instance;
	}

	/**
	 * Will override the instance. So this will clear the agents etc.
	 * 
	 * @return Returns a new instance of this class.
	 */
	public static ControllersManager resetInstance() {
		instance = new ControllersManager();
		return instance;
	}

	/**
	 * Logger registered in this class.
	 */
	private final Logger logger = LogManager.getLogger(ControllersManager.class.getName());

	/**
	 * Radiant controllers.
	 */
	TeamContext radiantTeamContext = new TeamContext(Team.RADIANT);

	/**
	 * Dire controllers.
	 */
	TeamContext direTeamContext = new TeamContext(Team.DIRE);

	/**
	 * Constructor.
	 */
	private ControllersManager() {

	}

	/**
	 * Loads the agents.
	 * 
	 * @return Returns true if all the agents were loaded succesfully and if there
	 *         was at least one agent..
	 */
	public boolean load() {
		boolean result = true;

		result = result && radiantTeamContext.load();
		result = result && direTeamContext.load();

		if (!checkTeamContext())
			result = false;

		return result;
	}

	/**
	 * Check that the team contexts are ok. There must be exactly 1 player.
	 * 
	 * @return Returns true, if team context is correctly set.
	 */
	private boolean checkTeamContext() {
		int playerCount = radiantTeamContext.countControllers(TYPE.HUMAN)
				+ direTeamContext.countControllers(TYPE.HUMAN);

		if (playerCount == 0) {
			logger.error("You have not supplied a player. Please supply one.");
			return false;
		} else if (playerCount > 1) {
			logger.error("There should be max one player. You have given me more.");
			return false;
		}

		return true;
	}

	/**
	 * Initializes the agents.
	 */
	public void initializeAgents() {
		radiantTeamContext.initialize();
		direTeamContext.initialize();
		
		FrameworkEvent agentsInitializedEvent = new FrameworkEvent();
		ListenersManager.triggerFrameworkEvent("agents_initialized", agentsInitializedEvent);
	}

	/**
	 * Passes the incoming request to correct team context and agent's controller.
	 * 
	 * @param arg
	 *            RequestArguments that contain message, method and agent'sName.
	 * @return Returns a response from agent's controller.
	 */
	public Response handle(RequestArguments arg) {
		Response res = new Response();

		ControllerWrapper bw = radiantTeamContext.getControllerWrapper(arg.getAgentName());
		if (bw == null)
			bw = direTeamContext.getControllerWrapper(arg.getAgentName());

		if (bw == null) {
			logger.error("Unable to find agent with id: " + arg.getAgentName());
			res.set(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Unable to find agent with id: " + arg.getAgentName());
			return res;
		}

		return bw.handle(arg.getMethod());
	}

	/**
	 * Handle chat event. For now just send the events to all wrappers and they can
	 * handle them. [this might change]
	 * 
	 * @param e
	 *            ChatEvent
	 * @return Returns response for chat event. Now I return empty one, but in
	 *         future this might be needed.
	 */
	public Response chatEvent(ChatEvent e) {
		radiantTeamContext.chatEvent(e);
		direTeamContext.chatEvent(e);

		return new Response();
	}

	/**
	 * Handles BotsSelected request. == Assigns in-game ids to agent wrappers.
	 * 
	 * @param msg
	 *            Body of the request.
	 * @return Returns response to that request. Empty message with OK status if
	 *         everything goes according to plan.
	 */
	public Response handleSelectedAgent(String msg) {
		Response res = new Response();

		// Read data from agent that was selected. Should contain name of the agent and
		// then it's hero's in-game id.
		SelectedAgentDummy d;
		try {
			d = MapperWrapper.readValue(msg, SelectedAgentDummy.class);
		} catch (IOException e) {
			logger.error("Could not deserialize to SelectedAgentDummy.", e);

			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setMsg("Could not convert agent to SelectedAgentDummy on BotsSelected.");
			return res;
		}

		logger.info("Seting id: " + d.getEntid() + " to agent with name: " + d.getName());

		// Take index and id of AgentDummy and give it to ControllerWrapper with same
		// position. ==
		// Assign in-game id to agent.
		boolean agentSet = false;

		agentSet = radiantTeamContext.tryToSetAgent(d.getName(), d.getEntid());
		if (!agentSet)
			agentSet = direTeamContext.tryToSetAgent(d.getName(), d.getEntid());

		if (agentSet == false) {
			logger.fatal("Unable to set agent with name and id:" + d.getName() + " " + d.getEntid());
		}

		return res;
	}

	/**
	 * Returns true if the selection is done.
	 * 
	 * @return True if all agents were selected.
	 */
	public boolean areAgentsSelected() {
		boolean r = radiantTeamContext.areAllSelected();
		r = r && direTeamContext.areAllSelected();
		return r;
	}

	/**
	 * Finds agent with supplied name and returns it.
	 * 
	 * @param name
	 *            Name of the agent we want to find.
	 * @return BotWrapper of agent with supplied name or null.
	 */
	public ControllerWrapper getControllerWrapperWithName(String name) {
		ControllerWrapper bw = radiantTeamContext.getControllerWrapper(name);
		if (bw == null)
			bw = direTeamContext.getControllerWrapper(name);

		return bw;
	}

	/**
	 * Updates agent context.
	 * 
	 * @param name
	 *            Name of the agent.
	 * @param u
	 *            World object that stores the updated entities.
	 */
	public void updateAgentContext(String name, WorldUpdate u) {
		ControllerWrapper bw = getControllerWrapperWithName(name);
		if (bw == null) {
			logger.warn("Unable to get agent's context for agent with name: " + name);
			return;
		}
		bw.updateAgentContext(u);
	}

	/**
	 * 
	 * @return Returns all agent controllers.
	 */
	public ArrayList<AgentController> getAllControllers() {
		ArrayList<AgentController> result = new ArrayList<>();
		result.addAll(radiantTeamContext.getAllControllers());
		result.addAll(direTeamContext.getAllControllers());

		return result;
	}

	/**
	 * 
	 * @return Returns all controller wrappers.
	 */
	public ArrayList<ControllerWrapper> getAllControllerWrappers() {
		ArrayList<ControllerWrapper> result = new ArrayList<>();
		result.addAll(radiantTeamContext.getAllControllerWrappers());
		result.addAll(direTeamContext.getAllControllerWrappers());

		return result;
	}
	
	/**
	 * 
	 * @return Returns list of the names of the controllers.
	 */
	public List<String> getAllControllerNames(){
		LinkedList<String> names = new LinkedList<>();
		names.addAll(radiantTeamContext.getAllControllerNames());
		names.addAll(direTeamContext.getAllControllerNames());
		return names;
	}

	/**
	 * 
	 * @return Returns count of the controllers inside this manager.
	 */
	public int countControllers() {
		return radiantTeamContext.countControllers() + direTeamContext.countControllers();
	}

	/**
	 * Should be called after the framework stopped to alert the manager.
	 */
	public void frameworkStopped() {
		radiantTeamContext.frameworkStopped();
		direTeamContext.frameworkStopped();

	}

	/**
	 * 
	 * @param team
	 *            Number of team.
	 * @return Returns a team context.
	 */
	public TeamContext getTeamContext(int team) {
		if (team == Team.DIRE) {
			return direTeamContext;
		} else {
			return radiantTeamContext;
		}
	}

	/**
	 * Updates a team context.
	 * 
	 * @param team
	 *            Number of team.
	 * @param u
	 *            World containing the update entities.
	 */
	public void updateTeamContext(int team, WorldUpdate u) {
		getTeamContext(team).update(u);
	}

}
