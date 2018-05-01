package cz.cuni.mff.kocur.graphics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.configuration.FrameworkConfiguration;

/**
 * Class responsible for layout of the main tab.
 * @author Banzindun
 *
 */
public class MainPanel extends JPanel {

	/**
	 * Generated serial version id. 
	 */
	private static final long serialVersionUID = -5114537895988531474L;

	/**
	 * Global configuration instance. 
	 */
	public static FrameworkConfiguration cfg = FrameworkConfiguration.getInstance();
	
	/**
	 * Logger registered for this class.
	 */
	public static Logger logger = LogManager.getLogger(MainPanel.class.getName());
	
	/**
	 * Body section.
	 */
	private MainBody body = new MainBody();
	
	/**
	 * GridBagConstraints to define styling.
	 */
	private GridBagConstraints gbc;
	

	/**
	 * Function for defining mainPanel.
	 */
	public MainPanel() {
    	super();
    }
	
	/**
	 * Setups the main panel.
	 */
	public void setup() {
		this.setLayout(new GridBagLayout());
		
		// Set GBC
    	gbc = setupGbc();
    	    	
    	// Build body
    	body.build();                	    	
    	
		// And add created body and footer to this
    	gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;
		gbc.gridy++;
		this.add(body, gbc);
   	}
	
	/**
	 * Initialization of GridBagConstraints - sets HORIZONTAL fill and NORTH anchor.
	 */
	private GridBagConstraints setupGbc() {
    	return ConstraintsBuilder.build()
    			.gridxy(0)
    			.fill(GridBagConstraints.HORIZONTAL)
    			.anchor(GridBagConstraints.NORTH)
    			.weightxy(1,0)
    			.insets(10,50,10,50)
    			.get();
	}
}
