-- This file holds functions that handle the responses from a bot and calls the appropriate LUA functions
--

Dota2AI._Error = false;

 function Dota2AI:ParseHeroLevelUp(eHero, body)
 	if body == nil or body.command ~= "LEVELUP" then 
 		return 
 	end

 	local abilityPoints = eHero:GetAbilityPoints()
	if abilityPoints <= 0 then
		Warning(eHero:GetName() .. " has no ability points. Why am I levelling up?")
		return
	end
	
	if (body == nil) then
 		return 
 	end

	local abilityIndex = body.abilityIndex

	print("Levelling up: " .. abilityIndex)

	if abilityIndex > -1 then --a bot may send -1 to delay the level up
		local ability = eHero:GetAbilityByIndex(abilityIndex)
		if ability:GetLevel() == ability:GetMaxLevel() then
			Warning(eHero:GetName() .. ": " .. ability:GetName() .. " is maxed out")
			return
		end		
		ability:UpgradeAbility(false)
		eHero:SetAbilityPoints(abilityPoints - 1) --UpgradeAbility doesn't decrease the ability points
	end
 end

 -- Main entry function --
 function Dota2AI:ParseHeroCommand(eHero, result)	
	local command = result.command

	local eAbility = eHero:GetCurrentActiveAbility()
	if eAbility then 
		self:Noop(eHero, result)
		Warning("active not null")
		return
	end

	if command == "MOVE"  then
		self:MoveTo(eHero, result)
	elseif command == "ATTACK" then
		self:Attack(eHero, result)
	elseif command == "CAST" then
		self:Cast(eHero, result)
	elseif command == "BUY" then
		self:Buy(eHero, result)
	elseif command == "SELL" then
		self:Sell(eHero, result)
	elseif command == "USE_ITEM" then
		self:UseItem(eHero, result)
	elseif command == "NOOP" then
		self:Noop(eHero, result)
	elseif command == "PICKUP_RUNE" then
		self:aPickupRune(eHero, result)
	elseif command == "GRAB_ALL" then 
		self:GrabAll(eHero)
	else 
		self._Error = true
		Warning(eHero:GetName() .. " sent invalid command " .. reply)
	end
 end



function Dota2AI:Sell(eHero, result)
	local slot = result.slot
	
	if eHero:CanSellItems() then 
		local eItem = eHero:GetItemInSlot(slot)
		if eItem then
			EmitSoundOn("General.Sell", eHero)
			eHero:ModifyGold(eItem:GetCost()/2, true, DOTA_ModifyGold_SellItem ) 
			eHero:RemoveItem(eItem)
		else
			Warning("No item in slot " .. slot)
		end
	else
		Warning("Shop not in range.")
	end
end

function Dota2AI:UseItem(eHero, result)
	local slot = result.slot
	local eItem = eHero:GetItemInSlot(slot)
	if eItem then
		self:UseAbility(eHero, eItem, result)
	else
		Warning("Bot tried to use item in empty slot")
	end
end
 
 function Dota2AI:Noop(eHero, result)
	--Noop
end
 
function Dota2AI:MoveTo(eHero, result)
	if result.target ~= nil and result.target ~= -1 then 
		eHero:MoveToNPC(EntIndexToHScript(result.target))
	else 
		eHero:MoveToPosition(Vector(result.x, result.y, result.z))
	end 
	
end

function Dota2AI:Attack(eHero, result)
	eHero:MoveToTargetToAttack(EntIndexToHScript(result.target))
end


-- Pickups a rune given by target (== id) inside result 
function Dota2AI:aPickupRune(eHero, result) 
	local rune = EntIndexToHScript(result.target)
	eHero:PickupRune(rune)
end