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


-- Finds empty slot inside inventory for hero and start index
-- Returns -1 if all slots are full
function Dota2AI:FindEmptySlot(eHero, start, theEnd)
	for i=start, theEnd, 1 do 
		local item = eHero:GetItemInSlot(i)

		if item == nil then
			return i
		end
	end

	return -1
end

-- Finds empty stash slot for given hero starting from slot start.
function Dota2AI:FindEmptyStashSlot(eHero, start)
	for i=start, DOTA_STASH_SLOT_6, 1 do 
		local item = eHero:GetItemInSlot(i)

		if item == nil then
			return i
		end
	end

	return -1
end

-- Grabs all items from stash and moves them to player's inventory
function Dota2AI:GrabAll(eHero)
	-- 6 inventory
	-- 3 backpack
	-- 6 stash

	print("Hero is grabbing all.")

	-- Check if I am close to shop
	local eShop = Entities:FindByClassnameWithin(nil, "trigger_shop", eHero:GetOrigin(), 0.01)		
	if eShop == nil then
		Warning("I am not close to any shop!!")
		return
	else
		local shopType = GetShopType(eShop:GetModelName())
		if shopType ~= 0 then
			Warning("I am not near radiant home shop!")
			return
		end
	end
	
	local _slot = 0
	for i=DOTA_STASH_SLOT_1, DOTA_STASH_SLOT_6, 1 do 
		local item = eHero:GetItemInSlot(i)

		if item then
			_slot = self:FindEmptySlot(eHero, _slot, DOTA_ITEM_SLOT_6 + 3)		
			if slot ~= -1 then 
				eHero:SwapItems(i, _slot)
			else 
				Warning("I have found an item in stash, but inventory is full.")
				return
			end
		end
	end
end


-- For given itemName and eHero, this function tries to find slot with given item
-- inside inventory (slots 0-5)
function Dota2AI:FindItemInInventory(itemName, eHero)
	for i=DOTA_ITEM_SLOT_6, DOTA_ITEM_SLOT_1, -1 do 
		local item = eHero:GetItemInSlot(i)
		if item ~= nil and item:GetName() == itemName then
			return i
		end

	end 
	return -1
end


function Dota2AI:JSONInventory(unit, eHero)
	-- Go through all the items, backpack will be slots 7-9
	unit.inventory={}
	for i=DOTA_ITEM_SLOT_1, DOTA_STASH_SLOT_6 do 
		local item = eHero:GetItemInSlot(i)
		
		unit.inventory[tostring(i)] = {}

		if item ~= nil then 
			unit.inventory[tostring(i)].name = item:GetName()
			if item:IsStackable() then 
				unit.inventory[tostring(i)].count = item:GetCurrentCharges()
			else 
				unit.inventory[tostring(i)].count = 1
			end
		else 
			unit.inventory[tostring(i)].name = ""
			unit.inventory[tostring(i)].count = 0
		end

	end 
	return -1
end

-- Handles buying of items for given hero. 
-- Takes a hero and body of the request. 
-- The body should contain the name of the item, that the hero should buy.
function Dota2AI:Buy(eHero, result)
	-- Get name of the item and its cost
	local itemName = result.item
	local itemCost = GetItemCost(itemName)

	print("Hero is buying: " .. itemName)
	
	-- Check the gold
	if eHero:GetGold() < itemCost then
		Warning(eHero:GetName() .. " tried to buy " .. itemName .. " but couldn't afford it")
		return
	end
	
	-- Find empty slot in inventory and backpack
	local targetSlot = self:FindEmptySlot(eHero, DOTA_ITEM_SLOT_1, DOTA_ITEM_SLOT_9)
		
	-- Find the closest shop
	local eShop = Entities:FindByClassnameWithin(nil, "trigger_shop", eHero:GetOrigin(), 0.01)		

	if eShop and targetSlot ~= -1  then 
		local shopType = GetShopType(eShop:GetModelName())
		
		-- Check availability of item
		if IsAvailableInShop(itemName, shopType) then
			self:BuyAndAddItem(eHero, itemName)
		else
			Warning("Item: " .. itemName .. " is not available in shop of type: " .. shopType)		
			return
		end
	else
		-- Check if it is available inside base radiant shop, if so buy to stash
		if IsAvailableInShop(itemName, 0) then
			print("Buying to stash.")

			local stashSlot = self:FindEmptySlot(eHero, DOTA_STASH_SLOT_1 , DOTA_STASH_SLOT_6)
			if stashSlot == -1 then 
				Warning("No space in stash.")
				return
			end

			local items = {}
			for i = DOTA_ITEM_SLOT_1, DOTA_ITEM_SLOT_9, 1 do 
				local itemHandle = eHero:GetItemInSlot(i)
				if itemHandle ~= nil then 
					items[tostring(i)] = {}
					items[tostring(i)].name = itemHandle:GetName() 
					if itemHandle:IsStackable() then 
						items[tostring(i)].count = itemHandle:GetCurrentCharges() 
					else
						items[tostring(i)].count = 1
					end
					eHero:RemoveItem(itemHandle)
				else 
					items[tostring(i)] = {}
				end
			end

			print("I have removed:")
			for n, item in pairs(items) do
				if item.count ~= nil then 
					print( item.name .. ", " .. item.count)
				else 
					print( item.name)
				end
			end

			self:BuyAndAddItem(eHero, itemName)
			
			local slot = self:FindItemInInventory(itemName, eHero)
			if slot == -1 then 
				eHero:SwapItems(DOTA_ITEM_SLOT_1, stashSlot)
			else 
				eHero:SwapItems(slot, stashSlot)
			end 

			-- To be sure, remove all items from inventory and backpack
			for i = DOTA_ITEM_SLOT_1, DOTA_ITEM_SLOT_9, 1 do 
				local itemHandle = eHero:GetItemInSlot(i)
				if itemHandle ~= nil then 
					eHero:RemoveItem(itemHandle)
				end
			end

			for n, item in pairs(items) do
				if item ~= nil then
					if item.name ~= nil then 
						if item.count ~= nil then
							for i=1, item.count do
								local eItem = CreateItem(item.name, eHero, eHero)		
								eHero:AddItem(eItem)
							end
						end
					end
				end
			end
		end
		return
	end
end

function Dota2AI:BuyAndAddItem(eHero, itemName)
		local eItem = CreateItem(itemName, eHero, eHero)		
		EmitSoundOn("General.Buy", eHero)
		eHero:AddItem(eItem)
		eHero:SpendGold(GetItemCost(itemName), DOTA_ModifyGold_PurchaseItem)
end