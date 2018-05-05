package cz.cuni.mff.kocur.interests;

import cz.cuni.mff.kocur.agent.AgentParameters;
import cz.cuni.mff.kocur.agent.InterestParameter;
import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.world.GridBase;
import cz.cuni.mff.kocur.world.GridSystem;

/**
 * Class that represents a BaseInterest. This class stores informations, that
 * are already not inside Location and that might be usefull to interest
 * objects.
 * 
 * @author kocur
 *
 */
public class BaseInterest extends Location {
	/**
	 * Name of the model. Used by rune resolution.
	 */
	private String modelName = "";

	/**
	 * Name of the iterest.
	 */
	private String interestName = "";

	/**
	 * True if the interest is active (alive ..).
	 */
	protected boolean active = false;

	/**
	 * X grid coordinates. (so that we don't need to calculate them all the time)
	 */
	protected int gridX;

	/**
	 * Y grid coordinates. (so that we don't need to calculate them all the time)
	 */
	protected int gridY;

	public BaseInterest() {

	}

	/**
	 * Constructor.
	 * 
	 * @param x2
	 *            X coordinate.
	 * @param y2
	 *            Y coordinate.
	 */
	public BaseInterest(double x2, double y2) {
		this.x = x2;
		this.y = y2;

		GridBase g = GridBase.getInstance();

		Integer[] grdxy = GridSystem.toTile(g.resolveXY(x2, y2));
		gridX = grdxy[0];
		gridY = grdxy[1];
	}

	public int getGridX() {
		return gridX;
	}

	public void setGridX(int gridX) {
		this.gridX = gridX;
	}

	public int getGridY() {
		return gridY;
	}

	public void setGridY(int gridY) {
		this.gridY = gridY;
	}

	public void setGridXY(int gridx, int gridy) {
		this.gridY = gridy;
		this.gridX = gridx;
	}

	/**
	 * Returns the InterestParameter object that corresponds to this interests type.
	 * 
	 * @param params
	 *            Agent parameters.
	 * @return Returns interest parameters, that math the class of this interest.
	 */
	public InterestParameter getParameter(AgentParameters params) {
		InterestParameter p = params.getInterestParameter(BaseInterest.class);

		// If not entity parameter exists, then we will return new one to avoid getting
		// null reference errors
		if (p == null)
			return new InterestParameter();

		return p;
	}

	public String getInterestName() {
		return interestName;
	}

	public void setInterestName(String interestName) {
		this.interestName = interestName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * 
	 * @return Returns the in-game model's name.
	 */
	public String getModelName() {
		return modelName;
	}

	/**
	 * Sets a model name.
	 * 
	 * @param modelName
	 *            New model name.
	 */
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

}
