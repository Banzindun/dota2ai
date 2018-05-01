package cz.cuni.mff.kocur.bot;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * AgentParameters for entities.
 * They are stored inside map that uses entity's class as a key and EntityParameters as a value. Those can be later retrieved by entitites/agents and can store any type of information. 
 * For now the EntityParameter class is empty, but I extend in example agent implementation. There I store propagation functions, that depend on a class name and a team of the entity.
 * 
 *	The same principle is applied for Interests.
 * @author kocur
 *
 */
public class AgentParameters {
	/**
	 * Stores propagation functions, that should be addressed by class names (that is the 
	 * way how I will use this). PropagationFunctions should take InfluenceLayer and reference
	 * to entity, they are beeing applied to as arguments. They should be void. 
	 * {@link cz.cuni.mff.kocur.influence.PropagationFunction<T>} </br>
	 * 
	 * For example: There can be field "Hero" that will store function that takes InfluenceLayer
	 * and Hero entity as arguments that propagates the influence layer from hero.  
	 */
	/*private HashMap<Class<?>, BotFunction<?, ?>> functions = new HashMap<>();
	
	//private static final Logger logger = LogManager.getLogger(BotParameters.class.getName());
	
	public <O, T> void addFunction(Class<T> cl, BotFunction<O, T> f) {
		functions.put(cl, f);
	}
	
	public <O, T> BotFunction<O, T> getFunction(Class<T> cl) {
		return (BotFunction<O, T>) functions.get(cl);
	}*/
	
	/**
	 * Map of classname, InterestParameter pairs.
	 */
	private HashMap<Class<?>, InterestParameter> interestsParams = new HashMap<>();
	
	/**
	 * Map of classname, EntityParameter pairs.
	 */
	private HashMap<Class<?>, EntityParameter> entityParams = new HashMap<>();
	
	/**
	 * Adds a new entity parameter.
	 * @param cl Class of the entity.
	 * @param p The parameter.
	 */
	public <T> void addEntityParameter(Class<T> cl, EntityParameter p) {
		entityParams.put(cl, p);
	}
	
	/**
	 * 
	 * @param cl Class of the parameter, that we are looking for.
	 * @return Returns the entity parameter. 
	 */
	public <T> EntityParameter getEntityParameter(Class<T> cl) {
		return entityParams.get(cl);
	}

	/**
	 * Adds a new interest parameter. 
	 * @param cl Class of the interest parameter.
	 * @param ip The acual parameter.
	 */
	public <T> void addInterestParameter(Class<T> cl, InterestParameter ip) {
		interestsParams.put(cl , ip);		
	}
	
	/**
	 * 
	 * @param cl Classname of the interest parameter we want to get.
	 * @return Returns an interest parameter. 
	 */
	public <T> InterestParameter getInterestParameter(Class<T> cl) {
		return interestsParams.get(cl);
	}

}
