package cz.cuni.mff.kocur.graphics;

import javax.swing.JComboBox;

import cz.cuni.mff.kocur.configuration.CItem;

/**
 * Our combobox, that can be loaded from configuration json.
 * 
 * @author kocur
 *
 */
public class CCombo extends JComboBox<String> implements CSavable {

	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = -5409685890502001832L;

	/**
	 * CItem that this combobox reresents.
	 */
	private CItem item;

	/**
	 * Constructor, that takes a CItem.
	 * @param item Item that we should represent.
	 */
	public CCombo(CItem item) {
		super(item.getOptions().toArray(new String[item.getOptions().size()]));

		// Initialize item reference
		this.item = item;

		// It shouldn't be editable
		this.setEditable(false);

		// Fill in initial value
		int initialValue = item.getOptions().indexOf(item.getValue());
		if (initialValue != -1)
			this.setSelectedIndex(initialValue);
		else {
			// Fuck it or log a warning.
		}

	}

	@Override
	public void save() {
		item.setValue(item.getOptions().get(this.getSelectedIndex()));
	}

}
