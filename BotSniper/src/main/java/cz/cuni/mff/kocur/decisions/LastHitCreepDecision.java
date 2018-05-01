package cz.cuni.mff.kocur.decisions;

import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.influence.InfluenceLayer;
import cz.cuni.mff.kocur.world.Creep;
import kocur.lina.bot.LayeredBotContext;

public class LastHitCreepDecision extends AttackCreepDecision {

	@Override
	public void updateContext(ExtendedBotContext bc, Creep c) {

		InfluenceLayer l = bc.getLayer(LayeredBotContext.FRIENDLY_THREATS);

		double[] coords = l.getEntityCoordinates(c);
		double influence = l.get((int) coords[0], (int) coords[1]);

		Target target = new Target(c);
		target.setValue(influence);

		this.context.setTarget(target);
	}
}