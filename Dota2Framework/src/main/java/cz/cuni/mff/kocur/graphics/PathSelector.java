package cz.cuni.mff.kocur.Graphics;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import cz.cuni.mff.kocur.Configuration.ConfigurationLoader;
import cz.cuni.mff.kocur.Configuration.GlobalConfiguration;
import cz.cuni.mff.kocur.Dota2AIOverlay.App;
import cz.cuni.mff.kocur.Exceptions.LoadingError;
import cz.cuni.mff.kocur.Logging.Logger;

public class PathSelector extends JPanel implements ActionListener{
	/**
	 * logger registered in Logger class.	
	 */
	private static final Logger logger = Logger.getLogger(App.class.getName());
	
	/**
	 * GlobalConfiguration reference
	 */
	private GlobalConfiguration cfg = GlobalConfiguration.getInstance();
	
	private JTextField field;
	private JButton selectButton;
	private File selectedFile;
	private JLabel status = new JLabel();
	private boolean showStatus;
	
	public PathSelector(String initial, boolean showStatus) {
		super();
		this.setMaximumSize(new Dimension(2000, 40));
		this.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0.7;
		gbc.insets = new Insets(0,10,0,10);
				
		field = new JTextField(initial);
		field.setMaximumSize(field.getPreferredSize());
		
		this.add(field, gbc);
		
		selectButton = new JButton("Select");
		selectButton.setHorizontalAlignment(SwingConstants.RIGHT);;
		selectButton.addActionListener(this);
		
		gbc.gridx = 1;
		gbc.weightx = 0.2;
		selectButton.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(selectButton, gbc);
		
		this.showStatus = showStatus;
		if (showStatus) addStatus(); 
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser();
		
		// Get directory where the data should be 
		File homeDir = new File(System.getProperty("user.home") + "/Dropbox/Dota2AI/data" );
		
		// Set up the directory
		if (homeDir.exists()) fileChooser.setCurrentDirectory(homeDir);
		else fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		
		// Set file filter = we only want to see files with cfg extensions
		fileChooser.setFileFilter(new FileNameExtensionFilter("Configuration file","cfg"));
	    		
		// Handle the result
		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile();
			
			// Load the configuration
			boolean ok = true;
			try {
				ConfigurationLoader loader = new ConfigurationLoader(selectedFile.getPath().toString());
				loader.loadBotCFGFromJSON();
			} catch(LoadingError err) {
				// Logs in here.
				err.printStackTrace();
				ok = false;
			}
			
			// Display if the operation was successful
			if (ok) {
				// ---------> Logs in here. <------------
				
				field.setText(selectedFile.toString());
				if (showStatus) status.setText("OK"); // For now
				return;
			}
		}
		
		// Else display Fail MSG
		if (showStatus) status.setText("X");
	}
	
	public void addStatus() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 2;
		gbc.weightx = 0.1;
		gbc.insets = new Insets(0,10,0,10);
		
		status.setText("X");
		this.add(status, gbc);
	}
	
	public File getFile() {
		return selectedFile;		
	}
	
	
}
