package cz.cuni.mff.kocur.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.jetty.server.Server;

import cz.cuni.mff.kocur.configuration.FrameworkConfiguration;
import cz.cuni.mff.kocur.console.CommandResponse;
import cz.cuni.mff.kocur.console.ConsoleHelp;
import cz.cuni.mff.kocur.console.ConsoleManager;
import cz.cuni.mff.kocur.console.Controllable;
import cz.cuni.mff.kocur.exceptions.KeyNotFound;

/**
 * ServerManager is a class that stores a server inside. It's goal is to manage
 * the server and RequestHandler, that the server uses to handle the incoming
 * requests.
 * 
 * @author kocur
 *
 */
public class ServerManager implements Controllable {
	/**
	 * Instance of global configuration.
	 */
	private final static FrameworkConfiguration cfg = FrameworkConfiguration.getInstance();

	/**
	 * logger registered in Logger class.
	 */
	private static final Logger logger = LogManager.getLogger(ServerManager.class.getName());

	/**
	 * The server we are managing.
	 */
	private Server server = null;

	/**
	 * Handler that handles all the incoming requests. We divide the work there etc.
	 */
	private RequestHandler handler;

	/**
	 * Creates the server on given port.
	 * 
	 * @param port
	 *            port on which this service should run
	 */
	public ServerManager() {
		ConsoleManager.register(this);
	}

	/**
	 * Takes the port from configuration and setups the server. Throws error if the
	 * port value couldn't be read or if it is not an int.
	 * 
	 * @throws KeyNotFound
	 *             when value for port was not supplied in configuration.
	 * @throws NumberFormatException
	 *             if value in configuration is not integer.
	 */
	public void setup() throws NumberFormatException, KeyNotFound {
		// Get the port from configuration
		int port = Integer.parseInt(cfg.getConfigValue("port"));

		logger.info("Binding server to port: " + port);

		// Create new server
		server = new Server(port);

		// Set the request handler
		handler = new RequestHandler();
		server.setHandler(handler);
	}

	/**
	 * Starts the server. 
	 * @throws Exception If something went wrong.
	 */
	public void startServer() throws Exception {
		logger.info("Starting server");
		if (!server.isRunning())
			server.start();

	}

	/**
	 * Stops the server.
	 * @throws Exception If something went wrong.
	 */
	public void stopServer() throws Exception {
		logger.info("Stopping the server.");
		handler.serverStopping();
		server.stop();
	}

	/**
	 * 
	 * @return Returns true if the server is running, false otherwise.
	 */
	public boolean isRunning() {
		return server.isRunning();
	}

	/**
	 * Pauses the game.
	 */
	public void pauseGame() {
		logger.info("Pausing the server.");
		handler.pause();
	}

	/**
	 * Unpauses the game.
	 */
	public void unpauseGame() {
		logger.info("Unpausing server.");
		handler.unpause();
	}

	@Override
	public CommandResponse command(cz.cuni.mff.kocur.console.ConsoleCommand cmd) {
		CommandResponse res = new CommandResponse();

		String field = cmd.getField();

		// Find command
		switch (field) {
		case "start":
			try {
				startServer();
			} catch (Exception e) {
				logger.error("Unable to start the server.", e);
			}
			if (server.isRunning())
				res.pass("Server is running.");
			else
				res.fail("Server is not running. Failed.");
			break;
		case "stop":
			try {
				stopServer();
			} catch (Exception e) {
				logger.error("Unable to stop the server.", e);
			}
			if (server.isRunning())
				res.fail("Failed to stop the server.");
			else
				res.pass("Server stopped.");

			break;
		default:
			res.fail("Unknown command");
			break;
		}

		return res;
	}

	@Override
	public String getHelp() {
		ConsoleHelp help = new ConsoleHelp();

		help.appendLines("Server commands are:", "\tstop - stops the server", "\tstart - starts the server");

		return help.toString();
	}

	@Override
	public String getControllableName() {
		return "server";
	}

	/**
	 * 
	 * @return Returns the RequestHandler used by the server.
	 */
	public RequestHandler getHandler() {
		return handler;
	}
}
