package cz.cuni.mff.kocur.configuration;

import java.util.HashMap;
import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.cuni.mff.kocur.base.Pair;
import cz.cuni.mff.kocur.exceptions.ConfigurationTestFailureException;
import cz.cuni.mff.kocur.interests.Team;


/**
 * Configuration of in-game bot. It should extend a HeroConfiguration and
 * it should have some custom fields as difficulty and lane.
 * Those parameters are explained below:
 * 
 * 	difficulty - difficulty of the bot (easy, medium, hard, unfair)
 * 	lane - lane where the bot plays	
 * 
 * @author kocur
 *
 */
public class BotConfiguration extends HeroConfiguration{

	/**
	 * Constructor, that sets configuration's required fields and type.
	 */
	public BotConfiguration() {
		type = TYPE.BOT;
		
		addRequiredItems("difficulty", "lane");
	}
	
	
	@Override
	public void test() throws ConfigurationTestFailureException{
		super.test();
	}
	
	@Override
	public HashMap<String, String> getSignature() {
		HashMap<String, String> values = super.getSignature();
		
		values.put("heroName", "npc_dota_hero_" + this.getConfigValue("champion"));
		values.put("team", Team.parseTeam(this.getConfigValue("team")) + "");
		values.put("lane", this.getConfigValue("lane"));
		values.put("difficulty", this.getConfigValue("difficulty"));
		
		return values;
	}
	
	@JsonIgnore
	public LinkedList<Pair<String, String>> getImportantValues() {
		LinkedList<Pair<String, String>> important = new LinkedList<>();

		important.add(new Pair<String, String>("Hero", getConfigValue("champion")));
		important.add(new Pair<String, String>("Lane", getConfigValue("lane")));
		important.add(new Pair<String, String>("Difficulty", getConfigValue("difficulty")));
		
		return important;
	}

	
}
