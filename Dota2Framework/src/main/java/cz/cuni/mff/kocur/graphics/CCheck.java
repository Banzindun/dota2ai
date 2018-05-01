package cz.cuni.mff.kocur.graphics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import cz.cuni.mff.kocur.base.Utils;
import cz.cuni.mff.kocur.configuration.CItem;
import cz.cuni.mff.kocur.exceptions.LoadingError;

/**
 * Represents CItems that can be represented as multiple check boxes.
 * 
 * @author kocur
 *
 */
public class CCheck extends JPanel implements CSavable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = -8610149056871982895L;

	/**
	 * CItem that this check represents.
	 */
	private CItem item;

	/**
	 * The actual check boxes.
	 */
	LinkedList<JCheckBox> boxes = new LinkedList<JCheckBox>();

	/**
	 * Maximum number of checkboxes in a row.
	 */
	private static int maxInRow = 3;

	/**
	 * Constructs a CCheck.
	 * 
	 * @param item
	 *            Item, that the checkboxes should represent.
	 * @throws LoadingError
	 *             Throws if the item couldn't be read.
	 */
	public CCheck(CItem item) throws LoadingError {
		super(new GridBagLayout());
		this.item = item;

		testItem();

		GridBagConstraints gbc = ConstraintsBuilder.build().gridxy(0).anchor(GridBagConstraints.WEST).get();

		String[] selected = Utils.parseArrayOfStrings(item.getValue());

		for (String s : item.getOptions()) {
			// Try to find this option in selected values
			boolean isSelected = false;
			for (String _s : selected)
				if (s.equals(_s))
					isSelected = true;

			// Add CheckBox, that is selected if option in selected values
			boxes.add(new JCheckBox(s, isSelected));
			boxes.getLast().setActionCommand(s);

			this.add(boxes.getLast(), gbc);
			gbc.gridx++;

			if (gbc.gridx == maxInRow) {
				gbc.gridy++;
				gbc.gridx = 0;
			}
		}
	}

	/**
	 * Tests the stored items.
	 * 
	 * @throws LoadingError
	 *             Thrown if there is something wrong with the stored items.
	 */
	private void testItem() throws LoadingError {
		List<String> options = item.getOptions();
		String value = item.getValue();

		if (value == null || options == null)
			throw new LoadingError("Options or supplied value are null.");

		if (options.size() == 0)
			throw new LoadingError("Error loading CCheck. Options empty.");

		String[] selected = Utils.parseArrayOfStrings(item.getValue());
		if (selected.length == 0)
			throw new LoadingError(
					"Nothing selected, or values are not stored in good representation. Store them as: [a, b, c]");

		for (String s : selected) {
			if (options.indexOf(s) == -1) {
				throw new LoadingError("Value: " + s + " not in options.");
			}
		}
	}

	@Override
	public void save() {
		ArrayList<String> selected = new ArrayList<String>();
		for (JCheckBox b : boxes) {
			if (b.isSelected()) {
				selected.add(b.getActionCommand());
			}
		}

		StringBuilder builder = new StringBuilder();
		builder.append("[");

		// Make string from selection in form of [a, b, c, d]
		for (int i = 0; i < selected.size() - 1; i++)
			builder.append(selected.get(i) + ", ");
		builder.append(selected.get(selected.size() - 1));

		builder.append("]");

		item.setValue(builder.toString());
	}

}
