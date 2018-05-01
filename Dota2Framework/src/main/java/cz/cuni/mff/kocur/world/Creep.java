package cz.cuni.mff.kocur.world;

import java.awt.Graphics2D;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.bot.AgentParameters;
import cz.cuni.mff.kocur.bot.ControllersManager;
import cz.cuni.mff.kocur.bot.EntityParameter;
import cz.cuni.mff.kocur.bot.TeamContext;
import cz.cuni.mff.kocur.interests.InterestsBase;
import cz.cuni.mff.kocur.interests.Team;
import cz.cuni.mff.kocur.server.TimeManager;

public class Creep extends BaseNPC{
	/**
	 * Time for how long this entity should be alive. Should be defined and configured for every entity.
	 */
	@JsonIgnore
	public static float timeToLive = 1;
	
	/**
	 * Reference to lane, to which this creep is assigned to.
	 */
	/*@JsonIgnore
	private Lane lane = null;*/
	
	/**
	 * Reference to camp that this creep is assigned to.
	 */
	/*@JsonIgnore
	private Camp camp = null;*/
	
	/*public Lane getLane() {
		return lane;
	}
	
	public void setLane(Lane lane) {
		this.lane = lane;
	}*/
	
	protected float getTimeToLive() {
		return timeToLive; 
	}
	
	@Override
	public void born() {
		super.born(); 
		
		if (team != Team.NEUTRAL) {
			TeamContext tc = ControllersManager.getInstance().getTeamContext(team);
			tc.addCreep(entid, this);
			
			InterestsBase.getInstance().getLanes().addCreep(team, this);
		} else {
			
			if (this.name.contains("roshan")) {
				InterestsBase.getInstance().getRiver().setRoshan(this);
			} else {
				InterestsBase.getInstance().getNearestCamp(this).addCreep(this);
			}
		}
	}
		
	public Creep() {
		super();
	}
	
	@JsonSetter("entid")
	@Override
    public void setEntid(int id) {
		super.setEntid(id);
    }
	
	@Override
	public void dying() {
		TeamContext tc = ControllersManager.getInstance().getTeamContext(team);
		tc.removeCreep(entid);
		
		if (team != Team.NEUTRAL) {
			InterestsBase.getInstance().getLanes().removeCreep(team, this);
		} else {
			InterestsBase.getInstance().removeCreepFromJungles(this);
		}		
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
		EntityParameter p = params.getEntityParameter(Creep.class);
		
		if (p == null) return super.getParameter(params);
		return p;
	} 
	
	@Override
	public void paint(Integer[] crds, Graphics2D g) {
		Colors.setTeamColor(team, g);
    	g.fillRect(crds[0]-2, crds[1]-2, 4, 4);
	}	

	@Override
	public String toString() {
		IndentationStringBuilder builder = new IndentationStringBuilder();
		builder.appendLine("Creep: " + entid);
		builder.indent();
		builder.appendLines(super.toString());
		return builder.toString();
	}

	
    @Override
	public boolean isCreep() {
		return true;
	}
	
}
