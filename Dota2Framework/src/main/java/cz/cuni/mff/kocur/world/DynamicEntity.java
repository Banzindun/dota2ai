package cz.cuni.mff.kocur.world;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.cuni.mff.kocur.agent.AgentParameters;
import cz.cuni.mff.kocur.agent.EntityParameter;

/**
 * Abstract class, that represents a dynamic entity. That is an entity, that can
 * move and change state toward the player.
 * 
 * @author kocur
 *
 */
public abstract class DynamicEntity extends BaseEntity {
	/**
	 * Time for how long this entity should be alive. Should be defined and
	 * configured for every entity.
	 */
	@JsonIgnore
	public static float timeToLive = 100;

	protected float getTimeToLive() {
		return timeToLive;
	}

	public DynamicEntity() {
		super();
	}

	@Override
	public EntityParameter getParameter(AgentParameters params) {
		EntityParameter p = params.getEntityParameter(DynamicEntity.class);

		if (p == null)
			return super.getParameter(params);
		return p;
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	public void update(BaseEntity e) {
		super.update(e);
	}

	@Override
	public boolean isDynamicEntity() {
		return true;
	}

}
