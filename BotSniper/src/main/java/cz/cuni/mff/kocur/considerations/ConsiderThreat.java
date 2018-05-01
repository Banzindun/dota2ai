package cz.cuni.mff.kocur.considerations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.influence.InfluenceLayer;
import cz.cuni.mff.kocur.interests.Team;
import cz.cuni.mff.kocur.world.Creep;
import kocur.lina.bot.LayeredBotContext;

public class ConsiderThreat extends Consideration{

	/**
	 * Logger registered for ConsiderThreatOnTarget class.
	 */
	private static final Logger logger = LogManager.getLogger(ConsiderThreat.class);
	
	public ConsiderThreat() {
		this.readableName = "ConsiderThreat";
	}
	
	
	@Override
	public double score(DecisionContext context) {
		double influence = context.getTarget().getValue();
		
		if (influence == Double.NEGATIVE_INFINITY) {
			return 0;
		}

		double max = this.getDoubleParameter(PARAM_RANGE_MAX);
		double min = this.getDoubleParameter(PARAM_RANGE_MIN);
		
		return normalize(influence, min, max);
	}	
}
