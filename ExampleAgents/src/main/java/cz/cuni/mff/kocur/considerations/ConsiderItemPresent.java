package cz.cuni.mff.kocur.considerations;

import cz.cuni.mff.kocur.decisions.DecisionContext;
import cz.cuni.mff.kocur.world.Hero;
import cz.cuni.mff.kocur.world.Item;

/**
 * Consideration that considers if an item is present inside the inventory.
 * 
 * Item is obtained using target's getItem() method. Hero's inventory is
 * searched and index is normalized using PARAM_RANGE_MAX and PARAM_RANGE_MIN.
 * 
 * @author kocur
 *
 */
public class ConsiderItemPresent extends Consideration {

	public ConsiderItemPresent() {
		super();
		readableName = "ConsiderItemPresent";
	}

	@Override
	public double score(DecisionContext context) {
		Hero hero = (Hero) context.getSource();
		Item target = context.getTarget().getItem();

		int index = hero.getInventory().findInInventory(target);

		double max = this.getDoubleParameter(PARAM_RANGE_MAX);
		double min = this.getDoubleParameter(PARAM_RANGE_MIN);

		if (index == -1)
			return max;

		return normalize(index, min, max);
	}

}
