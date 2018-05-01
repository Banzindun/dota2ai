package cz.cuni.mff.kocur.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import cz.cuni.mff.kocur.Exceptions.LoadingError;


public class ConfigurationSaver {
	private PrintWriter out;

	/**
	 * 
	 * @param path
	 * @throws LoadingError 
	 */
	public ConfigurationSaver(){
	}
	
	/**
	 * 
	 * @param _path
	 * @throws LoadingError
	 */
	private void loadOS(String path) throws LoadingError {
		File f = new File(path);
		try {
			f.createNewFile();
			if (f.exists() == false) throw new IOException();
			out = new PrintWriter(new FileOutputStream(f, false));
		} catch (IOException e) {
			throw new LoadingError("Can not create file at [" + path + "]");
		}		
	}
	
	/**
	 * Saves configuration to file. Path needs to point to old configuration file, so it can be loaded, modified and saved. 
	 * @param path Path pointing to old configuration file.
	 * @throws LoadingError 
	 */
	public void save(Configuration cfg, String path) throws LoadingError {
		loadOS(path);
				
		StringBuilder builder = new StringBuilder();
		for (CItem i : cfg.getItems().values()) {
			builder.append(i.toString());			
		}
		out.write("{ \n" + builder.toString() + "} \n");
		out.close();
	}
	
}
