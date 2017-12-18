package cz.cuni.mff.kocur.Graphics;

import cz.cuni.mff.kocur.Configuration.GlobalConfiguration;
import cz.cuni.mff.kocur.Configuration.BotConfiguration;
import cz.cuni.mff.kocur.Logging.Logger;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;



public class ConfigurationPane extends JPanel {
	/**
	 * logger registered in Logger class.	
	 */
	private static final Logger logger = Logger.getLogger(ConfigurationPane.class.getName());
	
	/**
	 * GlobalConfiguration reference
	 */
	private GlobalConfiguration cfg = GlobalConfiguration.getInstance();
	
	private LinkedList<ConfigurationTab> tabs = new LinkedList<ConfigurationTab>();
	
	private JTabbedPane tabPane;
			
	private GridBagConstraints gbc;
	
	private boolean loaded = false;
	
	public ConfigurationPane() {
		this.setLayout(new GridBagLayout());
		
		gbc = new GridBagConstraints();
		
		// Configure gbc 
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(20,20,20,20);
		tabPane = new JTabbedPane();
	}
	
	public void load() {
		if (loaded) return;
		
		// Get bot configurations 
		Map<String, BotConfiguration> botConfigurations = cfg.getConfigurations();
		if (botConfigurations.isEmpty()) return;

		// Create new tab for every configuration
		for(Entry<String, BotConfiguration> c : botConfigurations.entrySet()) {
			tabs.add(new ConfigurationTab(c));
			tabPane.add(new JScrollPane(tabs.getLast()), c.getKey());
		}
				
		this.add(tabPane, gbc);
		
		addSaveButton();
		loaded = true;
	}
	
	/**
	 * Adds save button.
	 */
	private void addSaveButton() {
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (ConfigurationTab t : tabs) t.saveValues();
				System.out.println("Configuration saved.");
				System.out.println(cfg.toString());
			}
		});
		
		//gbc.weighty = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy=1;
		gbc.weightx = 0.1;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		

		this.add(saveButton, gbc);
	}
}
