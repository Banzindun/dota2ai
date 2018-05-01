package cz.cuni.mff.kocur.influence;

import java.util.HashMap;
import java.util.Map.Entry;

import cz.cuni.mff.kocur.bot.AgentParameters;
import cz.cuni.mff.kocur.bot.BotFunction;

public class ParamsBuilder {
	
	//private static final Logger logger = LogManager.getLogger(ParamsBuilder.class.getName());
	
	public static ParamsBuilder build() {
		return new ParamsBuilder();
	}	
	
	private AgentParameters p;
	
	private HashMap<Class<?>, EntityParameterWithInfluence> entityParameters = new HashMap<>();
	
	private ParamsBuilder() {
		p = new AgentParameters();
	}
	
	public <T> AgentParameters get() {
		// Set the parameters to p
		for (Entry<Class<?>, EntityParameterWithInfluence> ep : entityParameters.entrySet()) {
			p.addEntityParameter(ep.getKey(), ep.getValue());
		}
		
		return p;
	}
	
	public <T> ParamsBuilder createEntityParameter(Class<T> cl, BotFunction<?, T> f) {
		EntityParameterWithInfluence ep = new EntityParameterWithInfluence();
		ep.setFunction(f);
		
		entityParameters.put(cl,  ep);

		return this;
	}
	
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


	public <T> ParamsBuilder createInterestParameter(Class<T> cl, BotFunction<?, T> f) {
		InterestParameterWithInfluence ip = new InterestParameterWithInfluence();
		ip.setFunction(f);
		
		p.addInterestParameter(cl, ip);
		
		return this;
	}
}
