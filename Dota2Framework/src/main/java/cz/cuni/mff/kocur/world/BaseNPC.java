package cz.cuni.mff.kocur.world;

import java.awt.Graphics2D;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.bot.AgentParameters;
import cz.cuni.mff.kocur.bot.EntityParameter;

public class BaseNPC extends DynamicEntity {
	/**
	 * Time for how long this entity should be alive. Should be defined and configured for every entity.
	 */
	@JsonIgnore
	public static float timeToLive = 1;
	
	protected float getTimeToLive() {
		return timeToLive; 
	}
		
	public BaseNPC() {
		super();
	}	

	
	@Override
	public void paint(Integer[] crds, Graphics2D g) {
		Colors.setTeamColor(team, g);
    	g.fillRect(crds[0]-1, crds[1]-1, 2, 2);
	}	
	
    @Override
    public String toString() {
        return super.toString();
    }

    @Override
	public EntityParameter getParameter(AgentParameters params) {
		EntityParameter p = params.getEntityParameter(BaseNPC.class);
		
		if (p == null) return super.getParameter(params);
		return p;
	}

    @Override
	public void update(BaseEntity h) {
		super.update(h);		
	}

}
