package cz.cuni.mff.kocur.interests;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.HashMap;

import cz.cuni.mff.kocur.agent.AgentParameters;
import cz.cuni.mff.kocur.agent.InterestParameter;
import cz.cuni.mff.kocur.base.GraphicResources;
import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.events.GameEvent;
import cz.cuni.mff.kocur.events.ListenersManager;
import cz.cuni.mff.kocur.exceptions.LoadingError;
import cz.cuni.mff.kocur.world.Creep;

/**
 * Class that represents a camp.
 * @author kocur
 *
 */
public class Camp extends BaseInterest {
	/**
	 * Team of this camp. (as camp can belong to jungles, that belong to teams)
	 */
	private int team = Team.NONE;
	
	/**
	 * Camps difficulty.
	 */
	private int difficulty = -1;

	/**
	 * The key, that represents this camp.
	 */
	private String key;

	/**
	 * Creeps in this camp.
	 */
	private HashMap<Integer, Creep> creeps = new HashMap<>();

	/**
	 * Difficulties of dire camps.
	 */
	private int[] direDifficulties = new int[] { Difficulties.HARD, Difficulties.EASY, Difficulties.MEDIUM,
			Difficulties.HARD, Difficulties.MEDIUM, Difficulties.LEGENDARY, Difficulties.MEDIUM, Difficulties.LEGENDARY,
			Difficulties.HARD };

	/**
	 * Difficulties of radiant camps.
	 */
	private int[] radiantDifficulties = new int[] { Difficulties.EASY, Difficulties.HARD, Difficulties.MEDIUM,
			Difficulties.HARD, Difficulties.MEDIUM, Difficulties.LEGENDARY, Difficulties.HARD, Difficulties.MEDIUM,
			Difficulties.LEGENDARY };

	public Camp(double x, double y) {
		super(x, y);
	}

	public String toString() {
		IndentationStringBuilder b = new IndentationStringBuilder();
		b.appendLine("Camp: [" + x + ", " + y + "]");

		b.appendLine("Creeps:");
		b.indent();
		for (Creep c : creeps.values()) {
			b.append(c.getName() + " id:" + c.getEntid());
		}

		return b.toString();
	}

	public void paint(Graphics2D g) {
		Image image; 
		
		switch (difficulty) {
		case Difficulties.EASY:
			image = GraphicResources.getMapIcon("easy_camp").getImage();
			break;
		case Difficulties.HARD:
			image = GraphicResources.getMapIcon("hard_camp").getImage();
			break;
		case Difficulties.MEDIUM:
			image = GraphicResources.getMapIcon("medium_camp").getImage();
			break;
		case Difficulties.LEGENDARY:
			image = GraphicResources.getMapIcon("legendary_camp").getImage();
			break;
		default:
			return;
		}		
    	 
    	int x = gridX - image.getWidth(null)/2;
    	int y = gridY - image.getHeight(null)/2;
		
		g.drawImage(image, x, y, null);	
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setDiffuculty(int d) {
		difficulty = d;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setTeam(int t) {
		team = t;
	}

	public int getTeam() {
		return team;
	}

	/**
	 * Sets camps difficulty given a team id.
	 * @param team Team.
	 * @throws LoadingError Thrown, if difficulty couldn't be set.
	 */
	public void setDifficultyForTeam(int team) throws LoadingError {
		String[] parsedName = key.split("_");

		try {
			int campNumber = Integer.parseInt(parsedName[parsedName.length - 1]);
			if (campNumber > 9 || campNumber < 1)
				return;

			if (team == Team.DIRE)
				difficulty = direDifficulties[campNumber - 1];
			else
				difficulty = radiantDifficulties[campNumber - 1];

		} catch (NumberFormatException ex) {
			throw new LoadingError("Unable to parse campNumber.", ex);
		}
	}

	/**
	 * Respawns the camp.
	 */
	public void respawn() {
		// Clear the creeps
		creeps.clear();

		if (active) {
			setActive(true);

			GameEvent e = new GameEvent();
			e.setXYZ(x, y, 0);
			e.setType(difficulty);

			ListenersManager.triggerEvent("camprespawn", new GameEvent());
		}

	}

	/**
	 * Should be called after the camp was cleared.
	 */
	public void cleared() {
		setActive(false);

		GameEvent e = new GameEvent();
		e.setXYZ(x, y, z);
		e.setType(difficulty);

		ListenersManager.triggerEvent("camp_cleared", e);
	}

	@Override
	public InterestParameter getParameter(AgentParameters params) {
		InterestParameter p = params.getInterestParameter(Camp.class);

		if (p == null)
			return super.getParameter(params);
		return p;
	}

	public void addCreep(Creep c) {
		creeps.put(c.getEntid(), c);
	}

	public void updateCreep(Creep c) {
		creeps.put(c.getEntid(), c);
	}

	public boolean removeCreep(int id) {
		return creeps.remove(id) != null;		
	}
}
