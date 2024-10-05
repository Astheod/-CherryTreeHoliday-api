package dev.peerat.training.cherrytreeholiday.routes.holidays;

import static dev.peerat.framework.RequestType.DELETE;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.framework.utils.json.JsonArray;
import dev.peerat.training.cherrytreeholiday.repository.Repository;
import dev.peerat.training.cherrytreeholiday.models.CherryUser;

public class RemoveUserHoliday implements Response{
	
	private Repository repo;
	
	public RemoveUserHoliday(Repository repo){
		this.repo = repo;
	}

	@Route(path = "/holiday/(\\d+)/users",type = DELETE, needLogin = true)
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		int holidayId = Integer.parseInt(matcher.group(1));
		CherryUser user = context.getUser();
		JsonArray json = reader.readJson();
		
		List<String> list = new ArrayList<>();
		for(Object u : json.toList()) list.add(u.toString());
		
		if(list.contains(user.getPseudo())){
			List<String> quit = new ArrayList<>();
			quit.add(user.getPseudo());
			if(repo.removeHolidayUser(quit, holidayId)){
				context.response(200);
			}else{
				context.response(403);
			}
			return;
		}
		
		if(repo.isHolidayOwner(user, holidayId)){
			if(repo.removeHolidayUser(list, holidayId)){
				context.response(200);
			}else{
				context.response(403);
			}
		}else context.response(401);
	}

}
