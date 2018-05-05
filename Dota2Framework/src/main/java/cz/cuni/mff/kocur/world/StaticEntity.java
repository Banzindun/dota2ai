package cz.cuni.mff.kocur.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import cz.cuni.mff.kocur.agent.AgentParameters;
import cz.cuni.mff.kocur.agent.EntityParameter;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Class that represents a static entity. Static entity is an entity that do not
 * move.
 * 
 * @author kocur
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(name = "Tower", value = Tower.class), @Type(name = "Building", value = Building.class),
		@Type(name = "Tree", value = Tree.class) })
public abstract class StaticEntity extends BaseEntity {
	/**
	 * Time for how long this entity should be alive. Should be defined and
	 * configured for every entity.
	 */
	@JsonIgnore
	public static float timeToLive = 100;

	protected float getTimeToLive() {
		return timeToLive;
	}

	public StaticEntity() {
		super();
	}

	@Override
	public EntityParameter getParameter(AgentParameters params) {
		EntityParameter p = params.getEntityParameter(StaticEntity.class);

		if (p == null)
			return super.getParameter(params);
		return p;
	}

	public String toString() {
		return super.toString();
	}

	@Override
	public boolean isStaticEntity() {
		return true;
	}

}
