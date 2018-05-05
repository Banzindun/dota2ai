package cz.cuni.mff.kocur.graphics;

import java.awt.Component;

import javax.swing.JPanel;

import cz.cuni.mff.kocur.exceptions.ComponentNotBuiltException;

/**
 * Abstract class that represents a JPanel with build() method.
 * 
 * @author kocur
 *
 */
public abstract class BuildableJPanel extends JPanel implements Buildable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = -8437570969046489693L;

	/**
	 * Was it built?
	 */
	private boolean built = false;

	/**
	 * Build method that builds the component. Should be called at the end of the
	 * build of extender, as it sets this object as built.
	 */
	@Override
	public void build() {
		built = true;

	}

	/**
	 * 
	 * @return Gets the panel.
	 */
	public JPanel getPanel() {
		return this;
	}

	@Override
	public Component getComponent() throws ComponentNotBuiltException {
		if (built == false)
			throw new ComponentNotBuiltException("This component wasn't built, but someone wants to get access to it!");
		return (Component) this;
	}

	@Override
	public Buildable get() throws ComponentNotBuiltException {
		if (built == false)
			throw new ComponentNotBuiltException("This component wasn't built, but someone wants to get access to it!");

		return this;
	}

}
