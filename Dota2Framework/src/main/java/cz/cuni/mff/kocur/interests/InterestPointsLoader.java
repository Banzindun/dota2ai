package cz.cuni.mff.kocur.interests;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

import cz.cuni.mff.kocur.base.Constants;
import cz.cuni.mff.kocur.base.IndentationStringBuilder;
import cz.cuni.mff.kocur.exceptions.LoadingError;
import cz.cuni.mff.kocur.server.MapperWrapper;
import cz.cuni.mff.kocur.world.Creep;
import cz.cuni.mff.kocur.world.GridBase;

/**
 * Class that loads all the interest points from the appropriate request. (/pushinterests)
 * @author kocur
 *
 */
public class InterestPointsLoader {
	/**
	 * Logger registered for this class.
	 */
	private  Logger logger = LogManager.getLogger(InterestPointsLoader.class.getName());
	
	/**
	 * Array of fountains.
	 */
	private Fountain[] fountains = new Fountain[2];
	
	/**
	 * Array of forts.
	 */
	private Fort[] forts = new Fort[2];

	/**
	 * Lanes object.
	 */
	private Lanes lanes = new Lanes(); 

	/**
	 * Radiant jungle.
	 */
	private Jungle radiantJungle = new Jungle(Team.RADIANT);
	
	/**
	 * Dire jungle.
	 */
	private Jungle direJungle = new Jungle(Team.DIRE);
	
	/**
	 * The river.
	 */
	private River river = new River();
	
	/**
	 * List of shops.
	 */
	private Shops shops = new Shops();
	
	/**
	 * Spawners - objects that spawn monsters in each camp. Used to identify the camps.
	 */
	private NeutralSpawner[] spawners = new NeutralSpawner[18];
	
	/**
	 * List of camps.
	 */
	private LinkedList<Camp> camps = new LinkedList<Camp>();
	
	/**
	 * Index of the spawner we are inserting.
	 */
	private int spawnersIndex = 0;
		
	/**
	 * Map interests loader constructor.
	 */
	public InterestPointsLoader() {
	}
	
	/**
	 * This method loads interest points from JSON and passed them to another method that should 
	 * load them to appropriate fields. 
	 * @param json JSON that contains interest points and should be parsed.
	 * @throws LoadingError If parsing JSON or loading of interests were unsuccessful
	 */
	public void load(String json) throws LoadingError {
		try {
		    TypeReference<HashMap<String,String>> typeRef 
            	= new TypeReference<HashMap<String,String>>() {};
			
			HashMap<String, String> interests =
					MapperWrapper.readValue(json, typeRef);
			
			parseInterests(interests);
		} catch (JsonParseException e) {
			throw new LoadingError("Unable to parse json.", e);
		} catch (JsonMappingException e) {
			throw new LoadingError("Unable to map to passed class.",e);
		} catch (IOException e) {
			throw new LoadingError("Unable to create new class instance.", e);
		}
		
		resolveCamps();
		sortLanes();
	}

	/**
	 * Sorts the lane's towers etc. by distance from forts.
	 */
	private void sortLanes() {
		lanes.sort(forts[0], forts[1]);
	}

	/**
	 * "neutralcamp_evil_1":"922 -4168.25,3671.25
	 * "neutralcamp_evil_2":"270 -3031.9997558594,4498.125
	 * "neutralcamp_good_1":"925 3008.2502441406,-4431.0625
	 * "neutralcamp_good_2":"926 4816.376953125,-4204.9921875
	 * 
	 * Resolves the camps from format above to Camp objects.
	 */
	private void resolveCamps() {
		for(NeutralSpawner sp : spawners) {
			int gridx = sp.getGridX();
			int gridy = sp.getGridY();
			
			// Find the closes camp and if present, set its gridX and gridY to to the girdxy fo neutral spawner.
			camps.stream()
			.min( (c1, c2) -> Double.compare(
					GridBase.distance(gridx, gridy, c1.getGridX(), c1.getGridY()),
					GridBase.distance(gridx, gridy, c2.getGridX(), c2.getGridY())))
					.ifPresent(c -> c.setGridXY(gridx, gridy));					
		}
		
	}

	/**
	 * Parses all the incoming interests to structure. 
	 * @param interests Map containing all the interests with name and in string representation.
	 * @throws LoadingError If interest couldn't be parsed.
	 */
	private void parseInterests(HashMap<String, String> interests) throws LoadingError{
		for (Entry<String, String> e : interests.entrySet()) {
			String key = e.getKey();
			
			// Parse information inside value
			double[] values = parseValues(e.getValue()); 
			
			int id = (int) values[0];
			double x = values[1];
			double y = values[2];
			
			// Check if good or evil
			boolean good = key.contains("good");
			boolean evil = key.contains("evil") || key.contains("bad");
			
			int team = good ? Team.RADIANT : Team.DIRE;
			
			if (evil || good) {
				if (key.contains("tower")) {
					handleTower(key, team, id, x, y);
				} else if (key.contains("pathcorner")) {
					handlePath(key, team, x, y);
				} else if (key.contains("rax")) {
					handleRax(key, team, id, x, y);
				} else if (key.contains("fountain")) {
					handleFountain(good, x, y);
				} else if (key.contains("neutralcamp")) {
					handleCamp(key, good, x, y);
				} else if (key.contains("healer")) {
					handleHealer(good, id, x, y);
				} else if (key.contains("fort")) {
					handleFort(good, id, x, y);
				}
				 else {
					logger.warn("Unknown interest point: " + key);
				}
				
			}
			else {
				if (key.contains("shop")) {
					handleShop(id, x, y);
				} else if (key.contains("bounty")) {
					handleBounty(id, x, y);
				} else if (key.contains("powerup")) {
					handlePowerup(id, x, y);
				} else if (key.contains("roshan")) {
					handleRoshan(id, x, y);
				}else if (key.contains("neutral_spawner")) {
					handleSpawner(id, x, y);
				} else {
					logger.warn("Unknown interest point:" + key);
				}
			}
		}
		
		// And add resolved shops to jungles
		addSecretShopsToJungles();
	}
	


	/**
	 * Handles a monster spawner.
	 * @param id id.
	 * @param x x.
	 * @param y y.
	 */
	private void handleSpawner(int id, double x, double y) {
		NeutralSpawner s = new NeutralSpawner(id, x, y);
		
		spawners[spawnersIndex] = s;
		spawnersIndex++;
	}

	/**
	 * Parses values from string that contains interest object.
	 * @param s String with interest.
	 * @return Returns array of doubles wit numbers from the string.
	 * @throws LoadingError If something was wrong with the string.
	 */
	private double[] parseValues(String s) throws LoadingError {
		double[] res = new double[3];
		
		String[] idAndOrigin = s.split("\\s+");
		
		if (idAndOrigin.length != 2)
			throw new LoadingError("Too few or many values to parse: " + s);
			
		String[] fields = idAndOrigin[1].split(",");
		
		if (fields.length != 2)
			throw new LoadingError("Bad coordinates: " + s);
		
		// Parse id
		res[0] = Integer.parseInt(idAndOrigin[0].trim());
		
		// x and y will be resolved to grid coordinates - when using iterests this 
		// will be more usable than in-game coordinates
		res[1] = Double.parseDouble(fields[0].trim());
		res[2] = Double.parseDouble(fields[1].trim());
				
		return res;
	}
	
	private void handleRoshan(int id, double x, double y) {
		Creep r = new Creep();
		r.setEntid(id);
		r.setX(x);
		r.setY(y);
		
		river.setRoshan(r);
	}
	
	/**
	 * "dota_item_rune_spawner_powerup1":"863 -1760,1216
	 * "dota_item_rune_spawner_powerup2":"883 2250.5905761719,-1857.8344726563
	 * 
	 * Handles the powerup.
	 * 
	 * @param id Id of the powerup.
	 * @param x x
	 * @param y y
	 */
	private void handlePowerup(int id, double x, double y) {
		Rune p = new Rune(id, x, y);
		p.setType(Rune.DOTA_RUNE_HASTE);
		
		if (y > 0) 
			river.addTopPowerup(p);
		else 
			river.addBotPowerup(p);
		
	}

	/**
	 * "dota_item_rune_spawner_bounty1":"881 -4328.0776367188,1591.9674072266
	 * "dota_item_rune_spawner_bounty2":"882 4167.9223632813,-1704.0325927734
	 * "dota_item_rune_spawner_bounty3":"884 3686.955078125,-3624.8107910156
	 * "dota_item_rune_spawner_bounty4":"885 -3149.0339355469,3725.8413085938
	 * 
	 * Handles the bounty.
	 * 
	 * @param id Id of the bounty.
	 * @param x x
	 * @param y y
	 */
	private void handleBounty(int id, double x, double y){
		Rune b = new Rune(id, x, y);
		b.setType(Rune.DOTA_RUNE_BOUNTY);
		
		if (y < 0) {
			// Either dire or radiant bot
			if (y < Constants.rightRiverEndY) {
				radiantJungle.addBotBounty(b);
			} else 
				direJungle.addBotBounty(b);
		} else {
			// Either dire or radiant top
			if (y < Constants.leftRiverStartY)
				radiantJungle.addTopBounty(b);
			else 
				direJungle.addTopBounty(b);
		}
	}
	/**
	 *	"ent_dota_shop1":"522 -7542.490234375,-6171.169921875 // Radiant base
	 * 	"ent_dota_shop2":"525 6697.416015625,6809.2998046875 // Dire base
	 * 	"ent_dota_shop3":"727 -4890.8959960938,1465.8024902344 // Radiant secret
	 * 	"ent_dota_shop4":"729 7596.7275390625,-4086.8618164063 // Radiant side
	 * 	"ent_dota_shop5":"861 -7708.865234375,4409.6103515625 // Dire side
	 * 	"ent_dota_shop6":"864 4747.7724609375,-1372.9008789063 // Dire secret
	 * 
	 * Handles the shops. 
	 * 
	 * @param id Shop's id. 
	 * @param x Shop's x position.
	 * @param y Shop's y position.
	 */
	private void handleShop(int id, double x, double y) {
		shops.addShop(id, x, y);
	}
	/**
	 * "good_healer_6":"641 -4348.0727539063,198.23217773438
	 * "good_healer_7":"640 1317.41796875,-4163.599609375
	 * "bad_healer_6":"643 3446.2194824219,330.17523193359
	 * "bad_healer_7":"642 -1201.7805175781,3914.1752929688
	 * 
	 * Handles the healers.
	 * 
	 *  @param good True if healer belongs to radiant. 
	 *  @param id Healers id.
	 *   @param x x 
	 *    @param y y 
	 */
	private void handleHealer(boolean good, int id, double x, double y) {
		Healer h = new Healer(id, x, y);
		if (good)
			h.setTeam(Team.RADIANT);
		else 
			h.setTeam(Team.DIRE);
		
		// Check x
		if (x > 0) {
			// Either radiant or dire bot
			if (y < Constants.rightRiverEndY) 
				radiantJungle.addBotHealer(h);
			else 
				direJungle.addBotHealer(h);
		} else {
			// Either dire or radiant top
			if (y < Constants.leftRiverStartY) 
				radiantJungle.addTopHealer(h);
			else 
				direJungle.addTopHealer(h);
		}
	}

	/**
	 * Handles the camp interest.
	 * 
	 * @param key The camp's string representation.
	 * @param good True if it is radiant camp.
	 * @param x x
	 * @param y y
	 * @throws LoadingError Thhrown if handling failed.
	 */
	private void handleCamp(String key, boolean good, double x, double y) throws LoadingError {
		Camp camp = new Camp(x, y);
		camp.setKey(key);
		
		camps.add(camp);
				
		if (good)
			radiantJungle.addCamp(camp);
		else {
			direJungle.addCamp(camp);
		}
	}

	/**
	 * Handles a fountain.
	 * @param good True if this is a radiant fountain.
	 * @param x x
	 * @param y y
	 */
	private void handleFountain(boolean good, double x, double y) {
		Fountain f = new Fountain(x, y);
		
		if (good) {
			f.setTeam(Team.RADIANT);
			fountains[0] = f;
		}
		else { 
			f.setTeam(Team.DIRE);
			fountains[1] = f;
		}
	}
	
	/**
	 * Handles fort.
	 * @param good True if the fort is radiant.
	 * @param id Its id.
	 * @param x x
	 * @param y y
	 */
	private void handleFort(boolean good, int id, double x, double y) {
		Fort f = new Fort(x, y);
		
		if (good) {
			f.setTeam(Team.RADIANT);
			forts[0] = f;
		}
		else { 
			f.setTeam(Team.DIRE);
			forts[1] = f;
		}
		
	}

	/**
	 * Handles barracks.
	 * @param key Its string representation (we want to know if they are melee or not)
	 * @param team Team number.
	 * @param id Id of the object.
	 * @param x x
	 * @param y y
	 * @throws LoadingError Thrown if something went wrong.
	 */
	private void handleRax(String key, int team, int id, double x, double y) throws LoadingError {
		boolean melee = key.contains("melee");
			
		Rax rax = new Rax(id, x, y);
				
		if (key.contains("top")) {
			lanes.getLane(team, Lane.TYPE.TOP).addRax(melee, rax);
		} else if (key.contains("mid")) {
			lanes.getLane(team, Lane.TYPE.MID).addRax(melee, rax);
		} else if (key.contains("bot")) {
			lanes.getLane(team, Lane.TYPE.BOT).addRax(melee, rax);			
		}
		else {
			throw new LoadingError("Unable to find top, bot or mid in rax's name.");
		}
		
	}

	/**
	 * Handles a path corner.
	 * @param key Key (we want to know the path corner's number).
	 * @param team Team number.
	 * @param x x
	 * @param y y
	 * @throws LoadingError Thrown if something went wrong.
	 */
	private void handlePath(String key, int team, double x, double y) throws LoadingError {
		PathCorner node = new PathCorner(x, y);
		
		if (key.contains("top")) {
			lanes.getLane(team, Lane.TYPE.TOP).addPathNode(node);
		} else if (key.contains("mid")) {
			lanes.getLane(team, Lane.TYPE.MID).addPathNode(node);
		} else if (key.contains("bot")) {
			lanes.getLane(team, Lane.TYPE.BOT).addPathNode(node);
		}
		else {
			throw new LoadingError("Unable to find top, bot or mid in path node's name.");
		}
		
	}

	/**
	 * Handles the tower. 
	 * @param key String representation - we want to the towers position (top, bot..)
	 * @param team Team number.
	 * @param id id
	 * @param x x
	 * @param y y 
	 * @throws LoadingError If something went wrong.
	 */
	private void handleTower(String key, int team, int id, double x, double y) throws LoadingError {
		Tower tower = new Tower(id, team, x, y);
		
		if (key.contains("top")) {
			lanes.getLane(team, Lane.TYPE.TOP).addTower(tower);
		} else if (key.contains("mid")) {
			lanes.getLane(team, Lane.TYPE.MID).addTower(tower);
		} else if (key.contains("bot")) {
			lanes.getLane(team, Lane.TYPE.BOT).addTower(tower);
		}
		else {
			throw new LoadingError("Unable to find top, bot or mid in path node's name.");
		}
	}
	
	/**
	 * Adds secret shops to both jungles.
	 */
	private void addSecretShopsToJungles() {
		direJungle.setSecretShop(shops.getDireSecret());
		radiantJungle.setSecretShop(shops.getRadiantSecret());
	}
		
	public String toString() {
		IndentationStringBuilder b = new IndentationStringBuilder();
		
		b.appendLine("Interesting points loaded:");
		b.indent();
		
		b.append("Dire " + fountains[0].toString());
		b.append("Radiant " + fountains[1].toString());
		
		b.append("Dire " + forts[0].toString());
		b.append("Radiant " + forts[1].toString());
			
		b.append(lanes.toString());
				
		b.append("Radiant " + radiantJungle.toString(b.getIndent()));
		b.append("Dire " + direJungle.toString(b.getIndent()));
		
		b.append(river.toString());
		
		b.append(shops.toString());
		
		/*for(NeutralSpawner s : spawners) {
			b.append(s.toString());
		}*/
		
		return b.toString();
	}

	public Fountain[] getFountains() {
		return fountains;
	}

	public Lanes getLanes() {
		return lanes;
	}

	
	public Jungle getRadiantJungle() {
		return radiantJungle;
	}
	
	public Jungle getDireJungle() {
		return direJungle;
	}

	public River getRiver() {
		return river;
	}
	
	public Shops getShops() {
		return shops;
	}
	
	public Fort[] getForts() {
		return forts;
	}
	
	/*public NeutralSpawner[] getSpawners() {
		return spawners;
	}

	public void setSpawners(NeutralSpawner[] spawners) {
		this.spawners = spawners;
	}*/

	
}
