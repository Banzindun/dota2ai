package cz.cuni.mff.kocur.interests;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.LinkedList;

import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.exceptions.LoadingError;
import cz.cuni.mff.kocur.world.GridBase;

/**
 * Class that represents jungle and stores all interests that it contains.
 * 
 * @author kocur
 *
 */
public class Jungle {

	/**
	 * Top shrine.
	 */
	protected Healer topHealer = null;

	/**
	 * Bot shrine.
	 */
	protected Healer botHealer = null;

	/**
	 * List of camps.
	 */
	protected ArrayList<Camp> camps = new ArrayList<>();

	/**
	 * Rune that represents top bounty.
	 */
	protected Rune topBounty = null;

	/**
	 * Rune that represents bottom bounty.
	 */
	protected Rune botBounty = null;

	/**
	 * Secret shop.
	 */
	protected Shop secretShop = null;

	/**
	 * Team, that this jungle belongs to.
	 */
	private int team = Team.NONE;

	public Jungle(int team) {
		this.team = team;
	}

	/**
	 * Adds a camp to this jungle.
	 * 
	 * @param c
	 *            The camp.
	 * @throws LoadingError
	 *             Thrown, if difficulty couldn't be deduced.
	 */
	public void addCamp(Camp c) throws LoadingError {
		c.setDifficultyForTeam(team);
		camps.add(c);
	}

	/**
	 * Adds the bot shrine.
	 * 
	 * @param h
	 *            The shrine (healer).
	 */
	public void addBotHealer(Healer h) {
		botHealer = h;
	}

	/**
	 * Adds top shrine.
	 * 
	 * @param h
	 *            The top healer.
	 */
	public void addTopHealer(Healer h) {
		topHealer = h;
	}

	/**
	 * Adds bottom bounty.
	 * 
	 * @param b
	 *            Bounty rune.
	 */
	public void addBotBounty(Rune b) {
		botBounty = b;

	}

	/**
	 * Adds top bounty.
	 * 
	 * @param b
	 *            Bounty rune.
	 */
	public void addTopBounty(Rune b) {
		topBounty = b;

	}

	public String toString(String indent) {
		IndentationStringBuilder b = new IndentationStringBuilder(indent);
		b.appendLine("Jungle:");
		b.indent();

		b.append("Top " + topHealer.toString());
		b.append("Bot " + botHealer.toString());

		b.appendLine("Camps:");
		b.indent();
		for (Camp c : camps)
			b.append(c.toString());
		b.deindent();

		b.append("Top " + topBounty.toString());
		b.append("Bot " + botBounty.toString());

		return b.toString();
	}

	/**
	 * Paints the jungle objects to the supplied graphics object.
	 * 
	 * @param g
	 *            Object, where to paint the jungle.
	 */
	public void paint(Graphics2D g) {
		topHealer.paint(g);
		botHealer.paint(g);

		for (Camp c : camps)
			c.paint(g);

		topBounty.paint(g);
		botBounty.paint(g);
	}

	/**
	 * 
	 * @return Returns the team this jungle belongs to.
	 */
	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}

	/**
	 * 
	 * @return Returns top bounty rune.
	 */
	public Rune getTopBounty() {
		return topBounty;
	}

	/**
	 * 
	 * @return Returns bottom bounry rune.
	 */
	public Rune getBotBounty() {
		return botBounty;
	}

	/**
	 * 
	 * @return Returns top healer (shrine).
	 */
	public Healer getTopHealer() {
		return topHealer;
	}

	/**
	 * 
	 * @return Returns bottom healer (shrine).
	 */
	public Healer getBotHealer() {
		return botHealer;
	}

	/**
	 * Tries to respawn all the camps.
	 */
	public void respawnCamps() {
		for (Camp c : camps)
			c.respawn();
	}

	/**
	 * Tries to respawn all the shrines.
	 * 
	 * @param time
	 *            Current time.
	 * @return Returns false if no healer was respawned.
	 */
	public boolean respawnHealers(float time) {
		boolean b = botHealer.respawn(time);
		b = b && topHealer.respawn(time);

		return b;
	}

	/**
	 * 
	 * @param l
	 *            The location.
	 * @return Returns camp, that is closest to given location.
	 */
	public Camp getNearestCamp(Location l) {
		return camps.stream().min((l1, l2) -> Double.compare(GridBase.distance(l1, l), GridBase.distance(l2, l))).get();
	}

	/**
	 * 
	 * @param diff
	 *            Difficulty of the camp.
	 * @return Returns camps from this jungle with given difficulty.
	 */
	public Camp[] getCampsWithDifficulty(int diff) {
		LinkedList<Camp> cs = new LinkedList<>();
		for (Camp c : camps) {
			if (c.getDifficulty() == diff)
				cs.add(c);
		}
		return cs.toArray(new Camp[cs.size()]);
	}

	/**
	 * 
	 * @param secret
	 *            Sets a new secret shop.
	 */
	public void setSecretShop(Shop secret) {
		secretShop = secret;
	}

	/**
	 * 
	 * @return Returns secret shop located in this jungle.
	 */
	public Shop getSecretShop() {
		return secretShop;
	}
}
