package cz.cuni.mff.kocur.agent;

import cz.cuni.mff.kocur.configuration.HeroConfiguration;
import cz.cuni.mff.kocur.console.Controllable;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.world.ChatEvent;
import cz.cuni.mff.kocur.world.Hero;

/**
 * This interface represents a controller. The controller is a class that
 * controls the bot. So the functions that might be necessary to that must be
 * supplied. For example update, levelup etc.
 * 
 * @author Banzindun
 *
 */
public interface AgentController extends Controllable {
	/**
	 * Sets id of this bot.
	 * 
	 * @param id
	 *            Id of this bot.
	 */
	public void setId(int id);

	/**
	 * 
	 * @return Returns id of the bot, that this controller handles.
	 */
	public int getId();

	/**
	 * 
	 * @return Returns the Hero object controlled by this object.
	 */
	public Hero getHero();

	/**
	 * Sets the hero for this controller.
	 * 
	 * @param hero
	 *            New hero.
	 */
	public void setHero(Hero hero);

	/**
	 * Called on hero's levelup.
	 * @return Should return the index of ability, that the hero wants to level up.
	 */
	public int onLevelup();

	/**
	 * 
	 * Will be called after chat event occured in game.
	 * 
	 * @param e
	 *            ChatEvent object that contains the text that was send etc.
	 * 
	 */
	void onChat(ChatEvent e);

	/**
	 * Resets the bot.
	 */
	void reset();

	/**
	 * This function is called each update. It should be extended by the agent and
	 * should return its command in response to world data and its logic.
	 * 
	 * @return Returns the agent's command.
	 */
	AgentCommand update();

	/**
	 * Initializes this bot controler. Called after bots were selected and interest
	 * loaded. That means after the bot's context is initialized.
	 * 
	 */
	public void initialize();

	/**
	 * Sets the configuration for this controller.
	 * 
	 * @param bc
	 *            New bot configuration.
	 */
	public void setConfiguration(HeroConfiguration bc);

	/**
	 * 
	 * @return Returns this bot's configuration.
	 */
	public HeroConfiguration getConfiguration();

	/**
	 * This function is called when this controller is being destroyed.
	 */
	public void destroy();

	/**
	 * 
	 * @return Returns this bot's context. The context contains some information
	 *         that is regularly used by the bot.
	 */
	public AgentContext getContext();

	/**
	 * Updates our hero. We want to keep the reference pointing at the same object,
	 * so that controllers etc. don't need to ask for a hero reference every tick.
	 * 
	 * @param h
	 *            Hero with id that corresponds to our hero.
	 */
	public void updateHero(Hero h);

	/**
	 * 
	 * @return Returns teamContext of this controller.
	 */
	public TeamContext getTeamContext();

	/**
	 * Sets team context for this controller.
	 * 
	 * @param teamContext
	 *            Team context.
	 */
	public void setTeamContext(TeamContext teamContext);
}
