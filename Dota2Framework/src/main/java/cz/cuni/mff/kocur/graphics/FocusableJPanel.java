package cz.cuni.mff.kocur.graphics;

import javax.swing.JPanel;

/**
 * Abstract class, that represents JPanel, that can be focused.
 * 
 * @author kocur
 *
 */
public abstract class FocusableJPanel extends JPanel implements Focusable {

	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = 2137716597980654127L;

	@Override
	public abstract void focused();

}
