package cz.cuni.mff.kocur.interests;

import java.awt.Graphics2D;
import java.awt.Image;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonSetter;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.base.CustomStringBuilder;
import cz.cuni.mff.kocur.base.GraphicResources;
import cz.cuni.mff.kocur.bot.AgentParameters;
import cz.cuni.mff.kocur.bot.InterestParameter;
import cz.cuni.mff.kocur.events.GameEvent;
import cz.cuni.mff.kocur.events.ListenersManager;
import cz.cuni.mff.kocur.world.GridBase;

/**
 * Class that represents a rune.
 * 
 * @author kocur
 *
 */
public class Rune extends BaseInterest {
	public static Logger logger = LogManager.getLogger(Rune.class.getName());

	public static final int DOTA_RUNE_INVALID = -1;
	public static final int DOTA_RUNE_DOUBLEDAMAGE = 0;
	public static final int DOTA_RUNE_HASTE = 1;
	public static final int DOTA_RUNE_ILLUSION = 2;
	public static final int DOTA_RUNE_INVISIBILITY = 3;
	public static final int DOTA_RUNE_REGENERATION = 4;
	public static final int DOTA_RUNE_BOUNTY = 5;
	public static final int DOTA_RUNE_COUNT = 6;
	public static final int DOTA_RUNE_HAUNTED = 7;
	public static final int DOTA_RUNE_SPOOKY = 8;
	public static final int DOTA_RUNE_RAPIER = 9;
	public static final int DOTA_RUNE_TURBO = 10;
	public static final int DOTA_RUNE_MYSTERY = 11;
	public static final int DOTA_HALLOWEEN_RUNE_COUNT = 12;

	private int type = DOTA_RUNE_INVALID;

	private String typeString = "";

	public Rune() {
		super(0, 0);
	}

	public Rune(double x2, double y2) {
		super(x2, y2);
	}

	public Rune(int id, double x, double y) {
		super(x, y);
		this.entid = id;
	}

	@JsonSetter("origin")
	public void setOrigin(double[] origin) {
		x = origin[0];
		y = origin[1];
	}

	public String toString() {
		CustomStringBuilder b = new CustomStringBuilder();
		b.appendLine("Powerup: " + entid + " [" + x + ",  " + y + "]");
		return b.toString();
	}

	public void paint(Graphics2D g) {
		if (type != DOTA_RUNE_INVALID && active) {
			Image image = GraphicResources.getMapIcon(typeString).getImage();
			int x = gridX - image.getWidth(null) / 2;
			int y = gridY - image.getHeight(null) / 2;
			g.drawImage(image, x, y, null);
		}
	}

	/**
	 * Sets the rune's type.
	 * 
	 * @param type
	 *            New type.
	 */
	public void setType(int type) {
		this.type = type;

		switch (type) {
		case DOTA_RUNE_DOUBLEDAMAGE:
			typeString = "doubledamage";
			break;
		case DOTA_RUNE_HASTE:
			typeString = "haste";
			break;
		case DOTA_RUNE_ILLUSION:
			typeString = "illusion";
			break;
		case DOTA_RUNE_INVISIBILITY:
			typeString = "invisibility";
			break;
		case DOTA_RUNE_REGENERATION:
			typeString = "regeneration";
			break;
		case DOTA_RUNE_BOUNTY:
			typeString = "bounty";
			break;
		case DOTA_RUNE_INVALID:
			typeString = "";
		}
	}

	/**
	 * Sets rune's type from string, that contains written rune type. (doubledamage,
	 * haste, illusion)
	 * 
	 * @param str
	 *            String that contains rune's name.
	 */
	public void setTypeFromString(String str) {
		if (str.contains("doubledamage")) {
			setType(DOTA_RUNE_DOUBLEDAMAGE);
		} else if (str.contains("haste")) {
			setType(DOTA_RUNE_HASTE);
		} else if (str.contains("illusion")) {
			setType(DOTA_RUNE_ILLUSION);
		} else if (str.contains("invisibility")) {
			setType(DOTA_RUNE_INVISIBILITY);
		} else if (str.contains("regeneration")) {
			setType(DOTA_RUNE_REGENERATION);
		} else if (str.contains("goldxp")) {
			setType(DOTA_RUNE_BOUNTY);
		} else {
			logger.error("UNKNOWN RUNE TYPE");
			setType(DOTA_RUNE_INVALID);
		}
	}

	public int getType() {
		return type;
	}

	/**
	 * Respawns the rune.
	 */
	public void respawn() {
		if (!active) {
			setActive(true);

			GameEvent e = new GameEvent();
			e.setXYZ(this.getX(), this.getY(), 0);
			e.setType(type);

			if (type == DOTA_RUNE_BOUNTY)
				ListenersManager.triggerEvent("runerespawn", e);
			else {
				ListenersManager.triggerEvent("powerupsrespawn", e);
			}
		}
	}

	/**
	 * Should be called after the rune was picked up.
	 */
	public void pickedUp() {
		active = false;
	}

	@Override
	public InterestParameter getParameter(AgentParameters params) {
		InterestParameter p = params.getInterestParameter(Rune.class);

		if (p == null)
			return super.getParameter(params);
		return p;
	}

}
