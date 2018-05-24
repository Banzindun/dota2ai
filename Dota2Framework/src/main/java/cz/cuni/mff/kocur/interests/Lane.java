package cz.cuni.mff.kocur.interests;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.base.Location;
import cz.cuni.mff.kocur.world.Creep;
import cz.cuni.mff.kocur.world.GridBase;

/**
 * Class that represents a lane.
 * 
 * @author kocur
 *
 */
public class Lane {

	/**
	 * Type of the lane. Can be BOT, MID, TOP or OTHER. This is used by heroes,
	 * minions, buildings etc.
	 * 
	 * @author kocur
	 *
	 */
	public enum TYPE {
		BOT, MID, TOP, OTHER
	}

	/**
	 * Type of this lane. Which lane is it?
	 */
	private TYPE type = TYPE.OTHER;

	/**
	 * Team that this lane belongs to? Lanes are obviously not team related, but we
	 * have pathcorners, that define the lane and the pathcorners belong to one of
	 * the teams. (so does the lanes then)
	 */
	private int team = Team.NEUTRAL;

	/**
	 * Arraylist of path corners. They will be sorted later, so that the one closest
	 * to base is at the start.
	 */
	private ArrayList<PathCorner> pathCorners = new ArrayList<>();

	/**
	 * List of towers. Those will be sorted, so that first tower is the one closest
	 * to the base.
	 */
	private ArrayList<Tower> towers = new ArrayList<>();

	/**
	 * List of friendly creeps currently on the lane. Can be iterated, the older
	 * creeps will be at the start.
	 */
	private LinkedHashMap<Integer, Creep> laneCreeps = new LinkedHashMap<>();

	/**
	 * Melee barracks.
	 */
	private Rax meleeRax = null;

	/**
	 * Ranged barracks.
	 */
	private Rax rangedRax = null;

	/**
	 * Index of path corner, that is approximately in the middle of the lane. //
	 */
	private PathCorner centerPathCorner;

	/**
	 * Range of -1 to 1 indicating if lane is pushed beyond center of the lane. 0 if
	 * the lane is on center of the lane. (or no creeps are spawned yet)
	 */
	private double pushedRatio = 0;

	/**
	 * Constructor.
	 * 
	 * @param t
	 *            Type of this lane. (top, mid, bot)
	 * @param team
	 *            Team that this lane belongs to (dire, radiant)
	 */
	public Lane(TYPE t, int team) {
		this.type = t;
		this.team = team;
	}

	/**
	 * Initializes the center of lane. This information is hard coded :(. I have not
	 * found a better way how to do this, because every path looks a bit different.
	 * So this must be tweaked if the map changes dramatically.
	 */
	private void initCenterOfLane() {
		if (team == Team.RADIANT) {
			if (type == TYPE.BOT)
				centerPathCorner = pathCorners.get(2);
			else if (type == TYPE.TOP)
				centerPathCorner = pathCorners.get(9);
			else
				centerPathCorner = pathCorners.get(3);
		} else {
			if (type == TYPE.BOT)
				centerPathCorner = pathCorners.get(7);
			else if (type == TYPE.TOP)
				centerPathCorner = pathCorners.get(2);
			else
				centerPathCorner = pathCorners.get(5);
		}
	}

	/**
	 * Adds melee or ranged barracks to the lane.
	 * 
	 * @param melee
	 *            Boolean that is true, if the supplied barracks are melee.
	 * @param rax
	 *            Barracks, that should be added to the lane.
	 */
	public void addRax(boolean melee, Rax rax) {
		if (melee)
			meleeRax = rax;
		else
			rangedRax = rax;
	}

	/**
	 * Adds a pathCorner to this lane.
	 * 
	 * @param node
	 *            PathCorner that should be added.
	 */
	public void addPathNode(PathCorner node) {
		pathCorners.add(node);
	}

	/**
	 * Adds tower to this lane.
	 * 
	 * @param tower
	 *            Tower to be added to this lane.
	 */
	public void addTower(Tower tower) {
		towers.add(tower);
	}

	/**
	 * Sorts the good/bad corners and towers using distance from supplied location.
	 * 
	 * @param base Location we use.
	 */
	public void sort(Location base) {
		pathCorners = sortCorners(pathCorners, base);
		sortTowers(towers, base);

		initCenterOfLane();
	}

	/**
	 * Sorts the towers by distance from given location.
	 * 
	 * @param towers
	 *            List of towers to be sorted.
	 * @param l
	 *            Location that should be the towers should to.
	 */
	private void sortTowers(ArrayList<Tower> towers, Location l) {
		towers.sort((t1, t2) -> Double.compare(GridBase.distance(l.getX(), l.getY(), t1.getX(), t1.getY()),
				GridBase.distance(l.getX(), l.getY(), t2.getX(), t2.getY())));
	}

	/**
	 * Sorts the corners by distance to given location.
	 * 
	 * @param corners
	 *            ArrayList of PathCorners.
	 * @param l
	 *            Location.
	 * @return Returns sorted arrayList.
	 */
	private ArrayList<PathCorner> sortCorners(ArrayList<PathCorner> corners, Location l) {
		ArrayList<PathCorner> out = new ArrayList<PathCorner>();

		// Copy the corners
		LinkedList<PathCorner> _corners = new LinkedList<>();
		for (PathCorner c : corners)
			_corners.add(c);

		// Find corner closest to location l
		PathCorner closest = findClosest(_corners, l);
		_corners.remove(closest);

		// Clear the corners and add closest
		out.add(closest);

		// Go throught each of the corners and find the closest one
		for (int i = 0; i < corners.size() - 1; i++) {
			closest = findClosest(_corners, closest);
			out.add(closest);
			_corners.remove(closest);
		}

		return out;
	}

	/**
	 * 
	 * @param list
	 *            List of path corners.
	 * @param l
	 *            Location.
	 * @return Returns PathCorner, that is closest to the supplied location.
	 */
	private PathCorner findClosest(List<PathCorner> list, Location l) {
		return list.stream().min((t1, t2) -> Double.compare(GridBase.distance(l.getX(), l.getY(), t1.getX(), t1.getY()),
				GridBase.distance(l.getX(), l.getY(), t2.getX(), t2.getY()))).get();
	}

	/**
	 * 
	 * @param indent
	 *            Indendation that lines in the returned string should have.
	 * @return Returns string representation of the lane.
	 */
	public String toString(String indent) {
		IndentationStringBuilder b = new IndentationStringBuilder(indent);

		b.appendLine("Lane:");
		b.indent();

		for (int i = 0; i < pathCorners.size(); i++) {
			b.append(i + ": " + pathCorners.get(i).toString());
		}

		b.appendLine("Towers:");
		b.indent();
		for (Tower t : towers)
			b.append(t.toString());
		b.deindent();

		b.append("Melee rax " + meleeRax.toString());
		b.append("Ranged rax " + rangedRax.toString());

		b.appendLine("");
		b.appendLine("Creeps:");
		b.indent();
		for (Creep c : laneCreeps.values()) {
			b.append(c.getName() + " id:" + c.getEntid());
		}

		return b.toString();
	}

	/**
	 * Paints the lane to supplied graphics.
	 * 
	 * @param g
	 *            Graphics, where we should paint.
	 */
	public void paint(Graphics2D g) {
		paintCorners(g, pathCorners);
		paintTowers(g, towers);
	}

	/**
	 * Paints the towers to the supplied graphics object.
	 * 
	 * @param g
	 *            Where are we painting?
	 * @param towers
	 *            ArrayList of towers that should be painted.
	 */
	private void paintTowers(Graphics2D g, ArrayList<Tower> towers) {
		for (int i = 0; i < towers.size(); i++) {
			Tower t = towers.get(i);
			t.paint(g);
			g.drawString(i + "", t.getGridX(), t.getGridY());
		}

	}

	/**
	 * Paints path corners to the supplied graphics.
	 * 
	 * @param g
	 *            Graphics, where we are painting the corners.
	 * @param corners
	 *            ArrayList of pathCorners, that should be painted to graphics.
	 */
	private void paintCorners(Graphics2D g, ArrayList<PathCorner> corners) {
		for (int i = 0; i < corners.size() - 1; i++) {
			corners.get(i).paint(g);

			PathCorner c1 = corners.get(i);
			PathCorner c2 = corners.get(i + 1);

			g.setPaint(Colors.BLUE);
			g.drawLine(c1.getGridX(), c1.getGridY(), c2.getGridX(), c2.getGridY());

			g.drawString("" + i, c1.getGridX(), c1.getGridY());

		}

		PathCorner last = corners.get(corners.size() - 1);
		last.paint(g);
		g.drawString("" + (corners.size() - 1), last.getGridX(), last.getGridY());
	}

	/**
	 * 
	 * @return Returns type of this lane. (top, bot, mid)
	 */
	public TYPE getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 *            New type of this lane.
	 */
	public void setType(TYPE type) {
		this.type = type;
	}

	/**
	 * Sets the lane type (bot, mid, top) by using its string representation. Or
	 * just a string, that contains "bot" etc..
	 * 
	 * @param str
	 *            String with lane type in it.
	 */
	public void setTypeFromString(String str) {
		this.type = Lane.getTypeFromString(str);
	}

	/**
	 * 
	 * @param str
	 *            String with lane type in it.
	 * @return Returns the type of lane, that was found inside the supplied string.
	 */
	public static TYPE getTypeFromString(String str) {
		if (str.contains("bot"))
			return TYPE.BOT;
		else if (str.contains("top"))
			return TYPE.TOP;
		else if (str.contains("mid"))
			return TYPE.MID;
		else {
			return TYPE.OTHER;
		}
	}

	/**
	 * Should be called after a tower is destroyed. The tower is set to be dead and
	 * is removed from the array of towers.
	 * 
	 * @param id
	 *            Id of the destroyed tower.
	 * @return Returns boolean if a tower was destroyed (else we have a problem).
	 */
	public boolean towerWithIdDestroyed(int id) {
		for (int i = 0; i < towers.size(); i++) {
			if (towers.get(i).getEntid() == id) {
				towers.get(i).destroyed();
				return true;
			}
		}

		return false;
	}

	/**
	 * 
	 * @param c
	 *            Creep that should be added to array of lane creeps.
	 */
	public void addCreep(Creep c) {
		int entid = c.getEntid();

		// From documentation of LinkedHashMap:
		// This linked list defines the iteration ordering, which is normally the order
		// in which keys were inserted into the map (insertion-order). Note that
		// insertion order is not affected if a key is re-inserted into the map. (A key
		// k is reinserted into a map m if m.put(k, v) is invoked when m.containsKey(k)
		// would return true immediately prior to the invocation.)
		//
		// So creeps inserted first will remain on the front of the map - so the oldest
		// one should be on the first index
		laneCreeps.put(entid, c);
	}

	/**
	 * Removes a creep from array of lane creeps.
	 * 
	 * @param id
	 *            If of a creep that should be removed.
	 * @return Returns true if the creep was removed.
	 */
	public boolean removeCreep(Integer id) {
		return laneCreeps.remove(id) != null;
	}

	/**
	 * 
	 * @return Returns a creep that is the oldest. (meaning is at the front of the
	 *         oldest wave)
	 */
	public Creep getMostDistantCreep() {
		if (laneCreeps.size() == 0)
			return null;
		return laneCreeps.entrySet().iterator().next().getValue();
	}

	/**
	 * 
	 * @param l
	 *            Location.
	 * @return Returns the distance between location and this lane.
	 */
	public double getDistanceFromLane(Location l) {
		PathCorner c = pathCorners.get(0);
		double d = GridBase.distance(c.getX(), c.getY(), l.getX(), l.getY());
		return d;
	}

	/**
	 * 
	 * @return Returns first tower (closest to base) without counting towers that
	 *         protect the ancient.
	 */
	public Tower getFirstTower() {
		if (this.getType() == Lane.TYPE.BOT || this.getType() == Lane.TYPE.TOP) {
			return towers.get(1);
		} else
			return towers.get(0);
	}

	/**
	 * 
	 * @return Returns the team number.
	 */
	public int getTeam() {
		return team;
	}

	/**
	 * 
	 * @param team
	 *            Number of team of this lane.
	 */
	public void setTeam(int team) {
		this.team = team;
	}

	/**
	 * 
	 * @return Returns number in range [-1,1], that indicates if the lane is pushed
	 *         towards this lanes's base (-1,0) or the enemy base(0,1).
	 */
	public double getPushedRatio() {
		Creep c = this.getMostDistantCreep();
		if (c == null)
			return 0;

		// Max distance is distance of center path corner to the last path corner
		double maxDistance = GridBase.distance(centerPathCorner, pathCorners.get(pathCorners.size() - 1));

		double distanceCreepToCenter = GridBase.distance(c, centerPathCorner);

		int sign = 1;
		double[] cr = GridBase.getInstance().getEntityCoordinates(c);
		if (team == Team.DIRE) {
			if (cr[0] > centerPathCorner.gridX && cr[1] > centerPathCorner.gridY) {
				sign = 1;
			} else {
				sign = -1;
			}
		} else {
			if (cr[0] < centerPathCorner.gridX && cr[1] < centerPathCorner.gridY) {
				sign = 1;
			} else {
				sign = -1;
			}
		}

		pushedRatio = distanceCreepToCenter / maxDistance;

		if (pushedRatio > 1)
			pushedRatio = 1;

		return sign * pushedRatio;
	}

	/**
	 * Sets new ratio of how much is this lane pushed.
	 * 
	 * @param pushedRatio
	 *            New ratio.
	 */
	public void setPushedRatio(double pushedRatio) {
		this.pushedRatio = pushedRatio;
	}

	/**
	 * 
	 * @return Returns last (in enemy base direction) standing tower. Or null if
	 *         none is standing.
	 */
	public Tower getLastStandingTower() {
		for (int i = towers.size() - 1; i > 0; i--) {
			Tower t = towers.get(i);
			if (t.isStanding()) {
				return t;
			}
		}

		return null;
	}

}
