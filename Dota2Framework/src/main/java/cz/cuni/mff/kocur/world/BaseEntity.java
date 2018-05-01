package cz.cuni.mff.kocur.world;

import java.awt.Graphics2D;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.bot.AgentParameters;
import cz.cuni.mff.kocur.bot.EntityParameter;
import cz.cuni.mff.kocur.interests.Team;
import cz.cuni.mff.kocur.server.TimeManager;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(name = "Hero", value = Hero.class), @Type(name = "BaseNPC", value = BaseNPC.class),
		@Type(name = "Tower", value = Tower.class), @Type(name = "Building", value = Building.class),
		@Type(name = "Tree", value = Tree.class), @Type(name = "Ability", value = Ability.class),
		@Type(name = "Creep", value = Creep.class), @Type(name = "Courier", value = Courier.class) })
public abstract class BaseEntity extends Location {
	/**
	 * Time for how long this entity should be alive. Should be defined and
	 * configured for every entity.
	 */
	@JsonIgnore
	public static float timeToLive = 1;

	protected float getTimeToLive() {
		return timeToLive;
	}

	/**
	 * Last time that this entity was seen in milliseconds.
	 */
	@JsonIgnore
	protected float lastUpdate = 0;

	
	public void born() {
		// Update the time of last update
		lastUpdate = TimeManager.getGameTime();
		
	}
	
	public void dying() {
		
	}
	

	
	public BaseEntity() {
		super();
		
	}

	/**
	 * Checks if this entity is without visual long enough so that it can be removed
	 * safely. This method implicitly returns true and should be overridden by
	 * entities that don't want to be removed (dynamic ones).
	 * 
	 * @return True if the entity should be removed (it lived longer without any
	 *         visuals than intended). False otherwise.
	 */
	public boolean shouldDie() {
		if (TimeManager.getGameTime() - lastUpdate > getTimeToLive())
			return true;
		return false;
	}

	public EntityParameter getParameter(AgentParameters params) {
		EntityParameter p = params.getEntityParameter(BaseEntity.class);
		return p;
	}

	/**
	 * Paints this entity to graphics.
	 * 
	 * @param g
	 */
	public void paint(Integer[] cords, Graphics2D g) {
		g.setColor(Colors.PURPLE);
		g.fillRect(cords[0] - 3, cords[1] - 3, 6, 6);
	}

	protected String name;
	protected int health;
	protected int maxHealth;
	protected int damage;
	
	protected int maxGoldBounty;
	protected int minGoldBounty;
	protected int projectileSpeed;


	protected int team = Team.NONE;

	protected int level;

	// unit.absOrigin = VectorToString(eunit:GetAbsOrigin())
	// unit.center = VectorToString(eunit:GetCenter())

	protected boolean alive;
	protected boolean blind;
	protected boolean dominated;
	protected boolean deniable;
	protected boolean disarmed;
	protected boolean rooted;
	protected boolean idle;
	
	protected float attackRange;
	protected int attackTarget;
	protected int visionRange;
	protected float mana;
	protected float maxMana;
	protected float speed;
			
	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}	

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public int getVisionRange() {
		return visionRange;
	}

	public void setVisionRange(int range) {
		visionRange = range;
	}

	public float getAttackRange() {
		return attackRange;
	}

	public int getAttackTarget() {
		return attackTarget;
	}

	public int getLevel() {
		return level;
	}

	public float getMana() {
		return mana;
	}

	public float getMaxMana() {
		return maxMana;
	}

	public boolean isAlive() {
		return alive;
	}

	public boolean isBlind() {
		return blind;
	}

	public boolean isDeniable() {
		return deniable;
	}

	public boolean isDisarmed() {
		return disarmed;
	}

	public boolean isDominated() {
		return dominated;
	}

	public boolean isRooted() {
		return rooted;
	}
	
	public boolean isIdle() {
		return idle;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public void setAttackRange(float attackRange) {
		this.attackRange = attackRange;
	}

	public void setAttackTarget(int attackTarget) {
		this.attackTarget = attackTarget;
	}

	public void setBlind(boolean blind) {
		this.blind = blind;
	}

	public void setDeniable(boolean deniable) {
		this.deniable = deniable;
	}

	public void setDisarmed(boolean disarmed) {
		this.disarmed = disarmed;
	}

	public void setDominated(boolean dominated) {
		this.dominated = dominated;
	}

	public void setIdle(boolean idle) {
		this.idle = idle;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}

	public void setMana(float mana) {
		this.mana = mana;
	}

	public void setMaxMana(float maxMana) {
		this.maxMana = maxMana;
	}

	public void setRooted(boolean rooted) {
		this.rooted = rooted;
	}

	public int getTeam() {
		return team;
	}

	public int getHealth() {
		return health;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public String getName() {
		return name;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTeam(int team) {
		this.team = team;
	}

	public String toString() {
        IndentationStringBuilder builder = new IndentationStringBuilder();
        builder.appendLines(
        		"entid:" + entid,
        		"team:" + team,
        		"level:" + level,
                "origin: " + x + ", " + y + ", " + z,
                "alive:" + alive,
                "health:" + health,
                "maxHealth" + maxHealth,
                "blind:" + blind,
                "dominated:" + dominated,
                "deniable:" + deniable,
                "disarmed:" + disarmed,
                "rooted:" + rooted,
                "idle:" + idle,
                "name:" + name,
                "visionRange:" + visionRange,
                "attackRange:" + attackRange,
                "attackTarget:" + attackTarget,
                "mana:" + mana,
                "maxMana:" + maxMana,
                "maxGoldBounty: " + maxGoldBounty,
                "minGoldBounty: " + minGoldBounty,
                "projectileSpeed: " + projectileSpeed
                );
        
        return builder.toString();
	}

	public void update(BaseEntity e) {
		// Update Location 
		super.update(e);
		
		// Update the time of last update
		lastUpdate = TimeManager.getGameTime();
		
		this.name = e.getName();
		this.health = e.getHealth();
		this.maxHealth = e.getMaxHealth();
		this.damage = e.getDamage();
		this.team = e.getTeam();
		this.level = e.getLevel();
		this.alive = e.isAlive();
		this.blind = e.isBlind();
		this.dominated = e.isDominated();
		this.deniable = e.isDeniable();
		this.disarmed = e.isDisarmed();
		this.rooted = e.isRooted();
		this.idle = e.isIdle();
		this.attackRange = e.getAttackRange();
		this.attackTarget = e.getAttackTarget();
		this.visionRange = e.getVisionRange();
		this.mana = e.getMana();
		this.maxMana = e.getMaxMana();
		this.speed = e.getSpeed();
		this.maxGoldBounty = e.getMaxGoldBounty();
		this.minGoldBounty = e.getMinGoldBounty();
	}


	public int getMaxGoldBounty() {
		return maxGoldBounty;
	}

	public void setMaxGoldBounty(int maxGoldBounty) {
		this.maxGoldBounty = maxGoldBounty;
	}

	public int getMinGoldBounty() {
		return minGoldBounty;
	}

	public void setMinGoldBounty(int minGoldBounty) {
		this.minGoldBounty = minGoldBounty;
	}
	
	public boolean isHero() {
		return false;
	}
	
	public boolean isBuilding() {
		return false;
	}
	
	public boolean isStaticEntity() {
		return false;
	}
	
	public boolean isDynamicEntity() {
		return false;
	}
	
	public boolean isCreep() {
		return false;
	}
	
	public boolean isTower() {
		return false;
	}

	public int getProjectileSpeed() {
		return projectileSpeed;
	}

	public void setProjectileSpeed(int projectileSpeed) {
		this.projectileSpeed = projectileSpeed;
	}
	
}
