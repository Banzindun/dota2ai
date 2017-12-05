package cz.cuni.mff.kocur.Dota2AIOverlay;
import cz.cuni.mff.kocur.Logging.*;
import cz.cuni.mff.kocur.Configuration.GlobalConfiguration;
import cz.cuni.mff.kocur.Exceptions.OverlayException;
import cz.cuni.mff.kocur.Graphics.*;

/**
 * Starts window application for BOT configuration, management and debugging.
 */
public class App 
{
	/**
	 * logger registered in Logger class.	
	 */
	private static final Logger logger = Logger.getLogger(App.class.getName());
	
	/**
	 * GlobalConfiguration reference
	 */
	private GlobalConfiguration cfg = GlobalConfiguration.getInstance();
	
	/**
	 * Window of the application.
	 */
	private Window window;
	
	private boolean running;
	
	public App(){
		logger.log(Log.Type.MESSAGE, 1, "APP initialization.");
		    	
	}
	
	/**
	 * Finalize the class. 
	 */
	@Override
	public void finalize(){
		//saveConfig("C:\\Users\\Banzindun\\Dropbox\\Dota2AI\\data\\init2.cfg"); TO DO: should be saved inside finalize of GlobalConfiguration
	}
	
	
	/**
	 * Creates the window and runs the application.
	 */
	public void start() {
		window = new Window();
		window.start();
		running = true;
	}
	
	/**
	 * @return Returns true if the app is running.
	 */
	public boolean isRunning() {
		return running;		
	}
		
	
	/**
	 * Runs the application - mainloop here.
	 */
	public void run() {
		while(running) {
			running = step();
			if (window.shouldStop()) running = false;
		}
		System.out.println("OUT");
	}
	
	/**
	 * Makes one step towards something. Not sure if I will use it. 
	 * @return
	 */
	private boolean step() {
		
		return true;
	}

	
	
	/**
	 * Loads configuration.
	 * @param path
	 */
	private void loadConfig(String path) {
		// LOGS HERE
		try {
			// Loads configuration
			cfg.load(path);
		} catch (OverlayException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		    	
		// LOGS HERE
	}

	/**
	 * Saves configuration into file. 
	 * TO DO: Move this to GlobalConfiguration.
	 * @param path
	 */
	private void saveConfig(String path) {
		try {
			cfg.save(path);
		} catch (OverlayException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
