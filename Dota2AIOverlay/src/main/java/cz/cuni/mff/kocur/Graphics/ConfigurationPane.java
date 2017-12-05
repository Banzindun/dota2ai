package cz.cuni.mff.kocur.Graphics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import cz.cuni.mff.kocur.Configuration.GlobalConfiguration;
import cz.cuni.mff.kocur.Configuration.BotConfiguration;
import cz.cuni.mff.kocur.Configuration.CItem;
import cz.cuni.mff.kocur.Logging.Logger;

public class ConfigurationPane extends JPanel {
	/**
	 * logger registered in Logger class.	
	 */
	private static final Logger logger = Logger.getLogger(ConfigurationPane.class.getName());
	
	/**
	 * GlobalConfiguration reference
	 */
	private GlobalConfiguration cfg = GlobalConfiguration.getInstance();
	
	private JTabbedPane tabs;
			
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
		tabs = new JTabbedPane();
	}
	
	public void load() {
		if (loaded) return;
		
		// Get bot configurations 
		Map<String, BotConfiguration> botConfigurations = cfg.getConfigurations();
		if (botConfigurations.isEmpty()) return;
		
		// Create new tab for every configuration
		for(Entry<String, BotConfiguration> c : botConfigurations.entrySet()) {
			JPanel tab = loadConfiguration(c.getValue());
			
			tabs.add(new JScrollPane(tab), c.getKey());

			// Reset the gbc
			gbc.gridy = 0;
		}
				
		this.add(tabs, gbc);
		loaded = true;
	}
	
	/**
	 * Loads all the config arguments etc. from JSON file. Inserts them to JFrame?? and returns it.
	 */
	private JPanel loadConfiguration(BotConfiguration cfg) {
		// Go through the entries and create sections.
		JPanel tab = new JPanel();
		tab.setLayout(new GridBagLayout());
		
		gbc.insets = new Insets(0,0,20,0);
		gbc.weighty = 0;
		tab.setBorder(BorderFactory.createTitledBorder("TAB"));
		Map<String, CItem> items = cfg.getConfiguration();
		for ( Entry<String, CItem> i : items.entrySet()) {			
			JPanel section = createSection(i.getValue(), i.getKey());
			if (section != null) { 
				tab.add(section, gbc);
				gbc.gridy+=1;
				// throw something
			}
		}
		gbc.insets = new Insets(0,0,0,0);
		
		gbc.weighty = 1;
		
		// Filler
		tab.add(new JPanel(), gbc); 

		return tab;
	}
	
	/**
	 * Creates section that contains 
	 * @return
	 */
	private JPanel createSection(CItem i, String name) {
		JPanel section = new JPanel();
		section.setLayout(new GridBagLayout());
		section.setBorder(BorderFactory.createTitledBorder(name));
		
		// Setup gbc 
		GridBagConstraints _gbc = new GridBagConstraints();
		_gbc.anchor = GridBagConstraints.NORTHWEST;
		_gbc.fill = GridBagConstraints.HORIZONTAL;
		_gbc.weightx = 1;
		
		
		_gbc.weighty = 1;
		JLabel label = new JLabel(i.getLabel());
		section.add(label, _gbc);	

		_gbc.weighty = 0.5;
		
		_gbc.gridy = 1;
				
		String type = i.getType();
		if (type.equals("JRadioButton")) section.add(createJRadio(i), _gbc);
		else if (type.equals("JCheckBox")) section.add(createJCheck(i), _gbc);
		else if (type.equals("JComboBox")) section.add(createJCombo(i), _gbc);
		else if (type.equals("JList")) section.add(createJList(i), _gbc);
		//else if (type == "JSpinner") section.add(createJSpinner(i));			
		else if (type.equals("JTextField")) section.add(createJText(i), _gbc);
		// JTEXTPANE??
		// JFILECHOOSER
		else {
			// Error here.
			return null;
		}
				
		return section;
	}

	private JTextField createJText(CItem i) {
		JTextField field = new JTextField(i.getValue());
		
		// Add action listener
		
		return field;
	}

	/*private JSpinner createJSpinner(CItem i) {
		SpinnerListModel model = new SpinnerListModel(i.getSelection().toArray());
		JSpinner spinner = new JSpinner(model);
		return spinner;
	}*/

	private JList<String> createJList(CItem i) {
		JList<String> list = new JList<String>(i.getSelection().toArray(new String[i.getSelection().size()]));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(10);
		
		int init = i.getSelection().indexOf(i.getValue());
		if(init != -1) list.setSelectedIndex(init);
		else {
			// Log a warning. Or throw an ERROR - we want to force the user to modify data correctly.
		} 
		return list;
	}

	private JComboBox<String> createJCombo(CItem i) {
		
		JComboBox<String> box = new JComboBox<String>(i.getSelection().toArray(new String[i.getSelection().size()]));
		int init = i.getSelection().indexOf(i.getValue());
		if (init != -1) box.setSelectedIndex(init);
		else {
			// Fuck it or log a warning.			
		}
		return box;
	}

	private JPanel createJCheck(CItem i) {
		JPanel boxes = new JPanel();
		
		for (String s : i.getSelection()) {
			JCheckBox cb = new JCheckBox(s);
			if (s.equals(i.getValue())) cb.setSelected(true);
			boxes.add(cb);			
		}
		return boxes;		
	}

	private JPanel createJRadio(CItem i) {
		JPanel panel = new JPanel();
		ButtonGroup group = new ButtonGroup();
				
		for (String s : i.getSelection()) {
			JRadioButton b = new JRadioButton(s);
			if (s.equals(i.getValue())) b.setSelected(true);
			group.add(b);
			panel.add(b);
		}				
		return panel;		
	}
	
}
