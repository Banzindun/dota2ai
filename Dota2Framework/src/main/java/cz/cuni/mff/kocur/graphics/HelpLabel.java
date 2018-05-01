package cz.cuni.mff.kocur.graphics;

import javax.swing.JLabel;

import cz.cuni.mff.kocur.base.GraphicResources;

/**
 * Class that represent question mark label, that gives some hint if you hover
 * over it.
 * 
 * @author kocur
 *
 */
public class HelpLabel extends JLabel {

	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = 3815346121401972528L;

	/**
	 * Constructs a help label with given text.
	 * @param msg Message to be displayed after hover.
	 */
	public HelpLabel(String msg) {

		this.setToolTipText(msg);
		this.setIcon(GraphicResources.infoI);
	}

}
