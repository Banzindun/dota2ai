package cz.cuni.mff.kocur.configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import cz.cuni.mff.kocur.exceptions.ConfigurationSavingError;
import cz.cuni.mff.kocur.exceptions.LoadingError;


/**
 * This is class that should save the supplied configuration to file, that is stored on supplied path.
 * @author kocur
 *
 */
public class ConfigurationSaver {
	private PrintWriter out;

	/**
	 * Constructor.
	 */
	public ConfigurationSaver(){
		
	}
	
	/**
	 * Loads output stream on the given path.
	 * @param path Path where the output should be stored.
	 * @throws LoadingError Thrown when the output stream couldn't be created.
	 */
	private void loadOS(String path) throws ConfigurationSavingError {
		File f = new File(path);
		try {
			f.createNewFile();
			if (f.exists() == false) throw new IOException();
			out = new PrintWriter(new FileOutputStream(f, false));
		} catch (IOException e) {
			throw new ConfigurationSavingError("Can not create file at [" + path + "]");
		}		
	}
	
	
	/**
	 * Saves configuration to path. 
	 * @param cfg Configuration to be saved.
	 * @param path Path where should we save it.
	 * @throws ConfigurationSavingError Thrown when there was an error during saving.
	 */
	public void save(Configuration cfg, String path) throws ConfigurationSavingError {
		loadOS(path);
			
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, false);
			out.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cfg));
		} catch (JsonProcessingException e) {
			throw new ConfigurationSavingError("Unable to deserailize the configuration object.", e);
		}
		    
		out.close();
	}
	
}
