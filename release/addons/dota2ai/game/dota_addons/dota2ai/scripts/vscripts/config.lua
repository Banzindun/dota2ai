-- This list is to pick enemies. The player bot will be picked using a JSON query
--Dota2AI.sHeroSelection = {"npc_dota_hero_lina","npc_dota_hero_brewmaster","npc_dota_hero_lone_druid","npc_dota_hero_meepo","npc_dota_hero_kunkka","npc_dota_hero_lycan","npc_dota_hero_bristleback","npc_dota_hero_ember_spirit","npc_dota_hero_earth_spirit","npc_dota_hero_terrorblade"}
Dota2AI.baseURL = "http://localhost:8080/Dota2AI"

bots = {}

-- We will assign slot to every bot
slots = {}

-- Is the mod initialized?
INIT = false

--playersWillBeJoining = false

updatePeriod = 0.25
updatesPerSecond = math.floor(1/updatePeriod)


-- Set this to 0 to skip the configuration dialog
Dota2AI.ConfigUITimeout = 0
Dota2AI.config = {}