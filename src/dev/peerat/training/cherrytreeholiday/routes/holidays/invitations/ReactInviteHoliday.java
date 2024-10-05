package dev.peerat.training.cherrytreeholiday.routes.holidays.invitations;

import static dev.peerat.framework.RequestType.PUT;

import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.framework.utils.json.JsonMap;
import dev.peerat.training.cherrytreeholiday.repository.Repository;

public class ReactInviteHoliday implements Response{
	
	private Repository repo;
	
	public ReactInviteHoliday(Repository repo){
		this.repo = repo;
	}

	@Route(path = "/holiday/(\\d+)/invite",type = PUT, needLogin = true)
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		int holidayId = Integer.parseInt(matcher.group(1));
		
		JsonMap json = reader.readJson();
		
		if(!json.has("action")){
			context.response(400);
			return;
		}
		
		if(repo.inviteHoliday(context.getUser(), holidayId, json.get("action"))){
			context.response(200);
		}else{
			context.response(400);
		}
	}

}
