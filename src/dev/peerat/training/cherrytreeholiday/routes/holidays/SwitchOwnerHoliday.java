package dev.peerat.training.cherrytreeholiday.routes.holidays;

import static dev.peerat.framework.RequestType.*;

import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.framework.utils.json.JsonMap;
import dev.peerat.training.cherrytreeholiday.repository.Repository;
import dev.peerat.training.cherrytreeholiday.routes.FormResponse;

public class SwitchOwnerHoliday extends FormResponse{
	
	private Repository repo;
	
	public SwitchOwnerHoliday(Repository repo){
		this.repo = repo;
		
		require((json) -> ((JsonMap)json).has("pseudo"));
		hasLength((j)->j, (json) -> ((JsonMap)json).get("pseudo"), 1, 50);
	}

	@Route(path = "/holiday/(\\d+)/owner", type = PUT, needLogin = true)
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		int holidayId = Integer.parseInt(matcher.group(1));
		if(!repo.isHolidayOwner(context.getUser(), holidayId)){
			context.response(401);
			return;
		}
		JsonMap json = readJson(reader);
		if(!isValid(() -> context.response(400))) return;
		
		if(repo.switchHolidayOwner(holidayId, json.get("pseudo"))){
			context.response(200);
		}else{
			context.response(403);
		}
	}

}
