package cz.cuni.mff.kocur.configuration;

import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import cz.cuni.mff.kocur.exceptions.LoadingError;

/**
 * This class makes loading the configurations possible. It uses
 * {@link com.fasterxml.jackson.databind.ObjectMapper} and other Jackson
 * functionalities to load both bot and framework configurations.
 * 
 * @author kocur
 *
 */
public class ConfigurationLoader {
	/**
	 * Logger registered for this class.
	 */
	private static final Logger logger = LogManager.getLogger(ConfigurationLoader.class.getName());

	/**
	 * Path to the configuration.
	 */
	private Path path = null;

	/**
	 * Constructor.
	 */
	public ConfigurationLoader() {

	}

	/**
	 * Constructor that takes and uses path to initialize itself.
	 * 
	 * @param path
	 *            String representing the path of the configuration that will be
	 *            loaded.
	 * @throws LoadingError
	 *             If loading fails.
	 */
	public ConfigurationLoader(String path) throws LoadingError {
		loadPath(path);
	}

	/**
	 * Resolves path, so it points to an existing file that can be parsed
	 * 
	 * @param path
	 *            Path to the file which contains JSON data
	 * @throws LoadingError
	 *             If {@link #path} does not exist.
	 */
	private void loadPath(String path) throws LoadingError {
		File f = new File(path);
		if (!f.exists()) {
			logger.error("Unable to load configuration file at " + path);
			throw new LoadingError("Unable to load configuration file at " + path);
		}

		// Check if the supplied path is file
		if (f.isFile())
			this.path = Paths.get(path);
		else {
			path = null; // Null the path, so the loading will fail etc.
			throw new LoadingError("File at " + path + " is not a file!");
		}
	}

	/**
	 * Loads the Json to Map containing Strings.
	 * 
	 * @return Returns map of items obtained from JSON.
	 * @throws LoadingError
	 *             when unable to parse JSON file.
	 */
	public Map<String, String> loadJSON() throws LoadingError {
		Map<String, String> items = new HashMap<String, String>();
		try {
			byte[] jsonData;
			jsonData = Files.readAllBytes(path);
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(Include.NON_NULL);
			TypeFactory typeFactory = objectMapper.getTypeFactory();
			MapType itemsMapType = typeFactory.constructMapType(HashMap.class, String.class, String.class);
			items = objectMapper.readValue(jsonData, itemsMapType);
		} catch (IOException e) {
			logger.error("Unable to parse JSON configuration file at " + path.toString(), e);
			throw new LoadingError("Unable to parse JSON configuration file at " + path.toString());
		}
		return items;
	}

	/**
	 * Loads class from path supplied on construction/loadPath.
	 * 
	 * @param <T>
	 *            Type of the loaded class.
	 * @param cl
	 *            Class where should be the information stored. (and in what format
	 *            it will be returned)
	 * @return Returns new object of passed class that contains all the JSON data
	 *         that could be parsed.
	 * @throws LoadingError
	 *             Thrown when the loading fails. (bad path, parsing failed..)
	 */
	public <T> T loadClass(Class<T> cl) throws LoadingError {
		checkPath();

		// Create new instance of the supplied class
		T out;
		try {
			out = cl.newInstance();
		} catch (InstantiationException | IllegalAccessException e1) {
			logger.error("Unable to create instance of class:" + cl.getName());
			throw new LoadingError("Unable to create instance of class:" + cl.getName());
		}

		// Parse the file
		try {
			byte[] jsonData;
			jsonData = Files.readAllBytes(path);
			ObjectMapper objectMapper = new ObjectMapper();
			out = objectMapper.readValue(jsonData, cl);
		} catch (IOException e) {
			logger.error("Unable to parse JSON configuration file at " + path.toString(), e);
			throw new LoadingError("JSON file [" + path + "] could not be parsed.");
		}

		// Loaded successfully, return the new instance of the parsed class
		return out;
	}

	/**
	 * Basically does the same thing as {@link #loadClass(Class)}, but outputs
	 * directly to {@link cz.cuni.mff.kocur.configuration.HeroConfiguration}
	 * 
	 * @return Returns new bot configuration.
	 * @throws LoadingError
	 *             If the loading failed.
	 */
	public HeroConfiguration loadBotConfiguration() throws LoadingError {
		checkPath();

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			byte[] jsonData;
			jsonData = Files.readAllBytes(path);
			objectMapper.setSerializationInclusion(Include.NON_NULL);
			HeroConfiguration bcfg = objectMapper.readValue(jsonData, HeroConfiguration.class);
			bcfg.setPath(path.toAbsolutePath().toString());
			return bcfg;
		} catch (IOException e) {
			logger.fatal("Could not load BotConfiguration from path:" + path, e);
			throw new LoadingError("Could not load BotConfiguration from path:" + path);
		}
	}

	/**
	 * Checks if the path is not null. If it is it will throw LoadingError.
	 * 
	 * @throws LoadingError
	 *             If the path stored inside this instance is null.
	 */
	private void checkPath() throws LoadingError {
		if (path == null) {
			logger.error("Supplied path is null!");
			throw new LoadingError("Supplied path is null!");
		}
	}

	/**
	 * Loads the framework configuration.
	 * 
	 * @throws LoadingError
	 *             Thrown if loading failed.
	 */
	public void loadFrameworkConfiguration() throws LoadingError {
		checkPath();

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			byte[] jsonData;
			jsonData = Files.readAllBytes(path);
			objectMapper.setSerializationInclusion(Include.NON_NULL);
			objectMapper.readValue(jsonData, FrameworkConfiguration.class);
		} catch (IOException e) {
			logger.fatal("Could not load GlobalConfiguration.", e);
		}
	}
}
