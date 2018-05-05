package cz.cuni.mff.kocur.server;

import java.util.HashMap;

import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.console.CommandResponse;
import cz.cuni.mff.kocur.console.ConsoleCommand;
import cz.cuni.mff.kocur.console.ConsoleManager;
import cz.cuni.mff.kocur.console.Controllable;
import cz.cuni.mff.kocur.server.ServerCommands.GameCommand;

/**
 * This class can issue in-game commands.
 * 
 *  
 * @author kocur
 *
 */
public class GameCommandsIssuer implements Controllable {
	
	/**
	 * Command to be issued.
	 */
	private GameCommand gc = null;
	
	/**
	 * Map of scenarios (arrays of commands).
	 */
	private HashMap<String, String[]> scenarios = new HashMap<String, String[]>();
	
	/**
	 * Constructor.
	 */
	public GameCommandsIssuer() {
		ConsoleManager.register(this);
	}
	
	/**
	 * Adds a command to scenario.
	 * @param name Name of the scenario, we are changing.
	 * @param str Console commands separated by ;.
	 */
	public void addScenario(String name, String str) {
		String[] cmds = str.trim().split(";");
		addScenario(name, cmds);		
	}

	/**
	 * Creates a new scenario in scenarios.
	 * @param name Name of the scenario.
	 * @param cmds It's commands.
	 */
	public void addScenario(String name, String[] cmds) {
		scenarios.put(name, cmds);
	}
	
	/**
	 * Removes scenario with given name.
	 * @param name Name of the scenario to be removed.
	 */
	public void removeScenario(String name) {
		scenarios.remove(name);
	}
	
	/**
	 * Loads the scenarios to this class. 
	 * @param path Path where the scenarios should be located.
	 */
	public void load(String path) {
		
	}
	
	/**
	 * Saves the scenarios from this class to file.
	 * @param path Path, where to save the scenarios.
	 */
	public void save(String path) {
		
	}
	
	/**
	 * 
	 * @return Returns the game command and then looses the reference to it.
	 */
	public GameCommand getCommand() {
		GameCommand _gc = gc;
		gc = null;
		return _gc;
	}
	
	@Override
	public CommandResponse command(ConsoleCommand cmd) {
		CommandResponse r = new CommandResponse();
		String first = cmd.getField();
		String second = cmd.getField();
		
		if (first == null || second == null) 
			return r.fail("One of arguments wasn't specified.");
		
		String[] cmds = scenarios.get(second);
		
		if (cmds == null) 
			return r.fail("Unknown scenario name.");
				
		if (first.equals("send")) {
			gc  = new GameCommand(cmds);
			r.pass("Commands scheduled.");
		}else if (first.equals("list")) {
			r.appendLines(cmds);
		}else {
			r.fail("Unknown command.");
		}
		
		return r;
	}

	@Override
	public String getHelp() {
		IndentationStringBuilder b = new IndentationStringBuilder(); 
		
		b.appendLine("Available scenarios: ");
		b.indent();
		
		for (String s : scenarios.keySet()) {
			b.appendLine(s);
		}
		
		b.deindent();
		b.appendLine("Write -" + getControllableName() + " [scenario name], to get list the commands of the given scenario");
		
		return b.toString();
	}

	@Override
	public String getControllableName() {
		return "gameConsole";
	}

	
	
	
	
	
}
