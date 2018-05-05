package cz.cuni.mff.kocur.agent;

import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class that stores ability indexes, that a agent wants to take. This class
 * send ability index according to the agent's level.
 * 
 * @author kocur
 *
 */
public class LevelUpSequence {

	/**
	 * Logger for LevelUpSequence class.
	 */
	private static final Logger logger = LogManager.getLogger(LevelUpSequence.class);

	/**
	 * The ability indices.
	 */
	private LinkedList<Integer> abilityIndexes = new LinkedList<>();

	/**
	 * Last level we had.
	 */
	private int lastLevel = -1;

	/**
	 * Constructor
	 */
	public LevelUpSequence() {

	}

	/**
	 * Sets the sequence of abilities.
	 * 
	 * @param abilities
	 *            Array of integer indices, corresponding to abilities, that should
	 *            be picked up.
	 */
	public void setSequence(Integer[] abilities) {
		for (Integer a : abilities) {
			abilityIndexes.add(a);
		}
	}

	/**
	 * Returns ability index, that corresponds to given level. This code asserts,
	 * that the ability will not be returned two times for the same level.
	 * 
	 * @param level Level of the hero.
	 * @return Returns ability index. 
	 */
	public int getAbilityIndex(int level) {
		if (abilityIndexes.size() == 0) {
			logger.warn("Abilitites are empty.");
			return -1;
		}

		int index = -1;

		if (level != lastLevel) {
			// If we are on the first spell, just return it
			if (lastLevel == -1)
				index = abilityIndexes.getFirst();

			// Else we want to remove the first one (as that one will be there from the last
			// get)
			else {
				abilityIndexes.removeFirst();
				if (abilityIndexes.size() > 0)
					index = abilityIndexes.getFirst();
			}
		}

		lastLevel = level;

		return index;
	}

	/**
	 * Clears the queue.
	 */
	public void clear() {
		abilityIndexes.clear();
	}

}
