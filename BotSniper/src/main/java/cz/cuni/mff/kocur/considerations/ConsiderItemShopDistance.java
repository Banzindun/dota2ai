package cz.cuni.mff.kocur.considerations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.world.GridBase;
import cz.cuni.mff.kocur.world.Item;

public class ConsiderItemShopDistance extends Consideration{
	private static final Logger logger = LogManager.getLogger(ConsiderItemShopDistance.class);

	public ConsiderItemShopDistance() {
		super();
		readableName = "ConsiderItemShopDistance";
	}
	
	@Override
	public double score(DecisionContext context) {
		Location s = context.getSource();
		
		// This is the correct shop type, just check the distance to it.
		Location shop = context.getTarget().getLocation();
		if (shop == null) {
			logger.debug("Item was null");
			return 1;
		}
		
		
		double minRange = getDoubleParameter(PARAM_RANGE_MIN);
		double maxRange = getDoubleParameter(PARAM_RANGE_MAX);
		
		double distance = GridBase.distance(s, shop);
		
		double normalized = normalize(distance, minRange, maxRange);
		
		return normalized;
	}

	
}
