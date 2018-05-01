package cz.cuni.mff.kocur.world;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.base.GraphicResources;
import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.bot.AgentParameters;
import cz.cuni.mff.kocur.bot.EntityParameter;
import cz.cuni.mff.kocur.server.TimeManager;

public class Hero extends BaseNPC {
	/**
	 * Time for how long this entity should be alive. Should be defined and
	 * configured for every entity.
	 */
	@JsonIgnore
	public static float timeToLive = 1;

	protected float getTimeToLive() {
		return timeToLive;
	}

	@Override
	public void born() {
		super.born();
	}

	@Override
	public void dying() {

	}

	@Override
	public void setEntid(int id) {
		this.entid = id;
	}

	@Override
	public boolean shouldDie() {
		if (TimeManager.getGameTime() - lastUpdate > getTimeToLive()) {
			return true;
		}
		return false;
	}

	@Override
	public void paint(Integer[] crds, Graphics2D g) {
		Colors.setTeamColor(team, g);

		Image icon = GraphicResources.getBotMiniIcon(name).getImage();
		int x = crds[0] - icon.getHeight(null) / 2;
		int y = crds[1] - icon.getWidth(null) / 2;

		g.drawImage(icon, x, y, null);
	}

	@Override
	public EntityParameter getParameter(AgentParameters params) {
		EntityParameter p = params.getEntityParameter(Hero.class);

		if (p == null)
			return super.getParameter(params);
		return p;
	}

	public Hero() {
		super();
	}

	public Hero(Hero h) {
		super();
		this.update(h);
	}

	/**
	 * Type of this hero.
	 */
	private final static String TYPE = "Hero";

	/**
	 * Hero's inventory. Will contain names of items.
	 */
	protected Inventory inventory = new Inventory();

	@JsonSetter("inventory")
	public void setInventory(HashMap<Integer, ItemDummy> inv) {
		inventory = new Inventory();
		
		for (Entry<Integer, ItemDummy> e : inv.entrySet()) {
			ItemDummy id = e.getValue();
			if (id.getName().equals(""))
				inventory.addToSlot(e.getKey(), null);
			else {
				Item item = new Item(ItemsBase.getItem(id.getName()));
				item.setCount(id.getCount());
				inventory.addToSlot(e.getKey(), item);
			}

		}
	}

	/**
	 * Experience.
	 */
	private int xp;

	/**
	 * How many times hero died.
	 */
	private int deaths;

	/**
	 * How many denies hero had.
	 */
	private int denies;

	/**
	 * How much gold did hero earn.
	 */
	private int gold;

	/**
	 * How many kills do I have?
	 */
	private int kills;

	/**
	 * Map of abilities. Abilities are handled as objects, so they have its own id,
	 * name, damage etc.
	 */
	protected Map<Integer, Ability> abilities;

	/**
	 * 
	 * 
	 * @return Returns map of abilities.
	 */
	public Map<Integer, Ability> getAbilities() {
		return abilities;
	}

	/**
	 * Sets the hero abilities.
	 * 
	 * @param abilities
	 *            Map of hero abilities.
	 */
	public void setAbilities(Map<Integer, Ability> abilities) {
		this.abilities = abilities;
	}

	public Ability getAbility(int index) {
		return abilities.get(index);
	}

	/**
	 * 
	 * @return Returns number of hero deaths.
	 */
	public int getDeaths() {
		return deaths;
	}

	/**
	 * 
	 * @return Returns number of denies this hero did.
	 */
	public int getDenies() {
		return denies;
	}

	/**
	 * 
	 * @return Returns the gold of this hero.
	 */
	public int getGold() {
		return gold;
	}

	/**
	 * 
	 * @return Returns the type of this entity.
	 */
	public String getType() {
		return TYPE;
	}

	/**
	 * 
	 * @return Returns the experience of this hero.
	 */
	public int getXp() {
		return xp;
	}

	/**
	 * Sets number of deaths for this hero.
	 * 
	 * @param deaths
	 *            New number of deaths.
	 */
	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	/**
	 * 
	 * @param denies
	 *            Sets the number of denies for this hero.
	 */
	public void setDenies(int denies) {
		this.denies = denies;
	}

	/**
	 * Sets the gold for this hero.
	 * 
	 * @param gold
	 *            Amount of gold this hero has.
	 */
	public void setGold(int gold) {
		this.gold = gold;
	}

	/**
	 * Sets the experience of this hero.
	 * 
	 * @param xp
	 *            Experience.
	 */
	public void setXp(int xp) {
		this.xp = xp;
	}

	/**
	 * 
	 * @return Returns the inventory of this hero.
	 */
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * 
	 * @return Returns how many kills does this hero have.
	 */
	public int getKills() {
		return kills;
	}

	/**
	 * 
	 * @param kills
	 *            New amount of kills this hero has done.
	 */
	public void setKills(int kills) {
		this.kills = kills;
	}

	/**
	 * Returns string representation of hero's attributes.
	 */
	public String toString() {
		IndentationStringBuilder builder = new IndentationStringBuilder();
		builder.appendLine("Hero: " + entid);
		builder.indent();
		builder.appendLines(super.toString());

		// Indent more
		builder.indent();
		builder.appendLines("xp: " + xp, "deaths: " + deaths, "denies: " + denies, "gold: " + gold);

		builder.append(inventory.toString());
		for (Ability a : abilities.values()) {
			builder.appendLine(a.toString());
		}

		return builder.toString();
	}

	@Override
	public void update(BaseEntity b) {
		super.update(b);

		Hero h = (Hero) b;
		
		this.inventory = h.getInventory();
		
		this.xp = h.getXp();
		this.deaths = h.getDeaths();

		this.denies = h.getDenies();
		this.gold = h.getGold();

		this.kills = h.getKills();
		this.abilities = h.getAbilities();

	}

	@Override
	public boolean isHero() {
		return true;
	}

}
