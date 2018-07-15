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

		// Create lina ability decision set
		LinaAbilitiesDS abilitiesDS = new LinaAbilitiesDS();
		abilitiesDS.createDecisions();

		// Add abilities decision set
		this.addDecisionSet(abilitiesDS);

		// Create lina goal set (go to rune etc.)
		LinaGoalDS goals = new LinaGoalDS();
		goals.createDecisions();

		// Add goals decision set
		this.addVoidDecisionSet(goals);
	}

}
