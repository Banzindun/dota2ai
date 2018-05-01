----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
-- This file defines functions that works with abilities. The main goal is to define functions that 
-- would be able to handle any type of ability.
-- 
--    
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------

-- Performs a bitwise and on two passed numbers.
local function BitAND(a,b)
    local p,c=1,0
    while a>0 and b>0 do
        local ra,rb=a%2,b%2
        if ra+rb>1 then c=c+p end
        a,b,p=(a-ra)/2,(b-rb)/2,p*2
    end
    return c
end

-- Casts the ability, using the body of request that came from the framework.
-- 		eHero - hero that is casting the ability
-- 		result - body of request that came from the framework
function Dota2AI:Cast(eHero, result)
	-- Find the ability by index from the request
	local eAbility = eHero:GetAbilityByIndex(result.ability)

	--print ("------------------------------------------------")
	--for name, value in pairs(eAbility:GetAbilityKeyValues()) do 
		--if type(value) ~= "table" then 
			--print(name .. ": " .. value)
		--else
			--print (name .. ":")
			--PrintTable(value, "\t")
		--end
	--end

	self:UseAbility(eHero, eAbility, result)		
end

-- This function takes hero, ability and table, that represents the response that came from the framwork.
-- I then check if the argument is passed in correct way and that the ability can be cast. 
-- Returns true if the cast can be performed (or handled and checked in more detail), else returns false.
function Dota2AI:CheckAbility(eHero, eAbility, result)
	-- Check that level is > than 0
	local level = eAbility:GetLevel()
	if level == 0 then 
		Warning("Level of ability is 0.")
		return false
	end

	-- Check mana cost
	local manaCost = eAbility:GetManaCost(level)
	if eHero:GetMana() < manaCost then 
		Warning("No mana.")
		return false
	end 

	-- Check the cooldown
	if eAbility:GetCooldownTimeRemaining() > 0 then
		Warning("On cooldown.")
		return false
	end


	-- Load coordinates or target that will be used.
	local x = result.x
	local y = result.y
	local z = result.z
	local targetID = result.target
	
	--print("Casting: " .. abilityName)

	-- Check range, we set x and y to -1 if we do not cast to position.
	local range = eAbility:GetCastRange()
	if x ~= -1 and y ~= -1 then 
		if not eHero:IsPositionInRange(Vector(x,y,z), range) then
			Warning("Casting to position not in range.")
			return false
		end
	end

	-- Check range for target, we set target to -1 if we do not use it.
	if targetID ~= -1 then 
		local target = EntIndexToHScript(result.target)
		
		if target == nil then 
			Warning("Target doesn't exist.")
			return false
		end	

		if not eHero:IsPositionInRange(target:GetAbsOrigin(), range) then 
			Warning("Target not in range.")
			return false
		end
	end

	return true
end


-- Takes reference to hero, ability that should be cast and body of framework's response
-- Should cast a spell (if possible) using the specified arguments.
function Dota2AI:UseAbility(eHero, eAbility, result)
	if not self:CheckAbility(eHero, eAbility, result) then 
		return 
	end 

	
	-- Name of the ability
	local abilityName = eAbility:GetName() 

	-- Bahavior 
	local behaviour = eAbility:GetBehavior()

	-- Get player
	local player = eHero:GetPlayerOwnerID()

	-- Load coordinates or target that will be used.
	local x = result.x
	local y = result.y
	local z = result.z

	local targetID = result.target

	-- Resolve ability by behavior
	-- I do not need to deal with all of the types, for example Channel abilities are 
	-- of type NO_TARGET/TARGET etc. 
	if (BitAND(behaviour, DOTA_ABILITY_BEHAVIOR_NO_TARGET) > 0) then
		print("DOTA_ABILITY_BEHAVIOR_NO_TARGET")
		eHero:CastAbilityNoTarget(eAbility, player)
	elseif (BitAND(behaviour, DOTA_ABILITY_BEHAVIOR_PASSIVE) > 0) then 
		print("DOTA_ABILITY_BEHAVIOR_PASSIVE")
		Warning("Cannot cast passive spell.")
		return 
	elseif(BitAND(behaviour, DOTA_ABILITY_BEHAVIOR_HIDDEN) > 0)  then
		print("DOTA_ABILITY_BEHAVIOR_HIDDEN")
		Warning("Cannot cast hidden spell.")
		return
	elseif(BitAND(behaviour, DOTA_ABILITY_BEHAVIOR_UNIT_TARGET ) > 0) then
		print("DOTA_ABILITY_BEHAVIOR_UNIT_TARGET")
		
		-- If we have position, set Cursor position, else cast on a target
		if (BitAND(behaviour, DOTA_ABILITY_BEHAVIOR_POINT ) > 0) then 
			if x ~= -1  and y ~= -1 and z ~= -1 then 
				eHero:SetCursorPosition(Vector(result.x, result.y, result.z))
			end
		else 
			if targetID == -1 then 
				Warning("No target! " .. abilityName )
				return 
			end 

			local target = EntIndexToHScript(result.target)
			if target:IsAlive() then
				eHero:CastAbilityOnTarget(target, eAbility, player)
			end
		end
	elseif(BitAND(behaviour, DOTA_ABILITY_BEHAVIOR_POINT ) > 0) then
		print("DOTA_ABILITY_BEHAVIOR_POINT")

		if targetID == -1 then 
			-- Set mouse cursor to position
			eHero:SetCursorPosition(Vector(result.x, result.y, result.z))
		else 
			local target = EntIndexToHScript(result.target)
			if target:IsAlive() then
				eHero:SetCursorCastTarget(target)
			end
		end 

		--eHero:CastAbilityOnPosition(Vector(result.x, result.y, result.z), eAbility, player)
	else
		Warning(eHero:GetName() .. " sent invalid cast command " .. behaviour)
		self._Error = true
		return
	end

	-- ability can be null here - for example item is spent instantly and then is null
	if not eAbility:IsNull() then 
		eAbility:OnAbilityPhaseStart()
		eAbility:OnSpellStart()

		if not eAbility:IsNull() then 
			local level = eAbility:GetLevel() -- When using clarity I got error here, so I have added one more check??
			eAbility:StartCooldown(eAbility:GetCooldown(level))
			eAbility:PayManaCost()
		end
	end
end


