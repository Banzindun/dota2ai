package kocur.lina.bot;

import cz.cuni.mff.kocur.bot.BaseAgentController;
import cz.cuni.mff.kocur.botGraphics.BotTab;
import cz.cuni.mff.kocur.brain.Brain;
import cz.cuni.mff.kocur.influence.EnemyThreatsLayer;
import cz.cuni.mff.kocur.influence.ExtendedBotContext;
import cz.cuni.mff.kocur.influence.FarmingLayer;
import cz.cuni.mff.kocur.influence.FriendlyThreatsLayer;
import cz.cuni.mff.kocur.influence.GoalLayer;
import cz.cuni.mff.kocur.influence.InfluenceLayer;
import cz.cuni.mff.kocur.world.GridBase;

public class LayeredBotContext extends ExtendedBotContext {

	public static int FRIENDLY_THREATS = 0;
	public static int ENEMY_THREATS = 1;
	public static int COMBINED_THREATS = 2;
	public static int FARM = 3;
	public static int GOAL = 4;
	public static int GLOBAL = 5;

	
	
	private FriendlyThreatsLayer friendlyThreats = null;
	
	private EnemyThreatsLayer enemyThreats = null;

	private FarmingLayer farmLayer = null;
	
	protected GoalLayer goalLayer = null;
	
	////
	//private FOWLayer fowLayer = null;
	
	private BotTab graphics = null;

	
	

	public LayeredBotContext(BaseAgentController bc) {
		super(bc);
		
	}
	
	public void initializeLayers() {
		GridBase grid = GridBase.getInstance();
		
		friendlyThreats = new FriendlyThreatsLayer(grid, this);
		enemyThreats = new EnemyThreatsLayer(grid, this);
		
		// Grid and resolution
		goalLayer = new GoalLayer(grid.getWidth()/10, grid.getHeight()/10, 10);
		goalLayer.setParent(grid);
		goalLayer.setMaxInfluence(1.0);
		goalLayer.createParams();
		
		//fowLayer = new FOWLayer(grid, this);
		farmLayer = new FarmingLayer(grid, this);

		this.addLayer(FRIENDLY_THREATS, friendlyThreats);
		this.addLayer(ENEMY_THREATS, enemyThreats);
		this.addLayer(GOAL, goalLayer);
		this.addLayer(FARM, farmLayer);
		//this.addLayer("fow_layer", fowLayer);
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
		
		//try {
			//fowLayer.propagate();
		//} catch (Exception e) {
			//e.printStackTrace();
		//}
		
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
			this.addLayer(COMBINED_THREATS,(new FriendlyThreatsLayer(friendlyThreats)).addToLayer(enemyThreats));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		/*try {
			this.addLayer(GLOBAL, (new FriendlyThreatsLayer(friendlyThreats)).addToLayer(enemyThreats).addToLayer(InfluenceLayerTemplates.getRadiantConstantThreats()));
		} catch (Exception e) {
			e.printStackTrace();
		}*/

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
		return new String[] {"FriendlyThreats", "EnemyThreats", "CombinedThreats", "Farm", "Goal", "Global"};	
	}
	
	public static Integer getLayerNumber(String name) {
		if (name.equals("FriendlyThreats"))
			return FRIENDLY_THREATS;
		else if (name.equals("EnemyThreats"))
			return ENEMY_THREATS;
		else if (name.equals("CombinedThreats"))
			return COMBINED_THREATS;
		else if (name.equals("Farm"))
			return FARM;
		else if (name.equals("Goal"))
			return GOAL;
		else if (name.equals("Global"))
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
