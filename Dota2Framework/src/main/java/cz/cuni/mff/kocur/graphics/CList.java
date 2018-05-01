package cz.cuni.mff.kocur.graphics;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

import cz.cuni.mff.kocur.configuration.CItem;

/**
 * This class represents a list. The list can be used to configure a store CItem, that needs a list representation.
 * @author kocur
 *
 */
public class CList extends JList<String> implements CSavable {
	
	/**
	 * Generated serial version id. 
	 */
	private static final long serialVersionUID = -3747846550483925042L;
	
	/**
	 * CItem that this list represents.
	 */
	CItem item;

	/**
	 * Constructor of CList. 
	 * @param item Item that we should be representing.
	 */
	public CList(CItem item) {
		super(item.getOptions().toArray(new String[item.getOptions().size()]));
		
		this.item = item;
		
		// We select single item out of this.
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		this.setVisibleRowCount(-1);
		
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
