package cz.cuni.mff.kocur.Graphics;

import java.util.List;

import javax.swing.JComboBox;

import cz.cuni.mff.kocur.Configuration.CItem;


public class CCombo extends JComboBox<String> implements CSavable{
	
	private CItem item;
	
	public CCombo(CItem item) {
		super(item.getOptions().toArray(new String[item.getOptions().size()]));
		
		// Initialize item reference
		this.item = item;
				
		// Fill in initial value
		int initialValue = item.getOptions().indexOf(item.getValue());
		if (initialValue != -1) this.setSelectedIndex(initialValue);
		else {
			// Fuck it or log a warning.			
		}
		
	}

	@Override
	public void save() {
		item.setValue(item.getOptions().get(this.getSelectedIndex()));
	}
	
}
