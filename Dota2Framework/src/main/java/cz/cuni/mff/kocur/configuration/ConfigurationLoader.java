package cz.cuni.mff.kocur.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import cz.cuni.mff.kocur.Exceptions.LoadingError;
import cz.cuni.mff.kocur.Logging.Logger;

public class ConfigurationLoader {
	private Path path;
	
	/**
	 * logger registered in Logger class.	
	 */
	private static final Logger logger = Logger.getLogger(ConfigurationLoader.class.getName());
	
	/**
	 * GlobalConfiguration reference
	 */
	private GlobalConfiguration cfg = GlobalConfiguration.getInstance();
	
	
	/**
	 * Initial construct.
	 */
	public ConfigurationLoader() {
		
		
	}
	
		
	/**
	 * Initializes class.
	 */
	public ConfigurationLoader(String path) throws LoadingError {
		loadPath(path);
	}

	/**
	 * 
	 * @param cfg
	 * @throws LoadingError
	 */
	public void load(Configuration cfg) throws LoadingError {
		cfg.loadItems(loadJSON());
	}
	
	/**
	 * 
	 * @param cfg
	 * @throws LoadingError
	 */
	public void loadFromFile(Configuration cfg, File f) throws LoadingError {
		path = f.toPath();
		cfg.loadItems(loadJSON());
	}
	
	/**
	 * Resolves path, so it points to existing file which can be parsed
	 * @param _path Path to the file which contains JSON data
	 * @throws LoadingError If {@link #path} does not exist.
	 * @return True if path loaded correctly.
	 */
	private void loadPath(String _path) throws LoadingError {
		File f = new File(_path);
		if (f.exists()) path = Paths.get(_path);
		else throw new LoadingError("JSON file [" + _path + "] does not exist.");
	}
	
	
	/**
	 * 
	 * @param path
	 * @return
	 * @throws LoadingError
	 */
	public Map<String, CItem> loadJSON() throws LoadingError {
		Map<String, CItem> items = new HashMap<String, CItem>();
				
		// TODO check, that there are all the catches that are needed + return custom error messages 
		try {
			byte[] jsonData;
			jsonData = Files.readAllBytes(path);
			ObjectMapper objectMapper = new ObjectMapper();
	    	TypeFactory typeFactory = objectMapper.getTypeFactory();
	    	MapType itemsMapType = typeFactory.constructMapType(HashMap.class, String.class, CItem.class);
	    	items = objectMapper.readValue(jsonData, itemsMapType);
		} catch (IOException e) {
			e.printStackTrace();
			throw new LoadingError("JSON file [" + path + "] could not be parsed.");
		}		
		
		return items;
	}
	
	
	
	public void loadBotCFGFromJSON() throws LoadingError {
		// TODO check, that there are all the catches that are needed + return custom error messages 
		BotConfiguration botCFG = new BotConfiguration();		
		try {
			byte[] jsonData;
			jsonData = Files.readAllBytes(path);
			ObjectMapper objectMapper = new ObjectMapper();
	    	botCFG = objectMapper.readValue(jsonData, BotConfiguration.class);
	    	cfg.setBotCfg(botCFG);
		} catch (IOException e) {
			e.printStackTrace();
			throw new LoadingError("JSON file [" + path + "] could not be parsed.");
		}
	}
}
