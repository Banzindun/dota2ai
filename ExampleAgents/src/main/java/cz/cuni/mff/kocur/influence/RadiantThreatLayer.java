package cz.cuni.mff.kocur.influence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.interests.Fort;
import cz.cuni.mff.kocur.interests.InterestsBase;
import cz.cuni.mff.kocur.interests.Team;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.GridBase;
import cz.cuni.mff.kocur.world.Tower;
import cz.cuni.mff.kocur.world.World;
import cz.cuni.mff.kocur.world.WorldManager;

/**
 * Class that represents radiant threats. We want to spread influence from every
 * radiant entity, that might be dangerous (towers, creeps etc.) And we spread
 * influence from radiant fort. The main goal of this class is to capture
 * radiant's part of the game, so it can be analysed later.
 * 
 * @author kocur
 *
 */
public class RadiantThreatLayer extends InfluenceLayer {

	/**
	 * Logger registered for RadiantThreatLayer class.
	 */
	private Logger logger = LogManager.getLogger(RadiantThreatLayer.class);

	public RadiantThreatLayer(int w, int h, int resolution) {
		super(w, h, resolution);

		logger.info("Created radiant layer with height and width: " + height + ", " + width);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param l
	 *            Radiant threat layer.
	 */
	public RadiantThreatLayer(RadiantThreatLayer l) {
		super(l);

		setParams(l.getParams());
	}

	/**
	 * Creates parameters.
	 */
	public void createParams() {
		//double h = GridBase.getInstance().getHeight();
		double w = GridBase.getInstance().getWidth();
		// double halfDiagonal = (int) (Math.sqrt(h*h + w*w)/2)/resolution;

		PolynomialAttackRangePropagationFunction<Tower> towerP = new PolynomialAttackRangePropagationFunction<Tower>();
		towerP.setMaxInfluence(1.0);
		towerP.setPower(2.0);

		params = ParamsBuilder.build().createEntityParameter(Tower.class, towerP, Team.RADIANT)
				.createInterestParameter(Fort.class, new FortPropagation<Fort>(1, w / resolution)).get();

	}

	@Override
	public synchronized void propagate() {
		World w = WorldManager.getWorld();

		// Clear the grid
		clearInfluence();

		for (BaseEntity e : w.getEntities().values()) {
			propagateEntity(params, e);
		}
		;

		Fort f = InterestsBase.getInstance().getRadiantFort();
		propagateInterest(params, f);
	}
}
//