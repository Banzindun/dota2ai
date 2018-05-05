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
 * Dire threats layer. Used inside constant layers. 
 * @author kocur
 *
 */
public class DireThreatLayer extends InfluenceLayer {

	private static final Logger logger = LogManager.getLogger(DireThreatLayer.class);

	public DireThreatLayer(int w, int h, int resolution) {
		super(w, h, resolution);

		logger.info("Created dire layer with height and width: " + height + ", " + width);
	}

	public DireThreatLayer(DireThreatLayer l) {
		super(l);
		setParams(l.getParams());
	}

	@Override
	public void createParams() {
		// Calculate half the diagonal.
		// I want to spread influence from forts to that range
		//double h = GridBase.getInstance().getHeight();
		double w = GridBase.getInstance().getWidth();
		// double halfDiagonal = (Math.sqrt(h*h + w*w)/2)/resolution;

		PolynomialAttackRangePropagationFunction<Tower> towerP = new PolynomialAttackRangePropagationFunction<Tower>();
		towerP.setMaxInfluence(2.0);
		towerP.setPower(2.0);
		towerP.setSign(-1);

		// Initialize parameters.
		params = ParamsBuilder.build().createEntityParameter(Tower.class, towerP, Team.DIRE)
				.createInterestParameter(Fort.class, new FortPropagation<Fort>(-1, w / resolution)).get();

	}

	@Override
	public void propagate() {
		World w = WorldManager.getWorld();

		// Clear the grid
		clearInfluence();

		for (BaseEntity e : w.getEntities().values()) {
			propagateEntity(params, e);
		}
		;

		Fort f = InterestsBase.getInstance().getDireFort();
		propagateInterest(params, f);

	}
}
