package cz.cuni.mff.kocur.graphics;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTabbedPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Implementation of JTabbedPane that can have its tabs dragged and released to create new windows.
 * @author kocur
 *
 */
public class DraggableTabbedPanel  extends JTabbedPane{
	/**
	 * Generated serial version id. 
	 */
	private static final long serialVersionUID = -1364444334989155057L;

	/**
	 * Logger registered for DraggableTabbedPanel class.
	 */
	private Logger logger =  LogManager.getLogger(DraggableTabbedPanel.class.getName());
	
	/**
	 * True if we are dragging the tab.
	 */
	private boolean dragging = false;
	
	/**
	 * True if we exited the borders of the tabbed pane. 
	 */
	private boolean exited = false;
	
	/**
	 * Index of currently dragged pane.
	 */
	private int draggedTabIndex = -1;
	  	
	/**
	 * Constructor that initializes the tabbed panel. Adds listeners to panel.
	 */
	public DraggableTabbedPanel() {
		super();
			    
		addMouseListener(new MouseAdapter() {
			// Listener that listens for mouse dragged event.
			public void mouseDragged(MouseEvent e) {
   		
	    		super.mouseDragged(e);
	    	}
	    	
			// Listener that listens for mouse pressed event
	    	public void mousePressed(MouseEvent e) {
	    		// Gets the tab index based on the mouse position
		        int tabNumber = getUI().tabForCoordinate(DraggableTabbedPanel.this, e.getX(), e.getY());
		        
		        // If tabNumber points to tab, then change draggedTabIndex and set dragging to true.
		        if(tabNumber >= 0) {
		        	draggedTabIndex = tabNumber;
		        	dragging = true;
		        }
		        super.mousePressed(e);
	    	}	
	    	
	    	// Method that is called upon mouse release
	    	public void mouseReleased(MouseEvent e) {
	    		// If we are dragging then check if we are out of this window. 
		    	if (dragging) {	    				    				    		
		    		if (exited && draggedTabIndex >= 0){
		    			// If so, then remove this tab and create new window
		    			Component comp = getComponentAt(draggedTabIndex);
		    			
		    			Window w = new Window(comp, ((LoggingTab) comp).getName());
		    			w.start();
		    			
		    			removeTab();
		    		} 
		    		
		    		dragging = false;
		    		exited = false;		    		
		    	}		    		
		    	
		    	super.mouseReleased(e);
		    }
		    
	    	// Called when mouse exits from panel
		    public void mouseExited(MouseEvent e) {
		    	if (dragging) {
		    		exited = true;
		    	}
		    }
	    	
		    // Called when mouse enters panel
		    public void mouseEntered(MouseEvent e) {
		    	if (dragging) {
		    		exited = false;
		    	}
		    }
	    });
	    
	  }
	  
	/**
	 * Removes tab that is currenlty on draggedTabIndex.
	 */
	private void removeTab() {
		try {
			this.remove(draggedTabIndex);
		} catch(IndexOutOfBoundsException e) {
			logger.error("Unable to remove tab with index: " + draggedTabIndex + " from DraggableTabbedPanel.");
		}
	}
}
