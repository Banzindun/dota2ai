----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
-- This file defines a few functions, that are used by main script to send requests to the framework.
-- Main features theese functions supply are:
-- 		- create request headers and creat JSON from passed data
--		- send the request to server and handle the response (parses the incoming JSON to table etc.)
--		- parse a command that the framework issued
--			- this is used mainly to check for server commands (PAUSE, UNPAUSE)
-- 		- call a function after the request failed/succeded
--		
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------

-- This file contains functions that serialise game entities into JSON 
function Dota2AI:JSONChat(event)	
	jsonEvent = {}
	jsonEvent.teamOnly = event.teamonly -- we should probably test one day if the bot is on the same team
    jsonEvent.player = event.userid
    jsonEvent.text = event.text

    print("Encoding the chat message.")

	return package.loaded['game/dkjson'].encode(jsonEvent)
end

 function Dota2AI:JSONHero(unit, eUnit)
	unit.gold = eUnit:GetGold()
	unit.type = "Hero"
	unit.xp = eUnit:GetCurrentXP()
	unit.deaths = eUnit:GetDeaths()
	unit.kills = eUnit:GetKills()
	unit.denies = eUnit:GetDenies()
	
	-- Abilities are actually in CBaseNPC, but we'll just send them for Heros to avoid cluttering the JSON--
	unit.abilities = {}
	local abilityCount = eUnit:GetAbilityCount() - 1 --minus 1 because lua for loops are upper boundary inclusive
	
	for index=0,abilityCount,1 do			
		local eAbility = eUnit:GetAbilityByIndex(index)
		-- abilityCount returned 16 for me even though the hero had only 5 slots (maybe it's actually max slots?). We fix that by checking for null pointer
		if eAbility then
			--print ("------------------------------------------------")
			--for name, value in pairs(eAbility:GetAbilityKeyValues()) do 
				--if type(value) ~= "table" then 
					--print(name .. ": " .. value)
				--else
					--print (name .. ":")
					--PrintTable(value, "\t")
				--end
			--end

			unit.abilities[index] = {}
			unit.abilities[index].type = "Ability"
			unit.abilities[index].name = eAbility:GetAbilityName()
			unit.abilities[index].targetFlags = eAbility:GetAbilityTargetFlags()
			unit.abilities[index].targetTeam = eAbility:GetAbilityTargetTeam()
			unit.abilities[index].targetType = eAbility:GetAbilityTargetType()
			unit.abilities[index].abilityType = eAbility:GetAbilityType()
			unit.abilities[index].abilityIndex = eAbility:GetAbilityIndex()
			unit.abilities[index].behavior = eAbility:GetBehavior() 
			unit.abilities[index].level = eAbility:GetLevel()				
			unit.abilities[index].maxLevel = eAbility:GetMaxLevel()
			unit.abilities[index].abilityDamage = eAbility:GetAbilityDamage()
			unit.abilities[index].abilityDamageType = eAbility:GetAbilityDamageType()
			unit.abilities[index].cooldownTime = eAbility:GetCooldownTime()
			unit.abilities[index].cooldownTimeRemaining = eAbility:GetCooldownTimeRemaining()
			unit.abilities[index].castRange = eAbility:GetCastRange()
		end
	end

	self:JSONInventory(unit, eUnit)
 end

 function Dota2AI:JSONTree(eTree)	
	local tree = {}
	tree.origin = VectorToArray(eTree:GetOrigin())
	tree.type = "Tree"
	return tree
 end
 
 function Dota2AI:JSONInterest(inter, name)
 	local unit = {}
 	unit.interestName = name
	unit.entid = inter:entindex() 	
	unit.origin = VectorToArray(inter:GetOrigin())
	unit.modelName = inter:GetModelName() 
	return unit
 end


 function Dota2AI:JSONunit(eUnit)	
	local unit = {}
	unit.entid = eUnit:entindex() 	
	unit.level = eUnit:GetLevel()
	unit.origin = VectorToArray(eUnit:GetOrigin())
	--unit.absOrigin = VectorToArray(eUnit:GetAbsOrigin())
	--unit.center = VectorToArray(eUnit:GetCenter())
	--unit.velocity = VectorToArray(eUnit:GetVelocity())
	--unit.localVelocity = VectorToArray(eUnit:GetForwardVector())
	unit.health = eUnit:GetHealth()
	unit.maxHealth = eUnit:GetMaxHealth()
	unit.mana = eUnit:GetMana()
	unit.maxMana = eUnit:GetMaxMana()
	unit.alive = eUnit:IsAlive()
	unit.blind = eUnit:IsBlind()	
	unit.dominated = eUnit:IsDominated()
	unit.deniable = eUnit:IsDeniable()
	unit.disarmed = eUnit:IsDisarmed()
	unit.rooted = eUnit:IsRooted()
	unit.name = eUnit:GetName()
	unit.team = eUnit:GetTeamNumber()
	unit.attackRange = eUnit:GetAttackRange()	
	unit.visionRange = eUnit:GetCurrentVisionRange()
	unit.idle = eUnit:IsIdle() -- For example for courier we want to know this
	unit.speed = eUnit:GetIdealSpeed() 
	unit.damage = eUnit:GetAttackDamage() -- random int between min and max attack
	unit.maxGoldBounty = eUnit:GetMaximumGoldBounty()
	unit.minGoldBounty = eUnit:GetMinimumGoldBounty()
	unit.projectileSpeed = eUnit:GetProjectileSpeed()
	
	if eUnit:IsHero() then
		self:JSONHero(unit, eUnit)
	elseif eUnit:IsCreep() then
		unit.type = "Creep"
	elseif eUnit:IsCourier() then
		unit.type = "Courier"
	elseif eUnit:IsBuilding() then
		if eUnit:IsTower() then
			unit.type = "Tower"
		else
			unit.type = "Building"
		end
	else
		unit.type = "BaseNPC"
	end
	
	
	local attackTarget = eUnit:GetAttackTarget()
	if attackTarget then
		unit.attackTarget = attackTarget:entindex()
	end
	
	return unit
 end
 
function CheckForBuilding(unit) 
	return unit:IsBuilding()
end


function Dota2AI:JSONWorld(eHero)	
	local world = {}

	world.entities = {}

	-- We will send empty world, but we will still send the update 
	-- (if we are stepping, this is necessary)
	if not eHero.hero:IsAlive() then 
		world.entities[eHero.hero:entindex()]=self:JSONunit(eHero.hero)
		return package.loaded['game/dkjson'].encode(world)
	end
	
	local allUnits = FindUnitsInRadius(eHero.hero:GetTeamNumber(), 
                              eHero.hero:GetAbsOrigin(),
                              nil,
                              eHero.hero:GetCurrentVisionRange(),
                              DOTA_UNIT_TARGET_TEAM_BOTH,
                              DOTA_UNIT_TARGET_ALL,
                              DOTA_UNIT_TARGET_FLAG_FOW_VISIBLE,
                              FIND_ANY_ORDER,
                              true)
							 
	for _,unit in pairs(allUnits) do
		world.entities[unit:entindex()]=self:JSONunit(unit)
	end

	local buildings = Entities:FindAllInSphere(eHero.hero:GetOrigin(), eHero.hero:GetCurrentVisionRange())
    for _,unit in pairs(buildings) do
    	local isBuilding = false
 		local b = pcall(function() isBuilding = unit:IsBuilding() end)
		if b and isBuilding then
			if unit:IsTower() and unit:GetTeamNumber() == eHero.hero:GetTeamNumber() then
				world.entities[unit:entindex()]=self:JSONunit(unit)
			end
		end
		--world.entities[unit:entindex()]=self:JSONunit(unit)
	end

	-- Add runes or healers there, if they are in hero's range
	self:AddInterestsToWorld(eHero, world, "dota_item_rune")
	self:AddInterestsToWorld(eHero, world, "npc_dota_healer")	

	return package.loaded['game/dkjson'].encode(world)
end 


function Dota2AI:AddInterestsToWorld(eHero, world, name)
	local interests = Entities:FindAllByClassnameWithin(name, eHero.hero:GetAbsOrigin(), eHero.hero:GetCurrentVisionRange()) 

	local count = 0
  	for _,inter in pairs(interests) do
  		if eHero.hero:CanEntityBeSeenByMyTeam(inter) then
  			count = count + 1 
  		end
  	end


	if count > 0 then
		if world.interests == nil then 
			world.interests = {}
		end
			
		for _,inter in pairs(interests) do
			if eHero.hero:CanEntityBeSeenByMyTeam(inter) then
				world.interests[inter:entindex()]= self:JSONInterest(inter, name)
			end
		end
	end
end



function Dota2AI:JSONBigWorld(targetTeam)	
	local world = {}
	world.time = GameRules:GetGameTime()

	world.entities = {}	

	--local tree = Entities:FindByClassname(nil, "ent_dota_tree")
	--while tree ~= nil do
		--if bots[0].hero:CanEntityBeSeenByMyTeam(tree) and not tree:IsStanding() then
			--world.entities[tree:entindex()]=self:JSONTree(tree)
		--end		
		--tree = Entities:FindByClassname(tree, "ent_dota_tree")
	--end

	
	local allUnits = FindUnitsInRadius(targetTeam, 
                              Vector(0,0,0),
                              nil,
                              FIND_UNITS_EVERYWHERE,
                              DOTA_UNIT_TARGET_TEAM_BOTH,
                              DOTA_UNIT_TARGET_ALL,
                              DOTA_UNIT_TARGET_FLAG_FOW_VISIBLE,
                              FIND_ANY_ORDER,
                              true)
							 
	for _,unit in pairs(allUnits) do
		if unit:GetTeam() ~= DOTA_TEAM_NEUTRALS then
			world.entities[unit:entindex()]=self:JSONunit(unit)
		end
	end
	
	-- Find all the couriers
	local couriers=Entities:FindAllByName("npc_dota_courier")
		
	for index,unit in ipairs(couriers) do
		world.entities[unit:entindex()]=self:JSONunit(unit)
	end

	-- FindUnitsInRadius somehow ignores all the buildings
	local buildings = {}
	if targetTeam == DOTA_TEAM_GOODGUYS then 
		buildings[0] =  Entities:FindByName(nil, "dota_goodguys_tower1_bot")
		buildings[1] =  Entities:FindByName(nil, "dota_goodguys_tower2_bot")
		buildings[2] =  Entities:FindByName(nil, "dota_goodguys_tower3_bot")
		
		buildings[3] =  Entities:FindByName(nil, "dota_goodguys_tower1_mid")
		buildings[4] =  Entities:FindByName(nil, "dota_goodguys_tower2_mid")
		buildings[5] =  Entities:FindByName(nil, "dota_goodguys_tower3_mid")
		
		buildings[6] =  Entities:FindByName(nil, "dota_goodguys_tower1_top")
		buildings[7] =  Entities:FindByName(nil, "dota_goodguys_tower2_top")
		buildings[8] =  Entities:FindByName(nil, "dota_goodguys_tower3_top")
		
		buildings[9] =  Entities:FindByName(nil, "dota_goodguys_tower4_top")
		buildings[10] =  Entities:FindByName(nil, "dota_goodguys_tower4_bot")
		
		buildings[11] =  Entities:FindByName(nil, "good_rax_melee_bot")
		buildings[12] =  Entities:FindByName(nil, "good_rax_range_bot")
		buildings[13] =  Entities:FindByName(nil, "good_rax_melee_mid")
		buildings[14] =  Entities:FindByName(nil, "good_rax_range_mid")
		buildings[15] =  Entities:FindByName(nil, "good_rax_melee_top")
		buildings[16] =  Entities:FindByName(nil, "good_rax_range_top")
		
		buildings[17] =  Entities:FindByName(nil, "ent_dota_fountain_good")

	else	
		--dire
		buildings[0] =  Entities:FindByName(nil, "dota_badguys_tower1_bot")
		buildings[1] =  Entities:FindByName(nil, "dota_badguys_tower2_bot")
		buildings[2] =  Entities:FindByName(nil, "dota_badguys_tower3_bot")
		
		buildings[3] =  Entities:FindByName(nil, "dota_badguys_tower1_mid")
		buildings[4] =  Entities:FindByName(nil, "dota_badguys_tower2_mid")
		buildings[5] =  Entities:FindByName(nil, "dota_badguys_tower3_mid")
		
		buildings[6] =  Entities:FindByName(nil, "dota_badguys_tower1_top")
		buildings[7] =  Entities:FindByName(nil, "dota_badguys_tower2_top")
		buildings[8] =  Entities:FindByName(nil, "dota_badguys_tower3_top")
		
		buildings[9] =  Entities:FindByName(nil, "dota_badguys_tower4_top")
		buildings[10] =  Entities:FindByName(nil, "dota_badguys_tower4_bot")
		
		buildings[11] =  Entities:FindByName(nil, "bad_rax_melee_bot")
		buildings[12] =  Entities:FindByName(nil, "bad_rax_range_bot")
		buildings[13] =  Entities:FindByName(nil, "bad_rax_melee_mid")
		buildings[14] =  Entities:FindByName(nil, "bad_rax_range_mid")
		buildings[15] =  Entities:FindByName(nil, "bad_rax_melee_top")
		buildings[16] =  Entities:FindByName(nil, "bad_rax_range_top")
		
		buildings[17] =  Entities:FindByName(nil, "ent_dota_fountain_bad")
	end

	for i,unit in ipairs(buildings) do
		world.entities[unit:entindex()]=self:JSONunit(unit)
	end

	return package.loaded['game/dkjson'].encode(world)
end 
 

 function VectorToArray(v)
	return {v.x, v.y, v.z}
 end
 
 