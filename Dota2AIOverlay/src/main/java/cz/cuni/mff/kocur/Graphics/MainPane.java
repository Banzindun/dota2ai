package cz.cuni.mff.kocur.Graphics;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Class responsible for layout of main tab.
 * @author Banzindun
 *
 */
public class MainPane extends JPanel {
	/**
	 * Button for starting the debug mode.
	 */
	private JButton debugButton;
	
	/**
	 * Mode for starting the release mode.
	 */
	private JButton releaseButton;
	
	/**
	 * Top section.
	 */
	private JPanel top;
	
	/**
	 * Right section.
	 */
	private JPanel right;
	
	/**
	 * Left section.
	 */
	private JPanel left;
	
	/**
	 * Body section.
	 */
	private JPanel body;
	
	/**
	 * Footer section.
	 */
	private JPanel footer;
	
	/**
	 * Path selectors for bot configurations.
	 */
	private PathSelector[] botSelectors;
	
	/**
	 * GridBagConstraints to define styling.
	 */
	private GridBagConstraints gbc;
	

	/**
	 * Function for defining mainPanel.
	 */
	public MainPane() {
    	super();
    	this.setLayout(new GridBagLayout());

    	// Set GBC
    	setGBC();

    	// Build top component
    	buildTop();
    	
    	// Build left
    	buildLeft();
    	
    	// Build right
    	buildRight();
    	
    	// Build body
    	buildBody();
        
        // Build footer
        buildFooter();    	    	
    	
    	// And add created body and footer to this
    	gbc.anchor = GridBagConstraints.NORTH;
    	gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
    	this.add(body, gbc);
    	
    	gbc.fill = GridBagConstraints.NONE;
    	gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.gridy = 2;
        gbc.weighty = 0;
    	this.add(footer, gbc);
    }
	
	
	/**
	 * Initialization of GridBagConstraints - sets HORIZONTAL fill and NORTH anchor.
	 */
	private void setGBC() {
    	gbc = new GridBagConstraints();
    	gbc.fill = GridBagConstraints.HORIZONTAL;
    	gbc.anchor = GridBagConstraints.NORTH;
    	gbc.weightx = 1;
		
	}
	
	/**
	 * Creation of TOP section.
	 */
	private void buildTop() {
    	// Construct top
    	top = new JPanel();
    	top.setBorder(BorderFactory.createTitledBorder("TOP"));
    	top.add(new JLabel("a"));
        top.add(new JLabel("b"));
        top.add(new JLabel("c"));
        top.add(new JLabel("d"));
    	
        this.add(top, gbc);
		
	}
	
	/**
	 * Builds body section.
	 */
	private void buildBody() {

    	
    	body = new JPanel();
    	body.setLayout(new GridBagLayout());
    	body.setBorder(BorderFactory.createTitledBorder("BODY"));
        
    	gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.4;
        gbc.weighty = 1;
    	body.add(left, gbc);
    	
    	gbc.gridx = 1;
    	gbc.weightx = 0.6;
    	body.add(right, gbc);
    	
	}
		
	
	/**
	 * Build left section.
	 */
	private void buildLeft() {
		left = new JPanel();
    	left.setBorder(BorderFactory.createTitledBorder("LEFT"));
        left.add(new JLabel("e"));
        left.add(new JLabel("f"));
        
		
	}

	
	/**
	 * Build right section.
	 */
	private void buildRight() {
		right = new JPanel();
    	right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
    	right.setBorder(BorderFactory.createTitledBorder("RIGHT"));

    	botSelectors = new PathSelector[] {
        		new PathSelector("./bot.cfg", true),
        		new PathSelector("./bot.cfg", true),
        		new PathSelector("./bot.cfg", true),
        		new PathSelector("./bot.cfg", true),
        		new PathSelector("./bot.cfg", true)    		
        	};

    	for (PathSelector s : botSelectors){
    		right.add(s);    		
    	}
		
	}
	
	/**
	 * Build footer section.
	 */
	private void buildFooter() {
		debugButton = new JButton("Debug");
		releaseButton = new JButton("Release");
    	footer = new JPanel();
    	footer.add(releaseButton);
    	footer.add(debugButton);		
	}
}
