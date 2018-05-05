package cz.cuni.mff.kocur.graphics;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JFrame;

import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.framework.App;

import org.apache.logging.log4j.LogManager;

/**
 * Class that handles mostly the window graphics etc. 
 * @author Banzindun
 *
 */
public class Window {
	/**
	 * Application reference.
	 */
	protected App app = null;
		
	/**
	 * Get logger registered for this class.
	 */
	private Logger logger = LogManager.getLogger(Window.class.getName());
		
	/**
	 * Java frame for this window.
	 */
	protected JFrame frame;
	
	/**
	 * True if the window is running.
	 */
	protected boolean running = false;
	
	/**
	 * A component that we can display in this window.
	 */
	private Component drawableComponent = null;
	
	/**
	 * Constuctor, that takes reference to applicatoin.
	 * @param app Reference to application.
	 */
	public Window(App app) {
		this.app = app;		
	}
			
	/**
	 * Constructor.
	 * @param title Title of this window.s
	 */
	public Window(String title) {
		frame = new JFrame(title);		
	}
	
	
	/**
	 * Constructor that takes app reference and a title.
	 * @param app Reference to app.
	 * @param title Title of the window.
	 */
	public Window(App app, String title){
		this(app);
		frame = new JFrame(title);
	}

	/**
	 * Constructor that takes drawable component and a title.
	 * @param comp Drawable component that should be displayed inside the window.
	 * @param title Title of the window.
	 */
	public Window(Component comp, String title) {
		this(title);
		drawableComponent = comp;		
	}
	
	/**
	 * Starts the window.
	 */
	public void start() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				running = true;
				createWindow();
			}
		});		
	}
	
	/**
	 * Stops the window.
	 */
	public void stop() {
		frame.setVisible(false);
		frame.dispose();		
	}
	
	
	/**
	 * Creates the window.
	 */
	protected void createWindow() {
		initializeFrame();
    	initializeComponent();
    	    	    	
    	// Display the window
    	frame.pack();
    	frame.setVisible(true);
	}
	
	/**
	 * Initializes the window's frame.
	 */
	protected void initializeFrame() {
		// Decorate window
		//JFrame.setDefaultLookAndFeelDecorated(true);
				    	
		JFrame.setDefaultLookAndFeelDecorated(false);
		// Set sizes
		frame.setMinimumSize(new Dimension(400, 300));
		frame.setPreferredSize(new Dimension(1000, 800));
		
		// Add close operation.
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			
			@Override
		    	public void windowClosing(java.awt.event.WindowEvent windowEvent) {
					running = false;
		    	  	logger.info("Closing the supporting window.");
		    	  	closing();
		        }
		    });
	}
	
	/**
	 * Initialize draeable component.
	 */
	protected void initializeComponent() {
		if(drawableComponent != null) frame.getContentPane().add(drawableComponent);
	}
	
	
	/**
	 * Function that is called when the window is beeing closed.
	 */
	public void closing() {
				
	}
	
	/**
	 * 
	 * @return Returns true, if the window is running.
	 */
	public boolean isRunning() {
		return running;		
	}
}
