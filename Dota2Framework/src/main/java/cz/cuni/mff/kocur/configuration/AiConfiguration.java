package cz.cuni.mff.kocur.configuration;

import java.util.HashMap;
import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.cuni.mff.kocur.base.Pair;
import cz.cuni.mff.kocur.exceptions.ConfigurationTestFailureException;
import cz.cuni.mff.kocur.interests.Team;

/**
 * Configuration of artificial agent. It should extend a HeroConfiguration and
 * it should have some custom fields as class, roles, lane and a champion.
 * Those parameters are explained below:
 * 
 * 	class - class where is the agent's implementation stored (== it's controller)
 * 	logger_classpath - class for which we register our logger, preferably root of our project
 * 	roles - roles, that the AI plays
 *  lane - lane that the AI plays
 *  champion - name of the champion that the AI plays 	
 * @author kocur
 *
 */
public class AiConfiguration extends HeroConfiguration {

	/**
	 * Constructor that sets the type of the Ai and its required items.
	 */
	public AiConfiguration() {
		type = TYPE.AI;

		addRequiredItems("class", "logger_classpath", "roles", "lane");
	}

	@Override
	public void test() throws ConfigurationTestFailureException {
		super.test();

	}

	@Override
	public HashMap<String, String> getSignature() {
		HashMap<String, String> values = super.getSignature();

		values.put("heroName", "npc_dota_hero_" + this.getConfigValue("champion"));
		values.put("team", Team.parseTeam(this.getConfigValue("team")) + "");

		return values;
	}

	@JsonIgnore
	@Override
	public LinkedList<Pair<String, String>> getImportantValues() {
		LinkedList<Pair<String, String>> important = new LinkedList<>();

		important.add(new Pair<String, String>("Hero", getConfigValue("champion")));
		important.add(new Pair<String, String>("Lane", getConfigValue("lane")));
		important.add(new Pair<String, String>("Roles", getConfigValue("roles")));
		important.add(new Pair<String, String>("Class", getConfigValue("class")));
		important.add(new Pair<String, String>("Logger classpath", getConfigValue("logger_classpath")));

		return important;
	}

}
