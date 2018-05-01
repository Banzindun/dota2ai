package cz.cuni.mff.kocur.graphics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import cz.cuni.mff.kocur.configuration.CItem;

/**
 * Class that represents configuration entry, that should contain a radio box.
 * 
 * @author kocur
 *
 */
public class CRadio extends JPanel implements CSavable {

	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = 1971382493715642377L;

	/**
	 * Item stored inside this panel.
	 */
	CItem item;

	/**
	 * Group of butons.
	 */
	ButtonGroup bGroup;

	/**
	 * Maximum number of buttons in a row.
	 */
	private static int maxInRow = 3;

	/**
	 * Constructor, that takes item that this Radio box should represent.
	 * @param item Item that we should represent.
	 */
	public CRadio(CItem item) {
		super(new GridBagLayout());

		this.item = item;

		GridBagConstraints gbc = ConstraintsBuilder.build().gridxy(0).get();

		bGroup = new ButtonGroup();

		for (String s : item.getOptions()) {
			JRadioButton b = new JRadioButton(s, s.equals(item.getValue()));
			b.setActionCommand(s);

			bGroup.add(b);
			this.add(b, gbc);

			gbc.gridx++;

			if (gbc.gridx == maxInRow) {
				gbc.gridy++;
				gbc.gridx = 0;
			}
		}

	}

	@Override
	public void save() {
		item.setValue(bGroup.getSelection().getActionCommand());
	}

}
