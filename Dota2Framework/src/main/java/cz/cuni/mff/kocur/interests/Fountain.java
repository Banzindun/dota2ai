package cz.cuni.mff.kocur.interests;

import java.awt.Graphics2D;

import cz.cuni.mff.kocur.agent.AgentParameters;
import cz.cuni.mff.kocur.agent.InterestParameter;
import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.base.CustomStringBuilder;
import cz.cuni.mff.kocur.world.GridBase;

/**
 * Class that represents a fountain.
 * 
 * @author kocur
 *
 */
public class Fountain extends BaseInterest {
	/**
	 * This fountain's team.
	 */
	private int team = Team.NONE;

	public Fountain(double x, double y) {
		super(x, y);

		// Fountain is outside of the map, move it inside.
		int[] xy = GridBase.getInstance().findClosestPassableTile((int) this.gridX, (int) this.gridY, 1000);
		this.gridX = xy[0];
		this.gridY = xy[1];
	}

	public String toString() {
		CustomStringBuilder b = new CustomStringBuilder();
		b.appendLine("Fountain: [" + x + ", " + y + "]");
		return b.toString();
	}

	public void paint(Graphics2D g) {
		Colors.setTeamColor(team, g);
		g.fillOval(gridX - 5, gridY - 5, 10, 10);
	}

	public void setTeam(int t) {
		team = t;
	}

	public int getTeam() {
		return team;
	}

	@Override
	public InterestParameter getParameter(AgentParameters params) {
		InterestParameter p = params.getInterestParameter(Fountain.class);

		if (p == null)
			return super.getParameter(params);
		return p;
	}

}
