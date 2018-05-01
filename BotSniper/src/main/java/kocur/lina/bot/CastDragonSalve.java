package kocur.lina.bot;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.decisions.Target;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.server.AgentCommands;
import cz.cuni.mff.kocur.world.Ability;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.Hero;

public class CastDragonSalve extends Decision{
	private static final Logger logger = LogManager.getLogger(CastDragonSalve.class);

	@Override
	public AgentCommand execute() {
		Location location = context.getTarget().getLocation();
		Ability ability = context.getTarget().getAbility();
		
		super.execute();
		
		return new AgentCommands.Cast(ability.getAbilityIndex(), location);
	}
	
	@Override
	public void updateContext(ExtendedBotContext bc) {
		// Get list of entities around me, sorted by distance.
		List<BaseEntity> entitiesInAttackRange = bc
				.findEntititesAroundMe(bc.getHero().getAttackRange());
		
		ArrayList<BaseEntity> enemiesInRange = new ArrayList<>();
		for (BaseEntity e : entitiesInAttackRange) {
			if (e.getTeam() == bc.getEnemyTeam()) {
				if (e.isCreep() || e.isHero()) {
					enemiesInRange.add((BaseEntity) e);
				}
			}
		}
		
		Hero h = bc.getHero();
		
		Target target = new Target();
		target.setValue(enemiesInRange.size());
		target.setAbility(h.getAbility(0));
		
		// Calculate the center of mass and set it at target location,
		// we wil cast the shrapnel there
		double[] xy = centerOfMass(enemiesInRange);
		target.setLocation(new Location(xy));
		
		this.context.setTarget(target);		
	}

	private double[] centerOfMass(ArrayList<BaseEntity> enemies) {
		double xSum = 0;
		double ySum = 0;
		
		double massSum = 0;
		
		for (BaseEntity e : enemies) {
			double healthRatio = e.getHealth()/e.getMaxHealth(); 
			xSum += healthRatio*e.getX();
			ySum += healthRatio*e.getY();
			
			massSum += healthRatio;
		}
		
		return new double[]{xSum/massSum, ySum/massSum};
	}

	@Override
	public void presetContext(ExtendedBotContext bc) {
		this.context.setBotContext(bc);
		
		this.context.setSource(bc.getHero());
	}
	
}
