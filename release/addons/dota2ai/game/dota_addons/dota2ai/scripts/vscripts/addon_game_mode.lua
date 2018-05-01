 

if Dota2AI == nil then
	_G.Dota2AI = class({})
end

------------------------------------------------------------------------------------------
-- Required .lua files, which just exist to help organize functions contained in our addon. 
------------------------------------------------------------------------------------------
require( "json_helpers" )
require( "hero_tools" )
require( "utility_functions" )
require( "events" )
require( "requests" )
require( "interests" )
require( "timers" )
require( "inventory" )
require( "abilities")

--------------------------------------------------------------------------------
-- ACTIVATE
--------------------------------------------------------------------------------
function Activate()
    GameRules.Dota2AI = Dota2AI()
    GameRules.Dota2AI:InitGameMode()	
end


require ("config")

--------------------------------------------------------------------------------
-- INIT
--------------------------------------------------------------------------------
function Dota2AI:InitGameMode()
	print( "Initializing Dota2 AI mode" )
	CustomNetTables:SetTableValue( "game_state", "base_url", { value =  Dota2AI.baseURL} )
	local GameMode = GameRules:GetGameModeEntity()
	
	-- Enable bot thinking
	GameMode:SetBotThinkingEnabled( true )
		
	GameRules:SetCustomGameSetupTimeout( 60 )
	GameRules:SetCustomGameSetupRemainingTime( 60 )
	
	GameRules:SetShowcaseTime( 0 )
	GameRules:SetStrategyTime( 0 )
	--GameRules:SetStrategyTime( 0 )
	GameRules:SetHeroSelectionTime( 20 )
	--GameRules:SetCustomGameSetupTimeout( Dota2AI.ConfigUITimeout ) -- skip the custom team UI with 0, or do indefinite duration with -1
		
	-- Events
	ListenToGameEvent( "game_rules_state_change", Dynamic_Wrap( Dota2AI, 'OnGameRulesStateChange' ), self )
	ListenToGameEvent("player_chat", Dynamic_Wrap(Dota2AI, 'OnPlayerChat'), self)
	
	-- Set the function, that is called before item is added to inventory.
	GameRules:GetGameModeEntity():SetItemAddedToInventoryFilter(function(ctx, event)
    	local item = EntIndexToHScript(event.item_entindex_const)

    	-- Heroes are buying tpscroll on the start of the game. We will disable that by this conditional.
    	if item:GetAbilityName() == "item_tpscroll" and item:GetPurchaser() == nil then
    	 return false 
    	end


    	return true
	end, self)

	-- Add listener for url change
	CustomGameEventManager:RegisterListener( "base_url_changed", function(...) return self:OnBaseURLChanged( ... ) end  )	
 end

print ("Addon loaded.")