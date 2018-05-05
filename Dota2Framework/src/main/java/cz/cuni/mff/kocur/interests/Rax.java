package cz.cuni.mff.kocur.interests;

import java.awt.Graphics2D;

import cz.cuni.mff.kocur.agent.AgentParameters;
import cz.cuni.mff.kocur.agent.InterestParameter;
import cz.cuni.mff.kocur.base.CustomStringBuilder;

/**
 * Class that represent barracks. 
 * @author kocur
 *
 */
public class Rax extends BaseInterest{	
	public Rax(int id, double x, double y) {
		super(x, y);
		this.entid = id;		
	}
	
	public String toString() {
		CustomStringBuilder b = new CustomStringBuilder();
		b.appendLine("Rax: " + entid + ", [" + x + ", " + y + "]");
		return b.toString();		
	}
	
	public void paint(Graphics2D g) {
		// Should be already displayed
		/*g.setPaint(Colors.YELLOW);
		int[] coords = Grid.getTileCoordinates(x, y);
		g.fillOval(coords[0], coords[1], 4, 4);*/
	}
	
	
    @Override
	public InterestParameter getParameter(AgentParameters params) {
		InterestParameter p = params.getInterestParameter(Rax.class);
		
		if (p == null) return super.getParameter(params);
		return p;
	} 
}
