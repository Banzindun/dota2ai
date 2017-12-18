package cz.cuni.mff.kocur.Graphics;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cz.cuni.mff.kocur.Configuration.BotConfiguration;
import cz.cuni.mff.kocur.Configuration.CItem;

public class ConfigurationTab extends JPanel {
	private LinkedList<Component> components = new LinkedList<Component>();
	
	public ConfigurationTab(Entry<String, BotConfiguration> entry) {
		super();
		this.setLayout(new GridBagLayout());

		// Create grid bag constraints
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.weightx = 0.5;
		gbc.weighty = 0.5;
		gbc.gridx = 0;
		gbc.insets = new Insets(5, 0, 5, 0); // Insets from top and bottom
						
		Map<String, CItem> items = entry.getValue().getConfiguration();
		for ( Entry<String, CItem> i : items.entrySet()) {			
			JPanel section = createSection(i.getValue(), i.getKey());
			if (section != null) {
				
				// Filler
				gbc.weightx = 0.25;
				gbc.gridx = 0;
				this.add(new JPanel(), gbc);
				
				gbc.gridx = 1;
				gbc.weightx = 0.5;
				this.add(section, gbc);
				
				// Filler
				gbc.weightx = 0.25;
				gbc.gridx = 2;
				this.add(new JPanel(), gbc);
				
				gbc.gridy+=1;
				// throw something
			}
		}
		gbc.insets = new Insets(0,0,0,0);
		
		gbc.weighty = 1;
		
		
		// Filler
		this.add(new JPanel(), gbc); 
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
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(2, 0, 2, 0);
		
		JLabel label = new JLabel(i.getLabel());
		section.add(label, gbc);	

		gbc.weighty = 0.5;
		gbc.gridy = 1;
		
		try {
			Class<?> cls = Class.forName(this.getClass().getPackage().getName() + "." + i.getType());
			components.add((Component) cls.getDeclaredConstructor(CItem.class).newInstance(i));
			section.add(components.getLast(), gbc);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return section;
	}
	
	
	public void saveValues() {
		for (Component c : components) ((CSavable) c).save();		
	}
	
}
