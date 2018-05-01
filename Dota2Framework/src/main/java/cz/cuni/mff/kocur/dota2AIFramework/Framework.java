package cz.cuni.mff.kocur.dota2AIFramework;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.basic.BasicLookAndFeel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bulenkov.darcula.DarculaLaf;

import cz.cuni.mff.kocur.exceptions.SetupException;

/**
 * Class that represents the framework. 
 * Here is the main entrypoint to the application. 
 * @author kocur
 *
 */
public class Framework {
	/**
	 * Framework logger.
	 */
	private static Logger logger = LogManager.getLogger(Framework.class.getName());
	
	public static void main( String[] args )
    {
		// Set Darcula look and feel. We demand that, because the GUI would look trashy otherwise.
		try {
			BasicLookAndFeel darcula = new DarculaLaf();
	        UIManager.setLookAndFeel(darcula);
		} 
		catch (UnsupportedLookAndFeelException e) {
			logger.fatal("Unable to set darcula look and feel.", e);
			return;
		}
		
		// Create/get the application object
		App app = App.getInstance();
		
		// Setup the application.
		try {
			app.setup(args);
		} catch (SetupException e) {
			logger.fatal("Unable to setup the framework.", e);
			e.printStackTrace();
			return;
		}
		
		if (App.state == App.State.ERROR) {
			logger.fatal("Unable to setup the application");
			return;
		}
		
		// Start the app.
		app.start();		
    }
	
}


