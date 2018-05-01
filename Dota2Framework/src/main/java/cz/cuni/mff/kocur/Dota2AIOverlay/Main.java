package cz.cuni.mff.kocur.Dota2AIOverlay;

import cz.cuni.mff.kocur.Logging.Logger;
import cz.cuni.mff.kocur.Logging.Log;

public class Main {
	public static void main( String[] args )
    {
		Logger logger = Logger.getLogger(Main.class.getName());
		
		logger.log(Log.Type.MESSAGE, 1, "Starting APP.");
		App app = new App();
		app.start();
		app.run();		
		logger.log(Log.Type.MESSAGE, 1, "APP finished.");
    }
	
}
