package dev.peerat.training.cherrytreeholiday.routes.activities;

import static dev.peerat.framework.RequestType.DELETE;

import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.framework.utils.json.JsonMap;
import dev.peerat.training.cherrytreeholiday.models.CherryUser;
import dev.peerat.training.cherrytreeholiday.repository.Repository;

public class LeaveActivity implements Response {

	private Repository repo;
	
	public LeaveActivity(Repository repo){
		this.repo = repo;
	}
	
	
	@Route(path = "/activity/(\\d+)/lv",type = DELETE, needLogin = true)
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		int activityId = Integer.parseInt(matcher.group(1));
		CherryUser user = context.getUser();
		JsonMap json = reader.readJson();
		
		if(repo.isActivityOwner(user, activityId)){
			if(repo.removeFromActivity(json.get("user"), activityId)){
				context.response(200);
			}else{
				context.response(403);
			}
		}else context.response(401);
	}
}
