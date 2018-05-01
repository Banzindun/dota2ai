package cz.cuni.mff.kocur.interests;

import java.awt.Graphics2D;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.base.CustomStringBuilder;
import cz.cuni.mff.kocur.bot.AgentParameters;
import cz.cuni.mff.kocur.bot.InterestParameter;
import cz.cuni.mff.kocur.events.GameEvent;
import cz.cuni.mff.kocur.events.ListenersManager;
import cz.cuni.mff.kocur.server.TimeManager;
import cz.cuni.mff.kocur.world.GridBase;

/**
 * Class that represents a shrine.
 * 
 * @author kocur
 *
 */
public class Healer extends BaseInterest {
	/**
	 * Team of this shrine.
	 */
	private int team = Team.NONE;

	/**
	 * Cooldown of the shrine.
	 */
	private final int cooldown = 300;

	/**
	 * When was it depleted.
	 */
	private float timeDepleted = 0;

	public Healer(int id, double x, double y) {
		super(x, y);
		this.entid = id;
	}

	public String toString() {
		CustomStringBuilder b = new CustomStringBuilder();
		b.appendLine("Healer: " + entid + " [" + x + ", " + y + "]");
		return b.toString();
	}

	public void paint(Graphics2D g) {
		Colors.setTeamColor(team, g);
		g.fillOval(gridX - 2, gridY - 2, 4, 4);
	}

	public void setTeam(int t) {
		team = t;
	}

	public int getTeam() {
		return team;
	}

	/**
	 * Depletes this healer and triggers an event healer_depleted.
	 */
	public void deplete() {
		if (active) {
			timeDepleted = TimeManager.getGameTime();
			active = false;

			GameEvent e = new GameEvent();
			e.setXYZ(x, y, z);
			ListenersManager.triggerEvent("healer_depleted", e);
		}
	}

	@Override
	public InterestParameter getParameter(AgentParameters params) {
		InterestParameter p = params.getInterestParameter(Healer.class);

		if (p == null)
			return super.getParameter(params);
		return p;
	}

	/**
	 * Tries to respawn this shrine.
	 * 
	 * @param time
	 *            Time.
	 * @return Returns true, if the shrine has respawned.
	 */
	public boolean respawn(float time) {
		// Spawning start at 5 minute mark
		if (time > 300) {
			if (!active && time - timeDepleted > cooldown) {
				active = true;
				GameEvent e = new GameEvent();
				e.setXYZ(x, y, z);
				ListenersManager.triggerEvent("healer_refilled", e);
				return true;
			}
		}

		return false;
	}

	/**
	 * Updates the shrine.
	 * @param e The shrine.
	 */
	public void update(BaseInterest e) {
		if (GridBase.distance(e.getX(), e.getY(), x, y) < 10) {
			this.entid = e.getEntid();
		}
	}

}
