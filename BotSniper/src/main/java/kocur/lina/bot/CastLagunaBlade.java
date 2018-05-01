package kocur.lina.bot;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.decisions.Target;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.server.AgentCommands;
import cz.cuni.mff.kocur.world.Ability;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.Hero;

public class CastLagunaBlade extends Decision{
	private static final Logger logger = LogManager.getLogger(CastLagunaBlade.class);

	@Override
	public AgentCommand execute() {
		BaseEntity target = context.getTarget().getEntity();
		Ability ability = context.getTarget().getAbility();
		
		super.execute();
		
		return new AgentCommands.Cast(ability.getAbilityIndex(), target);
	}
	
	@Override
	public void updateContext(ExtendedBotContext bc) {
		// Get the hero and the lagunaBlade ability
		Hero h = bc.getHero();
		Ability lagunaBlade = h.getAbility(5);

		List<BaseEntity> entitiesInAttackRange = bc.findEntitiesInRadius(h, lagunaBlade.getCastRange());
		
		ArrayList<BaseEntity> heroesInRange = new ArrayList<>();
		for (BaseEntity e : entitiesInAttackRange) {
			if (e.getTeam() == bc.getEnemyTeam()) {
				if (e.isHero()) {
					heroesInRange.add((BaseEntity) e);
				}
			}
		}
		
		BaseEntity woundedHero = null;
		if (heroesInRange.size() > 0) 
			woundedHero = heroesInRange.stream().min((a,b) -> Integer.compare(a.getHealth(), b.getHealth())).get();
		
		Target target = new Target();
		target.setEntity(woundedHero);
		target.setAbility(h.getAbility(0));
		this.context.setTarget(target);
	}

	@Override
	public void presetContext(ExtendedBotContext bc) {
		this.context.setBotContext(bc);
		this.context.setSource(bc.getHero());
	}
	
	
}