package cz.cuni.mff.kocur.interests;

import java.awt.Graphics2D;

import cz.cuni.mff.kocur.agent.AgentParameters;
import cz.cuni.mff.kocur.agent.InterestParameter;
import cz.cuni.mff.kocur.base.CustomStringBuilder;

/**
 * Class that represents a tower.
 * 
 * @author kocur
 *
 */
public class Tower extends BaseInterest {

	public Tower(int id, int team, double x, double y) {
		super(x, y);

		active = true;
		
		this.entid = id;
	}

	public String toString() {
		CustomStringBuilder b = new CustomStringBuilder();

		b.appendLine("Tower: " + entid + " [" + x + ", " + y + " ]");

		return b.toString();
	}

	/**
	 * Paints the tower.
	 * 
	 * @param g
	 *            Graphics.
	 */
	public void paint(Graphics2D g) {
		// I already painted this when I painted the world.
	}

	/**
	 * Destroys the tower. (the tower is no longer alive)
	 */
	public void destroyed() {
		active = false;
	}

	/**
	 * 
	 * @return Returns true, if this tower is standing.
	 */
	public boolean isStanding() {
		return active;
	}

	@Override
	public InterestParameter getParameter(AgentParameters params) {
		InterestParameter p = params.getInterestParameter(Tower.class);

		if (p == null)
			return super.getParameter(params);
		return p;
	}

}
