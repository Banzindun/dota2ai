----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------
-- This file defines a few functions, that are used by main script to send requests to the framework.
-- Main features theese functions supply are:
--    - create request headers and creat JSON from passed data
--    - send the request to server and handle the response (parses the incoming JSON to table etc.)
--    - parse a command that the framework issued
--      - this is used mainly to check for server commands (PAUSE, UNPAUSE)
--    - call a function after the request failed/succeded
--    
----------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------

nOfHeroes = 0 -- For the player
loadedBots = 0 -- One less than nOfBots


-- FinishCustomGameSetup()

--------------------------------------------------------------------------------
-- GameEvent:OnGameRulesStateChange
--
-- Handles the game state cycles. Not much to do here yet
--
--------------------------------------------------------------------------------
function Dota2AI:OnGameRulesStateChange()
  local nNewState = GameRules:State_Get()  
  if nNewState == DOTA_GAMERULES_STATE_CUSTOM_GAME_SETUP then 
    -- Setup the game here
    Dota2AI:GameSetup()

  elseif nNewState == DOTA_GAMERULES_STATE_INIT then
    self:Reset()
    
  elseif nNewState == DOTA_GAMERULES_STATE_HERO_SELECTION then
    print( "OnGameRulesStateChange: Hero Selection" )
  	
    for name, slot in pairs(slots) do
      if bots[name].type == "human" then 
        print("Setting team and random hero to human on pos:" .. slot .. " " .. "[" .. name .. "]")
        PlayerResource:SetCustomTeamAssignment( slot, bots[name].team  )
        PlayerResource:GetPlayer(slot):MakeRandomHeroSelection()
      end

      if bots[name].type ~= "bot" then 
        nOfHeroes = nOfHeroes + 1
      end 
    end

    --PlayerResource:SetCustomTeamAssignment( 0, DOTA_TEAM_FIRST  ) -- put PlayerID 0 on Radiant team (== team 2)	
  	--PlayerResource:GetPlayer(0):MakeRandomHeroSelection()

	elseif nNewState == DOTA_GAMERULES_STATE_PRE_GAME then
    print( "OnGameRulesStateChange: Pre Game Selection" )
    --SendToServerConsole( "dota_dev forcegamestart" ) -- Skip the draft process
      
      GameStart()
      
      Timers:CreateTimer({
        endTime = 4, -- Call bot pick after 4 seconds, there were some issues with calling it immidietely
        callback = function()
          BotPick() 
        end
      })
      
      Timers:CreateTimer(2, function()
          if INIT == true then 
            Dota2AI:createBigUpdate(DOTA_TEAM_GOODGUYS) 
          end
          return 1.0
        end
      )

      Timers:CreateTimer(3, function()
         if INIT == true then 
          Dota2AI:createBigUpdate(DOTA_TEAM_BADGUYS) 
         end
          return 1.0
        end
      )

      ---- Finish the game after 5 minutes
      --Timers:CreateTimer(3, function()
          --if GameRules:GetGameTime() > 390 then 
            --GameRules:MakeTeamLose(DOTA_TEAM_BADGUYS)
            --return
          --end
          --return 1.0
        --end
      --)


  elseif nNewState == DOTA_GAMERULES_STATE_GAME_IN_PROGRESS then
    print( "OnGameRulesStateChange: Game In Progress" )
  end    
end

-- Sends the server a message that the game starts
function Dota2AI:GameSetup()
  local msg = {}
  request = SetRequest("/server/setup", package.loaded['game/dkjson'].encode(msg))
  sendAndHandleRequest(request, nil, function( body ) 
      Dota2AI:HandleGameSetup(body)
    end 
  )
end

function Dota2AI:HandleGameSetup(body)
  print("Game is trying to setup from: ")
  PrintTable(body, "\t")

  local slotIndex = 1

  for botName, cfg in pairs(body) do
    if botName == "__config" then 
      Dota2AI.config = cfg
    else 

      if Dota2AI:CheckBotsConfiguration(botName, cfg) then 
        bots[botName] = {} 
        bots[botName].name = botName
        bots[botName].team = tonumber(cfg.team) 
        bots[botName].type = cfg.type 
        bots[botName].heroName = cfg.heroName
        bots[botName].lane = cfg.lane
        bots[botName].difficulty = cfg.difficulty


        if cfg.type == "human" then 
          slots[botName] = 0
        elseif cfg.type == "bot" then
          slots[botName] = slotIndex
          slotIndex = slotIndex + 1
        end
      end
   
    end
  end

  for botName, cfg in pairs(bots) do
    if cfg.type == "ai" then 
      slots[botName] = slotIndex
      slotIndex = slotIndex + 1
    end
  end


  print("Slots look like: ")
  PrintTable(slots, "\t")

  if Dota2AI.config.skipSetup ~= nil then 
    GameRules:SetCustomGameSetupRemainingTime(0)
  end

end

function Dota2AI:CheckBotsConfiguration(botName, cfg) 
  print("------------------Checking the configuration with name:------------------" .. botName)

  if cfg.team == nil then 
    Warning("No team supplied. " .. botName)
    return false 
  else 
    if cfg.team ~= tostring(DOTA_TEAM_BADGUYS) and cfg.team ~= tostring(DOTA_TEAM_GOODGUYS) then 
      Warning("Unknown team." .. cfg.team)
      return false
    end
  end

  if cfg.type == nil then 
    Warning("No type supplied in player configuration.")
    return false
  else
    if cfg.type ~= "bot" and cfg.type ~= "ai" and cfg.type ~= "human" then 
      Warning("Bot type should be bot or ai. Instead you have passed " .. cfg.type)
      return false
    end
  end

  if cfg.heroName == nil then 
    Warning("No champion name supplied in player configuration.")
    return false
  end

  if cfg.type == "bot" then 
    if cfg.lane == nil then 
      Warning("You haven't supplied bot's lane.")
      return false
    end

    if cfg.lane ~=  "top" and cfg.lane ~= "bot" and cfg.lane ~= "mid" then
      Warning("Lane should be top, bot or mid. You have supplied:" .. cfg.lane)
      return false
    end
  end

  return true
end

function GameStart()
  local msg = {}
  request = SetRequest("/server/gamestart", package.loaded['game/dkjson'].encode(msg))
  sendAndHandleRequest(request,nil, function( body ) end, nil) 
end

--------------------------------------------------------------------------------
-- GameEvent:OnHeroPicked
--
-- Once a hero is picked, a "context think function" is set that make makes a web call every time it's called
--------------------------------------------------------------------------------
function Dota2AI:OnHeroPicked(name)
    local msg = {}
    msg.name = name
    msg.entid = bots[name].entid

    print("Bot with id " .. tostring(bots[name].entid) .. " sucessfully picked.")

    request = SetRequest("/server/selected", package.loaded['game/dkjson'].encode(msg))
    sendAndHandleRequest(request, nil, function( body ) 
        loadedBots = loadedBots + 1
        -- Check if all the bots loaded
        if loadedBots == nOfHeroes then
          SendInterests()
        end
      end 
    )
  
	print("Setting context think for bot with name: " .. name .. " [" .. bots[name].entid .. "]")
  bots[name].hero:SetContextThink( "Dota2AI:BotThink", function() return Dota2AI:BotThink(bots[name]) end, 2) 
end



function Dota2AI:Reset()
  local msg = {}
  request = SetRequest("/server/reset", package.loaded['game/dkjson'].encode(msg))
  sendAndHandleRequest(request, nil, function( body ) end) 
end

--------------------------------------------------------------------------------
-- GameEvent:OnHeroLevelUp
--
-- When a hero levels up, this function makes a web call to check which ability should be levelled
--------------------------------------------------------------------------------
function Dota2AI:OnHeroLevelUp(event)  
  local userid = event.player
  local heroindex = event.heroindex;
  local class = event.hero   

  
  -- Find entity in bots
  local hero = nil
  for k, v in pair(bots) do
    if v.entid == heroindex then 
      hero = v
      break
    end
  end
  
  -- only make a web call if it's the controlled hero. Dota2AI:OnHeroLevelUp is also called for other heroes
  if hero ~= nil then
    self:BotLevelUp(hero)
  end
end
 
 -- Helper function for Dota2AI:OnHeroLevelUp
function Dota2AI:BotLevelUp(heroEntity) 
  if INIT == false then 
    return
  end 

  request = SetRequest("/agent/" .. heroEntity.name .. "/levelup", "")
  sendAndHandleRequest(request, nil, function( body ) 
		  self:ParseHeroLevelUp(heroEntity.hero, body) 
    end, nil
  )
end
 
function Dota2AI:createBigUpdate(targetTeam) 
  if INIT == false then 
    return
  end 

  if targetTeam == DOTA_TEAM_GOODGUYS  then 
    request = SetRequest("/server/updateradiant", self:JSONBigWorld(targetTeam))
  else 
    request = SetRequest("/server/updatedire", self:JSONBigWorld(targetTeam))
  end

  
  sendAndHandleRequest(request, nil,  function( body ) end, nil)
end

function Dota2AI:createUpdate(heroEntity)
  -- If hero is dead we should not send the update, because it wouldn't contain anything.
  --if not heroEntity.hero:IsAlive() then 
  --  heroEntity.requests = heroEntity.requests - 1 
  --  return
  --end
  if INIT == false then 
    heroEntity.requests = heroEntity.requests - 1
    return
  end 

  request = SetRequest("/agent/" .. heroEntity.name .. "/update", self:JSONWorld(heroEntity))
    sendAndHandleRequest(request, heroEntity, 
      function( body ) -- Success function
        if body ~= nil then 
          self:ParseHeroCommand(heroEntity.hero, body) 
        end
      end, 
      function()  end -- Function that is called on fail
  )
end

function Dota2AI:BotThink(heroEntity)  
  -- Check if bot already issued all his available updates
  -- If so, wait updatePeriod and then try again to send an update
  if heroEntity.requests == updatesPerSecond then 
    return updatePeriod
  end

  -- Send bot update and increment the count of requests
  heroEntity.requests = heroEntity.requests + 1 
  Dota2AI:createUpdate(heroEntity)

  if heroEntity.hero:GetAbilityPoints() > 0 then
    self:BotLevelUp(heroEntity)
  end

  -- If there was an error try again right now	  
  if self._Error == true then
	  return 0
  else
	  return updatePeriod
  end 
end
 
 --------------------------------------------------------------------------------
 -- GameEvent:OnPlayerChat
 --
 -- This function doesn't do anything except forwarding chat messages of other players to the bot.
 -- I used it to control my test implementation, i.e. "bot go", "bot retreat", "bot attack" as simple chat commands
 --------------------------------------------------------------------------------
function Dota2AI:OnPlayerChat(event)
  print("ChatEvent with text:" .. event.text)

  print(self:JSONChat(event))

  --local heroes = HeroList:GetAllHeroes()
  request = SetRequest("/server/chat", self:JSONChat(event))
  sendAndHandleRequest(request, nil, function(body)   

    end, nil
  )
end

function Dota2AI:OnBaseURLChanged(event)
  Dota2AI.baseURL = CustomNetTables:GetTableValue( "game_state", "base_url" )["value"]
	print("New base URL " .. Dota2AI.baseURL)
end
 

-----------------------------------------------------------------------------------
-- Creates POST for getting the information about what character to pick from bot.
-- 
--
--
-----------------------------------------------------------------------------------
function BotPick(inDebug)
    for name, cfg in pairs(bots) do 
      local slot = slots[name]

      if slot ~= nil then 
        PlayerResource:SetCustomTeamAssignment( slot, cfg.team )
        if cfg.type == "human" then
          -- Player is always on slot 0
          --PlayerResource:GetPlayer(slot):MakeRandomHeroSelection()
          PrecacheUnitByNameAsync(cfg.heroName, function() 
              local hero = PlayerResource:ReplaceHeroWith(slot, cfg.heroName, 600, 0)
               
              --hero = CreateHeroForPlayer(name, PlayerResource:GetPlayer(index))
              --hero:RespawnHero(false, false) 
                          
              cfg.entid = hero:GetEntityIndex()
              cfg.hero = hero
              cfg.requests = 0

              Dota2AI:OnHeroPicked(name)
            end, 0)
        elseif cfg.type == "bot" then 
          print("Adding bot " .. name .. ": " .. cfg.heroName .. " to " .. cfg.lane .. " that is " .. cfg.difficulty)
          if cfg.team == DOTA_TEAM_GOODGUYS  then 
            Tutorial:AddBot( cfg.heroName, cfg.lane, cfg.difficulty, true );
          else 
            Tutorial:AddBot( cfg.heroName, cfg.lane, cfg.difficulty, false );
          end
        end

      end
    end

    SendToServerConsole( "dota_create_fake_clients")

    for name, cfg in pairs(bots) do 
      local slot = slots[name]

      if slot ~= nil then 
        PlayerResource:SetCustomTeamAssignment( slot, cfg.team )
        if cfg.type == "ai" then
          PrecacheUnitByNameAsync(cfg.heroName, function()                
              local hero = CreateHeroForPlayer(cfg.heroName, PlayerResource:GetPlayer(slot))
              hero:RespawnHero(false, false) 
                          
              cfg.entid = hero:GetEntityIndex()
              cfg.hero = hero
              cfg.requests = 0

              Dota2AI:OnHeroPicked(name)
            end, 0)
        end

      end
    end
end
