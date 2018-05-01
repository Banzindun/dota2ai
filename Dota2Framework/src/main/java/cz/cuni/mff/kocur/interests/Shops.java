package cz.cuni.mff.kocur.interests;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cz.cuni.mff.kocur.base.Constants;
import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.world.GridBase;
import cz.cuni.mff.kocur.world.Item;

/**
 * Class that represents all the shops on the map.
 * 
 * @author kocur
 *
 */
public class Shops {
	private ArrayList<Shop> allShops = new ArrayList<>();

	private Shop radiant;
	private Shop dire;

	private Shop radiantSecret;
	private Shop radiantSide;

	private Shop direSide;
	private Shop direSecret;

	/**
	 * 
	 * Adds a shop to shops.
	 * 
	 * @param id
	 *            Shop's id.
	 * @param x
	 *            Shop's x.
	 * @param y
	 *            Shop's y.
	 */
	public void addShop(int id, double x, double y) {
		Shop shop = new Shop(id, x, y);

		// Dire or Radiant base shops will have both coordinates negative or positive.
		if (x < 0 && y < 0) {
			// Radiant base shop
			radiant = shop;
			shop.setType(Shop.BASE);
		} else if (x > 0 && y > 0) {
			// Dire base shop
			dire = shop;
			shop.setType(Shop.BASE);
		} else if (x < 0 && y > 0) {
			// Radiant secret or dire side
			if (y > Constants.leftRiverStartY) {
				direSide = shop;
				shop.setType(Shop.SIDE);
			} else {
				radiantSecret = shop;
				shop.setType(Shop.SECRET);
			}
		} else {
			// Radiant side or dire secret
			if (y > Constants.rightRiverEndY) {
				direSecret = shop;
				shop.setType(Shop.SECRET);
			} else {
				radiantSide = shop;
				shop.setType(Shop.SIDE);
			}
		}

		allShops.add(shop);
	}

	public String toString() {
		IndentationStringBuilder b = new IndentationStringBuilder();

		b.appendLine("Shops:");
		b.indent();

		b.append("Good " + radiant.toString());
		b.append("Evil " + dire.toString());

		b.append("Good secret " + radiantSecret.toString());
		b.append("Good side " + radiantSide.toString());

		b.append("Evil side " + direSide.toString());
		b.append("Evil secret " + direSecret.toString());

		return b.toString();
	}

	/**
	 * Paints the shop to supplied graphics.
	 * @param g
	 */
	public void paint(Graphics2D g) {
		radiant.paint(g);
		dire.paint(g);

		radiantSecret.paint(g);
		radiantSide.paint(g);

		direSide.paint(g);
		direSecret.paint(g);
	}

	public Shop getRadiant() {
		return radiant;
	}

	public void setRadiant(Shop radiant) {
		this.radiant = radiant;
	}

	public Shop getDire() {
		return dire;
	}

	public void setDire(Shop dire) {
		this.dire = dire;
	}

	public Shop getRadiantSecret() {
		return radiantSecret;
	}

	public void setRadiantSecret(Shop radiantSecret) {
		this.radiantSecret = radiantSecret;
	}

	public Shop getRadiantSide() {
		return radiantSide;
	}

	public void setRadiantSide(Shop radiantSide) {
		this.radiantSide = radiantSide;
	}

	public Shop getDireSide() {
		return direSide;
	}

	public void setDireSide(Shop direSide) {
		this.direSide = direSide;
	}

	public Shop getDireSecret() {
		return direSecret;
	}

	public void setDireSecret(Shop direSecret) {
		this.direSecret = direSecret;
	}

	/**
	 * Searches for shop with given id.
	 * 
	 * @param id
	 *            Id of the shop.
	 * @return Returns shop with given id or null.
	 */
	public Location searchById(int id) {
		for (Shop s : allShops) {
			if (s.getEntid() == id)
				return s;
		}

		return null;
	}

	/**
	 * Finds shop that is nearest to given location.
	 * 
	 * @param l
	 *            Location to be found.
	 * @return Returns the location of the shop or null.
	 */
	public Location findNearest(Location l) {
		return allShops.stream().min((s1, s2) -> Double.compare(GridBase.distance(s1, l), GridBase.distance(s2, l)))
				.get();
	}

	/**
	 * Finds shop, that is nearest to given location and that sells item i.
	 * @param l Location.
	 * @param i Item.
	 * @return Returns the closest shop.
	 */
	public Shop findNearest(Location l, Item i) {
		List<Shop> sorted = allShops.stream()
				.sorted((s1, s2) -> Double.compare(GridBase.distance(s1, l), GridBase.distance(s2, l)))
				.collect(Collectors.toList());

		for (Shop s : sorted) {
			if (i.correctShop(s))
				return s;
		}

		return null;
	}

	public ArrayList<Shop> getAllShops() {
		return allShops;
	}

	public void setAllShops(ArrayList<Shop> allShops) {
		this.allShops = allShops;
	}

	public Shop getBaseShop(int team) {
		if (team == Team.RADIANT)
			return this.getRadiant();

		return this.getDire();
	}

	public Shop getSecretShop(int team) {
		if (team == Team.RADIANT)
			return this.getRadiantSecret();

		return this.getDireSecret();
	}

	public Shop getSideShop(int team) {
		if (team == Team.RADIANT)
			return this.getRadiantSide();

		return this.getDireSide();
	}

}
