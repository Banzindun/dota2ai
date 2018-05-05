/**
 * cz.cuni.mff.kocur.base This package contains some classes, that are used across the application.  
 */
package cz.cuni.mff.kocur.base;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;

import cz.cuni.mff.kocur.interests.Team;

/**
 * Class that stores basic colors and ranges, that are needed across the whole application.
 * @author kocur
 *
 */
public class Colors {
	public static Color GREEN = new Color(122, 255, 51);
	public static Color ORANGE = new Color(255, 163, 51);
	public static Color RED = new Color(170, 57, 57);
	public static Color YELLOW = new Color(255, 224, 51);
	public static Color BLUE = new Color(34, 102, 102);
	public static Color PURPLE = new Color(143, 51, 255);
	public static Color WHITE = new Color(255,255,255);
	public static Color BASE = new Color(188, 204, 185);
	public static Color TREEGREEN = new Color(85, 170, 85);
	public static Color DARKGRAY = new Color(74, 78, 79); 
	
	/**
	 * Returns red from set of reds different by their brightness.
	 * Green is selected as two digits behind 0. from 0-1 range. 
	 * Bigger alpha results in brighter color. 
	 * @param alpha Value of alpha in range 0-1.
	 * @return Red color shade corresponding to argument.
	 */
	public static Color getRedAlpha(double alpha) {
		int index = (int) (alpha*100);
		return reds[index];
	}
	
	/**
	 * Returns green from set of greens different by their brightness.
	 * Green is selected as two digits behind 0. from 0-1 range. 
	 * Bigger alpha results in brighter color. 
	 * @param alpha Value of alpha in range 0-1. 
	 * @return Green color shade corresponding to argument.
	 */
	public static Color getGreenAlpha(double alpha) {
		int index = (int) (alpha*100);
		return greens[index];
	}
		
	/**
	 * Sets the color for team. Radiant are green, dire are red.
	 * The rest is orange.
	 * @param t Team number.
	 * @param g Graphics to which we will set the color.
	 */
	public static void setTeamColor(int t, Graphics2D g) {
    	if (t == Team.RADIANT) {
    		g.setPaint(Colors.GREEN);
    	} else if (t == Team.DIRE) { 
    		g.setPaint(Colors.RED);
    	} else if (t == Team.NEUTRAL) {
    		g.setPaint(Colors.ORANGE);
    	} else {
    		g.setPaint(Colors.ORANGE);
    	}
	}	
	
	/**
	 * Array containing reds ranging in brightness.
	 */
	private static Color[] reds = new Color[101];

	
	/**
	 * Array containing greens ranging in brightness.
	 */
	private static Color[] greens = new Color[101];
			
	static {
		for (int i = 0; i < 101; i++) {
			reds[i] = Color.getHSBColor(1, 1, i/100f);
			greens[i] = Color.getHSBColor(0.35f, 0.95f, i/100f);
		}		
	}

	/**
	 * Returns red or green color (red for negative values) of influence passed as argument.
	 * @param inf Influence from range 0-1.
	 * @return Returns color corresponding to level of influence.
	 */
	public static Paint getColor(double inf) {
		if (inf > 0) 
			return getGreenAlpha(inf);
		else if (inf < 0)
			return getRedAlpha(-inf);
		
		return BASE;
	}
}

