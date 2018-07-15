package cz.cuni.mff.kocur.interests;

import java.awt.Graphics2D;
import java.awt.Image;

import cz.cuni.mff.kocur.base.GraphicResources;
import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.world.Creep;
import cz.cuni.mff.kocur.world.GridBase;

/**
 * Class that represents a river. 
 * @author kocur
 *
 */
public class River {

	private Rune topPowerup = null;
	
	private Rune botPowerup = null;
	
	private Creep roshan = null;

	public void setRoshan(Creep c) {
		roshan = c;
	}

	public void addTopPowerup(Rune p) {
		topPowerup = p;
	}
	
	public void addBotPowerup(Rune p) {
		botPowerup = p;
	}
	
	public String toString() {
		IndentationStringBuilder b = new IndentationStringBuilder();
		
		b.appendLine("River:");
		b.indent();
		
		b.append("Top " + topPowerup.toString());
		b.append("Bot " + botPowerup.toString());
		
		if (roshan != null) b.append("Roshan:" + roshan.toString());
		
		return b.toString();
	}
	
	public void paint(Graphics2D g) {
		topPowerup.paint(g);
		botPowerup.paint(g);
		
		drawRoshan(g);
	}
	
	/**
	 * Draws a roshan to its position insider graphics g.
	 * @param g Graphics.
	 */
	private void drawRoshan(Graphics2D g) {
		if (roshan != null) {
			double[] roshanCoords = GridBase.getInstance().getEntityCoordinates(roshan);
			Image roshanI = GraphicResources.getMapIcon("roshan").getImage();
			roshanCoords[0] -= roshanI.getWidth(null)/2;
			roshanCoords[1] -= roshanI.getHeight(null)/2;
			g.drawImage(roshanI, (int)roshanCoords[0], (int)roshanCoords[1], null);
		}
	}
	
	/**
	 * 
	 * @return Returns the bot powerup.
	 */
	public Rune getBotPowerup() {
		return botPowerup;
	}
	
	/**
	 * 
	 * @return Returns the top powerup.
	 */
	public Rune getTopPowerup() {
		return topPowerup;
	}
	
	/**
	 * 
	 * @return Returns the roshan.
	 */
	public Creep getRoshan() {
		return roshan;
	}

	/**
	 * Looks at powerups and baron and returns the one with given id.
	 * @param id Id of the target we are searching for.
	 * @return Returns location of powerup or baron or null if none found.
	 */
	public Location searchById(int id) {
		if (topPowerup.getEntid() == id)
			return topPowerup;
		else if (botPowerup.getEntid() == id)
			return botPowerup;
		else if (roshan.getEntid() == id)
			return roshan;
		
		return null;
	}
}
