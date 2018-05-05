package cz.cuni.mff.kocur.world;

import java.awt.Graphics2D;
import java.awt.Image;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.cuni.mff.kocur.agent.AgentParameters;
import cz.cuni.mff.kocur.agent.ControllersManager;
import cz.cuni.mff.kocur.agent.EntityParameter;
import cz.cuni.mff.kocur.agent.TeamContext;
import cz.cuni.mff.kocur.base.GraphicResources;
import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.events.GameEvent;
import cz.cuni.mff.kocur.events.GameEventBuilder;
import cz.cuni.mff.kocur.events.ListenersManager;
import cz.cuni.mff.kocur.interests.Team;

/**
 * Class that represents a courier.
 * 
 * @author kocur
 *
 */
public class Courier extends BaseNPC {
	private static final Logger logger = LogManager.getLogger(Courier.class);

	/**
	 * Time for how long this entity should be alive. Should be defined and
	 * configured for every entity.
	 */
	@JsonIgnore
	public static float timeToLive = 100;

	protected float getTimeToLive() {
		return timeToLive;
	}

	public Courier() {
		super();
	}

	@Override
	public void born() {
		super.born();

		TeamContext tc = ControllersManager.getInstance().getTeamContext(team);
		tc.setTeamCourier(this);
	}

	@Override
	public void dying() {
		logger.info("Courier with id: " + entid + " killed.");

		TeamContext tc = ControllersManager.getInstance().getTeamContext(team);
		tc.setTeamCourier(null); // Courier died

		GameEvent e = GameEventBuilder.build().location(x, y, z).source(entid).team(team).get();
		ListenersManager.triggerEvent("courier_killed", e);
	}

	@Override
	public void paint(Integer[] crds, Graphics2D g) {
		Image image;
		if (team == Team.DIRE)
			image = GraphicResources.getMapIcon("courier_dire").getImage();
		else
			image = GraphicResources.getMapIcon("courier_radiant").getImage();

		double[] coords = GridBase.getInstance().getEntityCoordinates(this);
		coords[0] -= image.getWidth(null) / 2;
		coords[1] -= image.getHeight(null) / 2;

		g.drawImage(image, (int) coords[0], (int) coords[1], null);
	}

	@Override
	public EntityParameter getParameter(AgentParameters params) {
		EntityParameter p = params.getEntityParameter(Courier.class);

		if (p == null)
			return super.getParameter(params);
		return p;
	}

	@Override
	public String toString() {
		IndentationStringBuilder builder = new IndentationStringBuilder();
		builder.appendLine("Courier: " + entid);
		builder.indent();
		builder.appendLines(super.toString());
		return builder.toString();
	}
}
