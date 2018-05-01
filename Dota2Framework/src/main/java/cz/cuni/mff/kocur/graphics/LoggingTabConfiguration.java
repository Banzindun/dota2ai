package cz.cuni.mff.kocur.graphics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * Class that represents a configuration panel of the logging tab.
 * @author kocur
 *
 */
public class LoggingTabConfiguration extends JPanel{

	/**
	 * Generated serial version id. 
	 */
	private static final long serialVersionUID = -7487674562996292771L;

	//private Logger logger = LogManager.getLogger(LoggingTabConfiguration.class.getName());
	
	private LoggingTab parent = null;
	
	public LoggingTabConfiguration(LoggingTab parent) {
		this.parent = parent;		
	}
	
	
	/**
	 * Private class that holds configuration of fields for logs.
	 * Log should be filtered so only filters that are demanded are displayed.
	 * @author Banzindun
	 *
	 */
	private class Fields{
		/**
		 * Fields of logs that can be hidden or logged by logger.
		 */
		private String[] fields = new String[] { "date", "priority", "thread", "category", "message" };
		
		/**
		 * Patterns that correspond to {@link #fields}, that are later merged to create PatternLayout.
		 */
		private String[] patterns = new String[] { "%d", "%-5p", "[%t]", "%c", "%m" };
		
		/**
		 * Array that stores booleans that are true if the field is displayed.
		 */
		private boolean[] display;
		
		/**
		 * Holds layout of current selected pattern. 
		 */
		private PatternLayout layout;
		
		public Fields() {
			display = new boolean[fields.length];
			for(int i = 0; i < fields.length; i++) display[i] = true;
			
			// Build default layout.
			buildLayout();			
		}
		
		/**
		 * 
		 * @return {@link #display} array of booleans (true=is logged)
		 */
		public boolean[] getValues() {
			return display;
		}
		
		/**
		 * 
		 * @return {@link #fields} fields that can be modified
		 */
		public String[] getFields() {
			return fields;			
		}
	
		/**
		 * Sets array of displayed fields.
		 * @param d Field of booleans that specify what fields are logged.
		 */
		public void setDisplayed(boolean[] d ) {
			if (Arrays.equals(display, d)) return;
			else {
				display = d;
				buildLayout();
			}	
		}
		
		/**
		 * Builds PatternLayout from enabled and disabled fields.
		 */
		private void buildLayout() {
			StringBuilder pattern = new StringBuilder();
			
			if (display == null || display.length != fields.length) return;
			
			for(int i = 0; i < display.length; i++) {
				if (display[i]) pattern.append(patterns[i] + " ");
			}
			
			pattern.append("%n");			
			
			layout = PatternLayout.newBuilder()
					.withPattern(pattern.toString())
					.build();		
		}
		
		/**
		 * 
		 * @return Layout of current configuration.
		 */
		public PatternLayout getLayout() {
			return layout;			
		}
	}
	
	/**
	 * Displayed fields.  
	 */
	Fields displayedFields = new Fields();
		
	/**
	 * What level should be the minimum of logged messages?
	 */
	private Level filterLevel = null;
	
	/**
	 * If we want to display levels that are equal to the selected one.
	 */
	private boolean logEqualLevel = false;
	
	/**
	 * If we want to log levels that are more specific than the selected one.
	 */
	private boolean logMoreSpecificLevel = false;
	
	/**
	 * Name of file where we want to save the logs. 
	 */
	private String fileName = null;
	
	/**
	 * True if we are saving logs to file.
	 */
	private boolean savingToFile = false;
	
	/**
	 * True if logging is turned on (writes logs only in file if disabled)	
	 */
	private boolean logsOn = true;
	
	// SWING COMPONENTS:
	
	/**
	 * Array of check boxes that are checked when user doesn't want to display the checked fields.
	 * Initially everything is displayed. 
	 */
	private JCheckBox[] hideJCheckBoxes;
	
	/**
	 * Select the lowest level of messages you want to display. 
	 */
	private JComboBox<Level> levelJComboBox;
	
	/**
	 * Select the level you only want to display.
	 */
	private JCheckBox equalLevelCB;
	
	/**
	 * Selected if user wants to display logs with level that is more specific than the selected one.
	 */
	private JCheckBox moreSpecificLevelCB;
	
	/**
	 * Panel that holds information and configuration for saving to file.
	 */
	private JPanel saveToPanel = null;
	
	/**
	 * TextFiled for filling name of logs file.
	 */
	private JTextField saveTo;
	
	/**
	 * If selected then logs are on.
	 */
	private JCheckBox logsOnCB = null;

	/**
	 * Setups the configuration. 
	 * Initializes constraints and adds fields to configuration. 
	 */
	public void setup() {
		this.setLayout(new GridBagLayout());
		
		// Initialize constraints
		GridBagConstraints gbc = ConstraintsBuilder.build()
				.gridy(0)
				.gridx(0)
				.fill(GridBagConstraints.HORIZONTAL)
				.anchor(GridBagConstraints.EAST)
				.insets(2,2,2,2)
				.get();
		
		// Add display field.
		this.add(initDisplayField(), gbc);
		
		// Move one grid down and add filter field.
		gbc.gridy++;
		this.add(initFilterField(), gbc);
		
		// Move one grid down add save to file field.
		gbc.gridy++;
		this.add(initSaveToFile(), gbc);
		
		// Move down and add filler (so the rest is on the bottom)
		gbc.gridy++;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.VERTICAL;
		this.add(new JPanel(), gbc);
		
		// Setup GBC for the last part.
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy++;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.SOUTHEAST;
		
		// Add footer (set button and logs toggle)
		this.add(initFooter(), gbc);		
	}	
	
	/**
	 * Initializes display field. That is a field that lets user select what he wants to log.
	 * @return JPanel that contains all the necessary CheckBoxes etc. to let the user configure all the fields. 
	 */
	private JPanel initDisplayField() {	
		// Create the field and set border and layout.
		JPanel field = new JPanel();
		field.setBorder(BorderFactory.createTitledBorder("Hide fields:"));
		field.setLayout(new GridBagLayout());
		
		// Setup the gbc.
		GridBagConstraints gbc = ConstraintsBuilder.build()
				.fill(GridBagConstraints.BOTH)
				.anchor(GridBagConstraints.NORTH)
				.gridy(0)
				.gridx(0)
				.weightx(1)
				.insets(2, 5, 2, 5)
				.get();
								
		// Get the fields that we will allow the user to work with
		String[] fields = displayedFields.getFields();
		
		// Initialize check boxes array
		hideJCheckBoxes = new JCheckBox[fields.length];
		
		// Initialize the check boxes and add them to field 
		for (int i = 0; i < fields.length; i++) {
			hideJCheckBoxes[i] = new JCheckBox(fields[i]);
			field.add(hideJCheckBoxes[i], gbc);
			gbc.gridy++;
		}
		
		return field;		
	}
	
	/**
	 * Updates display field (after set).
	 * Selects already selected fields. 
	 */
	private void updateDisplayField() {
		boolean[] values = displayedFields.getValues();
		for (int i = 0; i < values.length; i++) 
			hideJCheckBoxes[i].setSelected(!values[i]);			
	}
	
	/**
	 * Initializes field with filters by level.
	 * @return JPanel that contains all the options, so that user can filter the logs by level.
	 */
	private JPanel initFilterField() {	
		// Create new JPanel and add border and layout
		JPanel field = new JPanel();
		field.setLayout(new GridBagLayout());
		field.setBorder(BorderFactory.createTitledBorder("Filter by level:"));
		
		// Setup the constraints
		GridBagConstraints gbc = ConstraintsBuilder.build()
				.fill(GridBagConstraints.BOTH)
				.anchor(GridBagConstraints.NORTH)
				.gridy(0)
				.gridx(0)
				.insets(2, 5, 0, 5)
				.get();
		
		// Create subfield
		JPanel subfield = new JPanel();
		
		// Initialize combo box for level selection.
		levelJComboBox = new JComboBox<Level>(Level.values());
	
		// Add options to display equal or more specific levels
		subfield.add(new JLabel("Level: "));
		subfield.add(levelJComboBox);
		field.add(subfield, gbc);		
		
		equalLevelCB = new JCheckBox("equal ");
		moreSpecificLevelCB = new JCheckBox("more specific");
				
		gbc.gridy++;
		subfield = new JPanel();
		subfield.add(new JLabel("log: "));
		subfield.add(equalLevelCB);
		subfield.add(moreSpecificLevelCB);
		field.add(subfield, gbc);

		return field;		
	}
	
	/**
	 * Updates filter fields
	 */
	private void updateFilterFields() {
		if (filterLevel != null) levelJComboBox.setSelectedItem(filterLevel);
		equalLevelCB.setSelected(logEqualLevel);
		moreSpecificLevelCB.setSelected(logMoreSpecificLevel);
	}
	
	/**
	 * Initializes save to file field.
	 * @return JPanel with text area that lets user to save to file
	 */
	private JPanel initSaveToFile() {
		if (saveToPanel == null) {
			saveToPanel = new JPanel();
			saveToPanel.setLayout(new GridBagLayout());
		}
		saveToPanel.setBorder(BorderFactory.createTitledBorder("Save to file:"));

		saveTo = new JTextField("", 10);
						
		saveToPanel.add(saveTo);
		return saveToPanel;
	}
	
	
	/**
	 * Updates save to file - adds button that will stop the output stream if necessary and name of file.
	 */
	private void updateSaveToFile() {
		// Clear the panel
		saveToPanel.removeAll();
		
		// If we are saving to file
		if (savingToFile) {
			// Set the border
			saveToPanel.setBorder(BorderFactory.createTitledBorder("Saving to file:"));
			
			// Initialize constraints
			GridBagConstraints gbc = ConstraintsBuilder.build()
					.weightx(1)
					.insets(2,5,2,5)
					.anchor(GridBagConstraints.WEST)
					.fill(GridBagConstraints.EAST)
					.get();
			
			// Display fileName
			JLabel label = new JLabel(fileName);
			saveToPanel.add(label, gbc);

			// Create stop button
			JButton stopButton = new JButton("Stop");
			stopButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(savingToFile) {
						parent.closeFile();
						fileName = null;
						savingToFile = false;
						updateSaveToFile();
					}
				}				
			});
			
			gbc.gridx++;
			gbc.weightx++;
			saveToPanel.add(stopButton, gbc);
		} else {
			// Else go to default display
			initSaveToFile();
		}
		
		// Revalidate and repaint
		saveToPanel.revalidate();
		saveToPanel.repaint();
	}
	
	
	/**
	 * Initialize footer - it holds buttons to set configuration or stop logging.
	 * @return
	 */
	private JPanel initFooter() {
		JPanel panel = new JPanel();
				
		// Set button
		JButton set = new JButton("Set");
		set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				configure();
			}
		});
		
		// CheckBox for logging on/off
		logsOnCB = new JCheckBox("Logs on");
		logsOnCB.setSelected(logsOn);
		
		panel.add(logsOnCB);
		panel.add(set);
			
		return panel;
	}
	
	/**
	 * Updates footer - namely the check box that enables or disables the logging.
	 */
	private void updateFooter() {
		logsOnCB.setSelected(logsOn);		
	}
	
	
	/**
	 * Configures the logging from the information given by user.
	 */
	private void configure() {
		// Get displayed fields.
		boolean[] displayed = new boolean[hideJCheckBoxes.length];
		for (int i = 0; i < hideJCheckBoxes.length; i++) 
			displayed[i] = !hideJCheckBoxes[i].isSelected();
		
		// Set them
		displayedFields.setDisplayed(displayed);
		
		// Get the level we want to filter by
		filterLevel = (Level) levelJComboBox.getSelectedItem();
		
		// Get the level filtering options (log equal levels, log more specific levels)
		logEqualLevel = equalLevelCB.isSelected();
		logMoreSpecificLevel = moreSpecificLevelCB.isSelected();
		
		// Check if logs are on or off
		logsOn = logsOnCB.isSelected();
				
		// If we are not saving to file, get the information regarding where to save
		if (!savingToFile) {
			fileName = saveTo.getText();
			if (fileName.trim().equals("")) {
				savingToFile = false;
				fileName = null;
			}
			else{
				savingToFile = true;
				parent.openFile(fileName);
				saveTo.setText("");
			}
		}
		
		// Update all fields
		update();
	}
	
	/**
	 * Updates all the fields (usually after set)
	 */
	public void update() {
		updateDisplayField();
		updateFilterFields();
		updateSaveToFile();		
		updateFooter();
	}

	/**
	 * 
	 * @return Returns PatternLayout given by {@link #displayedFields} selected by user.
	 */
	public PatternLayout getPatternLayout() {
		return displayedFields.getLayout();
	}
	
	/**
	 * @return {@link #filterLevel} given by user. 
	 */
	public Level getFilterLevel() {
		return filterLevel;
	}
	
	/**
	 * 
	 * @return true if we display only logs that are of this level.
	 */
	public boolean getLogEqualLevel() {
		return logEqualLevel;
	}
	
	/**
	 * 
	 * @return true if we log more specific levels than the one selected.
	 */
	public boolean getLogMoreSpecificLevel() {
		return logMoreSpecificLevel;
	}
	
	/**
	 * 
	 * @return true if the logging is turned on.
	 */
	public boolean getLogsOn() {
		return logsOn;		
	}
	
}
