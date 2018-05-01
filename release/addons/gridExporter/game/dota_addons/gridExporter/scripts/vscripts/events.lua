-- Here are the event trigger that are called by the game on certain occasions asynchronously to the game's ticks

--------------------------------------------------------------------------------
-- GameEvent:OnGameRulesStateChange
--
-- Handles the game state cycles. Not much to do here yet
--
--------------------------------------------------------------------------------
function Dota2AI:OnGameRulesStateChange()
  local nNewState = GameRules:State_Get()  
  if nNewState == DOTA_GAMERULES_STATE_INIT then
    self:Reset()
  elseif nNewState == DOTA_GAMERULES_STATE_HERO_SELECTION then
    print( "OnGameRulesStateChange: Hero Selection" )
	PlayerResource:SetCustomTeamAssignment( 0, DOTA_TEAM_FIRST  ) -- put PlayerID 0 on Radiant team (== team 2)	
	PlayerResource:GetPlayer(0):MakeRandomHeroSelection()	--FIXME
  elseif nNewState == DOTA_GAMERULES_STATE_PRE_GAME then
    print( "OnGameRulesStateChange: Pre Game Selection" )
    SendToServerConsole( "dota_dev forcegamestart" ) -- Skip the draft process
  elseif nNewState == DOTA_GAMERULES_STATE_GAME_IN_PROGRESS then
    print( "OnGameRulesStateChange: Game In Progress" )
	BotPick()
	
  end    
end

--------------------------------------------------------------------------------
-- GameEvent:OnHeroPicked
--
-- Once a hero is picked, a "context think function" is set that make makes a web call every time it's called
--------------------------------------------------------------------------------

 function Dota2AI:OnHeroPicked ()
	-- limited to player one for now
	print(" minX:" .. GetWorldMinX() .. "maxX: " .. GetWorldMaxX())
	print(" minY:" .. GetWorldMinY() .. "maxY: " .. GetWorldMaxY())

	local heroEntity = PlayerResource:GetSelectedHeroEntity(0)
	heroEntity:SetContextThink( "Dota2AI:BotThink", function() return Dota2AI:BotThink(heroEntity) end, 0.1 )		
 end

 --------------------------------------------------------------------------------
 -- GameEvent:BotThink
 --
 -- Main function for a bot. Each tick, it makes a web call to determine if any web action should be taken
 -- The return value is the time in s when this function should be called again, and the function will
 -- halt and wait for the HTTP call to return. Thinking too long will prompt a warning in the game
 -- and should be sanytioned in the future
 --------------------------------------------------------------------------------
 function Dota2AI:BotThink(heroEntity)  
 	local origin = heroEntity:GetOrigin()
 	
 	if gridWritten == false then 
 		writeGridData()
 	end

 	if itemsWritten == false then
 		writeItems()
 	end

 	
    return 0.33 
 end

function writeItems()

	file = io.open("items.data", "w")
	io.output(file)

	for _, item in pairs(items) do 
		local price = 0
		if item.name ~= nil then 
			price = GetItemCost(item.name)
		end 

		local components = ""
		if item.components ~= nil then 
			for _, component in pairs(item.components) do
				components = components .. component .. ","
			end
		end

		local shop = "base"
		if item.shop ~= nil then 
			shop = item.shop
		end
	

		io.write(item.name .. " " .. price .. " " .. shop .. " [" .. components .. "]\n")

	end

	io.close(file)
	itemsWritten = true

end

function writeGridData()
	local array = {}
	
	minX = 0
	maxX = 0
	half = GetWorldMaxX()/2

	minY = -1
	maxY = -1

	step = 8

	print(minX)
	print(maxX)
	print(half)


	file = io.open("grid.data", "w")
	io.output(file)
		
	for y = GetWorldMinY(), GetWorldMaxY(), step do
		lineTooLow = true
		for x = GetWorldMinX(), GetWorldMaxX(), step do
			if GetGroundHeight(Vector(x, y, 0), nil) ~= -16384 then
				lineTooLow = false
				if x < 0 then
					if minX > x then 
						minX = x
					end
				else
					if maxX < x then
						maxX = x
					end
				end
			end
		end

		if lineTooLow == false then
			if minY == -1 and maxY == -1 then 
				minY = y
			end
		end

		if lineTooLow == true then 
			if minY ~= -1 and maxY == -1 then 
				maxY = y-1
			end
		end 
	end

	print("after")
	print(minX)
	print(maxX)
	print(half)



	writeHeader()
	writeGrid()
	writeHeightMap()

	io.close(file)
	gridWritten = true	
end
 
function writeHeader()
	io.write("#header\n")
	io.write("Step:" .. tostring(step) .. "\n")
	

	io.write("X:[" .. tostring(minX) .. ", " .. tostring(maxX) .. "]\n")
	io.write("Y:[" .. tostring(minY) .. ", " .. tostring(maxY) .. "]\n")


	
	io.write("#\n")
end

function writeGrid() 
	io.write("#navmap\n")
	for y = maxY, minY, -step do
		for x = minX, maxX, step do
			if GetGroundHeight(Vector(x, y, 0), nil) == -16384 then
				io.write("9")
			else 
				local traversable = GridNav:IsTraversable(Vector(x, y, 0))
				local blocked = GridNav:IsBlocked(Vector(x, y, 0))

				if traversable and blocked then
					io.write("1")
				elseif blocked and not traversable then 
					io.write("2")
				elseif not traversable and not blocked then 
					io.write("3")
				else 
					io.write("0")
				end	
			end				
		end
		io.write("\n")
	end
	io.write("#\n")
end

function writeHeightMap()
	io.write("#heightmap\n")
	for y = maxY, minY, -step do
		for x = minX, maxX, step do
			local height = GetGroundHeight(Vector(x,y, 0), nil)
			if  math.abs(height) > 1000 then 
				io.write("x ")
			else 
				io.write(tostring(math.floor(height)) .. " ")	
			end				
		end
		io.write("\n")
	end
	io.write("#\n")
end
 
 function Dota2AI:OnBaseURLChanged(event)
	Dota2AI.baseURL = CustomNetTables:GetTableValue( "game_state", "base_url" )["value"]
	print("New base URL " .. Dota2AI.baseURL)
 end
 
 function BotPick()
    PrecacheUnitByNameAsync("npc_dota_hero_lina", 
	function( )
		--CreateHeroForPlayer( command.hero, PlayerResource:GetPlayer(0)	) 
		PlayerResource:ReplaceHeroWith( 0, "npc_dota_hero_lina", 0, 0) --FIXME
		Dota2AI:OnHeroPicked()
	end,
	0)	  		
    
 end

 -- Prints x and y coordinates of given vector
 function printVector(vec)
 	io.write("[" .. tostring(math.floor(vec.x)) .. ", " .. tostring(math.floor(vec.y)) .. "]")
 end

function VectorToArray(v)
	return {v.x, v.y, v.z}
end
