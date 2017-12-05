package cz.cuni.mff.kocur.Graphics;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cz.cuni.mff.kocur.Configuration.GlobalConfiguration;
import cz.cuni.mff.kocur.Logging.Logger;


/**
 * Class that handles mostly the window graphics etc. 
 * @author Banzindun
 *
 */
public class Window {
	/**
	 * Get logger registered for this class.
	 */
	private static final Logger logger = Logger.getLogger(Window.class.getName());
	
	/**
	 * Global configuration that stores bot configurations.
	 */
	private GlobalConfiguration cfg = GlobalConfiguration.getInstance();
	
	/**
	 * True if window is running.
	 */
	private boolean running = false;
	
	/**
	 * True if window should stop. (so it can save the progress etc.)
	 */
	private boolean shouldStop = false;
	
	
	/**
	 * New java frame for displaying the overlay.
	 */
	private JFrame frame = new JFrame("Dota2AI");
	
	
	/**
	 * JTabbedPane for storing tabs
	 */
	private JTabbedPane tabs = new JTabbedPane(); 
	
	/**
	 * Lets user choose bot configuration files and start the bots.
	 */
	private MainPane main = new MainPane();
	
	/**
	 * Lets user control the bots and UI.
	 */
	private ConsolePane console = new ConsolePane();
	
	/**
	 * Here are displayed the logs.
	 */
	private LogsPane logs = new LogsPane();
	
	/**
	 * Configuration of bots. 
	 */
	private ConfigurationPane configuration = new ConfigurationPane();
		
	
	/**
	 * Window constructor. Does nothing now. WHY??
	 */
	public Window(){
		
	}

	
	/**
	 * Starts the window.
	 */
	public void start() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createWindow();
				shouldStop = true;
			}
		});		
	}
	
	/**
	 * 
	 * @return true if app is running.
	 */
	public boolean isRunning() {
		return running;
	}
	
	
	/**
	 * 
	 * @return true if app is sheduled to stop.
	 */
	public boolean shouldStop() {
		return shouldStop;		
	}
	
	
	/**
	 * Creates the window.
	 */
	private void createWindow() {
		// Decorate window
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		// Create tabs
    	createTabs();
    	
    	// Set sizes
    	frame.setMinimumSize(new Dimension(400, 300));
    	frame.setPreferredSize(new Dimension(800, 600));
    	
    	// Add close operation.
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.addWindowListener(new java.awt.event.WindowAdapter() {
    	    @Override
    	    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
    	        running = false;
      	    }
    	});
    	
    	// Display the window
    	frame.pack();
    	frame.setVisible(true);
        
	}
	
	
	/**
	 * Crates the tabs.
	 */
	private void createTabs() {
		tabs.add("main", main);
		tabs.add("console", console);
		tabs.add("configuration", configuration);
		tabs.add("logs", logs);
		
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
	        	JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
	        	int index = sourceTabbedPane.getSelectedIndex();
	        	if (sourceTabbedPane.getTitleAt(index).equals("configuration")){
	        		configuration.load();
	        	}
	        }
	      };
	    
	     tabs.addChangeListener(changeListener);
		
		frame.getContentPane().add(tabs);
	}
}
