package cz.cuni.mff.kocur.influence;

import java.util.HashMap;
import java.util.Map.Entry;

import cz.cuni.mff.kocur.agent.AgentParameters;
import cz.cuni.mff.kocur.agent.BotFunction;

/**
 * A builder that builds agent's parameters.
 * 
 * @author kocur
 *
 */
public class ParamsBuilder {

	/**
	 * 
	 * @return Returns new builder.
	 */
	public static ParamsBuilder build() {
		return new ParamsBuilder();
	}

	/**
	 * The parameters.
	 */
	private AgentParameters p;

	/**
	 * Entity parameters. We insert them to p at the get() method.
	 */
	private HashMap<Class<?>, EntityParameterWithInfluence> entityParameters = new HashMap<>();

	private ParamsBuilder() {
		p = new AgentParameters();
	}

	/**
	 * 
	 * @return Returns the constructed AgentParameters.
	 */
	public AgentParameters get() {
		// Set the parameters to p
		for (Entry<Class<?>, EntityParameterWithInfluence> ep : entityParameters.entrySet()) {
			p.addEntityParameter(ep.getKey(), ep.getValue());
		}

		return p;
	}

	/**
	 * Creates an entity parameter.
	 * 
	 * @param cl
	 *            Class of the entity.
	 * @param f
	 *            A function.
	 * @param <T>
	 *            Class of the entity.
	 * @return Returns this.
	 */
	public <T> ParamsBuilder createEntityParameter(Class<T> cl, BotFunction<?, T> f) {
		EntityParameterWithInfluence ep = new EntityParameterWithInfluence();
		ep.setFunction(f);

		entityParameters.put(cl, ep);

		return this;
	}

	/**
	 * Creates entity parameter.
	 * 
	 * @param cl
	 *            Class of the entity.
	 * @param f
	 *            Function.
	 * @param team
	 *            Team of the entity.
	 * @param <T>
	 *            Type of the entity's class.
	 * @return Returns this.
	 */
	public <T> ParamsBuilder createEntityParameter(Class<T> cl, BotFunction<?, T> f, int team) {
		EntityParameterWithInfluence ep = (EntityParameterWithInfluence) entityParameters.get(cl);

		if (ep != null) {
			ep.setFunction(f, team);
		} else {
			ep = new EntityParameterWithInfluence();
			ep.setFunction(f, team);
			entityParameters.put(cl, ep);
		}

		return this;
	}

	/**
	 * Creates interest parameter.
	 * 
	 * @param cl
	 *            Class of the interest.
	 * @param f
	 *            Function.
	 * @param <T>
	 *            Type of the interest class.
	 * @return Returns this.
	 */
	public <T> ParamsBuilder createInterestParameter(Class<T> cl, BotFunction<?, T> f) {
		InterestParameterWithInfluence ip = new InterestParameterWithInfluence();
		ip.setFunction(f);

		p.addInterestParameter(cl, ip);

		return this;
	}
}
