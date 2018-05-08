package cz.cuni.mff.kocur.agent;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.base.Utils;
import cz.cuni.mff.kocur.interests.Fort;
import cz.cuni.mff.kocur.interests.Fountain;
import cz.cuni.mff.kocur.interests.InterestsBase;
import cz.cuni.mff.kocur.interests.Jungle;
import cz.cuni.mff.kocur.interests.Lane;
import cz.cuni.mff.kocur.interests.Lane.TYPE;
import cz.cuni.mff.kocur.interests.Shop;
import cz.cuni.mff.kocur.interests.Team;
import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.GridBase;
import cz.cuni.mff.kocur.world.Hero;
import cz.cuni.mff.kocur.world.World;
import cz.cuni.mff.kocur.world.WorldManager;
import cz.cuni.mff.kocur.world.WorldUpdate;

/**
 * Represents a bot's contextual information. Bot's context will contain some
 * basic information a bot can need. These are objects that are in his vision
 * range. As well as some information taken from configurations like his lane,
 * roles etc. This should be a primary source of information that the bot uses.
 * 
 * @author kocur
 *
 */
public class AgentContext {
	/**
	 * Logger for BaseBotContext.
	 */
	private static final Logger logger = LogManager.getLogger(AgentContext.class.getName());

	/**
	 * World around the bot. Will basically represent the hero's vision.
	 */
	protected World world = null;

	/**
	 * Reference to bot's controller.
	 */
	protected BaseAgentController controller = null;

	/**
	 * Reference to team's context.
	 */
	protected TeamContext teamContext = null;

	/**
	 * Bot's lane.
	 */
	protected Lane myLane = null;

	/**
	 * Bot's hero's name.
	 */
	protected String heroName = "";

	/**
	 * Number of team I am on.
	 */
	protected int myTeam = Team.NONE;

	/**
	 * Array of roles, that I play.
	 */
	protected int[] myRoles = new int[9];

	/**
	 * My buy sequence. This will be returning items I should buy.
	 */
	protected BuySequence buySequence = null;

	/**
	 * Constructor.
	 * 
	 * @param bc
	 *            BaseBotController that this context wraps around.
	 */
	public AgentContext(BaseAgentController bc) {
		controller = bc;
	}
	
	/**
	 * Constructor, that copies values from passed agent's context.
	 * @param bc Bot controller.
	 * @param context
	 */
	public AgentContext(AgentContext context) {
		world = context.getWorld();
		controller = context.getController();
		teamContext = context.getTeamContext();
		myLane = context.getMyLane();
		heroName = context.getHeroName();
		myTeam = context.getMyTeam();
		myRoles = context.getMyRoles();
		buySequence = context.getBuySequence();		
	}

	/**
	 * Updates the world inside this context.
	 * 
	 * @param u
	 *            WorldUpdate from which we should update the bot's context.
	 */
	public void update(WorldUpdate u) {
		// We take the world information, store it inside this object.
		world = u;
	}

	/**
	 * 
	 * @return Returns bot's controller.
	 */
	public BaseAgentController getController() {
		return controller;
	}

	/**
	 * Sets a new controller.
	 * 
	 * @param controller
	 *            New controller.
	 */
	public void setController(BaseAgentController controller) {
		this.controller = controller;
	}

	/**
	 * Finds entitites in radius around specified Location.
	 * 
	 * @param l
	 *            The location around which we are looking.
	 * @param dist
	 *            The radius.
	 * @return Returns all the entities that have distance to specified location
	 *         less that the given radius.
	 */
	public List<BaseEntity> findEntitiesInRadius(Location l, double dist) {
		return world.findEntitiesInRadius(l, dist);
	}

	/**
	 * 
	 * Finds entites around hero from this context in given radius.
	 * 
	 * @param dist
	 *            Radius.
	 * @return Returns entitites in a radius around the hero.
	 */
	public List<BaseEntity> findEntititesAroundMe(double dist) {
		return world.findEntitiesInRadius(controller.getHero(), dist);
	}

	/**
	 * 
	 * @return Returns map containing all the entities.
	 */
	public ConcurrentHashMap<Integer, BaseEntity> getEntites() {
		return world.getEntities();
	}

	/**
	 * 
	 * @param e
	 *            Entity with attack range.
	 * @param l
	 *            The location, that we want to check.
	 * @return Returns true, if the location is in entity's range.
	 */
	public boolean inRange(BaseEntity e, Location l) {
		return GridBase.distance(e, l) < e.getAttackRange();
	}

	/**
	 * Check if location/entity with given id is in range of the given entity e.
	 * 
	 * @param e
	 *            Entity with attack range.
	 * @param id
	 *            Id of entity, that might be in range.
	 * @return Returns true if the entitty with given id is in range. False if not
	 *         or if the location doesn't exist.
	 */
	public boolean inRange(BaseEntity e, int id) {
		Location l = this.findLocationWithId(id);

		if (l == null)
			return false;

		return inRange(e, l);
	}

	/**
	 * Calculates distance from s to target.
	 * 
	 * @param s
	 *            Source location.
	 * @param target
	 *            Target location.
	 * @return Returns distance between target and source.
	 */
	public double distance(Location s, Location target) {
		return GridBase.distance(s, target);
	}

	/**
	 * Searches for location with given id in global world and in interests base.
	 * 
	 * @param id
	 *            Id of the object we are looking for. Can be id of interest/base
	 *            entity.
	 * @return Returns Location representing the object with given id or null.
	 */
	public Location findLocationWithIdEverywhere(int id) {
		Location l = WorldManager.getWorld().getEntity(id);

		if (l == null)
			l = InterestsBase.getInstance().searchById(id);

		return l;
	}

	/**
	 * Searches the context's world by id and returns the location if found.
	 * 
	 * @param id
	 *            Id of the location/entity we want to find.
	 * @return Returns Location or null.
	 */
	public Location findLocationWithId(int id) {
		return world.getEntity(id);
	}

	/**
	 * Searches through the interests base and finds location with given id.
	 * 
	 * @param id
	 *            Id of the interest.
	 * @return Returns Location with given id.
	 */
	public Location findLocationWithIdInInterests(int id) {
		return InterestsBase.getInstance().searchById(id);
	}

	/**
	 * 
	 * @return Returns number of my team. (see Team class)
	 */
	public int getMyTeam() {
		return myTeam;
	}

	/**
	 * 
	 * @return Returns number of enemy team.
	 */
	public int getEnemyTeam() {
		return Team.getOpositeTeam(getMyTeam());
	}

	/**
	 * 
	 * @return Returns the agent's fort (ancient).
	 */
	public Fort getMyBase() {
		return InterestsBase.getInstance().getFort(getMyTeam());
	}

	/**
	 * 
	 * @return Returns the enemy's fort (ancient).
	 */
	public Fort getEnemyBase() {
		return InterestsBase.getInstance().getFort(getEnemyTeam());
	}

	/**
	 * 
	 * @param l
	 *            Location.
	 * @param range
	 *            Range.
	 * @return Returns true, if location is in range of the hero.
	 */
	public boolean inRange(Location l, int range) {
		return GridBase.distance(l, controller.getHero()) < range;
	}

	/**
	 * 
	 * @return Returns lane of the agent. Taken from configuration.
	 */
	public Lane getMyLane() {
		return myLane;
	}

	/**
	 * 
	 * @param configValue
	 *            Name of the new lane.
	 */
	public void setMyLane(String configValue) {
		if (configValue == null) {
			logger.error("I obtained lane that is null.");
			return;
		}

		TYPE t = Lane.getTypeFromString(configValue);
		myLane = this.getController().getTeamContext().getLane(t);
	}

	/**
	 * 
	 * @param name
	 *            Sets a new hero name.
	 */
	public void setHeroName(String name) {
		if (name == null)
			this.heroName = "";
		else
			this.heroName = name;
	}

	/**
	 * 
	 * @return Returns the name of the hero controlled by this agent.
	 */
	public String getHeroName() {
		return heroName;
	}

	/**
	 * Sets roles from a string.
	 * 
	 * @param str
	 *            String that contains roles of this agent. [ESCAPE, CARRY]
	 */
	public void setMyRoles(String str) {
		String[] rolesInStrings = Utils.parseArrayOfStrings(str);
		myRoles = new int[rolesInStrings.length];

		for (int i = 0; i < rolesInStrings.length; i++) {

			// Check for the role manually and set it
			if (rolesInStrings[i].equals("ESCAPE"))
				myRoles[i] = Roles.ESCAPE;
			else if (rolesInStrings[i].equals("SUPPORT"))
				myRoles[i] = Roles.SUPPORT;
			else if (rolesInStrings[i].equals("CARRY"))
				myRoles[i] = Roles.CARRY;
			else if (rolesInStrings[i].equals("DISABLER"))
				myRoles[i] = Roles.DISABLER;
			else if (rolesInStrings[i].equals("DURABLE"))
				myRoles[i] = Roles.DURABLE;
			else if (rolesInStrings[i].equals("INITIATOR"))
				myRoles[i] = Roles.INITIATOR;
			else if (rolesInStrings[i].equals("JUNGLER"))
				myRoles[i] = Roles.JUNGLER;
			else if (rolesInStrings[i].equals("PUSHER"))
				myRoles[i] = Roles.PUSHER;
			else if (rolesInStrings[i].equals("NUKER"))
				myRoles[i] = Roles.NUKER;
		}
	}

	/**
	 * 
	 * @return Returns array of role identifiers.
	 */
	public int[] getMyRoles() {
		return myRoles;
	}

	/**
	 * 
	 * @param role
	 *            Number of role. (From Roles.ROLE class)
	 * @return Returns true if hero assigned with this context has the supplied
	 *         role.
	 */
	public boolean hasRole(int role) {
		boolean r = false;
		for (int i : myRoles)
			if (i == role)
				r = true;

		return r;
	}

	/**
	 * Sets the number of my team.
	 * 
	 * @param team
	 *            String representation of my team in upper case. (DIRE, RADIANT)
	 */
	public void setMyTeam(String team) {
		if (team.equals("DIRE")) {
			myTeam = Team.DIRE;
		} else if (team.equals("RADIANT")) {
			myTeam = Team.RADIANT;
		} else {
			logger.error("Unknown team. Setting to RADIANT." + team);
			myTeam = Team.RADIANT;
		}
	}

	/**
	 * 
	 * @return Returns my base shop.
	 */
	public Shop getMyBaseShop() {
		return InterestsBase.getInstance().getShops().getBaseShop(myTeam);
	}

	/**
	 * 
	 * @return Returns my secret shop.
	 */
	public Shop getMySecretShop() {
		return InterestsBase.getInstance().getShops().getSecretShop(myTeam);
	}

	/**
	 * 
	 * @return Returns my side shop.
	 */
	public Shop getMySideShop() {
		return InterestsBase.getInstance().getShops().getSideShop(myTeam);
	}

	/**
	 * 
	 * @return Returns my buy sequence object.
	 */
	public BuySequence getBuySequence() {
		return buySequence;
	}

	/**
	 * Sets a new buy sequence to this bot.
	 * 
	 * @param buySequence
	 *            New BuySequence.
	 */
	public void setBuySequence(BuySequence buySequence) {
		this.buySequence = buySequence;
	}

	/**
	 * Checks if the passed location l2 is in range of location l1.
	 * 
	 * @param l1
	 *            First location.
	 * @param l2
	 *            Second location. Is this location in range of l1??
	 * @param range
	 *            Range in which the locations should be.
	 * @return Returns true, if the object is in range.
	 */
	public boolean inRange(Location l1, Location l2, int range) {
		return GridBase.distance(l1, l2) < range;
	}

	/**
	 * Checks if the passed location l2 is in range of location with id id.
	 * 
	 * @param l1
	 *            First location.
	 * @param id
	 *            Id of second location.
	 * @param castRange
	 *            Range in which the locations should be.
	 * @return Returns true, if the object is in range.
	 */
	public boolean inRange(Location l1, int id, int castRange) {
		Location l2 = this.findLocationWithIdEverywhere(id);
		return inRange(l1, l2, castRange);
	}

	/**
	 * 
	 * @return Returns the hero controlled by this context's controller.
	 */
	public Hero getHero() {
		return controller.getHero();
	}

	/**
	 * 
	 * @return Returns my fountain.
	 */
	public Fountain getMyFountain() {
		if (myTeam == Team.DIRE)
			return InterestsBase.getInstance().getDireFountain();

		return InterestsBase.getInstance().getRadiantFountain();

	}

	/**
	 * Called while this object is beying destroyed. (after the framework is stopped
	 * etc.)
	 */
	public void destroyed() {

	}

	/**
	 * 
	 * @return Return's a team context of this agent.
	 */
	public TeamContext getTeamContext() {
		return teamContext;
	}

	/**
	 * Sets a new team context.
	 * 
	 * @param teamContext
	 *            New team context.
	 */
	public void setTeamContext(TeamContext teamContext) {
		this.teamContext = teamContext;
	}

	/**
	 * 
	 * @return Returns a jungle that belongs to this team.
	 */
	public Jungle getMyJungle() {
		return InterestsBase.getInstance().getJungle(myTeam);
	}

	public String toString() {
		return world.toString();
	}
	
	/**
	 * 
	 * @return Returns this context's world.
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * Sets this context's world.
	 * @param world World.
	 */
	public void setWorld(World world) {
		this.world = world;
	}
	
}
