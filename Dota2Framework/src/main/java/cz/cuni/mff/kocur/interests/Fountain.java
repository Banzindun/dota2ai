package cz.cuni.mff.kocur.interests;

import java.awt.Graphics2D;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.base.CustomStringBuilder;
import cz.cuni.mff.kocur.bot.AgentParameters;
import cz.cuni.mff.kocur.bot.InterestParameter;

/**
 * Class that represents a fountain.
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
	}
	
	public String toString() {
		CustomStringBuilder b = new CustomStringBuilder();
		b.appendLine("Fountain: [" + x + ", " + y + "]");
		return b.toString();
	}
	
	public void paint(Graphics2D g) {
		Colors.setTeamColor(team, g);
		g.fillOval(gridX-5, gridY-5, 10, 10);
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
		
		if (p == null) return super.getParameter(params);
		return p;
	} 
	
}
