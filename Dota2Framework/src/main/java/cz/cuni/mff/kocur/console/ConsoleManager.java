package cz.cuni.mff.kocur.console;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.dota2AIFramework.App;
import cz.cuni.mff.kocur.dota2AIFramework.Setup;
import cz.cuni.mff.kocur.graphics.ConsolePanel;

/**
 * Class that manages the Controllables and the ConsolePanel and the
 * CommandHistory.
 * 
 * Controllables should be objects, that allow the console to control them.
 * ConsolePanel displays the console. CommandHistory stores history of commands,
 * so ConsolePanel can use them for better usability.
 * 
 * @author kocur
 *
 */
public class ConsoleManager {
	/**
	 * Logger for this class.
	 */
	private static Logger logger = LogManager.getLogger(ConsoleManager.class.getName());

	/**
	 * Controllables that we registered inside ConsoleManager.
	 * {@see cz.cuni.mff.kocur.console.Controlable#}
	 */
	private static Map<String, Controllable> registeredControllables = new HashMap<String, Controllable>();

	/**
	 * Reference to ConsolePane where is the console shown.
	 */
	private static ConsolePanel consolePane;

	/**
	 * Commands history is stored here and can be displayed in field on arrow up or
	 * down.
	 */
	private static CommandHistory history = new CommandHistory();

	/**
	 * Sets the reference to console pane where all the logs are displayed.
	 * 
	 * @param consolePane
	 *            reference to ConsolePane that contains the console
	 */
	public static void setConsolePane(ConsolePanel cp) {
		consolePane = cp;
	}

	/**
	 * Registers given controllable inside registeredControlables
	 * 
	 * @param controllable
	 *            controllable that should be registered
	 */
	public static void register(Controllable controllable) {
		logger.info("Registering new controllable with name: " + controllable.getControllableName());
		registeredControllables.put(controllable.getControllableName(), controllable);
	}

	/**
	 * Unregisters the given controllable from controllables.
	 * 
	 * @param controllable
	 *            Controllable object.
	 */
	public static void unregister(Controllable controllable) {
		logger.info("Unregistering controllable with name: " + controllable.getControllableName());
		registeredControllables.remove(controllable.getControllableName());
	}

	/**
	 * 
	 * @param name
	 *            Name of the controllable.
	 * @return Returns the controllable with given name.
	 */
	public static Controllable getControllable(String name) {
		return registeredControllables.get(name);
	}

	/**
	 * 
	 * @return Returns array of controllable names.
	 */
	public static String[] getControllableNames() {
		return registeredControllables.keySet().toArray(new String[registeredControllables.size()]);
	}

	/**
	 * 
	 * @return Returns the command history.
	 */
	public static CommandHistory getHistory() {
		return history;
	}

	/**
	 * Commands. This tries to send command to given controllable. If non found
	 * (using -ControllableName), the command is passed to App's controllable
	 * interface.
	 * 
	 * @param cmd Issued console command.
	 * @return Returns the command's response.
	 */
	public static CommandResponse command(ConsoleCommand cmd) {
		history.addCommand(cmd);

		String firstField = cmd.peekField();
		CommandResponse response = new CommandResponse();

		// Check if we are passing command to controllable
		if (firstField.indexOf('-') == 0) {
			cmd.getField();
			// Get supplied name of the controllable
			String cName = firstField.substring(1, firstField.length());

			// Get controllable from controllables
			Controllable c = getControllable(cName);

			// If not null pass to the next controllable
			if (c == null)
				response.fail("\tUnable to find controllable with name " + cName);
			else {
				response = c.command(cmd);
			}
		} else if (firstField.equals("help") && cmd.size() > 1) {
			// Skip "help" field
			cmd.getField();

			// I want to show help for controllable -> get its name
			String cName = cmd.getField();

			if (cName.equals("controllables")) {
				response.appendLine("Controllables you can use are:");
				response.appendLines(getControllableNames());
				return response.pass();
			}

			Controllable c = getControllable(cName);

			if (c == null)
				response.fail("\tUnable to find controllable with name " + cName);
			else
				return response.pass(c.getHelp());
		} else if (firstField.equals("help") && cmd.size() == 1) {
			response.pass(getHelp());
		} else {
			// Else we pass the command to the app
			response = App.getInstance().command(cmd);
		}

		// Check if response should be written to file - if so, do it
		String fileName = cmd.getFileName();
		if (fileName != null) {
			writeToFile(response, fileName);
		}

		return response;
	}

	/**
	 * Writes command response to file. 
	 * @param res Command's response.
	 * @param fileName Name of the file where we should write the result.
	 */
	private static void writeToFile(CommandResponse res, String fileName) {
		// Get working directory
		String outputDir = Setup.getOutputDir();
		
		PrintWriter printWriter;
		try {
			printWriter = new PrintWriter(new FileWriter(outputDir + fileName));
			printWriter.write(res.toString());
			printWriter.close();

			res.clear();
			logger.info("Console output saved to: " + outputDir + fileName);
		} catch (IOException e) {
			res.appendLine("UNABLE TO WRITE TO FILE ON LOCATION: " + outputDir + fileName);
			logger.warn("Unable to write console output to: " + outputDir + fileName);
		}
	}

	/**
	 * 
	 * @return Returns help.
	 */
	public static String getHelp() {
		ConsoleHelp help = new ConsoleHelp();
		help.appendLines(new String[] { "Please specify a command. And hit enter to issue it.",
				"If you want to control a registered controllable use command written like this:",
				"\t -controllable [commands] -> where controllable is name of the controllable (handler, server ..)",
				"You can use help to display information about any controllable. Like: help controllable",
				"Commands you can use:" });

		help.append(App.getInstance().getHelp());
		help.appendLine(consolePane.getHelp());

		return help.toString();
	}
}
