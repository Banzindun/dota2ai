package cz.cuni.mff.kocur.graphics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.events.Event;
import cz.cuni.mff.kocur.events.FrameworkEventListener;
import cz.cuni.mff.kocur.events.ListenersManager;
import cz.cuni.mff.kocur.framework.App;

/**
 * Panel that displays the world.
 * 
 * @author kocur
 *
 */
public class WorldPanel extends BuildableJPanel implements FrameworkEventListener {
	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = -8713632292536555438L;

	WorldViewersWrapper wrap = new WorldViewersWrapper();
	
	/**
	 * Constructor, that sets the layout and builds the constraints.
	 */
	public WorldPanel() {
		ListenersManager.addFrameworkListener("state_changed", this);
	}

	/**
	 * Builds the panel.
	 */
	public void build() {
		this.add(wrap);

		// call build on super.
		super.build();
	}

	/**
	 * Called when user switches to this tab.
	 */
	public void focused() {
		wrap.focused();
	}

	/**
	 * Rebuilds the WorldPanel.
	 */
	private void rebuild() {
		this.removeAll();

		this.add(wrap);
		
		this.revalidate();
		this.repaint();
	}


	@Override
	public void triggered(Event e) {
		if (App.state == App.State.CONFIGURATION) {
			wrap.update();
		}
	}

	@Override
	public void triggered(Event e, Object... os) {
		// TODO Auto-generated method stub
		
	}

}
