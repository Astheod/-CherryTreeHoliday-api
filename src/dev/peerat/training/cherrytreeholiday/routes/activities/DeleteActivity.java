package dev.peerat.training.cherrytreeholiday.routes.activities;

import static dev.peerat.framework.RequestType.DELETE;

import java.util.regex.Matcher;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Response;
import dev.peerat.framework.Route;
import dev.peerat.training.cherrytreeholiday.repository.Repository;

public class DeleteActivity implements Response{
	
	private Repository repo;
	
	public DeleteActivity(Repository repo){
		this.repo = repo;
	}

	@Route(path = "/activity/(\\d+)/",type = DELETE, needLogin = true)
	public void exec(Matcher matcher, Context context, HttpReader reader, HttpWriter writer) throws Exception{
		int activityId = Integer.parseInt(matcher.group(1));
		if(!repo.isActivityOwner(context.getUser(), activityId)){
			context.response(401);
			return;
		}
		if(repo.deleteActivity(activityId)){
			context.response(200);
		}else{
			context.response(403);
		}
	}
}
