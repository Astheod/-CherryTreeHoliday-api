package dev.peerat.training.cherrytreeholiday.routes.activities;

import static dev.peerat.framework.RequestType.PUT;

import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.framework.utils.json.JsonMap;
import dev.peerat.training.cherrytreeholiday.repository.Repository;

public class SwitchOwnerActivity implements Response{
	
	private Repository repo;
	
	public SwitchOwnerActivity(Repository repo){
		this.repo = repo;
	}

	@Route(path = "/activity/(\\d+)/owner", type = PUT, needLogin = true)
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		int activityId = Integer.parseInt(matcher.group(1));
		if(!repo.isActivityOwner(context.getUser(), activityId)){
			context.response(401);
			return;
		}
		JsonMap json = reader.readJson();
		if(repo.switchActivityOwner(activityId, json.get("pseudo"))){
			context.response(200);
		}else{
			context.response(403);
		}
	}

}
