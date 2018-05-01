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
import cz.cuni.mff.kocur.events.GameEvent;
import cz.cuni.mff.kocur.events.GameEventBuilder;
import cz.cuni.mff.kocur.events.ListenersManager;
import cz.cuni.mff.kocur.server.TimeManager;

public class Building extends StaticEntity  {
	private static final Logger logger = LogManager.getLogger(Building.class);
	
	/**
	 * Time for how long this entity should be alive. Should be defined and configured for every entity.
	 */
	@JsonIgnore
	public static float timeToLive = 1;
	
	protected float getTimeToLive() {
		return timeToLive; 
	}
	
	@Override
	public void born() {
		super.born();
		
		TeamContext tc = ControllersManager.getInstance().getTeamContext(team);
		
		tc.addBuilding(entid, this);
	}
	
	@Override
	public void dying() {
		TeamContext tc = ControllersManager.getInstance().getTeamContext(team);
		
		tc.removeBuilding(entid);
		
		logger.info("Building with id " + entid + " destroyed.");
		
		GameEvent e = GameEventBuilder.build()
				.location(x, y, z)
				.source(entid)
				.team(team)
				.get();
		ListenersManager.triggerEvent("building_destroyed", e);
	}
	
	public Building() {
		super();
	}

	@Override
	public void paint(Integer[] crds, Graphics2D g) {
		Colors.setTeamColor(team, g);
		g.fillRect(crds[0]-3, crds[1]-3, 6, 6);
	}
	
	@Override
    public void setEntid(int id) {
		this.entid = id;
    }
	
	@Override
	public boolean shouldDie() {
		if (TimeManager.getGameTime() - lastUpdate > getTimeToLive()) {
			return true;
		}
		return false;
	}

    @Override
	public EntityParameter getParameter(AgentParameters params) {
		EntityParameter p = params.getEntityParameter(Building.class);
		
		if (p == null) return super.getParameter(params);
		return p;
	} 
    
    @Override
    public String toString() {
    	IndentationStringBuilder builder = new IndentationStringBuilder();
		builder.appendLine("Building: " + entid);
		builder.indent();
		builder.appendLines(super.toString());
		return builder.toString();
    }

    
    @Override 
	public boolean isBuilding() {
		return true;
	}
}
