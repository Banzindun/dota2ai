----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
-- Main purpose of functions declared in this file is to create and sends data about interesing 
-- points on the map to the framework.
-- For this purpose there are functions creating the data from different sources. (name, classname..)
--		
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------


-- Converts origin (that is a vector) to string in form "x, y"
function OriginToString(org)
	return org.x .. "," .. org.y
end

-- Adds interest to table object. The interest is found by name. If not found, the user is allerted.
-- 	msg - table that stores the interests
--	name - name of the interest (dota_badguys_tower1_bot)
function addInterest(msg, name)
	local ent = Entities:FindByName(nil, name)
	if ent == nil then 
		print("Missing:" .. name)
		return 
	end
	msg[name] = tostring(ent:entindex()) .. " " .. OriginToString(ent:GetOrigin())
end

-- This behaves the same as addIterest(..), but it should be called if there exist more objects 
-- with the same name, that should all be added to the message.
function addInterests(msg, name)
	local ents = Entities:FindAllByName(name)
 	for i, v in pairs(ents) do
 		local nname = name .. tostring(i)
 		msg[nname] = tostring(v:entindex()) .. " " .. OriginToString(v:GetOrigin())
 	end
end

-- This function finds all game objects by given classname and append them to the msg.
-- This is maninly used to find items, that have their name empty (like spawners and triggers).
-- 	msg - table that stores the interests
--	classname - classname of the interest (npc_dota_neutral_spawner)
function addInterestsByClassname(msg, classname)
	local ents = Entities:FindAllByClassname(classname)
 	for i, v in pairs(ents) do
 		local nname = classname .. tostring(i)
 		msg[nname] = tostring(v:entindex()) .. " " .. OriginToString(v:GetOrigin())
 	end
end

-- Crafts the message that will contain all the important iterest points on the map.
-- Returns the objects encoded in json.
function craftInterestsMsg()
	local msg = {}
	addInterest(msg, "dota_goodguys_tower1_bot")
	addInterest(msg, "dota_goodguys_tower2_bot")
	addInterest(msg, "dota_goodguys_tower3_bot")
	addInterest(msg, "dota_goodguys_tower1_mid")
	addInterest(msg, "dota_goodguys_tower2_mid")
	addInterest(msg, "dota_goodguys_tower3_mid")
	addInterest(msg, "dota_goodguys_tower1_top")
	addInterest(msg, "dota_goodguys_tower2_top")
	addInterest(msg, "dota_goodguys_tower3_top")
	addInterest(msg, "dota_goodguys_tower4_top")
	addInterest(msg, "dota_goodguys_tower4_bot")
	addInterest(msg, "good_rax_melee_bot")
	addInterest(msg, "good_rax_range_bot")
	addInterest(msg, "good_rax_melee_mid")
	addInterest(msg, "good_rax_range_mid")
	addInterest(msg, "good_rax_melee_top")
	addInterest(msg, "good_rax_range_top")
	addInterest(msg, "ent_dota_fountain_good")
	addInterest(msg, "dota_goodguys_fort")
	addInterest(msg, "dota_badguys_tower1_bot")
	addInterest(msg, "dota_badguys_tower2_bot")
	addInterest(msg, "dota_badguys_tower3_bot")
	addInterest(msg, "dota_badguys_tower1_mid")
	addInterest(msg, "dota_badguys_tower2_mid")
	addInterest(msg, "dota_badguys_tower3_mid")
	addInterest(msg, "dota_badguys_tower1_top")
	addInterest(msg, "dota_badguys_tower2_top")
	addInterest(msg, "dota_badguys_tower3_top")
	addInterest(msg, "dota_badguys_tower4_top")
	addInterest(msg, "dota_badguys_tower4_bot")
	addInterest(msg, "bad_rax_melee_bot")
	addInterest(msg, "bad_rax_range_bot")
	addInterest(msg, "bad_rax_melee_mid")
	addInterest(msg, "bad_rax_range_mid")
	addInterest(msg, "bad_rax_melee_top")
	addInterest(msg, "bad_rax_range_top")
	addInterest(msg, "ent_dota_fountain_bad")
	addInterest(msg, "dota_badguys_fort")
   
    addInterests(msg, "ent_dota_shop")

	addInterest(msg, "neutralcamp_good_1")
	addInterest(msg, "neutralcamp_good_2")
	addInterest(msg, "neutralcamp_good_3")
	addInterest(msg, "neutralcamp_good_4")
	addInterest(msg, "neutralcamp_good_5")
	addInterest(msg, "neutralcamp_good_6")
	addInterest(msg, "neutralcamp_good_7")
	addInterest(msg, "neutralcamp_good_8")
	addInterest(msg, "neutralcamp_good_9")
	addInterest(msg, "neutralcamp_evil_1")
	addInterest(msg, "neutralcamp_evil_2")
	addInterest(msg, "neutralcamp_evil_3")
	addInterest(msg, "neutralcamp_evil_4")
	addInterest(msg, "neutralcamp_evil_5")
	addInterest(msg, "neutralcamp_evil_6")
	addInterest(msg, "neutralcamp_evil_7")
	addInterest(msg, "neutralcamp_evil_8")
	addInterest(msg, "neutralcamp_evil_9")

	-- Rune spawners
	addInterests(msg, "dota_item_rune_spawner_powerup")
	addInterests(msg, "dota_item_rune_spawner_bounty")
		
	addInterest(msg, "npc_dota_roshan")

	addInterest(msg, "good_healer_7")
	addInterest(msg, "good_healer_6")
	addInterest(msg, "bad_healer_7")
	addInterest(msg, "bad_healer_6")

	--addInterest(msg, "lane_top_badguys_melee_spawner")
   	--addInterest(msg, "lane_mid_badguys_melee_spawner")
   	--addInterest(msg, "lane_bot_badguys_melee_spawner")
   	--addInterest(msg, "lane_bot_goodguys_melee_spawner")
   	--addInterest(msg, "lane_mid_goodguys_melee_spawner")
   	--addInterest(msg, "lane_top_goodguys_melee_spawner")
   	
	addInterest(msg, "lane_top_pathcorner_badguys_1")
	addInterest(msg, "lane_top_pathcorner_badguys_2")
	addInterest(msg, "lane_top_pathcorner_badguys_2b")
	addInterest(msg, "lane_top_pathcorner_badguys_2b1")
	addInterest(msg, "lane_top_pathcorner_badguys_3")
	addInterest(msg, "lane_top_pathcorner_badguys_4")
	addInterest(msg, "lane_top_pathcorner_badguys_5")
	addInterest(msg, "lane_top_pathcorner_badguys_6")
	addInterest(msg, "lane_mid_pathcorner_badguys_1")
	addInterest(msg, "lane_mid_pathcorner_badguys_2")
	addInterest(msg, "lane_mid_pathcorner_badguys_3")
	addInterest(msg, "lane_mid_pathcorner_badguys_4")
	addInterest(msg, "lane_mid_pathcorner_badguys_5")
	addInterest(msg, "lane_mid_pathcorner_badguys_6")
	addInterest(msg, "lane_mid_pathcorner_badguys_7")
	addInterest(msg, "lane_mid_pathcorner_badguys_8")
	addInterest(msg, "lane_bot_pathcorner_badguys_1")
	addInterest(msg, "lane_bot_pathcorner_badguys_2")
	addInterest(msg, "lane_bot_pathcorner_badguys_3")
	addInterest(msg, "lane_bot_pathcorner_badguys_4")
	addInterest(msg, "lane_bot_pathcorner_badguys_5")
	addInterest(msg, "lane_bot_pathcorner_badguys_6")
	addInterest(msg, "lane_bot_pathcorner_badguys_7")
	addInterest(msg, "lane_bot_pathcorner_badguys_8")
	addInterest(msg, "lane_bot_pathcorner_badguys_10")
	addInterest(msg, "lane_bot_pathcorner_badguys_11")
	addInterest(msg, "lane_bot_pathcorner_badguys_12")

	addInterest(msg, "lane_top_pathcorner_goodguys_1")
	addInterest(msg, "lane_top_pathcorner_goodguys_2")
	addInterest(msg, "lane_top_pathcorner_goodguys_2b")
	addInterest(msg, "lane_top_pathcorner_goodguys_2b1")
	addInterest(msg, "lane_top_pathcorner_goodguys_2b2")
	addInterest(msg, "lane_top_pathcorner_goodguys_2b3")
	addInterest(msg, "lane_top_pathcorner_goodguys_2b4")
	addInterest(msg, "lane_top_pathcorner_goodguys_2b5")
	addInterest(msg, "lane_top_pathcorner_goodguys_3")
	addInterest(msg, "lane_top_pathcorner_goodguys_4")
	addInterest(msg, "lane_top_pathcorner_goodguys_5")
	addInterest(msg, "lane_top_pathcorner_goodguys_6")
	addInterest(msg, "lane_top_pathcorner_goodguys_7")
	addInterest(msg, "lane_mid_pathcorner_goodguys_1")
	addInterest(msg, "lane_mid_pathcorner_goodguys_2")
	addInterest(msg, "lane_mid_pathcorner_goodguys_3")
	addInterest(msg, "lane_mid_pathcorner_goodguys_4")
	addInterest(msg, "lane_mid_pathcorner_goodguys_5")
	addInterest(msg, "lane_mid_pathcorner_goodguys_6")
	addInterest(msg, "lane_mid_pathcorner_goodguys_7")
	addInterest(msg, "lane_mid_pathcorner_goodguys_8")
	addInterest(msg, "lane_mid_pathcorner_goodguys_9")
	addInterest(msg, "lane_bot_pathcorner_goodguys_1")
	addInterest(msg, "lane_bot_pathcorner_goodguys_10")
	addInterest(msg, "lane_bot_pathcorner_goodguys_11")
	addInterest(msg, "lane_bot_pathcorner_goodguys_12")
	addInterest(msg, "lane_bot_pathcorner_goodguys_2")
	addInterest(msg, "lane_bot_pathcorner_goodguys_3")
	addInterest(msg, "lane_bot_pathcorner_goodguys_4")
	addInterest(msg, "lane_bot_pathcorner_goodguys_5")
	addInterest(msg, "lane_bot_pathcorner_goodguys_6")
	addInterest(msg, "lane_bot_pathcorner_goodguys_7")
	addInterest(msg, "lane_bot_pathcorner_goodguys_8")
	addInterest(msg, "lane_bot_pathcorner_goodguys_9")

	addInterestsByClassname(msg, "npc_dota_neutral_spawner")

	return package.loaded['game/dkjson'].encode(msg)
end

-- Function that crafts and sends the interests to the framework.
function SendInterests()
	request = SetRequest("/server/pushinterests", craftInterestsMsg())
    sendAndHandleRequest(request, nil, function( body ) 
        print("Interest points sucessfully accepted.")
        INIT = true
      end 
    )
end