package kocur.lina.agent;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.decisions.Target;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.server.AgentCommands;
import cz.cuni.mff.kocur.world.Ability;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.Hero;

/**
 * Decision to cast Lina's Light Array.
 * 
 * @author kocur
 *
 */
public class CastLightArray extends Decision {
	/**
	 * Logger for CastLightArray
	 */
	private static final Logger logger = LogManager.getLogger(CastLightArray.class);

	@Override
	public AgentCommand execute() {
		Location location = context.getTarget().getLocation();
		Ability ability = context.getTarget().getAbility();

		super.execute();

		// Log it
		logger.info("Casting light strike array");

		return new AgentCommands.Cast(ability.getAbilityIndex(), location);
	}

	@Override
	public void updateContext(ExtendedAgentContext bc) {
		// Get hero and lightStrikeArray ability
		Hero h = bc.getHero();
		Ability lightArray = h.getAbility(1);

		List<BaseEntity> entitiesInAttackRange = bc.findEntitiesInRadius(h, lightArray.getCastRange());

		ArrayList<BaseEntity> heroesInRange = new ArrayList<>();
		for (BaseEntity e : entitiesInAttackRange) {
			if (e.getTeam() == bc.getEnemyTeam()) {
				if (e.isHero()) {
					heroesInRange.add((BaseEntity) e);
				}
			}
		}

		// We should probably consider threat or something here, not just the health
		// But we should be doing selection for this spell using more decision,
		// so we are simplifying the process now.. Just select the one with lowest
		// health.
		BaseEntity woundedHero = null;
		if (heroesInRange.size() > 0)
			woundedHero = heroesInRange.stream().min((a, b) -> Integer.compare(a.getHealth(), b.getHealth())).get();

		Target target = new Target();
		target.setEntity(woundedHero);
		target.setAbility(h.getAbility(0));
		this.context.setTarget(target);
	}

	@Override
	public void presetContext(ExtendedAgentContext bc) {
		this.context.setBotContext(bc);
		this.context.setSource(bc.getHero());
	}
}
