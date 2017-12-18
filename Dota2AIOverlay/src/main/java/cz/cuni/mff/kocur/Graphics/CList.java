package cz.cuni.mff.kocur.Graphics;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

import cz.cuni.mff.kocur.Configuration.CItem;


public class CList extends JList<String> implements CSavable {
	
	CItem item;
	
	public CList(CItem item) {
		super(item.getOptions().toArray(new String[item.getOptions().size()]));
		
		this.item = item;
		
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setLayoutOrientation(JList.VERTICAL);
		this.setVisibleRowCount(10);
		
		int initialValue = item.getOptions().indexOf(item.getValue());
		if(initialValue != -1) this.setSelectedIndex(initialValue);
		else {
			// Log a warning. Or throw an ERROR - we want to force the user to modify data correctly.
		} 
		
	}

	@Override
	public void save() {
		int index = this.getSelectedIndex();
		
		// Check if not selected - and then store empty string there.
		if (index == -1) item.setValue("");
		else {
			// Get an option from options and save it to item value.
			item.setValue(item.getOptions().get(index));
		}			
	}
	
	
}
