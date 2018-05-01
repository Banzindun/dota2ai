package cz.cuni.mff.kocur.interests;

import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.events.FrameworkEventListener;
import cz.cuni.mff.kocur.events.ListenersManager;
import cz.cuni.mff.kocur.exceptions.LoadingError;
import cz.cuni.mff.kocur.interests.Lane.TYPE;
import cz.cuni.mff.kocur.world.Creep;
import cz.cuni.mff.kocur.world.GridBase;
import cz.cuni.mff.kocur.world.GridSystem;
import cz.cuni.mff.kocur.world.Hero;
import cz.cuni.mff.kocur.world.Item;
import cz.cuni.mff.kocur.world.Tower;

/**
 * Class that represents all the interesting objects, that are in the game.
 * 
 * @author kocur
 *
 */
public class InterestsBase extends GridSystem implements FrameworkEventListener {
	/**
	 * Logger for InterestsBase.
	 */
	private static final Logger logger = LogManager.getLogger(InterestsBase.class.getName());

	/**
	 * The interest base instance.
	 */
	private static InterestsBase instance = null;

	/**
	 * 
	 * @return Returns the InterestsBase instance.
	 */
	public static InterestsBase getInstance() {
		if (instance == null) {
			// Should be initialized before anyone calls this
			throw new NullPointerException();
		}

		return instance;
	}

	/**
	 * Array of fountains.
	 */
	private Fountain[] fountains = new Fountain[2];

	/**
	 * Array of forts.
	 */
	private Fort[] forts = new Fort[2];

	/**
	 * Lanes object. Stores all the lanes for both teams.
	 */
	private Lanes lanes = new Lanes();

	/**
	 * Radiant jungle. Stores camps, heals and bounties inside radiant jungle.
	 */
	private Jungle[] jungles = new Jungle[2];

	/**
	 * Represents river. Stores baron and powerups.
	 */
	private River river = null;

	/**
	 * Stores shops. Has their types and knows if they are dire etc.
	 */
	private Shops shops = null;

	/**
	 * Runes.
	 */
	private LinkedList<Rune> runes = new LinkedList<>();

	/**
	 * Loads the interests base using the interests points loader.
	 * @param json
	 * @throws LoadingError
	 */
	public static void load(String json) throws LoadingError {

		InterestPointsLoader loader = new InterestPointsLoader();
		loader.load(json);

		// Get grid and initialize the instance.
		GridBase grid = GridBase.getInstance();
		instance = new InterestsBase(grid, (int) grid.getResolution());
		instance.setSize(grid.getHeight(), grid.getWidth());

		// Load the data
		instance.load(loader);
	}

	/**
	 * Constructor.
	 * 
	 * @param grid
	 *            Takes parent grid.
	 * @param resolution
	 *            Resolution of this system.
	 */
	public InterestsBase(GridSystem grid, int resolution) {
		super();
		this.setParent(grid);
		this.setResolution(resolution);

		ListenersManager.addFrameworkListener("towerdestroyed", this);
	}

	/**
	 * Loads the interest objects from loader.
	 * 
	 * @param loader
	 *            Loader that has loaded all the necessary interests.
	 */
	private void load(InterestPointsLoader loader) {
		fountains = loader.getFountains();
		forts = loader.getForts();

		// Load lanes and sort them
		lanes = loader.getLanes();

		// Get the jungles
		jungles[0] = loader.getRadiantJungle();
		jungles[1] = loader.getDireJungle();

		// Get river and shops
		river = loader.getRiver();
		shops = loader.getShops();

		// Get runes and add them to jungles
		runes.add(river.getBotPowerup());
		runes.add(river.getTopPowerup());
		for (Jungle j : jungles) {
			runes.add(j.getBotBounty());
			runes.add(j.getTopBounty());
		}
	}

	public void update(ConcurrentHashMap<Integer, BaseInterest> interests) {
		for (BaseInterest e : interests.values()) {
			if (e.getInterestName().contains("rune")) {
				resolveRune(e);
			} else if (e.getInterestName().contains("healer"))
				resolveHealer(e);
		}
	}

	private void resolveHealer(BaseInterest e) {
		this.getRadiantJungle().getTopHealer().update(e);
		this.getRadiantJungle().getBotHealer().update(e);
	}

	/**
	 * Resolves a base interest that came during update to rune. It just find a rune
	 * that is closest and sets its id, if the id is different.
	 * 
	 * @param e
	 *            BaseInterest that should be resolved to rune.
	 */
	private void resolveRune(BaseInterest e) {
		for (Rune r : runes)
			if (GridBase.distance(e.getX(), e.getY(), r.getX(), r.getY()) < 40) {
				// Change only if ids are different
				if (r.getEntid() == e.getEntid()) {
					break;
				}

				logger.debug("Setting new id " + e.getEntid() + " to rune on:" + e.getX() + " " + e.getY());
				r.setEntid(e.getEntid());
				r.setTypeFromString(e.getModelName());
				r.setActive(true);
				break;
			}
	}

	@Override
	public void triggered() {
	}

	@Override
	public void triggered(Object... os) {
		Tower t = (Tower) os[0];
		lanes.towerDestroyed(t.getEntid());
	}

	public void respawnCamps() {
		for (Jungle j : jungles) {
			j.respawnCamps();
		}

	}

	/**
	 * Respawns the runes if the time is right.
	 * @param lastRuneRespawn Time of last rune respawn.
	 */
	public void respawnRunes(float lastRuneRespawn) {
		for (Rune r : runes) {
			if (r.getType() == Rune.DOTA_RUNE_BOUNTY) {
				r.respawn();
			} else {
				if (lastRuneRespawn >= 120) // Start respawning at 2:00
					r.respawn();
			}
		}
	}

	/**
	 * Respawns healers if the time is right.
	 * @param lastRuneRespawn Time of last healer respawn.
	 */
	public boolean respawnHealers(float time) {
		return getRadiantJungle().respawnHealers(time);
	}

	public Fort getFort(int team) {
		if (team == Team.RADIANT)
			return getRadiantFort();
		return getDireFort();
	}

	public Location searchById(int id) {
		// I am searching for id only on three places.
		// The rest of the stuff shouldn't be targeted or will be targeted on contextual
		// basis. (when standing close to it)
		Location result = river.searchById(id);

		if (result == null)
			result = shops.searchById(id);

		if (result == null) {
			for (Rune r : runes) {
				if (r.getEntid() == id)
					result = r;
			}
		}

		return result;
	}

	/**
	 * 
	 * @return Returns array of runes that are on top of the map.
	 */
	public Rune[] getTopRunes() {
		return new Rune[] { river.getTopPowerup(), this.getRadiantJungle().getTopBounty(),
				this.getDireJungle().getTopBounty() };
	}

	/**
	 * 
	 * @return Returns array of runes that are on the bottom part of the mao.
	 */
	public Rune[] getBotRunes() {
		return new Rune[] { river.getBotPowerup(), this.getRadiantJungle().getBotBounty(),
				this.getDireJungle().getBotBounty() };
	}

	public Location getNearestShop(double x, double y, double z, Item i) {
		shops.findNearest(new Location(x, y, z), i);
		return null;
	}

	/**
	 * 
	 * @param l Location.
	 * @return Returns a camp, that is nearest ot given location.
	 */
	public Camp getNearestCamp(Location l) {
		Camp camp = null;

		double distance = Double.POSITIVE_INFINITY;
		for (Jungle j : jungles) {
			Camp _camp = j.getNearestCamp(l);
			double _distance = GridBase.distance(_camp, l);
			if (_distance < distance) {
				camp = _camp;
				distance = _distance;
			}
		}

		return camp;
	}

	public Lanes getLanes() {
		return lanes;
	}

	public Rune getNearestRune(Hero hero) {
		return runes.stream().min((r1, r2) -> Double.compare(GridBase.distance(r1, hero), GridBase.distance(r2, hero)))
				.get();
	}

	public void paintInterests(Graphics2D g) {
		for (Fountain f : fountains) {
			f.paint(g);
		}

		for (Fort f : forts) {
			f.paint(g);
		}

		lanes.paint(g);

		for (Jungle j : jungles)
			j.paint(g);

		river.paint(g);
		shops.paint(g);
	}

	public String printInterests() {
		IndentationStringBuilder b = new IndentationStringBuilder();

		b.appendLine("Interesting points loaded:");
		b.indent();

		b.append("Dire " + fountains[1].toString());
		b.append("Radiant " + fountains[0].toString());

		b.append("Dire " + forts[1].toString());
		b.append("Radiant " + forts[0].toString());

		b.append(lanes.toString());

		b.append("Radiant " + jungles[0].toString(b.getIndent()));
		b.append("Dire " + jungles[1].toString(b.getIndent()));

		b.append(river.toString());

		b.append(shops.toString());

		return b.toString();
	}

	public Fountain getDireFountain() {
		return fountains[1];
	}

	public Fountain getRadiantFountain() {
		return fountains[0];
	}

	public Fort getDireFort() {
		return forts[1];
	}

	public Fort getRadiantFort() {
		return forts[0];
	}

	public Jungle getRadiantJungle() {
		return jungles[0];
	}

	public Jungle getDireJungle() {
		return jungles[1];
	}

	public River getRiver() {
		return river;
	}

	public Shops getShops() {
		return shops;
	}

	public Jungle getJungle(int team) {
		if (team == Team.RADIANT)
			return getRadiantJungle();
		return getDireJungle();

	}

	public Lane getLane(int team, TYPE type) {
		return lanes.getLane(team, type);
	}

	public void removeCreepFromJungles(Creep creep) {
		Camp c = getNearestCamp(creep);
		if (!c.removeCreep(creep.getEntid())) {
			logger.warn("Creep not removed.");
		}

	}

}
