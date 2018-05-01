package cz.cuni.mff.kocur.dota2AIFramework;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.bot.ControllersManager;
import cz.cuni.mff.kocur.configuration.FrameworkConfiguration;
import cz.cuni.mff.kocur.console.CommandResponse;
import cz.cuni.mff.kocur.console.ConsoleCommand;
import cz.cuni.mff.kocur.console.ConsoleHelp;
import cz.cuni.mff.kocur.console.Controllable;
import cz.cuni.mff.kocur.events.FrameworkEvent;
import cz.cuni.mff.kocur.events.ListenersManager;
import cz.cuni.mff.kocur.exceptions.KeyNotFound;
import cz.cuni.mff.kocur.exceptions.SetupException;
import cz.cuni.mff.kocur.graphics.MainWindow;
import cz.cuni.mff.kocur.server.RequestHandler;
import cz.cuni.mff.kocur.server.ServerManager;
import cz.cuni.mff.kocur.server.TimeManager;

/**
 * Class that represents the "main" of this application. </br>
 * It has a few states: SETUP, CONFIGURATION, INIT, RUNNING etc., that
 * correspond to the state of the execution. </br>
 * There are some functions as start(), debug(), stopServer() etc., that control
 * the application. Theese functions are called from server methods and from
 * buttons on main window. </br>
 * 
 * This is implemented as singleton. But this as well might have been a static
 * class.
 * 
 * @author kocur
 *
 */
public class App implements Controllable {
	/**
	 * Instance of this application.
	 */
	private static App instance = null;

	/**
	 * 
	 * @return Returns reference to application.
	 */
	public static App getInstance() {
		if (instance == null)
			instance = new App();
		return instance;
	}

	/**
	 * Private constructor (for singleton pattern).
	 */
	private App() {

	}

	/**
	 * Logger registered for the Logger class.
	 */
	private static Logger logger = LogManager.getLogger(App.class.getName());

	/**
	 * GlobalConfiguration reference
	 */
	private FrameworkConfiguration cfg;

	private static HashMap<String, CommandLineArgument> arguments = new HashMap<>();

	/**
	 * States that illustrate where the app is in current execution.
	 * 
	 * @author kocur
	 *
	 */
	public static enum State {
		SETUP, // When setting up the application .. basically before the window starts..
		CONFIGURATION, // Waiting for user to configure bots etc.
		INIT, // Waiting for information from the game (client)
		RUNNING, // Bots and server are running
		PAUSED, // Game is paused -> and so is the app
		ERROR, // Error state - something went wrong
		FINISHED // Game or server ended correctly
	};

	/**
	 * Current state of the execution
	 */
	public static State state = State.SETUP;

	/**
	 * Last state (I use this to store the last state before pause)
	 */
	public static State lastState = null;

	/**
	 * Window of the application.
	 */
	private static MainWindow window = null;

	/**
	 * ServerManager. Starts and stops the server etc.
	 */
	private static ServerManager serverManager = null;

	/**
	 * Setups the application. If not done correctly, sets state to ERROR and throws
	 * an exception.
	 * 
	 * @throws SetupException
	 *             If setup haven't gone as planned. (missing files, missing
	 *             configurations etc.)
	 */
	public void setup(String[] args) throws SetupException {
		Setup.setup(this);
		cfg = FrameworkConfiguration.getInstance();

		parseCommandLineArguments(args);

		CommandLineArgument arg = arguments.get("install");
		if (arg != null) {
			String value = arg.getValue();

			if (value == null) {
				Setup.installDota2aiAddon();
				Setup.installGridExporterAddon();
			} else if (value.equals("dota2ai"))
				Setup.installDota2aiAddon();
			else if (value.equals("exporter"))
				Setup.installGridExporterAddon();

		}
	}

	/**
	 * Parses command line arguments.
	 * @param args Array of strings, that represents command line arguments.
	 */
	private void parseCommandLineArguments(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String field = args[i];
			if (field.indexOf("-") == 0) {
				int signIndex = field.indexOf("=");

				// Create new argument object
				CommandLineArgument argument = new CommandLineArgument();

				// Insert differently if there is = in the string
				if (signIndex == -1) {
					argument.setFieldName(field.substring(1, field.length()));
				} else {
					String fieldName = field.substring(1, signIndex);
					String value = field.substring(signIndex + 1, field.length());
					argument = new CommandLineArgument(fieldName, value);
				}
				arguments.put(argument.getFieldName(), argument);
			} else {
				logger.warn("Unknown command line argument: " + field);
			}
		}
	}

	/**
	 * Creates the window and runs the application.
	 */
	public void start() {
		window = new MainWindow(this, "Dota2AI framework");
		window.build();
		window.start();
		changeState(State.CONFIGURATION);
	}

	/**
	 * Starts the server if it is not already running.
	 * 
	 * @return Returns true if server started, false if there were some errors.
	 */
	private boolean startServer() {
		if (serverManager != null && serverManager.isRunning()) {
			logger.warn("Server is already running.");
			return false;
		}

		serverManager = new ServerManager();

		try {
			serverManager.setup();

		} catch (NumberFormatException | KeyNotFound e) {
			logger.error("Unable to initialize server.", e);
			return false;
		}

		try {
			serverManager.startServer();
		} catch (Exception e) {
			logger.error("Unable to start the server.", e);
			return false;
		}

		return true;
	}

	/**
	 * Stops the server.
	 */
	public void stopServer() {
		if (serverManager == null)
			return;
		else {
			try {
				serverManager.stopServer();
			} catch (Exception e) {
				logger.error("Couldn't stop the server.", e);
			}
		}
	}

	/**
	 * 
	 * @return Returns the serverManager.
	 */
	public ServerManager getServer() {
		return serverManager;
	}

	/**
	 * Starts the server, loads the bots and transfers to INIT state.
	 */
	public void debug() {
		if (state == State.RUNNING)
			return;

		ControllersManager manager = ControllersManager.getInstance();

		// Load the bot manager, will load the bots from configurations.
		boolean loaded  = manager.load();
		
		if (!loaded)
			return;
		
		if (manager.countControllers() == 0) {
			logger.error("No bots loaded.");
			return; // Stay in CONFIGURATION state
		}
		

		// Start server
		if (!startServer()) {
			logger.error("Unable to start the server");
			return; // Will stay in CONFIGURATION state
		}

		// Change state to running
		changeState(State.INIT);
	}

	/**
	 * Stops the application.
	 */
	public void stop() {
		if (state == State.RUNNING || state == State.INIT) {
			stopServer();

			// Clear all the listeners - not the server ones.
			// They should be initialized on another game start. (when their classes load)
			ListenersManager.clearListeners();

			// Allert the controlelr manager, that we have stopped.
			ControllersManager manager = ControllersManager.getInstance();
			manager.frameworkStopped();

			// Change state to Configuration
			changeState(State.CONFIGURATION);
		}
	}



	/**
	 * Restarts the communication with game. Will call stop() and debug().
	 */
	public void reset() {
		stop();

		// Stop and start again
		debug();
	}

	/**
	 * Pauses the application.
	 */
	public void pause() {
		serverManager.pauseGame();

		TimeManager.frameworkPaused();

		changeState(State.PAUSED);
	}

	/**
	 * Unpauses the application.
	 */
	public void unpause() {
		serverManager.unpauseGame();

		TimeManager.frameworkUnpaused();

		changeState(State.RUNNING);
	}
	
	/**
	 * Will tell requestHandler, that there is supposed to be only one update for
	 * each hero and then the game should be paused.
	 */
	public void step() {
		logger.debug("Making one step.");
		
		RequestHandler requestHandler = serverManager.getHandler();
		requestHandler.startStepping();
		unpause();
	}

	@Override
	public CommandResponse command(ConsoleCommand cmd) {
		String firstField = cmd.getField();

		CommandResponse response = new CommandResponse();
		response.pass();

		switch (firstField) {
		case "debug":
			debug();
			response.append("Debugging started correctly.");
			break;
		case "p":
		case "pause":
			pause();
			response.append("Paused the game.");
			break;
		case "u":
		case "unpause":
			unpause();
			response.append("Unpaused the game.");
			break;
		case "stop":
			stop();
			response.append("Stopped the execution.");
			break;
		case "server":
			serverManager.command(cmd);
			break;
		case "status":
			response.append(state.name());
			break;
		case "config":
			response.appendLines("Printing global configuration.", cfg.toString());
			break;
		default:
			response.append("I could not resolve this command.");
			response.fail();
			break;
		}

		return response;
	}

	@Override
	public String getHelp() {
		ConsoleHelp help = new ConsoleHelp();

		help.appendLines("\tdebug -> starts debugging", "\tstop -> stops the execution",
				"\treset -> calls stop and debug", "\tpause -> pauses the game", "\tunpause -> unpauses the game",
				"\tconfig -> prints the configuration");

		return help.toString();
	}

	@Override
	public String getControllableName() {
		return null;
	}

	/**
	 * Changes state of application and alerts the window about it.
	 * 
	 * @param newState
	 *            New state of the application.
	 */
	public static void changeState(State newState) {
		if (state != State.ERROR) {
			lastState = state;
			state = newState;
		}

		ListenersManager.triggerFrameworkEvent("statechanged", new FrameworkEvent());
	}

	/**
	 * Finishes the application.
	 */
	public void finish() {
		changeState(State.FINISHED);
	}

	/**
	 * 
	 * @return Returns reference to main window.
	 */
	public MainWindow getWindow() {
		return window;
	}

	/**
	 * Should be called after bots were sucessfully selected and all the interests
	 * sent and loaded. -> that means that I am ready to initialize the bots and go
	 * to RUNNING state
	 * 
	 */
	public static void gameInitialized() {
		// Initialize the bots - now we should have all the data
		// that are needed during initialization
		ControllersManager.getInstance().initializeAgents();

		// Change state to running
		changeState(App.State.RUNNING);
	}

	/**
	 * 
	 * @param name Name of the command line argument.
	 * @return Returns command line argument with given name.
	 */
	public static CommandLineArgument getArgument(String name) {
		return arguments.get(name);
	}

	/**
	 * 
	 * @return Returns all the command line arguments stored inside hash map.
	 */
	public static HashMap<String, CommandLineArgument> getArguments() {
		return arguments;
	}

	/**
	 * 
	 * @param arguments New command line arguments to store.
	 */
	public static void setArguments(HashMap<String, CommandLineArgument> arguments) {
		App.arguments = arguments;
	}

}
