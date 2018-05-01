package cz.cuni.mff.kocur.world;

import java.awt.Graphics2D;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.bot.AgentParameters;
import cz.cuni.mff.kocur.bot.ControllersManager;
import cz.cuni.mff.kocur.bot.EntityParameter;
import cz.cuni.mff.kocur.bot.TeamContext;
import cz.cuni.mff.kocur.events.FrameworkEvent;
import cz.cuni.mff.kocur.events.GameEvent;
import cz.cuni.mff.kocur.events.GameEventBuilder;
import cz.cuni.mff.kocur.events.ListenersManager;
import cz.cuni.mff.kocur.server.TimeManager;

public class Tower extends Building {
	private static Logger logger = LogManager.getLogger(Tower.class);
	
	/**
	 * Time for how long this entity should be alive. Should be defined and configured for every entity.
	 */
	@JsonIgnore
	public static float timeToLive = 2;
	
	protected float getTimeToLive() {
		return timeToLive; 
	}
	
	@Override
	public void born() {
		super.born();
		
		TeamContext tc = ControllersManager.getInstance().getTeamContext(team);
		tc.addTower(entid, this);
	}
	
	@Override
	public void dying() {
		logger.info("Tower destroyed at:" + TimeManager.getGameTime() + "lu: " + lastUpdate);
		
		GameEvent e = GameEventBuilder.build()
				.location(x, y, z)
				.source(entid)
				.team(team)
				.get();
				
		TeamContext tc = ControllersManager.getInstance().getTeamContext(team);
		tc.removeTower(entid);
		
		ListenersManager.triggerEvent("tower_destroyed", e);
		ListenersManager.triggerFrameworkEvent("tower_destroyed", new FrameworkEvent(), this);

	}
	
	/**
	 * Checks if this entity is without visual long enough so that it can be removed safely.
	 * This method implicitly returns true and should be overridden by entities that don't want to be removed (dynamic ones).
	 * @return True if the entity should be removed (it lived longer without any visuals than intended). False otherwise.
	 */
	@Override
	public boolean shouldDie() {
		if (TimeManager.getGameTime() - lastUpdate > getTimeToLive()) {
			return true;
		}
		return false;
	}
		
	public Tower() {
		super();
	}

	public void paint(int[] crds, Graphics2D g) {
		Colors.setTeamColor(team, g);
		g.fillRect(crds[0]-9, crds[1]-9, 18, 18);

	}
	
	@Override
    public void setEntid(int id) {
		this.entid = id;
    }
	
	@Override
	public EntityParameter getParameter(AgentParameters params) {
		EntityParameter p = params.getEntityParameter(Tower.class);
		
		if (p == null) return super.getParameter(params);
		return p;
	} 
	
	@Override
    public String toString() {
		IndentationStringBuilder builder = new IndentationStringBuilder();
		builder.appendLine("Tower: " + entid);
		builder.indent();
		builder.appendLines(super.toString());
		return builder.toString();
    }

    
    @Override
	public boolean isTower() {
		return true;
	}
    
    @Override 
	public boolean isBuilding() {
		return true;
	}
}
