package cz.cuni.mff.kocur.interests;

import java.awt.Graphics2D;

import cz.cuni.mff.kocur.agent.AgentParameters;
import cz.cuni.mff.kocur.agent.InterestParameter;
import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.base.CustomStringBuilder;

/**
 * Class that represents a path corner. Path corners are used to navigate
 * creeps. We can use them to find the lanes.
 * 
 * @author kocur
 *
 */
public class PathCorner extends BaseInterest {

	public PathCorner(double x, double y) {
		super(x, y);
	}

	public String toString() {
		CustomStringBuilder b = new CustomStringBuilder();
		b.appendLine("Pathcorner: [" + x + ", " + y + "]");
		return b.toString();
	}

	public void paint(Graphics2D g) {
		g.setPaint(Colors.PURPLE);
		g.drawLine(gridX - 4, gridY, gridX + 4, gridY);
		g.drawLine(gridX, gridY - 4, gridX, gridY + 4);
	}

	@Override
	public InterestParameter getParameter(AgentParameters params) {
		InterestParameter p = params.getInterestParameter(PathCorner.class);

		if (p == null)
			return super.getParameter(params);
		return p;
	}

}
