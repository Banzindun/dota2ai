package cz.cuni.mff.kocur.server;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import cz.cuni.mff.kocur.bot.AgentController;
import cz.cuni.mff.kocur.bot.ControllerWrapper;
import cz.cuni.mff.kocur.bot.ControllersManager;
import cz.cuni.mff.kocur.configuration.ConfigurationChangeListener;
import cz.cuni.mff.kocur.configuration.FrameworkConfiguration;
import cz.cuni.mff.kocur.console.CommandResponse;
import cz.cuni.mff.kocur.console.ConsoleCommand;
import cz.cuni.mff.kocur.console.ConsoleHelp;
import cz.cuni.mff.kocur.console.ConsoleManager;
import cz.cuni.mff.kocur.console.Controllable;
import cz.cuni.mff.kocur.dota2AIFramework.App;
import cz.cuni.mff.kocur.dota2AIFramework.Setup;
import cz.cuni.mff.kocur.events.FrameworkEvent;
import cz.cuni.mff.kocur.events.ListenersManager;
import cz.cuni.mff.kocur.exceptions.LoadingError;
import cz.cuni.mff.kocur.interests.InterestPointsLoader;
import cz.cuni.mff.kocur.interests.InterestsBase;
import cz.cuni.mff.kocur.world.ChatEvent;
import cz.cuni.mff.kocur.world.WorldManager;

/**
 * This class extends a AbstractHandler. It's main purpose is to parse incoming
 * requests and pass them to the appropriate classes that should handle them.
 * This class also handles requests that are meant for the server - big updates,
 * taking in the interests etc.
 * 
 * 
 * @author kocur
 *
 */
public class RequestHandler extends AbstractHandler implements Controllable, ConfigurationChangeListener {
	/**
	 * Instance of global configuration.
	 */
	private static final FrameworkConfiguration cfg = FrameworkConfiguration.getInstance();

	/**
	 * logger registered in Logger class.
	 */
	private final Logger logger = LogManager.getLogger(ServerManager.class.getName());

	/**
	 * Bot manager instance. This class will redistribute messages to bots.
	 */
	private static ControllersManager controllerManager = null;

	/**
	 * If true, then scheduledCommand is not null.
	 */
	private boolean scheduled = false;

	/**
	 * If true, then we are saving incoming JSON POST requests to data in working
	 * directory. Names are generated as BOTID_timestamp.data or timestamp.data in
	 * case of bigupdate. </br>
	 * This can be used for example to analyze a session or for offline learning.
	 */
	private boolean saveIncoming = false;

	/**
	 * If true, saves outcoming JSON responses to working directory. The filename
	 * will be response_[name_of_request].data. Where name_of_request is name of the
	 * incoming request, as specified above.
	 */
	private boolean saveOutcoming = false;

	/**
	 * Scheduled command - if not null, then we should send it in next session
	 */
	private Command scheduledCommand = null;

	/**
	 * Will be set to true, if we are currently stepping. That means we want to take
	 * in only on update for each hero and then tell the game and framework to
	 * pause.
	 */
	private boolean stepping = false;

	/**
	 * List of controllers, that should make one step, before ending the stepping
	 * functionality.
	 */
	private LinkedList<String> controllersToMakeStep = new LinkedList<>();

	/**
	 * Constructor. Registers itself in ConsoleManager. Sets change listener.
	 * Initializes the handler.
	 */
	public RequestHandler() {
		super();

		// Get bot manager, it will be needed
		controllerManager = ControllersManager.getInstance();

		// Register handler to console, so user can issue some commands
		ConsoleManager.register(this);

		// Add change to framework configuration, so we can react to that
		FrameworkConfiguration.getInstance().addChangeListener(this);

		// Initialize the handler
		initialize();
	}

	/**
	 * CODES of incoming commands.
	 * 
	 * @author Banzindun
	 *
	 */
	public static enum CODE {
		SELECT, SELECTED, GAMESTART, CHAT, UPDATE, RESET, LEVELUP, PUSHINTERESTS, UPDATEDIRE, UPDATERADIANT, NONE, UNKNOWN, SETUP
	}

	/**
	 * Initializes the request handler. Will set booleans like saveIncoming,
	 * saveOutcoming etc.
	 */
	public void initialize() {
		saveIncoming = FrameworkConfiguration.getInstance().getConfigValue("save_incoming").equals("On");
		saveOutcoming = FrameworkConfiguration.getInstance().getConfigValue("save_outcoming").equals("On");

		if (saveIncoming)
			logger.info("Saving incoming requests to: " + Setup.getWorkingDir() + "data");

		if (saveOutcoming)
			logger.info("Saving outcoming requests to: " + Setup.getWorkingDir() + "data");
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// logger.debug("Received request with uri: " + request.getRequestURI());

		// Initially OK.
		Response res = new Response();

		// Read the request to string
		String msg = request.getReader().lines().collect((Collectors.joining(System.lineSeparator())));

		// Check if request is ok - if it isn't the error will be inside the Response
		// res object
		RequestChecker.checkRequest(request, res);

		if (!res.isOK()) {
			makeResponse(baseRequest, response, res);
			return;
		}

		// Get the uri fields and check that they are correct
		String[] uriFields = RequestChecker.getURIparams(request, res);

		if (!res.isOK()) {
			makeResponse(baseRequest, response, res);
			return;
		}

		RequestArguments arg = new RequestArguments();
		res = arg.set(uriFields);
		arg.setMessage(msg);

		if (!res.isOK()) {
			makeResponse(baseRequest, response, res);
			return;
		}

		// If we are saving incomming data, then save them.
		if (saveIncoming) {
			writeIncomingJSON(arg);
		}

		// If there is a agent's response scheduled (PAUSE, UNPAUSE), then sent the
		// response.
		if (scheduled)
			res = handleScheduled();
		// Else handle it, if the game is not paused.
		else {

			if (App.state == App.State.PAUSED)
				res.setMsg("App paused.");
			else {
				res = handle(arg);
			}
		}

		makeResponse(baseRequest, response, res, arg);
	}

	/**
	 * Makes response. This doesn't save outcoming so it should be called only on
	 * bad responses.
	 * 
	 * @param baseRequest
	 *            The request.
	 * @param response
	 *            The HttpServletResponse.
	 * @param res
	 *            Response to be sent out.
	 */
	private void makeResponse(Request baseRequest, HttpServletResponse response, Response res) {
		RequestChecker.craftServletResponse(response, res);
		baseRequest.setHandled(true);
	}

	/**
	 * Makes a response. If we are saving outcoming data, then they are saved.
	 * 
	 * @param baseRequest
	 *            The request.
	 * @param response
	 *            The HttpServletResponse.
	 * @param res
	 *            Response object.
	 * @param args
	 *            Request arguments.
	 */
	private void makeResponse(Request baseRequest, HttpServletResponse response, Response res, RequestArguments args) {
		String body = RequestChecker.craftServletResponse(response, res);

		if (saveOutcoming) {
			writeOutcomingJSON(body, args);
		}

		baseRequest.setHandled(true);
	}

	/**
	 * Handles the request arguments. Calls handleServer or handleClient.
	 * 
	 * @param arg
	 *            RequestArguments containing msg, method etc.
	 * @return Handles the request and returns the response.
	 */
	private Response handle(RequestArguments arg) {
		if (arg.forServer()) {
			return handleServer(arg);
		} else {
			return handleClient(arg);
		}
	}

	/**
	 * Handles all requests, that are meant for server.
	 * 
	 * @param arg
	 *            Arguments.
	 * @return Returns a response.
	 */
	private Response handleServer(RequestArguments arg) {
		Response res = new Response();

		switch (arg.getMethod()) {
		case SETUP:
			res = handleGameSetup(arg);
			break;
		case SELECTED:
			res = handleSelected(arg);
			break;
		case GAMESTART:
			logger.info("Game is starting.");
			res = handleGameStart(arg);
			break;
		case UPDATERADIANT:
		case UPDATEDIRE:
			res = handleServerUpdate(arg);
			break;
		case CHAT:
			res = handleChat(arg);
			break;
		case PUSHINTERESTS:
			res = handleInterests(arg);
			break;
		default:
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			res.setMsg("Unknown server method.");
			break;
		}

		return res;
	}

	/**
	 * Handles the game setup. This will initialize controllers managers and create
	 * response containing all the agents/players/bots that will be sent to the
	 * addon.
	 * 
	 * @param arg
	 *            Arguments.
	 * @return Returns a response.
	 */
	private Response handleGameSetup(RequestArguments arg) {
		// If I receive this command it should be in INIT state. That is in state when I
		// am waiting for connection from the game.
		// If I have received this command in other state I should restart the app.
		if (App.state != App.State.INIT) {
			App.getInstance().reset();
		}

		// Reset the manager, so we are nice and clean
		WorldManager.reset();

		// Trigger the game start event
		ListenersManager.triggerFrameworkEvent("setup", new FrameworkEvent());

		// Return signature of bots with config from app
		ControllersManager man = ControllersManager.getInstance();

		// Create signatures
		LinkedHashMap<String, HashMap<String, String>> sign = cfg.getHeroConfigurationsSignature();

		// Check if we are skipping the setup. If so, sent field __config with
		// skipSetup.
		if (App.getArgument("skipSetup") != null) {
			HashMap<String, String> __config = new HashMap<>();
			__config.put("skipSetup", ""); // Game mod will ass only for presence
			sign.put("__config", __config);
		}

		return new Response(sign);
	}

	/**
	 * Handles the server update from arguments.
	 * 
	 * @param arg
	 *            Arguments.
	 * @return Returns the response on this update.
	 */
	private Response handleServerUpdate(RequestArguments arg) {
		Response res = new Response();

		WorldManager wm = WorldManager.getInstance();
		wm.updateBig(arg);
		return res;
	}

	/**
	 * Handles the start of the game.
	 * 
	 * @param arg
	 *            Arguments.
	 * @return Returns a response.
	 */
	private Response handleGameStart(RequestArguments arg) {
		return new Response();
	}

	/**
	 * Handles map interests, that must be loaded and stored, before the game can
	 * start.
	 * 
	 * @param arg
	 *            Arguments, that contain message with json representation of
	 *            interests.
	 * 
	 * @return Returns a response.
	 */
	private Response handleInterests(RequestArguments arg) {
		Response res = new Response();

		// Create the loader.
		InterestPointsLoader loader = new InterestPointsLoader();

		try {
			// Check the args.
			if (!controllerManager.areAgentsSelected()) {
				// Interests come after all the bots are selected, if not there is something
				// wrong
				logger.error("Bots weren't loaded before the interest POST came.");
				App.getInstance().stop();
			} else if (App.state != App.State.INIT) {
				// The game should still be in init state
				logger.error("Init came during the RUNNING state.");
				return res.set(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Init came during the RUNNING state.");
			}

			// Now we can load the interests
			InterestsBase.load(arg.getMessage());
			logger.info("Interests loaded sucessfully.");

			// Tell the app we are initialized
			App.gameInitialized();
		} catch (LoadingError e) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setMsg(e.getMessage());
			logger.error("Unable to load interests", e);
		}
		return res;
	}

	/**
	 * Handles the client request. Passed the request to ControllerManager and
	 * implements the stepping functionality.
	 * 
	 * @param arg
	 *            Arguments.
	 * @return Returns response, that should be sent back to agent.
	 */
	private Response handleClient(RequestArguments arg) {
		// We should run client commands only after INIT state passed.
		// This should prevent us from running commands that are queued up from last
		// run.
		if (App.state == App.State.RUNNING || App.state == App.State.PAUSED) {
			// If we are stepping, we should check that this controller haven't already
			// received its update.
			if (stepping) {
				if (controllersToMakeStep.indexOf(arg.getAgentName()) == -1) {
					return new Response("");
				} else {
					controllersToMakeStep.remove(arg.getAgentName());
				}
			}
			
			// Update the world from agent's data and update agent's context
			if (arg.getMethod() == CODE.UPDATE)
				WorldManager.getInstance().update(arg);
			
			// Tell ControllerManager to handle the request.
			Response res = controllerManager.handle(arg);

			// Check if we have finished stepping.
			if (stepping && controllersToMakeStep.isEmpty()) {
				finishStepping();
			}
			return res;
		}

		return new Response("Not running, not paused.");
	}

	/**
	 * Called if we have scheduled some command. This create response with the
	 * scheduled command.
	 * 
	 * @return Returns the response containing the scheduled command.
	 */
	private Response handleScheduled() {
		Response res = new Response();

		// Set as object
		res.setObject(scheduledCommand);

		scheduled = false;
		return res;
	}

	/**
	 * Handles the chat request.
	 * 
	 * @param arg
	 *            Arguments.
	 * @return Response to chat request.
	 */
	private Response handleChat(RequestArguments arg) {
		Response res = new Response();

		// Read the chat event.
		ChatEvent e;
		try {
			e = MapperWrapper.readValue(arg.getMessage(), ChatEvent.class);
		} catch (IOException e1) {
			logger.error("Unable to read chat event from http session.", e1);

			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setMsg("Unable to read chat event from http session.");
			return res;
		}

		// Lets try to handle the chat command as a console command
		// We can use this to eg pause the game from within the game
		ConsoleCommand cmd = new ConsoleCommand(e.getText());
		CommandResponse response = App.getInstance().command(cmd);
		
		if (response.passed()) {
			logger.info("In-game command: " + e.getText() + " handled with msg. " + response.toString());
			return res;
		}
			
		// Else we tell controllers to react to it.
		res.setObject(controllerManager.chatEvent(e));
		return res;
	}

	/**
	 * Handles the selection of bots.
	 * 
	 * @param arg
	 *            Arguments.
	 * @return Response to the selection.
	 */
	private Response handleSelected(RequestArguments arg) {
		Response res = controllerManager.handleSelectedAgent(arg.getMessage());
		return res;
	}

	/**
	 * Schedules a command.
	 * 
	 * @param c
	 *            Command to be scheduled.
	 */
	private void schedule(Command c) {
		scheduledCommand = c;
		scheduled = true;
	}

	/**
	 * Schedules pause to be sent over to the game.
	 */
	public void pause() {
		logger.info("Sending PAUSE command from request handler.");
		schedule(new ServerCommands.Pause());
	}

	/**
	 * Schedules unpause to be sent over to the game.
	 */
	public void unpause() {
		logger.info("Sending UNPAUSE command from request handler.");
		schedule(new ServerCommands.Unpause());
	}

	@Override
	public CommandResponse command(ConsoleCommand cmd) {
		CommandResponse res = new CommandResponse();
		String field = cmd.getField();

		switch (field) {
		case "pause":
			pause();
			res = res.pass("Pause scheduled.");
			break;
		case "unpause":
			unpause();
			res = res.pass("Unpause scheduled.");
			break;
		case "reset":
			reset();
			res = res.pass("Reset scheduled.");
			break;
		default:
			res = res.fail("Unknown command.");
			break;
		}

		return res;
	}

	@Override
	public String getHelp() {
		ConsoleHelp help = new ConsoleHelp();

		help.appendLines("Commands you can use for RequestHandler (server) are:", "\tpause - pauses the game",
				"\tunpause - unpauses the game", "\rreset - resets the handler");

		return help.toString();
	}

	@Override
	public String getControllableName() {
		return "handler";
	}

	/**
	 * Resets the request handler.
	 */
	public void reset() {
		controllerManager = ControllersManager.resetInstance();
	}

	/**
	 * Should be called if the server is stopping.
	 */
	public void serverStopping() {
		controllerManager = ControllersManager.resetInstance();
	}

	@Override
	public void configurationChanged() {
		boolean _saveIncoming = FrameworkConfiguration.getInstance().getConfigValue("save_incoming").equals("On");
		boolean _saveOutcoming = FrameworkConfiguration.getInstance().getConfigValue("save_outcoming").equals("On");

		if (saveIncoming != _saveIncoming) {
			saveIncoming = _saveIncoming;
			logger.info("Saving incoming requests to: " + Setup.getWorkingDir() + "data");
		}

		if (saveOutcoming != _saveOutcoming) {
			saveOutcoming = _saveOutcoming;
			logger.info("Saving outcoming requests to: " + Setup.getWorkingDir() + "data");
		}
	}

	/**
	 * Should write the incoming JSON to file.
	 * 
	 * @param args
	 *            Arguments.
	 */
	private void writeIncomingJSON(RequestArguments args) {
		// Generate path to the new file
		String path = Setup.getOutputDir() + args.getStamp() + ".json";

		try (PrintWriter printWriter = new PrintWriter(new FileWriter(path))) {
			printWriter.write(args.getMessage());
		} catch (IOException e) {
			logger.warn("Unable to save incoming json to file.", e);
		}
	}

	/**
	 * Should write the outcoming JSON to file.
	 * 
	 * @param body
	 *            String to write.
	 * @param args
	 *            Arguments.
	 */
	private void writeOutcomingJSON(String body, RequestArguments args) {
		// Generate path to the new file
		String path = Setup.getOutputDir() + "response_" + args.getStamp() + ".json";

		try (PrintWriter printWriter = new PrintWriter(new FileWriter(path))) {
			printWriter.write(body);
		} catch (IOException e) {
			logger.warn("Unable to save outcoming json to file.", e);
		}
	}

	/**
	 * Starts stepping. Will set stepping boolean to true, initialize list of
	 * controllers that should make one step.
	 */
	public void startStepping() {
		stepping = true;
		controllersToMakeStep.clear();

		for (ControllerWrapper c : controllerManager.getAllControllerWrappers()) {
			controllersToMakeStep.add(c.getName());
		}
	}

	/**
	 * Will finish the stepping. Basically just pauses the game.
	 */
	public void finishStepping() {
		stepping = false;

		App.getInstance().pause();
	}

}
