package cz.cuni.mff.kocur.base;

import java.awt.Image;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class contains all the graphic resources like images, icons .., that I
 * might need. This is helpful because I don't need to load them each time I
 * need them. Also they might be shared (e. g. icons for buttons).
 * 
 * I am not loading everything - I am using hash maps to load icons only at the
 * time they are used.
 * 
 * @author kocur
 *
 */
public class GraphicResources {
	/**
	 * Custom logger registered for this class.
	 */
	private static Logger logger = LogManager.getLogger(GraphicResources.class.getName());

	/**
	 * Icon of a plus sign.
	 */
	public static ImageIcon plusI;

	/**
	 * Icon of a minus sign.
	 */
	public static ImageIcon minusI;

	/**
	 * Icon of a cross.
	 */
	public static ImageIcon crossI;

	/**
	 * Icon of saving disc.
	 */
	public static ImageIcon saveI;

	/**
	 * Icon of pencil.
	 */
	public static ImageIcon writeI;

	/**
	 * Information/help icon.
	 */
	public static ImageIcon infoI;

	/**
	 * Icon of play button.
	 */
	public static ImageIcon startI;

	/**
	 * Icon of stop sign.
	 */
	public static ImageIcon stopI;

	/**
	 * Icon of pause sign. Two parallel vertical lines.
	 */
	public static ImageIcon pauseI;

	/**
	 * Icon of resume sign.
	 */
	public static ImageIcon resumeI;

	/**
	 * Icon of restart sign.
	 */
	public static ImageIcon restartI;

	/**
	 * Step icon (the one that we use when we one to do just one update and pause).
	 */
	public static ImageIcon stepI;

	/**
	 * Icons that store color indicators - those are blue, red, green etc. circles.
	 * They can be used for example to display the state of the application.
	 */
	private static HashMap<String, ImageIcon> colorIndicators = new HashMap<>();

	/**
	 * Hash map that will contain references to mini hero icons. They will be
	 * addressed by hero name.
	 */
	private static HashMap<String, ImageIcon> botMiniIcons = new HashMap<>();

	/**
	 * Hash map that will contain references to hero icons. They will be addressed
	 * by hero name.
	 */
	private static HashMap<String, ImageIcon> botIcons = new HashMap<>();

	/**
	 * Hash map that will contain images of in-game items, that are currently being
	 * used.
	 */
	private static HashMap<String, ImageIcon> itemIcons = new HashMap<>();

	/**
	 * Hash map that will store icons for some entities on map and interesting
	 * points.
	 */
	private static HashMap<String, ImageIcon> mapIcons = new HashMap<>();

	// Static loader of icons and indicators.
	static {
		plusI = loadIcon("plus.png");
		crossI = loadIcon("cross.png");
		minusI = loadIcon("minus.png");
		saveI = loadIcon("save.png");
		writeI = loadIcon("write.png");
		infoI = loadIcon("info.png");
		startI = loadIcon("start.png");
		stopI = loadIcon("stop.png");
		pauseI = loadIcon("pause.png");
		resumeI = loadIcon("resume.png");
		restartI = loadIcon("restart.png");
		stepI = loadIcon("step.png");

		colorIndicators.put("green", loadIcon("green.png"));
		colorIndicators.put("blue", loadIcon("blue.png"));
		colorIndicators.put("red", loadIcon("red.png"));
		colorIndicators.put("purple", loadIcon("purple.png"));
		colorIndicators.put("yellow", loadIcon("yellow.png"));

		loadMapIcons();
	}

	/**
	 * Loads icon to supplied ImageIcon from resources with resource name.
	 * 
	 * @param i
	 *            ImageIcon object that where the loaded resource should be stored.
	 * @param resource
	 *            Name of the resource (icon) that should be loaded.
	 */
	private static ImageIcon loadIcon(String resource) {
		try {
			Image img = ImageIO.read(GraphicResources.class.getClassLoader().getResource(resource));
			return new ImageIcon(img);
		} catch (Exception ex) {
			logger.warn("Unable to load icon for: " + resource);
		}

		return null;
	}

	/**
	 * This class loads all the map icons that are necessary. This is hand-authored
	 * as we need to have specific names of icons inside mapIcons map.
	 */
	private static void loadMapIcons() {
		mapIcons.put("roshan", loadIcon("map/roshan.png"));

		// Runes
		mapIcons.put("doubledamage", loadIcon("map/doubledamage.png"));
		mapIcons.put("arcane", loadIcon("map/arcane.png"));
		mapIcons.put("bounty", loadIcon("map/bounty.png"));
		mapIcons.put("haste", loadIcon("map/haste.png"));
		mapIcons.put("illusion", loadIcon("map/illusion.png"));
		mapIcons.put("invisibility", loadIcon("map/invisibility.png"));
		mapIcons.put("regeneration", loadIcon("map/regeneration.png"));

		// Couriers
		mapIcons.put("flying_courier_dire", loadIcon("map/flying_courier_dire.png"));
		mapIcons.put("flying_courier_radiant", loadIcon("map/flying_courier_dire.png"));
		mapIcons.put("courier_dire", loadIcon("map/courier_dire.png"));
		mapIcons.put("courier_radiant", loadIcon("map/courier_radiant.png"));

		// Camps
		mapIcons.put("legendary_camp", loadIcon("map/ancient.png"));
		mapIcons.put("easy_camp", loadIcon("map/easy_camp.png"));
		mapIcons.put("hard_camp", loadIcon("map/hard_camp.png"));
		mapIcons.put("medium_camp", loadIcon("map/medium.png"));

		// Shops
		mapIcons.put("secret_shop", loadIcon("map/secret_shop.png"));
		mapIcons.put("shop", loadIcon("map/shop.png"));
		mapIcons.put("side_shop", loadIcon("map/side_shop.png"));
	}

	/**
	 * Sets the icon for the given label and sets it's text to empty. This comes in
	 * handy if you have defined label with initial text and you want it to only the
	 * icon in case it has loaded.
	 * 
	 * @param label Label that should have the icon set.
	 * @param icon ImageIcon that we are setting to the passed label.
	 */
	public static void setLabelIcon(JLabel label, ImageIcon icon) {
		if (icon != null) {
			label.setIcon(icon);
			label.setText("");
		}
	}

	/**
	 * Sets the icon for the given button and sets the text to empty. This comes in
	 * handy if you have defined button with initial text and you want it to
	 * disappear only in case its icon was loaded.
	 * 
	 * @param bttn Button that we are setting the icon to.
	 * @param icon
	 *            Icon you want to set. (preferably one of the static icons
	 *            referenced inside this class)
	 */
	public static void setButtonIcon(JButton bttn, ImageIcon icon) {
		if (icon != null) {
			bttn.setIcon(icon);
			bttn.setText("");
		}
	}

	/**
	 * Returns ImageIcon that corresponds to the indicator with color passed in
	 * argument.
	 * 
	 * @param color
	 *            Color of the indicator.
	 * @return Returns the indicator with given color
	 */
	public static ImageIcon getIndicator(String color) {
		return colorIndicators.get(color);
	}

	/**
	 * @param botName
	 *            Name of the bot whose icon we want
	 * @return Returns the ImageIcon associated with the given botName or null if no
	 *         such icon could be found.
	 */
	public static ImageIcon getBotMiniIcon(String botName) {
		if (!botMiniIcons.containsKey(botName))
			botMiniIcons.put(botName, loadIcon("miniheroes/" + botName + ".png"));

		return botMiniIcons.get(botName);
	}

	/**
	 * 
	 * @param botName
	 *            Name of the bot whose icon we want
	 * @return Returns the ImageIcon associated with the given botName or null if no
	 *         such icon could be found.
	 */
	public static ImageIcon getBotIcon(String botName) {
		if (!botIcons.containsKey(botName))
			botIcons.put(botName, loadIcon("heroes/" + botName + ".png"));

		return botIcons.get(botName);
	}

	/**
	 * Returns icon of passed item name. If the itemName contains recipe, then recipe icon is returned. If icon is not present, it is loaded. 
	 * @param itemName Name of the icon, that represents in-game object that we want.
	 * @return Returns the icon representing the item.
	 */
	public static ImageIcon getItemIcon(String itemName) {
		if (!itemIcons.containsKey(itemName)) {
			// If recipe, then create/get the recipe
			if (itemName.contains("recipe")) {
				if (!itemIcons.containsKey("item_recipe"))
					itemIcons.put("item_recipe", loadIcon("items/item_recipe.png"));
				return itemIcons.get("item_recipe");
			}

			// Load new icon from item resources
			itemIcons.put(itemName, loadIcon("items/" + itemName + ".png"));
		}

		return itemIcons.get(itemName);
	}

	/**
	 * 
	 * @param name Name of the in-game map object, that we want the icon for. (secret_shop, regeneration)
	 * @return Returns icon corresponding to passed name. Can be null if not found.
	 */
	public static ImageIcon getMapIcon(String name) {
		return mapIcons.get(name);
	}

}
