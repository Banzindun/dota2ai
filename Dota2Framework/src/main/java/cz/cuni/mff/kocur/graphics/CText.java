package cz.cuni.mff.kocur.graphics;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

import cz.cuni.mff.kocur.configuration.CItem;

/**
 * Class that represents configuration item, that can be represented as a text.
 * 
 * @author kocur
 *
 */
public class CText extends JTextArea implements CSavable {

	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = 2619761032933304486L;
	
	/**
	 * Item we represent.
	 */
	CItem item;

	/**
	 * Constructor.
	 * @param item Item we should represent.
	 */
	public CText(CItem item) {
		super(1, 40);
		this.item = item;

		this.setText(item.getValue());

		this.setBackground(this.getBackground());
		this.setBorder(BorderFactory.createEmptyBorder());

		this.setLineWrap(true);
		this.setWrapStyleWord(true);
	}

	@Override
	public void save() {
		item.setValue(this.getText());
	}
}
