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


-- This function takes a address relative to the framework's server and creates headers 
-- for a request. For example for /server/update will create a request header, that is send 
-- with the request to adress localhost:8080/Dota2Ai/server/update. (if you use port 8080)
-- Returns the request.
function SetHeaders(addr)
  local request = CreateHTTPRequestScriptVM( "POST", Dota2AI.baseURL .. addr)
  
  -- headers
  request:SetHTTPRequestHeaderValue("Accept", "application/json")
  request:SetHTTPRequestHeaderValue("X-Jersey-Tracing-Threshold", "VERBOSE" )
  
  return request
end

-- This function sets body and its type (application/json) to the passed request.
-- Returns the request.
function SetBody(request, body)
  request:SetHTTPRequestHeaderValue("Content-Length", tostring(string.len(body)))
  request:SetHTTPRequestRawPostBody('application/json', body)
  return request
end

-- This function sets the request's body and headers
-- Returns the request.
function SetRequest(addr, body)
 
  local request = SetBody(SetHeaders(addr), body)
  return request
end

-- This function outputs the contents of passed table as a warning to the console.
function badResult(result) 
	Dota2AI.Error = true 
	for k,v in pairs( result ) do
		Warning( string.format( "%s : %s\n", k, v ) )
	end
end

-- This function pauses the game.
function doPause()
	PauseGame(true)
end

-- This function unpauses the game.
function doUnpause()
	PauseGame(false)
end

-- This function doesn't have a function right now.
-- This was intended as a way how to send commands from framework's console directly to the one in game.
function issueCommands(cmds)
	print("Issuing commands:")
	for _, c in pair(cmds) do
		print(c)
	end
	-- This is just a reminder, that this might be implemented later
end

-- This is a function, that receives the json input sent as a response by the server. 
-- It decodes it first into table and then looks what commands were received. 
-- If the game should be paused, unpaused or GAME_COMMANDS were received, then the decoded 
-- json is passed directly to function that handles it. 
-- Else the result is passed to handle, that is supplied as argument. (a function that handles the json)
function parseCommand(json, customHandle)
	local decoded = package.loaded['game/dkjson'].decode(json)
	
	--print(json)

	local command = ""
	if decoded ~= nil then 
		command = decoded.command
	end

	if command == "PAUSE" then
		doPause()
	elseif command == "UNPAUSE" then
		doUnpause()
	elseif command == "GAME_COMMANDS" then
		issueCommands(decoded.commands)
	else 
		if customHandle ~= nil then 
			customHandle(decoded)
		end
	end

end

-- This functions takes a few arguments: 
-- 		request - request, that stores request header, body etc, that should be sent to framework
-- 		heroEntity - reference to heroEntity, that is sending the request
--		okHandle - this is called if the request is sucessful
--		failHandle - called if request fails
-- 
-- The function sends request to framework and lets parseCommand(...) handle the response if sucessful. 
-- Else it calls the failHandle. 
-- HeroEntities hold the number of calls they have made. This number is decreased after the response is 
-- handled.
function sendAndHandleRequest(request, heroEntity, okHandle, failHandle)
	request:Send( 
		function(result)
			if result["StatusCode"] == 200 then
				parseCommand(result["Body"], okHandle)
			else
				if (failHandle ~= nil) then
					failHandle()
				end

				badResult(result)
			end

			-- Decrement the count of requests for the heroEntity
			if heroEntity ~= nil then 
				heroEntity.requests = heroEntity.requests - 1 
			end
		end
	)
end

