package cz.cuni.mff.kocur.Dota2AIOverlay;
import cz.cuni.mff.kocur.Logging.*;
import cz.cuni.mff.kocur.Server.Server;
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
	
	private Server server = null;
	
	private boolean running = false;
	private boolean serverRunning = false;
	
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
		window = new Window(this);
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
			if (window.shouldStop()) running = false;
		}
	}
	
	/** 
	 * Starts the server.
	 */
	public void startServer() {
		if (server == null) {
			//server = new Server();
			serverRunning = true;
		}
		
	}
	
	/**
	 * 
	 * @return Returns true if the server is running.
	 */
	public boolean isServerRunning() {
		return serverRunning;
	}
	
	/**
	 * Stops the server.
	 * Can be stopped due to errors ..
	 */
	public void stopServer() {
		
		
		
		serverRunning = false;
	}
	
	/**
	 * 
	 * @return
	 */
	public Server getServer(){
		
		
		return server;
	}
	
	/**
	 * Starts the server and closes the gui.
	 */
	public void release() {
		startServer();
		window.stop();		
	}
	
	/**
	 * Starts server - called before window utilized.
	 */
	public void fullRelease() {
		
		
	}


}
