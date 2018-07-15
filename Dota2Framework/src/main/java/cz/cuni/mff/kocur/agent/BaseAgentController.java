package cz.cuni.mff.kocur.agent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.configuration.ConfigurationChangeListener;
import cz.cuni.mff.kocur.configuration.HeroConfiguration;
import cz.cuni.mff.kocur.console.ControllableAgent;
import cz.cuni.mff.kocur.interests.Healer;
import cz.cuni.mff.kocur.interests.Rune;
import cz.cuni.mff.kocur.server.AgentCommand;
import cz.cuni.mff.kocur.server.AgentCommands.Attack;
import cz.cuni.mff.kocur.server.AgentCommands.Buy;
import cz.cuni.mff.kocur.server.AgentCommands.Cast;
import cz.cuni.mff.kocur.server.AgentCommands.GrabAll;
import cz.cuni.mff.kocur.server.AgentCommands.Move;
import cz.cuni.mff.kocur.server.AgentCommands.Noop;
import cz.cuni.mff.kocur.server.AgentCommands.PickupRune;
import cz.cuni.mff.kocur.server.AgentCommands.Sell;
import cz.cuni.mff.kocur.world.Ability;
import cz.cuni.mff.kocur.world.Hero;

/**
 * Abstract class that implements some of the methods that can be used by sample
 * Controller. Also unregisters logger for root class of registered bot. Defines
 * and handles hero id and creates hero information. Implemented methods are for
 * example Attack, MoveTo, GrabAll, CastASpell
 * 
 * @author Banzindun
 *
 */
public abstract class BaseAgentController implements ControllableAgent, AgentController, ConfigurationChangeListener {
	/**
	 * BaseAgentController logger.
	 */
	protected Logger logger = LogManager.getLogger(BaseAgentController.class.getName());

	/**
	 * Agent's context.
	 */
	protected AgentContext agentContext = null;

	/**
	 * Team context of this agent. Contains reference to other heroes etc. on the same
	 * team.
	 */
	protected TeamContext teamContext = null;

	/**
	 * Configuration of this agent.
	 */
	protected HeroConfiguration configuration = null;

	/**
	 * Agent's id.
	 */
	protected Integer id = -1;

	/**
	 * Reference to actual hero object, that is the hero that comes through updates
	 * etc.
	 */
	protected Hero hero = null;

	protected String buyOrder = "";

	/**
	 * Constructor.
	 */
	public BaseAgentController() {
		// Will be -1
		agentContext = new AgentContext(this);
	}

	@Override
	public void setConfiguration(HeroConfiguration bc) {
		this.configuration = bc;
	}

	@Override
	public HeroConfiguration getConfiguration() {
		return configuration;
	}
	
	@Override
	public void initialize() {
		updateAgentContext();
		
		// Add change listener to configuration change
		configuration.addChangeListener(this);
	}
	
	/**
	 * Updates the agent's context from the configuration. 
	 * This is called on every configuration change and during init.
	 */
	private void updateAgentContext() {
		// Set my team
		agentContext.setMyTeam(configuration.getConfigValue("team"));

		// Set champion name
		agentContext.setHeroName(configuration.getConfigValue("champion"));

		// Set lane
		agentContext.setMyLane(configuration.getConfigValue("lane"));

		// Set roles of this hero
		agentContext.setMyRoles(configuration.getConfigValue("roles"));
	}

	@Override
	public void destroy() {
		agentContext.destroyed();
	}

	/**
	 * Resets the controller.
	 */
	public void reset() {
		// Do the reset.
	}

	@Override
	public AgentContext getContext() {
		return agentContext;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public Hero getHero() {
		return hero;
	}

	@Override
	public void setHero(Hero h) {
		// Hero set for the first time.
		this.hero = h;
	}

	@Override
	public void updateHero(Hero h) {
		if (hero == null)
			setHero(new Hero(h));
		else
			hero.update(h);
	}

	/**
	 * This is a command, that can be used by any controller to move to some
	 * location. This will be extended by BaseagentController.
	 * 
	 * @param l
	 *            Location where we want to move. The location can have id or x, y
	 *            set in it. If only id is set, we will move to entity with that id.
	 *            If only location is set, we will move to that location.
	 * @return Returns agentCommand, that moves the agent to given location.
	 */
	public AgentCommand moveTo(Location l) {
		return new Move(l.getX(), l.getY(), 0.0);
	}

	/**
	 * This is a command that can be used by extending controller to issue a command
	 * to pickup a rune.
	 * 
	 * @param b
	 *            The rune.
	 * @return Returns command to come closer to rune or to pick it up if standing
	 *         too close.
	 */
	public AgentCommand pickupRune(Rune b) {
		if (agentContext.inRange(b, 15) && b.isActive()) {
			b.pickedUp();
			return new PickupRune(b.getEntid());
		} else
			return new Move(b.getX(), b.getY(), 0.0);
	}

	/**
	 * This is a command that can be used by extending controller to issue a command
	 * to use a shrine.
	 * 
	 * @param h
	 *            The shrine (healer).
	 * @return Returns command to come closer to shrine or to use it, if very close.
	 */
	public AgentCommand useShrine(Healer h) {
		if (agentContext.inRange(h, 15) && h.isActive()) {
			h.deplete();
			return new Attack(h.getEntid());
		} else
			return new Move(h.getX(), h.getY(), 0.0);
	}

	/**
	 * This is a command that can be used by extending controller to issue a command
	 * to buy a item given by its name.
	 * 
	 * @param item
	 *            Name of the item to buy.
	 * @return Returns command to buy a specified item.
	 */
	public AgentCommand buy(String item) {
		return new Buy(item);
	}

	/**
	 * This is a command that can be used by extending controller to issue a command
	 * to sell a item on given slot.
	 * 
	 * @param slot
	 *            Slot that contains the item that should be sold.
	 * @return Returns command that sells item on given slot.
	 */
	public AgentCommand sell(int slot) {
		return new Sell(slot);
	}

	/**
	 * This is a command that can be used by extending controller to issue a command
	 * to sell item, given its name. The item is found using the name.
	 * 
	 * @param itemName
	 *            Name of the item to be sold.
	 * @return Returns command that sells the item with specified name.
	 */
	public AgentCommand sell(String itemName) {
		int slot = this.hero.getInventory().findInInventory(itemName);

		if (slot == -1) {
			logger.warn("I cannot sell item that is not inside inventory: " + itemName);
			return new Noop();
		}

		return new Sell(slot);
	}

	/**
	 * This is a command that can be used by extending controller to issue a command
	 * that casts a spell at index at given location.
	 * 
	 * @param l
	 *            Location with id or x, y coordinates. If only id given, the spell
	 *            will be cast on target with given id. Else the spell will be cast
	 *            on given location, if it is not a passive spell.
	 * @param index
	 *            Index of the spell.
	 * @return Returns a command to cast a spell (by index) to given location.
	 */
	public AgentCommand castASpell(Location l, int index) {
		Ability a = hero.getAbility(index);
		// Check that the location is in range
		if (l.isLocationSet() && !agentContext.inRange(this.hero, l, a.getCastRange())) {
			logger.warn("Out of range.");
			return new Noop();
		} else if (l.idSet() && !agentContext.inRange(this.hero, l.getEntid(), a.getCastRange())) {
			logger.warn("Target out of range.");
			return new Noop();
		}

		// Check if the spell is passive.
		if ((a.getBehavior() & Ability.DOTA_ABILITY_BEHAVIOR_PASSIVE) > 0) {
			logger.warn("Cannot cast a passive spell.");
		}

		logger.info("Trying to cast: " + a.toString());

		// Cast the spell
		if ((a.getBehavior() & Ability.DOTA_ABILITY_BEHAVIOR_UNIT_TARGET) > 0) {
			logger.info("Casting DOTA_ABILITY_BEHAVIOR_UNIT_TARGET");
			return new Cast(index, l);
		} else if ((a.getBehavior() & Ability.DOTA_ABILITY_BEHAVIOR_POINT) > 0) {
			logger.info("Casting DOTA_ABILITY_BEHAVIOR_POINT");

			if (l.getEntid() != -1)
				return new Cast(index, l.getEntid());
			else
				return new Cast(index, l.getX(), l.getY(), l.getZ());

		} else {
			logger.info("Casting TO LOCATION");
			return new Cast(index, l.getX(), l.getY(), l.getZ());
		}
	}

	/**
	 * This is a command that can be used by extending controller to issue a command
	 * to attack a entity.
	 * 
	 * @param e
	 *            Location, that has an id set.
	 * @return Returns a command to attack a entity with given id.
	 */
	public AgentCommand attack(Location e) {
		if (e == null) {
			logger.warn("Attacking null entity.");
			return null;
		}

		// Attack
		return new Attack(e.getEntid());
	}

	/**
	 * This is a command that can be used by extending controller to issue a command
	 * to grab all items. This should be called if you are close to the base shop.
	 * 
	 * @return Returns command to grab all items from stash at the base shop.
	 */
	public AgentCommand grabAll() {
		return new GrabAll();
	}

	@Override
	public TeamContext getTeamContext() {
		return teamContext;
	}

	@Override
	public void setTeamContext(TeamContext teamContext) {
		this.teamContext = teamContext;
		agentContext.setTeamContext(this.teamContext);
	}
	
	@Override
	public void configurationChanged() {
		// Initialize again, this will change the required values
		updateAgentContext();
	}

}
