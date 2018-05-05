package cz.cuni.mff.kocur.interests;

import java.awt.Graphics2D;

import cz.cuni.mff.kocur.agent.AgentParameters;
import cz.cuni.mff.kocur.agent.InterestParameter;
import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.base.CustomStringBuilder;

/**
 * Class that represents a neutral spawner. That is an object, that spawn
 * neutral creeps and can be used to find the camp positions.
 * 
 * @author kocur
 *
 */
public class NeutralSpawner extends BaseInterest {
	public NeutralSpawner(int id, double x, double y) {
		super(x, y);
		this.setEntid(id);
	}

	public String toString() {
		CustomStringBuilder b = new CustomStringBuilder();
		b.appendLine("Neutral spawner: [" + x + ", " + y + "]");
		return b.toString();
	}

	public void paint(Graphics2D g) {
		System.out.print(".");
		g.setPaint(Colors.ORANGE);
		g.fillOval(gridX, gridY, 5, 5);
		g.drawRect((int) x - 5, (int) y - 5, 10, 10);
	}

	@Override
	public InterestParameter getParameter(AgentParameters params) {
		InterestParameter p = params.getInterestParameter(NeutralSpawner.class);

		if (p == null)
			return super.getParameter(params);
		return p;
	}

}
