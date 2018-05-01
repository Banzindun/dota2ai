package cz.cuni.mff.kocur.interests;

import java.awt.Graphics2D;
import java.awt.Image;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.base.CustomStringBuilder;
import cz.cuni.mff.kocur.base.GraphicResources;
import cz.cuni.mff.kocur.bot.AgentParameters;
import cz.cuni.mff.kocur.bot.InterestParameter;

/**
 * Class that represents a shop.
 * @author kocur
 *
 */
public class Shop extends BaseInterest{
	public static final int BASE = 0;
	public static final int SECRET = 1;
	public static final int SIDE = 2;
		
	/**
	 * Type of the shop.
	 */
	private int type = 0;

	public Shop(int id, double x, double y) {
		super(x,y);
		this.entid = id;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public String toString() {
		CustomStringBuilder b = new CustomStringBuilder();
		b.appendLine("Shop: " + entid + ", [" + x + ", " + y + "]");
		return b.toString();
	}
	
	public void paint(Graphics2D g) {
		Image image; 
		
		switch (type) {
		case BASE:
			image = GraphicResources.getMapIcon("shop").getImage();
			break;
		case SIDE:
			image = GraphicResources.getMapIcon("side_shop").getImage();
			break;
		case SECRET:
			image = GraphicResources.getMapIcon("secret_shop").getImage();
			break;
		default:
			return;
		}		
    	 
    	int x = gridX - image.getWidth(null)/2;
    	int y = gridY - image.getHeight(null)/2;
		
		g.drawImage(image, x, y, null);	
	}
	
	
    @Override
	public InterestParameter getParameter(AgentParameters params) {
		InterestParameter p = params.getInterestParameter(Shop.class);
		
		if (p == null) return super.getParameter(params);
		return p;
	} 
	
}
