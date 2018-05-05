package kocur.lina.agent;

import org.apache.logging.log4j.LogManager;

import cz.cuni.mff.kocur.brain.BaseBrain;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;

/**
 * Class that represents a Lina's brain. It defines additional decision sets
 * lina wiil be using.
 * 
 * @author kocur
 *
 */
public class LinaBrain extends BaseBrain {

	public LinaBrain(ExtendedAgentContext botContext) {
		super(botContext);
		logger = LogManager.getLogger(LinaBrain.class.getName());

	}

	@Override
	public void createDecisions() {
		// This will create all the base decision sets that handle movement etc.
		super.createDecisions();

		LinaAbilitiesDS abilitiesDS = new LinaAbilitiesDS();
		abilitiesDS.createDecisions();

		this.addDecisionSet(abilitiesDS);

		LinaGoalDS goals = new LinaGoalDS();
		goals.createDecisions();

		this.addVoidDecisionSet(goals);

		// Here we must define our skill/ability sets
	}

}
