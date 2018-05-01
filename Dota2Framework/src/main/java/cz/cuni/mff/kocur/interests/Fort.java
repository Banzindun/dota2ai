package cz.cuni.mff.kocur.interests;

import java.awt.Graphics2D;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.base.CustomStringBuilder;
import cz.cuni.mff.kocur.bot.AgentParameters;
import cz.cuni.mff.kocur.bot.InterestParameter;
	
/**
 * Class that represents a fort (== ancient).
 * @author kocur
 *
 */
public class Fort extends BaseInterest{

	/**
	 * It's team.
	 */
	private int team = Team.NONE;
	
	public Fort(double x2, double y2) {
		super(x2, y2);
	}
	
	public String toString() {
		CustomStringBuilder b = new CustomStringBuilder();
		b.appendLine("Fort: [" + x + ", " + y + "]");
		return b.toString();
	}
	
	public void paint(Graphics2D g) {
		Colors.setTeamColor(team, g);
		g.fillOval(gridX-10, gridY-10, 20, 20);
	}
	
	public void setTeam(int t) {
		team = t;
	}
	
	public int getTeam() {
		return team;
	}

    @Override
	public InterestParameter getParameter(AgentParameters params) {
		InterestParameter p = params.getInterestParameter(Fort.class);
		
		if (p == null) return super.getParameter(params);
		return p;
	} 
	
}
