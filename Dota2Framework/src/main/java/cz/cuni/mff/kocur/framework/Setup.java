package cz.cuni.mff.kocur.framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.configuration.ConfigurationLoader;
import cz.cuni.mff.kocur.configuration.FrameworkConfiguration;
import cz.cuni.mff.kocur.exceptions.LoadingError;
import cz.cuni.mff.kocur.exceptions.SetupException;
import cz.cuni.mff.kocur.world.BaseViewer;
import cz.cuni.mff.kocur.world.GridLoader;
import cz.cuni.mff.kocur.world.ItemsLoader;

/**
 * This class setups the application. It loads important directoris and paths
 * (workingDirectory, outputDirectory ..) and checks, that necessary files are
 * there (grid.data, items.data), if they are not the user should be alerted and
 * application should end.
 * 
 * 
 * This class also contains function for moving the game mod automatically, if
 * user wants to do so. And for creating empty bots inside the mod's directory.
 * 
 * @author kocur
 *
 */
public class Setup {

	/**
	 * Logger for setup class.
	 */
	private static final Logger logger = LogManager.getLogger(Setup.class.getName());

	/**
	 * Empty script, that we will be writing for each ai bot, inside the bots
	 * directory (in the addon).
	 */
	private static String emptyAiScript = "function Think() return 1.0 end";

	/**
	 * Working directory of this application.
	 */
	private static String workingDir;

	/**
	 * Output directory of this application.
	 */
	private static String outputDir;

	/**
	 * Path to dota2. Should be supplied in configuration.
	 */
	private static String dota2Path = "";

	/**
	 * Path to dota2ai addon. It should be located in data folder inside release.
	 */
	private static String dota2aiAddonPath = "";

	/**
	 * Path to directory, that would normally contain bot scripts. We need to
	 * override them, if we are implementing a bot, that is not currently in the
	 * player's slot. Otherwise the game would issue it's own commands to our bot.
	 */
	private static String dota2aiBotsDirectory = "";

	/**
	 * Path to gridExporter addon. It should be located inside release/data folder.
	 */
	private static String gridExporterAddonPath = "";

	/**
	 * Path to configuration.
	 */
	private static String configurationPath;

	/**
	 * Files that must exist.
	 */
	private static final String[] files = { path("data", "config.cfg"), path("data", "grid.data"),
			path("data", "items.data"), };

	/**
	 * Directories that must exist.
	 */
	private static final String[] dirs = { "data", "out", "addons", path("addons", "dota2ai"),
			path("addons", "gridExporter") };

	/**
	 * Setups up all the necessary files. Does a cleanup of the working directory
	 * etc.
	 * 
	 * @param app
	 *            Application.
	 * @throws SetupException
	 *             If the setup failed.
	 */
	public static void setup(App app) throws SetupException {
		// Get the working directory
		workingDir = Paths.get("").toAbsolutePath().toString() + File.separator;
		logger.info("Working directory path is: " + workingDir);

		// Setup output directory (where we will be storing the output files)
		setOutputDir(workingDir + "out" + File.separator);
		logger.info("Output directory is:" + outputDir);

		// Setup paths, that the framework might need
		setupPaths();

		// Check that we have all files and directory structure, that we need
		checkStructure();

		// Initialize framework configuration
		initFrameworkConfiguration();

		dota2Path = FrameworkConfiguration.getInstance().getConfigValue("dota2_path");
		checkDota2Path();

		// Setup path to Dota2ai bots folder.
		Setup.setDota2aiBotsDirectory(
				path(dota2Path, "game", "dota_addons", "dota2ai", "scripts", "vscripts", "bots", ""));

		// Try to load grid from file - this will be just a test.
		tryToLoadGrid();

		// Try to load items
		tryToLoadItems();
	}

	/**
	 * Checks that the supplied dota2 path is correct. If none is supplied, then the
	 * user is asked for it by pop-up window.
	 */
	private static void checkDota2Path() {
		FrameworkConfiguration fcfg = FrameworkConfiguration.getInstance();
		if (dota2Path.length() == 0) {
			// Get the dota2path from user
			while (true) {
				dota2Path = (String) JOptionPane.showInputDialog("Enter path to Dota2 game folder:",
						"C:\\Program Files (x86)\\Steam\\steamapps\\common\\dota 2 beta");

				if (dota2Path != null && Files.exists(Paths.get(dota2Path))) {
					break;
				}
			}

			fcfg.setConfigValue("dota2_path", dota2Path);
			fcfg.save();
		}
		logger.info("I have loaded dota2_path " + dota2Path);
	}

	/**
	 * Setups all the necessary paths a release working directory should have.
	 */
	private static void setupPaths() {
		// Load the configuration path
		setConfigurationPath(workingDir + path("data", "config.cfg"));

		// Setup paths to addons
		setDota2aiAddonPath(workingDir + path("addons", "dota2ai"));
		setGridExporterAddonPath(workingDir + path("addons", "gridExporter"));

	}

	/**
	 * Tries to load the grid.
	 * 
	 * @throws SetupException
	 *             If loading went wrong.
	 */
	public static void tryToLoadGrid() throws SetupException {
		// Load the grid map - just a test, should be done elsewhere..
		try {
			GridLoader gl = new GridLoader(workingDir + "data/grid.data");
			gl.parse();

			BaseViewer.initImage();
		} catch (FileNotFoundException e) {
			logger.fatal("I have not found the grid.data at:" + workingDir + "data/");
			throw new SetupException("Could not setup the debug.", e);
		} catch (LoadingError e) {
			logger.fatal("Could not load the grid data.", e);
			e.printStackTrace();
			throw new SetupException("Could not setup the debug.", e);
		}
	}

	/**
	 * Tries to load items.
	 * 
	 * @throws SetupException
	 *             Throws setup exception if loaded incorrectly.
	 */
	public static void tryToLoadItems() throws SetupException {
		// Load the grid map - just a test, should be done elsewhere..
		try {
			ItemsLoader loader = new ItemsLoader();
			loader.loadItems(workingDir + "data\\items.data");
			// System.out.println(ItemsBase.getToString());
		} catch (FileNotFoundException e) {
			logger.fatal("I have not found the items.data at:" + workingDir + "data/");
			throw new SetupException("Could not setup the debug.", e);
		} catch (LoadingError e) {
			logger.fatal("Could not load the items data.", e);
			e.printStackTrace();
			throw new SetupException("Could not setup the debug.", e);
		}
	}

	/**
	 * Checks the project's structure. Adds folders that aren't there.
	 * 
	 * @throws SetupException
	 *             If there is something wrong with the structure.
	 */
	public static void checkStructure() throws SetupException {
		// Create directories, mainly out directory for storing output
		createDirs(dirs);

		if (checkDirs(dirs) == false) {
			logger.error("Missing directories.");
			throw new SetupException("Missing directories");
		}

		if (checkFiles(files) == false) {
			logger.fatal("Missing files.");
			throw new SetupException("Missing files.");
		}
	}

	/**
	 * Initializes the framework configuration.
	 * 
	 * @throws SetupException
	 *             Thrown if configuration init failed.
	 */
	private static void initFrameworkConfiguration() throws SetupException {
		try {
			ConfigurationLoader loader = new ConfigurationLoader(configurationPath);
			loader.loadFrameworkConfiguration();
		} catch (LoadingError e) {
			logger.error("Could not load global configuration from: ", configurationPath);
			throw new SetupException("Could not load the framework configuration.");
		}

		FrameworkConfiguration.getInstance().setPath(configurationPath);
	}

	/**
	 * Creates path from array of strings. (Joints them by separator)
	 * 
	 * @param args
	 *            Array of strings.
	 * @return Returns strings joined by file separator (ab/c/d/ for [ab, c, d]).
	 */
	public static String path(String... args) {
		return String.join(File.separator, args);
	}

	/**
	 * Checks if all required directories exist.
	 * 
	 * @param dirs
	 *            Names of the directories.
	 * @return Returns true, if all the directories exist in working directory.
	 */
	private static boolean checkDirs(String[] dirs) {
		for (String s : dirs) {
			File f = new File(workingDir + s);
			if (f.exists() && f.isDirectory())
				;
			else {
				logger.error("Missing directory: " + s);
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if all required directories exist.
	 * 
	 * @param dirs
	 *            Names of the directories.
	 * @return Returns true, if all the directories exist in working directory.
	 */
	private static boolean createDirs(String[] dirs) {
		boolean status = true;

		for (String s : dirs) {
			File f = new File(workingDir + s);

			if (!f.exists()) {
				status = f.mkdir();
				if (status)
					logger.info("I have created a new directory: " + s + " at " + workingDir + s);
			} else if (f.exists() && f.isFile()) {
				status = f.mkdir();
				if (status)
					logger.info("I have created a new directory: " + s + " at " + workingDir + s);
			}

			if (status == false)
				logger.warn("Unable to create directory" + s);
		}

		return status;
	}

	/**
	 * Checks if the files passed as arguments do exist.
	 * 
	 * @param files
	 *            Names of the directories.
	 * @return Returns true if they exist.
	 */
	private static boolean checkFiles(String[] files) {
		for (String s : files) {
			File f = new File(workingDir + s);
			if (f.exists() && f.isFile())
				;
			else {
				logger.error("Missing file: " + s);
				return false;
			}
		}

		return true;
	}

	/**
	 * 
	 * @return Returns the application's working directory.
	 */
	public static String getWorkingDir() {
		return workingDir;
	}

	/**
	 * 
	 * @param workingDir
	 *            Path to working directory.
	 */
	public static void setWorkingDir(String workingDir) {
		Setup.workingDir = workingDir;
	}

	/**
	 * 
	 * @return Returns string representation of working directory.
	 */
	public static String getOutputDir() {
		return outputDir;
	}

	/**
	 * 
	 * @param outputDir
	 *            Path to output directory.
	 */
	public static void setOutputDir(String outputDir) {
		Setup.outputDir = outputDir;
	}

	/**
	 * 
	 * @return Returns path to dota2 files.
	 */
	public static String getDota2Path() {
		return dota2Path;
	}

	/**
	 * 
	 * @param dota2Path
	 *            New path to dota2 files.
	 */
	public static void setDota2Path(String dota2Path) {
		Setup.dota2Path = dota2Path;
	}

	/**
	 * 
	 * @return Returns path to framework configuration.
	 */
	public static String getConfigurationPath() {
		return configurationPath;
	}

	/**
	 * 
	 * @param configurationPath
	 *            New path to configuration.
	 */
	public static void setConfigurationPath(String configurationPath) {
		Setup.configurationPath = configurationPath;
	}

	/**
	 * Installs Dota 2 AI Addon.
	 * 
	 * @throws SetupException
	 *             Thrown if something went wrong while moving the files.
	 */
	public static void installDota2aiAddon() throws SetupException {
		String source = dota2aiAddonPath;
		File srcDir = new File(source);

		String destination = dota2Path;
		File destDir = new File(destination);

		logger.info("Installing the dota2ai addon to dota2 directory. [" + source + " -> " + destination + "]");

		try {
			FileUtils.copyDirectory(srcDir, destDir);
		} catch (IOException e) {
			e.printStackTrace();
			logger.fatal("Unable to move dota2ai addon to the game's directory");
			throw new SetupException("Unable to move dota2ai addon to the game's directory", e);
		}

		/*
		 * try { FileUtils.copyDirectory(srcDir, destDir); } catch (IOException e) {
		 * e.printStackTrace(); }
		 */
		/*
		 * System.out.println("SOURCE"); File[] filesList = srcDir.listFiles(); for
		 * (File f : filesList) { if (f.isDirectory()) System.out.println(f.getName());
		 * if (f.isFile()) { System.out.println(f.getName()); } }
		 * 
		 * System.out.println("DESTINATION"); filesList = destDir.listFiles(); for (File
		 * f : filesList) { if (f.isDirectory()) System.out.println(f.getName()); if
		 * (f.isFile()) { System.out.println(f.getName()); } }
		 */

	}

	/**
	 * Installs Dota 2 GridExporter Addon.
	 * 
	 * @throws SetupException
	 *             Thrown if something went wrong while moving the files.
	 */
	public static void installGridExporterAddon() throws SetupException {
		String source = gridExporterAddonPath;
		File srcDir = new File(source);

		String destination = dota2Path;
		File destDir = new File(destination);

		logger.info("Installing the dota2ai addon to dota2 directory. [" + source + " -> " + destination + "]");

		try {
			FileUtils.copyDirectory(srcDir, destDir);
		} catch (IOException e) {
			e.printStackTrace();
			logger.fatal("Unable to move gridExporter addon to the game's directory");
			throw new SetupException("Unable to move gridExporter addon to the game's directory", e);
		}

		/*
		 * try { FileUtils.copyDirectory(srcDir, destDir); } catch (IOException e) {
		 * e.printStackTrace(); }
		 */

		/*
		 * System.out.println("SOURCE"); File[] filesList = srcDir.listFiles(); for
		 * (File f : filesList) { if (f.isDirectory()) System.out.println(f.getName());
		 * if (f.isFile()) { System.out.println(f.getName()); } }
		 * 
		 * System.out.println("DESTINATION"); filesList = destDir.listFiles(); for (File
		 * f : filesList) { if (f.isDirectory()) System.out.println(f.getName()); if
		 * (f.isFile()) { System.out.println(f.getName()); } }
		 */

	}

	/**
	 * 
	 * @return Returns path to dota2 ai addon.
	 */
	public static String getDota2aiAddonPath() {
		return dota2aiAddonPath;
	}

	/**
	 * Sets path to dota2ai addon.
	 * 
	 * @param dota2aiAddonPath
	 *            Sets new path to dota2ai addon.
	 */
	public static void setDota2aiAddonPath(String dota2aiAddonPath) {
		Setup.dota2aiAddonPath = dota2aiAddonPath;
	}

	/**
	 * 
	 * @return Returns path to gridExporter addon.
	 */
	public static String getGridExporterAddonPath() {
		return gridExporterAddonPath;
	}

	/**
	 * Sets gridExporter addon path.
	 * 
	 * @param gridExporterAddonPath
	 *            New path to gridExporter addon.
	 */
	public static void setGridExporterAddonPath(String gridExporterAddonPath) {
		Setup.gridExporterAddonPath = gridExporterAddonPath;
	}

	/**
	 * 
	 * @return Returns path to bots folder inside the dota2ai addon's directory.
	 */
	public static String getDota2aiBotsDirectory() {
		return dota2aiBotsDirectory;
	}

	/**
	 * Sets path to dota2ai bots directory.
	 * 
	 * @param dota2aiBotsDirectory
	 *            New path leading to bots directory located inside the dota2ai
	 *            addon.
	 */
	public static void setDota2aiBotsDirectory(String dota2aiBotsDirectory) {
		Setup.dota2aiBotsDirectory = dota2aiBotsDirectory;
	}

	/**
	 * Overrides scripts for ais inside bot directory inside dota2ai addon. This is
	 * because ais are fake clients on the game's side. And those clients internally
	 * use their own scripts. Those we override by placing scripts in bot directory.
	 * We place there a script, that does nothing. (so we might control the bot
	 * ourselves)
	 * 
	 * @param aisNames
	 *            ArrayList of hero champion names, fro which there should be a
	 *            empty script created.
	 */
	public static void overrideAisScripts(ArrayList<String> aisNames) {
		moveOldAisScripts(aisNames);

		for (String name : aisNames) {
			String path = dota2aiBotsDirectory + "bot_" + name + ".lua";

			File f = new File(path);
			try (FileWriter writer = new FileWriter(f);) {
				f.createNewFile();
				writer.write(emptyAiScript);
			} catch (IOException e) {
				logger.error("Unable to create file: " + path);
			}

		}

	}

	/**
	 * Checks, that _bot directory inside dota2ai addon exists. Then goes through
	 * all passed aisNames anc checks, if there are some scripts with given names
	 * (bot_ + name + .lua). If so, it then moves them to _bot directory. (So that
	 * the user doesn't loose some important bot scritps).
	 * 
	 * @param aisNames
	 *            Names of agent's heros.
	 */
	private static void moveOldAisScripts(ArrayList<String> aisNames) {
		// Make sure _bots folder is there
		File f = new File(dota2aiBotsDirectory + "_bots");
		if (f.exists() == false)
			f.mkdir();

		for (String name : aisNames) {
			String path = dota2aiBotsDirectory + "bot_" + name + ".lua";

			f = new File(path);
			if (f.exists()) {
				logger.warn("I have found an existing script inside bots directory: " + name
						+ " I am moving it to _bots directory.");
				f.renameTo(new File(dota2aiBotsDirectory + path("_bots", "bot_" + name + ".lua")));
			}
		}

	}

}
