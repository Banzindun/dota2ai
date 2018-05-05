


Dota2AI:HandleCourier(result)
	local team = result.team 
	if team == nil or team == -1
		Warning("Courier couln't be found. Supply a correct team.")
	end

	local courier = PlayerResource:GetNthCourierForTeam(0, team) 

	self:CourierMoveTo(courier, result)
end



Dota2AI:CourierMoveTo(courier, result)
	if result.x ~= -1 and result.y ~= -1 then 
		courier:MoveToPosition(result.x, result.y, result.z)

	elseif result.id ~= -1 then 
		courier:MoveToNPCToGiveItem(handle a, handle b) 
	end
	


end
















































