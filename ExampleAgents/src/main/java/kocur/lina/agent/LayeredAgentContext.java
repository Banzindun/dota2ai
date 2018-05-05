package kocur.lina.agent;

import cz.cuni.mff.kocur.agent.BaseAgentController;
import cz.cuni.mff.kocur.botGraphics.BotTab;
import cz.cuni.mff.kocur.brain.Brain;
import cz.cuni.mff.kocur.influence.EnemyThreatsLayer;
import cz.cuni.mff.kocur.influence.ExtendedAgentContext;
import cz.cuni.mff.kocur.influence.FarmingLayer;
import cz.cuni.mff.kocur.influence.FriendlyThreatsLayer;
import cz.cuni.mff.kocur.influence.GoalLayer;
import cz.cuni.mff.kocur.influence.InfluenceLayer;
import cz.cuni.mff.kocur.world.GridBase;

/**
 * Class that extends Agent's context and creates a context, that contains
 * influence map layers.
 * 
 * @author kocur
 *
 */
public class LayeredAgentContext extends ExtendedAgentContext {

	public static int FRIENDLY_THREATS = 0;
	public static int ENEMY_THREATS = 1;
	public static int COMBINED_THREATS = 2;
	public static int FARM = 3;
	public static int GOAL = 4;
	public static int GLOBAL = 5;

	/**
	 * Influence map containing friendly influence.
	 */
	private FriendlyThreatsLayer friendlyThreats = null;

	/**
	 * Influence map containing enemy influence.
	 */
	private EnemyThreatsLayer enemyThreats = null;

	/**
	 * Influence map with best locations to stand at during farming.
	 */
	private FarmingLayer farmLayer = null;

	/**
	 * Influence map containing goals influence.
	 */
	protected GoalLayer goalLayer = null;

	////
	// private FOWLayer fowLayer = null;

	/**
	 * And we store our bot's tab here.
	 */
	private BotTab graphics = null;

	public LayeredAgentContext(BaseAgentController bc) {
		super(bc);

	}

	/**
	 * Initializes the Influence Maps that are stored here.
	 */
	public void initializeLayers() {
		GridBase grid = GridBase.getInstance();

		friendlyThreats = new FriendlyThreatsLayer(grid, this);
		enemyThreats = new EnemyThreatsLayer(grid, this);

		// Grid and resolution
		goalLayer = new GoalLayer(grid.getWidth() / 10, grid.getHeight() / 10, 10);
		goalLayer.setParent(grid);
		goalLayer.setMaxInfluence(1.0);
		goalLayer.createParams();

		// fowLayer = new FOWLayer(grid, this);
		farmLayer = new FarmingLayer(grid, this);

		this.addLayer(FRIENDLY_THREATS, friendlyThreats);
		this.addLayer(ENEMY_THREATS, enemyThreats);
		this.addLayer(GOAL, goalLayer);
		this.addLayer(FARM, farmLayer);
		// this.addLayer("fow_layer", fowLayer);
	}

	@Override
	public synchronized void updateLayers() {
		if (this.getController().getHero() == null)
			return;

		try {
			farmLayer.propagate();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			friendlyThreats.propagate();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			enemyThreats.propagate();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			this.addLayer(COMBINED_THREATS, (new FriendlyThreatsLayer(friendlyThreats)).addToLayer(enemyThreats));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// try {
		// fowLayer.propagate();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		/*
		 * try { this.addLayer(GLOBAL, (new
		 * FriendlyThreatsLayer(friendlyThreats)).addToLayer(enemyThreats).addToLayer(
		 * InfluenceLayerTemplates.getRadiantConstantThreats())); } catch (Exception e)
		 * { e.printStackTrace(); }
		 */

		graphics.layersUpdated();
	}

	@Override
	public void setBrain(Brain b) {
		this.brain = b;
		// Create a graphics for this bot
		graphics = new BotTab(this, this.getHeroName());
	}

	@Override
	public void destroyed() {
		graphics.destroyed();

	}

	@Override
	public String[] getLayerNames() {
		return new String[] { "FriendlyThreats", "EnemyThreats", "CombinedThreats", "Farm", "Goal", "Global" };
	}

	/**
	 * 
	 * @param name
	 *            Name of the layer.
	 * @return Returns it's type. LayeredBotContext.FRIENDLY_THREATS etc.
	 */
	public static Integer getLayerNumber(String name) {
		String _name = name.toLowerCase();
		if (_name.equals("FriendlyThreats"))
			return FRIENDLY_THREATS;
		else if (_name.equals("EnemyThreats"))
			return ENEMY_THREATS;
		else if (_name.equals("CombinedThreats"))
			return COMBINED_THREATS;
		else if (_name.equals("Farm"))
			return FARM;
		else if (_name.equals("Goal"))
			return GOAL;
		else if (_name.equals("Global"))
			return GLOBAL;

		return -1;
	}

	public BotTab getGraphics() {
		return graphics;
	}

	public void setGraphics(BotTab graphics) {
		this.graphics = graphics;
	}

	public InfluenceLayer getEnemyThreatLayer() {
		return enemyThreats;
	}

	public InfluenceLayer getFriendlyThreatLayer() {
		return friendlyThreats;
	}
}
